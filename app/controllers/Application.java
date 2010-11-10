package controllers;

import play.Logger;
import play.Play;
import play.cache.Cache;
import play.db.jpa.Blob;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.*;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import models.*;
import play.data.validation.*;

public class Application extends Controller {

  public static void index() {
//        render();
    List<MainCategory> mainCategory = MainCategory.find(
        "order by id asc"
    ).fetch(100);
    System.out.println("mainCategory: " + mainCategory);
    render(mainCategory);
  }


	public static void search(@Required String searchString) {
		//if(myName.equals("")) render("nada");
		if(validation.hasErrors()) {
			flash.error("Oops, please enter your search criteria!");
			index();
		}
    flash.error("Not implemented yet, Papi!");
    index();
		//render(myName);
	}

  public static void maincategoryList(Long id) {
    MainCategory category = MainCategory.findById(id);
    List<Ad> ads = category.ads;
		render("Application/adList.html", category);
	}

  public static void subcategoryList(Long id) {
    SubCategory category = SubCategory.findById(id);
    List<Ad> ads = category.ads;
    System.out.println( "ads: " + ads);
    render("Application/adList.html", category);
	}

//  public static void adDetail(Long id) {
//    Ad ad = Ad.findById(id);
//		render(ad);
//	}


  static int no = 0;
  public static void createAd() {
    // TODO find a better user
    Ad object = new Ad();
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
    List<MainCategory> mainCategories = MainCategory.findAll();
    List<SubCategory> subCategories = object.mainCategory.children;
    no++;
    String randomID = Codec.UUID();
    render("Application/editAd.html", Ad.class, object, mainCategories, subCategories, randomID);
	}


  public static void editAd(Long id) {
    Ad object = Ad.findById(id);
    List<MainCategory> mainCategories = MainCategory.findAll();
    List<SubCategory> subCategories = object.mainCategory.children;
    String randomID = Codec.UUID();
    render("Application/editAd.html", Ad.class, object, mainCategories, subCategories, randomID);
	}

//  @Before
//  public static void addType() throws Exception {
//      CRUD.ObjectType type = CRUD.ObjectType.get(getControllerClass());
//      renderArgs.put("type", type);
//  }

  
  public static void saveAd(@Valid Ad object,
                            @Required(message="Please type the code") String code,
                            String randomID,
                            Picture picture, Picture picture1, Picture picture2, Picture picture3, Picture picture4) {

    System.out.println("params: " + params.allSimple());

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

    savePicture(object, picture);
    savePicture(object, picture1);
    savePicture(object, picture2);
    savePicture(object, picture3);
    savePicture(object, picture4);

    Cache.delete(randomID);
    Application.index();
  }


  private static void savePicture(Ad ad, Picture picture) {
    picture.ad = ad;
    Validation.ValidationResult res = validation.valid(picture);
    if(res.ok) {
      System.out.println("try to save picture ");
      picture.save();
    } else {
      System.out.println("no picture: " + res.error);
    }
  }


//  public static void uploadPicture(Picture picture, Long ad_id) {
//    System.out.println("try to upload picture: " + picture.getClass());
//    System.out.println("try to upload picture: " + picture);
//    System.out.println("ad_id: " + ad_id);
//    assert(null != ad_id);
//    //renderBinary(photo.image.get());
//    editAd(ad_id);
//  }

  public static void renderPicture(long id) {
    Ad ad = Ad.findById(id);
    renderBinary(ad.pictures.get(0).image.get());
  }


  public static void captcha(String id) {
      Images.Captcha captcha = Images.captcha();
      String code = captcha.getText("#E4EAFD");
      Cache.set(id, code, "10mn");
      renderBinary(captcha);
  }

}