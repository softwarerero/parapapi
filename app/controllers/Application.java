package controllers;

import jobs.UpdateCategoryCount;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.cache.CacheFor;
import play.db.jpa.JPA;
import play.i18n.Lang;
import play.i18n.Messages;
import play.libs.Images;
import play.mvc.*;

import java.io.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

import controllers.auth.Security;

import models.*;
import play.data.validation.*;
import play.modules.paginate.ValuePaginator;
import play.templates.Template;
import play.templates.TemplateLoader;
import play.vfs.VirtualFile;
import py.suncom.parapapi.db.DbHelper;
import py.suncom.parapapi.db.SearchBuilder;


public class Application extends Controller {

  static public int pageSize = Integer.parseInt(Play.configuration.getProperty("pagesize"));


//  @CacheFor("15mn")
  public static void index() {
    String[] mainCategories = Category.main;
    String language = Lang.get();
    Map categoryCountMap = UpdateCategoryCount.getCategoryCountMap("CategoryCountMap");
    render("Application/index.html", mainCategories, categoryCountMap, language);
  }


  public static void renderCategories() {
    String[] mainCategories = Category.main;
    String lang = Lang.get();
    Map categoryCountMap = UpdateCategoryCount.getCategoryCountMap("CategoryCountMap_" + lang);
    Map args = new HashMap();
    args.put("_categoryCountMap", categoryCountMap);
    args.put("_mainCategories", mainCategories);
    Template t = TemplateLoader.load("tags/renderCategories.html");
    String html = t.render(args); //Template mit args rendern
    renderText(html.toString());
  }


  public static void search(String searchString) {
    validation.required(searchString).message(Messages.get("views.main.noSearchCriteria"));
    validation.minSize(searchString, 3).message(Messages.get("views.main.moreSearchCriteria"));

		if(validation.hasErrors()) {
      params.flash(); // add http parameters to the flash scope
      validation.keep(); // keep the errors for the next request
			index();
		}

    SearchBuilder sb = new SearchBuilder();
    String text = params.get("object.text");
    if(null != searchString && !"".equals(searchString)) {
      sb.startExpression().like("title", searchString).or().like("content", searchString).endExpression();
    }
    addLanguageExpression(sb);
    sb.orderBy("id desc");

    List<Ad> ads = searchAds(sb);
    long noFound = getNoFound(ads);
    ValuePaginator paginator = new ValuePaginator(ads);
    paginator.setPageSize(pageSize);
		render("Application/adList.html", searchString, ads, paginator, noFound);
	}



  public static void renderMaincategoryList(String category, String language) {
    String catType = "mainCategory";
    renderAds(category, language, catType);
	}


  public static void renderSubcategoryList(String category, String language) {
    String catType = "subCategory";
    renderAds(category, language, catType);
	}

  private static void renderAds(String category, String language, String catType) {
    SearchBuilder sb = new SearchBuilder();
    sb.startExpression().eq(catType, sb.quote(category)).endExpression();
    addLanguageExpression(sb);
    sb.orderBy("id desc");

    List<Ad> ads = searchAds(sb);
    long noFound = getNoFound(ads);

    ValuePaginator paginator = new ValuePaginator(ads);
    paginator.setPageSize(pageSize);
    render("Application/adList.html", ads, paginator, noFound);
  }


  private static List<Ad> searchAds(SearchBuilder sb) {
    String search = sb.getSearchString();
//    Logger.info("search: " + search);
    List<Ad> ads = sb.exec();
    return ads;
  }


  private static long getNoFound(List<Ad> ads) {
    long noFound = ads.size();
    validation.min(noFound, 1).message(Messages.get("nothing_found"));
    if(validation.hasErrors()) {
      params.flash(); // add http parameters to the flash scope
      validation.keep(); // keep the errors for the next request
      index();
    }
    return noFound;
  }


  private static void addLanguageExpression(SearchBuilder sb) {
    sb.and().eq("language", Ad.Language.valueOf(Lang.get()).ordinal());
  }


//  //TODO example how to cache page fragments
//  public static void index(Long id){
//        String key="controllers.index+"_"+id;
//
//        String content=Cache.get(key, String.class);
//
//        if(content == null)
//        {
//                MyData md = MyData.find("byId",id).first();
//                notFoundIfNull(md);
//
//                Map args = new HashMap();
//                args.put("stuff", stuff);
//
//                Template t = TemplateLoader.load(template()); //Template laden
//                content = t.render(args); //Template mit args rendern
//
//                Cache.set(key, content, "30mn");
//        }
//        renderHtml(content);
//
//} 


  public static void adDetail(String url) {
    Ad ad = Ad.find("byUrl", url).first();
    if(null == ad) {
      validation.required(ad).message(Messages.get("validation.notFound", ad, url));
      validation.keep(); // keep the errors for the next request
      Application.index();
    }
		render(ad);
	}

  
  public static void captcha(String id) {
      Images.Captcha captcha = Images.captcha();
      String code = captcha.getText("#E4EAFD");
      Cache.set(id, code, "10mn");
      renderBinary(captcha);
  }


  public static String getOptionString4Category(String mainCategory) {
    String[] subCategories = Category.getSubCategory(mainCategory);
    StringBuilder optionString = new StringBuilder();
    optionString.append("<option value=''>").append(Messages.get("option.none")).append("</option>");

    if(null != mainCategory) {
      for(String cat: subCategories) {
        String name = Messages.get(Messages.get(cat));
        optionString.append("<option value='" + cat + "'>" + name + "</option>");
      }
    }
    return optionString.toString();
  }


  public static void termsOfUse() throws FileNotFoundException {
  	VirtualFile file = VirtualFile.fromRelativePath("/public/documents/termsOfUse.de.html");
    if("es".equals(Lang.get())) {
      file = VirtualFile.fromRelativePath("/public/documents/termsOfUse.es.html");
    }
    showDocument(file);
  }

  public static void dataPolicy() {
    VirtualFile file = VirtualFile.fromRelativePath("/public/documents/dataPolicy.de.html");
    if("es".equals(Lang.get())) {
      file = VirtualFile.fromRelativePath("/public/documents/dataPolicy.es.html");
    }
//    respondStaticHtml(file);
    showDocument(file);
  }

//  private static void respondStaticHtml(File file) throws FileNotFoundException {
//    InputStream is = new FileInputStream(file);
//    response.setHeader("Content-Length", file.length() + "");
//    response.cacheFor("24h");
//    response.contentType = "text/html; charset=utf-8";
//    response.direct = is;
//  }

  private static void showDocument(VirtualFile file) {
//    InputStream is = new FileInputStream(file);
//    response.setHeader("Content-Length", file.length() + "");
    response.cacheFor("24h");
//    response.contentType = "text/html; charset=utf-8";
//    response.direct = is;
    notFoundIfNull(file);
    String content = file.contentAsString();
    render("Application/showDocument.html", content);
  }


  public static void backup() throws ClassNotFoundException, SQLException {
    DbHelper.backup();
		String backupPathName = Play.configuration.getProperty("db.backup.path");
		renderText("backup'ed to " + backupPathName);
  }
}