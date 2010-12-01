package controllers;

import play.Logger;
import play.Play;
import play.cache.Cache;
import play.db.jpa.JPA;
import play.i18n.Lang;
import play.i18n.Messages;
import play.libs.Images;
import play.mvc.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import models.*;
import play.data.validation.*;
import play.modules.paginate.ValuePaginator;
import play.templates.JavaExtensions;

import javax.persistence.Query;


public class Application extends Controller {

  static public int pageSize = Integer.parseInt(Play.configuration.getProperty("pagesize"));

  public static void index() {
    List<MainCategory> mainCategory = MainCategory.find("order by id asc").fetch(100);
    render(mainCategory);
  }


	public static void search(String searchString) {
    validation.required(searchString).message(Messages.get("views.main.noSearchCriteria"));
    validation.minSize(searchString, 3).message(Messages.get("views.main.moreSearchCriteria"));

		if(validation.hasErrors()) {
      params.flash(); // add http parameters to the flash scope
      validation.keep(); // keep the errors for the next request
			index();
		}

    String searchTerm = '%' + searchString.toLowerCase() + '%';
//    List<Ad> ads = Ad.find("language = ? and (lower(title) like ? or lower(content) like ?)",
//            getLanguage(), searchTerm, searchTerm).fetch();
    List<Ad> ads = Ad.find("lower(title) like ? or lower(content) like ? order by id desc",
            searchTerm, searchTerm).fetch();
    long noFound = ads.size();
    ValuePaginator paginator = new ValuePaginator(ads);
    paginator.setPageSize(pageSize);
		render("Application/adList.html", searchString, ads, paginator, noFound);
	}


