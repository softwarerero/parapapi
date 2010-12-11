package controllers;

import jobs.UpdateCategoryCount;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.i18n.Lang;
import play.i18n.Messages;
import play.libs.Images;
import play.mvc.*;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

import models.*;
import play.data.validation.*;
import play.modules.paginate.ValuePaginator;
import play.templates.Template;
import play.templates.TemplateLoader;
import py.suncom.parapapi.db.SearchBuilder;


public class Application extends Controller {

  static public int pageSize = Integer.parseInt(Play.configuration.getProperty("pagesize"));


  public static void index() {
    String[] mainCategories = Category.main;
    String language = Lang.get();
    Map categoryCountMap = UpdateCategoryCount.getCategoryCountMap("CategoryCountMap");
    render("Application/index.html", mainCategories, categoryCountMap, language);
  }


//  public static void renderCategories(boolean german, boolean english, boolean spanish) {
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


//  public static void search(String searchString, boolean german, boolean english, boolean spanish) {
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
//    addLanguageExpression(german, english, spanish, sb);
    addLanguageExpression(sb);
    sb.orderBy("id");

    List<Ad> ads = searchAds(sb);
    long noFound = getNoFound(ads);
    ValuePaginator paginator = new ValuePaginator(ads);
    paginator.setPageSize(pageSize);
		render("Application/adList.html", searchString, ads, paginator, noFound);
	}


  public static void advancedSearch(AdSearch object) {
    validation.clear();
    System.out.println("object: " + object);
    if(!params._contains("object.text")) {
      String[] mainCategories = Category.main;
      object.language = Ad.Language.valueOf(Lang.get());
      render(object, mainCategories);
    }

//    if(validation.hasErrors()) {
//      params.flash(); // add http parameters to the flash scope
//      validation.keep(); // keep the errors for the next request
//      index();
//    }

    SearchBuilder sb = new SearchBuilder();

    sb.eq("1", 1);

    String text = params.get("object.text");
    if(null != text && !"".equals(text)) {
      sb.and().startExpression().like("title", text).or().like("content", text).endExpression();
    }

    Enum language = object.language;
    if(null != language && !"".equals(language)) {
      sb.and().eqEnum("language", Ad.Language.values(), language);
    }

    Enum offer = object.offer;
    if(null != offer && !"".equals(offer)) {
      sb.and().eqEnum("offer", Ad.OfferType.values(), offer);
    }

    Enum priceType = object.priceType;
    if(null != priceType && !"".equals(priceType)) {
      sb.and().eqEnum("priceType", Ad.PriceType.values(), priceType);
    }

    Enum handOver = object.handOver;
    if(null != handOver && !"".equals(handOver)) {
      sb.and().eqEnum("handOver", Ad.HandOver.values(), handOver);
    }

    Enum currency = object.currency;
    if(null != currency && !"".equals(currency)) {
      sb.and().eqEnum("currency", Ad.Currency.values(), currency);
    }

    Date postedAfter = object.postedAt;
    String postedAt = params.get("object.postedAt");
    if(null != postedAt && !"".equals(postedAt)) {
      sb.and().gte("postedAt", postedAt);
    }

    BigDecimal priceFrom = object.priceFrom;
    if(null != priceFrom && !"".equals(priceFrom)) {
      sb.and().gte("price", priceFrom);
    }

    BigDecimal priceTo = object.priceTo;
    if(null != priceTo && !"".equals(priceTo)) {
      sb.and().lte("price", priceTo);
    }

    String mainCategory = object.mainCategory;
    if(null != mainCategory && !"".equals(mainCategory)) {
      sb.and().eq("mainCategory",  "'" + mainCategory + "'");
    }

    String subCategory = object.subCategory;
    if(null != subCategory && !"".equals(subCategory)) {
      sb.and().eq("subCategory",  "'" + subCategory + "'");
    }

    Enum department = object.department;
    if(null != department && !"".equals(department)) {
      sb.and().eqEnum("department", Ad.Department.values(), department);
    }

    String city = params.get("object.city");
    if(null != city && !"".equals(city)) {
      sb.and().like("city", city);
    }

    String zone = params.get("object.zone");
    if(null != zone && !"".equals(zone)) {
      sb.and().like("zone", zone);
    }

    String orderBy = params.get("object.orderBy");
    if(null == orderBy || "".equals(orderBy)) {
      orderBy = "id";
    }

    String ascOrDesc = params.get("object.ascOrDesc");
    if(null == ascOrDesc || "".equals(ascOrDesc)) {
      ascOrDesc = "desc";
    }
    orderBy += " " + ascOrDesc;
    sb.orderBy(orderBy);

    String searchString = sb.getSearchString();
    Logger.info("searchString: " + searchString);

    List<Ad> ads = sb.exec();
    long noFound = ads.size();
    validation.min(noFound, 1).message(Messages.get("nothing_found"));
    if(validation.hasErrors()) {
      params.flash(); // add http parameters to the flash scope
      validation.keep(); // keep the errors for the next request
      render("Application/advancedSearch.html", object);
    }
    ValuePaginator paginator = new ValuePaginator(ads);
    paginator.setPageSize(pageSize);
    render("Application/adList.html", ads, paginator, noFound);
  }


