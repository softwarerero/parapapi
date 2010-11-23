package controllers;

import models.User;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.i18n.Lang;
import play.libs.Codec;
import play.libs.Mail;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 11.11.2010
 * Time: 16:50:32
 * Copyright.
 */
public class Register extends CRUD {
  
  public static void register() {
    editUser();
  }


  public static void editUser() {
    User object = new User();
    String randomID = Codec.UUID();
    renderTemplate("Users/editUser.html", object, randomID);
  }


  public static void saveUser(@Valid User object,
                            @Required(message="Please type the code") String code,
                            String randomID) throws EmailException {

    if(!Play.id.equals("test")) {
      validation.equals(code, Cache.get(randomID)).message("Invalid code. Please type it again");
    }

    long count = User.count("email = ? or nickname = ?", object.email, object.nickname);
    System.out.println("count: " + count);
    String userExistsError = "";
    if(count > 0) {
      userExistsError = "user.exists";
    }

    if(validation.hasErrors() || count > 0) {
      System.out.println("validation error: " + validation.errorsMap());
      Logger.debug("validation error: " + validation.errorsMap());
      render("Users/editUser.html", User.class, object, randomID, userExistsError);
    }

    Logger.debug("about to save: " + object.nickname);
    object.save();
    Cache.delete(randomID);
    registrationConfirmation(object);
  }


  // TODO parametrize hard coded values
  public static void registrationConfirmation(User object) throws EmailException {
    User user = User.find("byEmail", object.email).<User>first();
    SimpleEmail email = new SimpleEmail();
    email.setFrom("noreply@suncom.com.py");
    email.addTo(user.email);
    email.addTo("register@suncom.com.py");
    String path = request.path;
    email.setSubject("Activar su cuenta con Para Papi");
    String msg = "Siguese este link para empiezar: ";
    if(Lang.get().equals("de")) {
      email.setSubject("Bitte aktivieren Sie Ihr Benutzerkonto bei Para Papi");
      msg = "Bitte folgen Sie diesem Link um Ihr Benutzerkonto zu aktieren: ";
    } else if(Lang.get().equals("en")) {
      email.setSubject("Please activate your account with Para Papi");
      msg = "Please follow this Link to activate your account: ";
    }
    String confirmationToken = UUID.randomUUID().toString();
    user.confirmationToken = confirmationToken;
    user.save();
    msg += "\n\nhttp://" + request.host
            + "/confirmRegistration/" + user.email + "/" + confirmationToken;
    email.setMsg(msg);
    Mail.send(email);

    render("Users/registrationConfirmation.html");
  }


  static public String getLang() {
    return request.headers.get("accept-language").value().substring(0,2);
  }


  public static void confirmRegistration(String email, String confirmationToken) throws Throwable {
    System.out.println("email: " + email);
    System.out.println("confirmationToken: " + confirmationToken);
    User user = User.find("byEmail", email).<User>first();
    //render("Users/confirmRegistration.html");
    if(null == user) {
      Logger.fatal("user confirmation failed: " + email);
      Application.index();
    }
    if(user.confirmationToken.equals(confirmationToken)) {
      Logger.info("activate user: " + user.id);
      user.isActive = true;
      user.save();
    } else {
      Logger.fatal("user confirmation failed (wrong token): " + email);
      Application.index();
    }
    Secure.login();
  }
}