  public static void advancedSearch(AdSearch object) {

    if(!params._contains("object.text")) {
      List<MainCategory> mainCategories = MainCategory.findAll();
      render(object, mainCategories);
    }

    if(validation.hasErrors()) {
      params.flash(); // add http parameters to the flash scope
      validation.keep(); // keep the errors for the next request
      index();
    }

    SearchBuilder sb = new SearchBuilder();

    sb.eq("1", 1);

    String text = params.get("object.text");
    if(null != text && !"".equals(text)) {
      sb.and().like("title", text).or().like("content", text);
    }

    Enum language = object.language;
    if(null != language && !"".equals(language)) {
      sb.and().eqEnum("language", Ad.Language.values(), language);
    }

    Enum offer = object.offer;
    if(null != offer && !"".equals(offer)) {
      sb.and().eqEnum("offer", Ad.OfferType.values(), offer);
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

    MainCategory mainCategory = object.mainCategory;
    if(null != mainCategory && !"".equals(mainCategory)) {
      sb.and().eq("mainCategory", mainCategory.id);
    }

    SubCategory subCategory = object.subCategory;
    if(null != subCategory && !"".equals(subCategory)) {
      sb.and().eq("subCategory", subCategory.id);
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

    sb.orderBy("id desc");

    String searchString = sb.getSearchString();
    Logger.info("searchString: " + searchString);

    List<Ad> ads = sb.exec();
    long noFound = ads.size();
    ValuePaginator paginator = new ValuePaginator(ads);
    paginator.setPageSize(pageSize);
    render("Application/adList.html", ads, paginator, noFound);
  }


  static class SearchBuilder {
    StringBuilder search = new StringBuilder();

    public SearchBuilder like(String attr, String s) {
      search.append("lower(").append(attr).append(") like ");
      search.append("'%" + (s.toLowerCase()) + "%'");
      return this;
    }

    public SearchBuilder eq(String attr, Object s) {
      if(s instanceof String) {
        s = escape((String) s);
      }
      search.append(attr).append("=").append(s);
      return this;
    }

    public SearchBuilder gte(String attr, Object s) {
      if(s instanceof String || s instanceof Date) {
        s = "'" + escape(((String) s)) + "'";
      }
      search.append(attr).append(">=").append(s);
      return this;
    }


    public SearchBuilder lte(String attr, Object s) {
      if(s instanceof String || s instanceof Date) {
        s = "'" + escape(((String) s)) + "'";
      }
      search.append(attr).append("<=").append(s);
      return this;
    }

    public SearchBuilder eqEnum(String attr, Enum[] values, Enum enumValue) {
      int val = -1;
      for(int i=0; i<values.length; i++) {
        Enum e = values[i];
        if(e.name().equals(enumValue.name())) {
          val = i;
        }
      }
      search.append(attr).append("=").append(val);
      return this;
    }

    public SearchBuilder and() {
      search.append(" and ");
      return this;
    }

    public SearchBuilder or() {
      search.append(" or ");
      return this;
    }

    public SearchBuilder orderBy(String orderBy) {
      search.append(" order by " + orderBy);
      return this;
    }

    public String getSearchString() {
      return search.toString();
    }

    public String escape(String s) {
      //search.append("'%" + escape(s.toLowerCase()) + "%'");
      return JavaExtensions.escape(s).toString();
    }

    public List<Ad> exec() {
      return Ad.find(search.toString()).fetch();
    }
  }

  
  public static void maincategoryList(Long id) {
    MainCategory category = MainCategory.findById(id);
    List<Ad> ads = Ad.find("mainCategory = ? order by id desc", category).fetch();
    ValuePaginator paginator = new ValuePaginator(ads);
    paginator.setPageSize(pageSize);
    long noFound = ads.size();
		render("Application/adList.html", category, ads, paginator, noFound);
	}

  public static void subcategoryList(Long id) {
    SubCategory category = SubCategory.findById(id);
    List<Ad> ads = Ad.find("subCategory = ? order by id desc)", category).fetch();
    ValuePaginator paginator = new ValuePaginator(ads);
    paginator.setPageSize(pageSize);
    render("Application/adList.html", category, ads, paginator);
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
    List<MainCategory> mainCategories = MainCategory.findAll();
    List<SubCategory> subCategories = object.mainCategory.children;
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
    object.mainCategory = (MainCategory) MainCategory.findAll().get(0);
    object.subCategory = object.mainCategory.children.get(0);
    return object;
  }

  
  public static void saveAd(@Valid Ad object,
                            File picture, File picture1, File picture2, File picture3,
                            File picture4, File picture5) throws IOException {

    if(validation.hasErrors()) {
      Logger.debug("validation error: " + validation.errorsMap());
      List<MainCategory> mainCategories = MainCategory.findAll();
      List<SubCategory> subCategories = null != object.mainCategory ? object.mainCategory.children : null;
      render("Application/editAd.html", Ad.class, object, mainCategories);
    }

    Logger.debug("saving ad: " + object.id + ", " + object.title);
    object.save();

    Pictures.savePicture(object, picture);
    Pictures.savePicture(object, picture1);
    Pictures.savePicture(object, picture2);
    Pictures.savePicture(object, picture3);
    Pictures.savePicture(object, picture4);
    Pictures.savePicture(object, picture5);

    Users.dashboard();
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


  public static String getOptionString4Category(Long id) {
    StringBuilder optionString = new StringBuilder();
    optionString.append("<option value=''>").append(Messages.get("option.none")).append("</option>");

    if(null != id) {
      MainCategory mainCategory = MainCategory.findById(id);
      List<SubCategory> subCategories = mainCategory.children;
      for(SubCategory cat: subCategories) {
        String name = Messages.get(cat.getDisplayName());
        optionString.append("<option value='" + cat.id + "'>" + name + "</option>");
      }
    }
    return optionString.toString();
  }
  

  // TODO
  public static String termsOfUse() {
    return "hallo alter<br/>hallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alter<br/>hallo alter<br/>hallo alter<br/>hallo alter<br/>hallo alter<br/>hallo alter<br/>hallo alter<br/>";
  }

  // TODO
  public static String dataPolicy() {
    return "dataPolicy hallo alter<br/>hallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alterhallo alter<br/>hallo alter<br/>hallo alter<br/>hallo alter<br/>hallo alter<br/>hallo alter<br/>hallo alter<br/>hallo alter<br/>hallo alter<br/>";
  }


//  public static void test() {
//    //Query query = JPA.em().createNativeQuery("update MainCategory set adCount = 0");
//    //query.executeUpdate();
////            + "join cat.ads as ad where ad.language = 1 group by cat.id, cat.name");
//    System.out.println("noOfCats: " + JPA.em().createNativeQuery("select count(id) from MainCategory").getSingleResult());
//    System.out.println("noOfAds: " + JPA.em().createNativeQuery("select count(id) from Ad").getSingleResult());
//    Query query = JPA.em().createNativeQuery("select cat.id, cat.name, count(ad.id) from MainCategory as cat "
//        + "join Ad as ad on ad.MAINCATEGORY_ID = cat.id group by cat.id, cat.name");
//    List res = query.getResultList();
//    System.out.println("size:" + res.size());
//    for(int i=0; i<res.size(); i++) {
//      System.out.println(res.get(0)[0]);
//    }
//    for(int i=0; i<res.size(); i++) {
//          Object tab[] = res.get(i);
//
//          System.out.println("id : "+tab[0]);
//          System.out.println("name : "+tab[1]);
//          System.out.println("count : "+tab[2]);
//
//    }
//
//  }


//  public static void getMainCategories() throws Exception {
//    Logger.info("Update category stats");
//    Class.forName(Play.configuration.getProperty("db.driver"));
//    Connection conn = DriverManager.getConnection(Play.configuration.getProperty("db.url"), Play.configuration.getProperty("db.user"), Play.configuration.getProperty("db.pass"));
//    Statement st = conn.createStatement();
//    String queryString = "select cat.id, cat.name, count(ad.id) from MainCategory as cat "
//      + "join Ad as ad on ad.MAINCATEGORY_ID = cat.id group by cat.id, cat.name";
//
//    ResultSet rsCats = st.executeQuery(queryString);
//    System.out.println(" Row: " + rsCats.getFetchSize());
//    List<MainCategory> cats = new ArrayList<MainCategory>();
//    while(rsCats.next()) {
//      System.out.println(" Row:" + rsCats.getObject(1));
//      System.out.println(" Row:" + rsCats.getObject(2));
//      System.out.println(" noOfAds:" + rsCats.getObject(3));
//      MainCategory cat = new MainCategory();
//      cat.id = (Long) rsCats.getObject(1);
//      cat.name = (String) rsCats.getObject(2);
//      cat.adCount = (Long) rsCats.getObject(1);
//      cats.add(cat);
//
//      String queryStringSub = "select cat.id, cat.name, count(ad.id) from SubCategory as cat "
//        + "join Ad as ad on ad.MAINCATEGORY_ID = cat.id group by cat.id, cat.name";
//
//      ResultSet rsCats = st.executeQuery(queryString);
//      System.out.println(" Row: " + rsCats.getFetchSize());
//      List<MainCategory> cats = new ArrayList<MainCategory>();
//    }
//  }
}