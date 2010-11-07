package controllers;

import play.Play;
import play.cache.Cache;
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
      List<Category> categories = Category.find(
          "parent is null order by id desc"
      ).fetch(100);
      render(categories);

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

  public static void adList(Long id) {
    Category category = Category.findById(id);
		render(category);
	}


  public static void adDetail(Long id) {
    Ad ad = Ad.findById(id);
		render(ad);
	}


  static int no = 0;
  public static void createAd() {
    // TODO find a better user
    User user = User.findById(0L);
    Ad object = new Ad();
    object.author = user;
    object.prize = new BigDecimal("99.77");
    object.postedAt = new Date();
    object.title = "Title " + no;
    object.content = "Content " + no;
    object.phone = "123" + no;
    //ad.category = Category.findById(2L);
    List<Category> categories = Category.findAll();
//    Category cat = categories.get(0);
//    ad.category = cat;
//    System.out.println(ad.category);
    no++;
    String randomID = Codec.UUID();
    //renderTemplate("Application/editAd.html", ad, categories, randomID);
    render("Application/editAd.html", Ad.class, object, categories, randomID);

	}


  @Before
  public static void addType() throws Exception {

      CRUD.ObjectType type = CRUD.ObjectType.get(getControllerClass());
      renderArgs.put("type", type);
  }

  
  public static void saveAd(@Valid Ad object,
                            @Required(message="Please type the code") String code,
                            String randomID) {
   // Binder.bind(ad, "ad", params.all());
//    System.out.println(ad.validateAndSave());
    System.out.println("params: " + params.allSimple());
    System.out.println("ad.prize: " + params.get("object.prize"));
    System.out.println("ad.prize: " + object.prize);
    System.out.println("ad.category: " + params.get("object.category"));
    System.out.println("ad.category: " + object.category);
    System.out.println("ad.offer: " + params.get("object.offer"));
    System.out.println("ad.offer: " + object.offer);

    if(!Play.id.equals("test")) {
      validation.equals(code, Cache.get(randomID)).message("Invalid code. Please type it again");
    }

    if(validation.hasErrors()) {
      System.out.println("we have an error: " + validation.errorsMap());
      //List<Category> categories = Category.findAll();
//      render("Application/editAd.html", ad);
      List<Category> categories = Category.findAll();
      Object currentType = null;
      try {
        currentType = CRUD.ObjectType.get(getControllerClass());
      } catch(Exception e) {
        e.printStackTrace();
      }

      render("Application/editAd.html", Ad.class, object, categories, randomID, currentType);
    }


    System.out.println("try to save: " + object.title);
    object.save();
    Cache.delete(randomID);
    Application.index();
  }


  public static void uploadPhoto(String title, File photo) {
      
  }


  public static void captcha(String id) {
      Images.Captcha captcha = Images.captcha();
      String code = captcha.getText("#E4EAFD");
      Cache.set(id, code, "10mn");
      renderBinary(captcha);
  }

}