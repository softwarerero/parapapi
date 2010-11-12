package controllers;

import play.Logger;
import play.Play;
import play.cache.Cache;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

import models.*;
import play.data.validation.*;
import play.modules.paginate.ValuePaginator;


public class Application extends Controller {

  static public int pageSize = Integer.parseInt(Play.configuration.getProperty("pagesize"));

  @Before
  static void setConnectedUser() {
    if(Security.isConnected()) {
        User user = User.find("byEmail", Security.connected()).first();
        renderArgs.put("user", user.nickname);
    }
  }


  public static void index() {
    List<MainCategory> mainCategory = MainCategory.find("order by id asc").fetch(100);
    render(mainCategory);
  }


	public static void search(@Required String searchString) {
		if(validation.hasErrors()) {
			flash.error("Oops, please enter your search criteria!");
			index();
		}
    flash.error("Not implemented yet, Papi!");
    index();
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
    List<MainCategory> mainCategories = MainCategory.findAll();
    List<SubCategory> subCategories = object.mainCategory.children;
    String randomID = Codec.UUID();
    render("Application/editAd.html", Ad.class, object, mainCategories, subCategories, randomID);
	}


  static int no = 0;
  private static Ad populateTestAd(Ad object) {
    // TODO find a better user
    object.author = (User) User.findAll().get(0);
    object.price = new BigDecimal("99.77");
    object.postedAt = new Date();
    object.title = "Title " + no;
    object.content = "Content " + no;
    object.email = "test@test.de";
    object.offer = Ad.OfferType.offer;
    object.handOver = Ad.HandOver.sell;
    object.priceType = Ad.PriceType.fixedPrice;
    object.phone = "123" + no;
    object.mainCategory = (MainCategory) MainCategory.findAll().get(0);
    object.subCategory = object.mainCategory.children.get(0);
    System.out.println("object.subCategory: " + object.subCategory);
    return object;
  }

//  @Before
//  public static void addType() throws Exception {
//      CRUD.ObjectType type = CRUD.ObjectType.get(getControllerClass());
//      renderArgs.put("type", type);
//  }

  
  public static void saveAd(@Valid Ad object,
                            @Required(message="Please type the code") String code,
                            String randomID,
                            Picture picture, Picture picture1, Picture picture2, Picture picture3, Picture picture4) throws IOException {

    System.out.println("params: " + params.allSimple());
    System.out.println("id: " + object.id);

    if(!Play.id.equals("test")) {
      validation.equals(code, Cache.get(randomID)).message("Invalid code. Please type it again");
    }

    if(validation.hasErrors()) {
      Logger.debug("validation error: " + validation.errorsMap());
      List<MainCategory> mainCategories = MainCategory.findAll();
      List<SubCategory> subCategories = object.mainCategory.children;
      render("Application/editAd.html", Ad.class, object, mainCategories, subCategories, randomID);
    }

    Logger.debug("about to save: " + object.title);
    object.save();

    Pictures.savePicture(object, picture);
    Pictures.savePicture(object, picture1);
    Pictures.savePicture(object, picture2);
    Pictures.savePicture(object, picture3);
    Pictures.savePicture(object, picture4);

    Cache.delete(randomID);
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