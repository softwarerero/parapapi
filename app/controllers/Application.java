package controllers;

import play.Logger;
import play.Play;
import play.cache.Cache;
import play.i18n.Messages;
import play.libs.Images;
import play.mvc.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

import models.*;
import play.data.validation.*;
import play.modules.paginate.ValuePaginator;


public class Application extends Controller {

  static public int pageSize = Integer.parseInt(Play.configuration.getProperty("pagesize"));

//  @Before
//  static void setConnectedUser() {
//    if(Security.isConnected()) {
//        User user = User.find("byEmail", Security.connected()).first();
//        renderArgs.put("user", user.nickname);
//    }
//  }


  public static void index() {
    List<MainCategory> mainCategory = MainCategory.find("order by id asc").fetch(100);
    render(mainCategory);
  }


	public static void search(String searchString) {
    validation.required(searchString).message("Oops, please enter your search criteria!");
    validation.minSize(searchString, 3).message("Please enter more criteria!");

		if(validation.hasErrors()) {
      params.flash(); // add http parameters to the flash scope
      validation.keep(); // keep the errors for the next request
			index();
		}

    String searchTerm = '%' + searchString.toLowerCase() + '%';
    List<Ad> ads = Ad.find("lower(title) like ? or lower(content) like ?",
            searchTerm, searchTerm).fetch();
    System.out.println("ads: " + ads.size());
    long noFound = ads.size();
    ValuePaginator paginator = new ValuePaginator(ads);
    paginator.setPageSize(pageSize);
		render("Application/adList.html", searchString, ads, paginator, noFound);
	}


  public static void advancedSearch(AdSearch adSearch) {
    System.out.println("adSearch: " + adSearch.getClass());
//    validation.valid(adSearch).message("Not implemented yet, Papi!");
//    if(validation.hasErrors()) {
//      validation.clear();
//      AdSearch object = adSearch;
//      render(object);
//    }
    //validation.required(ad).message("Not implemented yet, Papi!");
//    validation.keep(); // keep the errors for the next request
    AdSearch object = adSearch;
    render(object);
  }

  
  public static void maincategoryList(Long id) {
    MainCategory category = MainCategory.findById(id);
    List<Ad> ads = category.ads;
    ValuePaginator paginator = new ValuePaginator(ads);
    paginator.setPageSize(pageSize);
		render("Application/adList.html", category, ads, paginator);
	}

  public static void subcategoryList(Long id) {
    SubCategory category = SubCategory.findById(id);
    List<Ad> ads = category.ads;
    ValuePaginator paginator = new ValuePaginator(ads);
    paginator.setPageSize(pageSize);
    render("Application/adList.html", category, ads, paginator);
	}

  public static void adDetail(Long id) {
    Ad ad = Ad.findById(id);
		render(ad);
	}


  public static void createAd() {
    Ad object = new Ad();
    populateTestAd(object);
    render4editAd(object);
//    List<MainCategory> mainCategories = MainCategory.findAll();
//    List<SubCategory> subCategories = object.mainCategory.children;
//    no++;
//    String randomID = Codec.UUID();
//    render("Application/editAd.html", Ad.class, object, mainCategories, subCategories, randomID);
	}


  public static void editAd(Long id) {
    Ad object = Ad.findById(id);
    render4editAd(object);
	}

  
  private static void render4editAd(Ad object) {
//    List<MainCategory> mainCategories = MainCategory.findAll();
    List<SubCategory> subCategories = object.mainCategory.children;
//    String randomID = Codec.UUID();
    render("Application/editAd.html", Ad.class, object, subCategories);
	}


  private static Ad populateTestAd(Ad object) {
    User user = User.find("byEmail", Security.connected()).first();
    long no = Ad.count() + 1;
    object.author = user;
    object.price = new BigDecimal("99.77");
    object.postedAt = new Date();
    object.title = "Title " + no;
    object.content = "Content " + no;
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

//  @Before
//  public static void addType() throws Exception {
//      CRUD.ObjectType type = CRUD.ObjectType.get(getControllerClass());
//      renderArgs.put("type", type);
//  }

  
  public static void saveAd(@Valid Ad object,
                            File picture, File picture1, File picture2, File picture3,
                            File picture4, File picture5) throws IOException {

    System.out.println(params.allSimple());

    if(validation.hasErrors()) {
      Logger.debug("validation error: " + validation.errorsMap());
      List<MainCategory> mainCategories = MainCategory.findAll();
      List<SubCategory> subCategories = object.mainCategory.children;
      render("Application/editAd.html", Ad.class, object, mainCategories, subCategories);
    }

    Logger.debug("about to save: " + object.title);
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
//  public static void deleteAd(Ad object) {
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

}