//  public static void renderMaincategoryList(String category, boolean german, boolean english, boolean spanish) {
  public static void renderMaincategoryList(String category, String language) {
    String catType = "mainCategory";
//    renderAds(category, german, english, spanish, catType);
    renderAds(category, language, catType);
	}


//  public static void renderSubcategoryList(String category, boolean german, boolean english, boolean spanish) {
  public static void renderSubcategoryList(String category, String language) {
    String catType = "subCategory";
    renderAds(category, language, catType);
	}

//  private static void renderAds(String category, boolean german, boolean english, boolean spanish, String catType) {
  private static void renderAds(String category, String language, String catType) {
//    Logger.info("category: " + category + " german: " + german + " english: " + english + " spanish: " + spanish);
    SearchBuilder sb = new SearchBuilder();
    sb.startExpression().eq(catType, sb.quote(category)).endExpression();
//    addLanguageExpression(german, english, spanish, sb);
    addLanguageExpression(sb);
    sb.orderBy("id");

    List<Ad> ads = searchAds(sb);
    long noFound = getNoFound(ads);

    ValuePaginator paginator = new ValuePaginator(ads);
    paginator.setPageSize(pageSize);
    render("Application/adList.html", ads, paginator, noFound);
  }


  private static List<Ad> searchAds(SearchBuilder sb) {
    String search = sb.getSearchString();
    Logger.info("search: " + search);
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

//  private static void addLanguageExpression(boolean german, boolean english, boolean spanish, SearchBuilder sb) {
//    if(german || english || spanish) {
//      sb.and().startExpression();
//      if(german) {
//        sb.eq("language", Ad.Language.valueOf("de").ordinal());
//      }
//      if(english) {
//        if(german) sb.or();
//        sb.eq("language", Ad.Language.valueOf("en").ordinal());
//      }
//      if(spanish) {
//        if(german || english) sb.or();
//        sb.eq("language", Ad.Language.valueOf("es").ordinal());
//      }
//      sb.endExpression();
//    }
//  }

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

  public static void adDetail(Long id) {
    Ad ad = Ad.findById(id);
		render(ad);
	}


  public static void createAd() {
    Ad object = new Ad();
    populateTestAd(object);
    render4editAd(object);
	}


  public static void editAd(Long id) {
    Ad object = Ad.findById(id);
    render4editAd(object);
	}

  
  private static void render4editAd(Ad object) {
    String[] mainCategories = Category.main;
    String[] subCategories = null != object.mainCategory ?
            Category.getSubCategory(object.mainCategory) : null;
    render("Application/editAd.html", Ad.class, object, mainCategories, subCategories);
	}


  private static Ad populateTestAd(Ad object) {
    User user = User.find("byEmail", Security.connected()).first();
//    long no = Ad.count() + 1;
    object.author = user;
//    object.price = new BigDecimal("99.77");
    object.postedAt = new Date();
//    object.title = "Title " + no;
//    object.content = "Content " + no;
    object.email = user.email;
    object.offer = Ad.OfferType.offer;
    object.handOver = Ad.HandOver.sell;
    object.priceType = Ad.PriceType.fixedPrice;
    object.phone = user.phone;
    object.mobilePhone = user.mobilePhone;
    object.zone = user.zone;
    object.department = user.department;
    object.city = user.city;
//    object.mainCategory = (MainCategory) MainCategory.findAll().get(0);
//    object.subCategory = object.mainCategory.children.get(0);
    return object;
  }

  
  public static void saveAd(@Valid Ad object,
                            File picture, File picture1, File picture2, File picture3,
                            File picture4, File picture5) throws IOException {

    checkAdValidation(object);

    Logger.debug("saving ad: " + object.id + ", " + object.title);
    object.save();

    Pictures.savePicture(object, picture);
    Pictures.savePicture(object, picture1);
    Pictures.savePicture(object, picture2);
    Pictures.savePicture(object, picture3);
    Pictures.savePicture(object, picture4);
    Pictures.savePicture(object, picture5);

    checkAdValidation(object);

    Users.dashboard();
  }

  private static void checkAdValidation(Ad object) {
    if(validation.hasErrors()) {
      Logger.debug("validation error: " + validation.errorsMap());
      String[] mainCategories = Category.main;
      String[] subCategories = null != object.mainCategory ? Category.getSubCategory(object.mainCategory) : null;
      render("Application/editAd.html", Ad.class, object, mainCategories);
    }
  }


  public static void deleteAd(Long id) {
    Ad object = Ad.findById(id);
    Logger.debug("delete: " + object);
    object.delete();
    Users.dashboard();
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
    File file = Play.getFile("public/documents/termsOfUse.de.html");
    if("es".equals(Lang.get())) {
      file = Play.getFile("public/documents/termsOfUse.es.html");
    }
    respondStaticHtml(file);
  }

  public static void dataPolicy() throws FileNotFoundException {
    File file = Play.getFile("public/documents/dataPolicy.de.html");
    if("es".equals(Lang.get())) {
      file = Play.getFile("public/documents/dataPolicy.es.html");
    }
    respondStaticHtml(file);
  }

  private static void respondStaticHtml(File file) throws FileNotFoundException {
    InputStream is = new FileInputStream(file);
    response.setHeader("Content-Length", file.length() + "");
    response.cacheFor("24h");
    response.contentType = "text/html; charset=utf-8";
    response.direct = is;
  }


  public static void backup() {
    
  }

}