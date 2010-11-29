package controllers;

import models.*;
import org.apache.commons.mail.EmailException;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.libs.Codec;
import play.mvc.Before;
import play.mvc.With;

import java.io.IOException;
import java.util.List;
import play.modules.paginate.ValuePaginator;


/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 06.11.2010
 * Time: 00:45:02
 * Copyright.
 */
//@Check("admin")
@With(Secure.class)
public class Users extends CRUD {

  static public int pageSize = Integer.parseInt(Play.configuration.getProperty("pagesize"));


  public static void dashboard() {
    List<Ad> ads = Ad.find("author.email = ? order by id desc", Security.connected()).fetch();
    ValuePaginator paginator = new ValuePaginator(ads);
    paginator.setPageSize(pageSize);
    render(ads, paginator);
  }


//  public static void editUser(User object, String randomID) {
////    User object = User.find("byEmail", Security.connected()).first();
////    String randomID = Codec.UUID();
//    System.out.println("user: " + object);
//    System.out.println("userandomIDr: " + randomID);
//    renderTemplate("tags/editUserForm.html", object, randomID);
//  }
//
//
//  public static void saveUser(@Valid User object,
//                            @Required(message="Please type the code") String code,
//                            String randomID) throws EmailException {
//
//    if(!Play.id.equals("test")) {
//      validation.equals(code, Cache.get(randomID)).message("Invalid code. Please type it again");
//    }
//
//    if(validation.hasErrors()) {
//      Logger.debug("validation error: " + validation.errorsMap());
////      render("Users/editUserForm.html", User.class, object, randomID, userExistsError);
////      renderTemplate("Users/dashboard.html", object, randomID);
//      params.flash(); // add http parameters to the flash scope
//      validation.keep(); // keep the errors for the next request
//    }
//
//    Logger.debug("about to save: " + object.nickname);
//    object.save();
//    Cache.delete(randomID);
//  }
}
