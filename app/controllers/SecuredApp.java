package controllers;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import controllers.auth.Security;

import models.Ad;
import models.AdSearch;
import models.Category;
import models.User;
import play.Logger;
import play.Play;
import play.data.validation.Valid;
import play.db.jpa.JPA;
import play.i18n.Lang;
import play.i18n.Messages;
import play.modules.paginate.ValuePaginator;
import play.mvc.Controller;
import play.mvc.With;
import py.suncom.parapapi.db.SearchBuilder;


@With(Secure.class)
public class SecuredApp extends Controller {

  static public int pageSize = Integer.parseInt(Play.configuration.getProperty("pagesize"));

  
  public static void advancedSearch(AdSearch object) {
    validation.clear();
    if(!params._contains("object.text")) {
      String[] mainCategories = Category.main;
      object.language = Ad.Language.valueOf(Lang.get());
      render(object, mainCategories);
    }

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
//    Logger.info("searchString: " + searchString);

    List<Ad> ads = sb.exec();
    long noFound = ads.size();
    validation.min(noFound, 1).message(Messages.get("nothing_found"));
    if(validation.hasErrors()) {
      params.flash(); // add http parameters to the flash scope
      validation.keep(); // keep the errors for the next request
//      render("Application/advancedSearch.html", object);
      render(object);
    }
    ValuePaginator paginator = new ValuePaginator(ads);
    paginator.setPageSize(pageSize);
    render("Application/adList.html", ads, paginator, noFound);
  }

  public static void createAd() {
    Ad object = new Ad();
    populateAd(object);
    render4editAd(object);
	}

  
  private static void render4editAd(Ad object) {
    String[] mainCategories = Category.main;
    String[] subCategories = null != object.mainCategory ?
            Category.getSubCategory(object.mainCategory) : null;
    render("Application/editAd.html", Ad.class, object, mainCategories, subCategories);
	}




  private static Ad populateAd(Ad object) {
    User user = User.find("byEmail", Security.connected()).first();
    object.language = Ad.Language.valueOf(Lang.get());
    object.author = user;
    object.postedAt = new Date();
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
                            File picture4, File picture5) throws Exception {

    checkAdValidation(object);
    object.save();

    try {
      Pictures.savePicture(object, picture);
      Pictures.savePicture(object, picture1);
      Pictures.savePicture(object, picture2);
      Pictures.savePicture(object, picture3);
      Pictures.savePicture(object, picture4);
      Pictures.savePicture(object, picture5);
    } catch(Exception e) {
      Logger.warn("can't save picture: " + e);
      JPA.setRollbackOnly();
    }

    object.computeUrl();
    object.save();
//    Logger.info("saving ad: " + object.id + ", " + object.url);

    checkAdValidation(object);
    object.save();

    Users.dashboard();
  }

  
  private static void checkAdValidation(Ad object) {
    if(validation.hasErrors()) {
      Logger.debug("validation error: " + validation.errorsMap());
      String[] mainCategories = Category.main;
      String[] subCategories = null != object.mainCategory ? Category.getSubCategory(object.mainCategory) : null;
      render("Application/editAd.html", Ad.class, object, mainCategories, subCategories);
    }
  }


  public static void deleteAd(Long id) {
    Ad object = Ad.findById(id);
    Logger.debug("delete: " + object);
    object.delete();
    Users.dashboard();
  }


  public static void editAd(Long id) {
    Ad object = Ad.findById(id);
    render4editAd(object);
//    https://www.googleapis.com/language/translate/v2?key=AIzaSyAO6JxIxKsHa8wBqXt924fPDNaAw8q-m28&q=flowers&source=en&target=fr&callback=handleResponse&prettyprint=true
	}

}
