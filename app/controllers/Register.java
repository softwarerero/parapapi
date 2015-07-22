package controllers;

import models.User;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import controllers.auth.Security;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.i18n.Lang;
import play.libs.Codec;
import play.libs.Mail;

import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 11.11.2010
 * Time: 16:50:32
 * Copyright.
 */
public class Register extends CRUD {
  private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";

  public static void register() {
    newUser();
  }

  public static void newUser() {
    User object = new User();
    String randomID = Codec.UUID();
    render("Register/editUser.html", object, randomID);
  }

  public static void editUser() {
    User object = User.find("byEmail", Security.connected()).first();
    object.passwordConfirmation = object.password;
    String randomID = Codec.UUID();
//    renderTemplate("tags/editUserForm.html", object, randomID);
    render(object, randomID);
  }


  public static void saveUser(@Valid User object,
                            String code,
                            String randomID) throws EmailException {

    if(!Play.id.equals("test") && !Play.id.equals("loadtest")) {
      validation.required(code).message("views.editUser.code");
      validation.equals(code, Cache.get(randomID)).message("views.editUser.code2");
    }
    if(Play.id.equals("loadtest")) {
      object.isActive = true;
    }

    long count = 0;
    String otherError = "";
    if(null == object.id) {
      count = User.count("email = ? or nickname = ?", object.email, object.nickname);
      if(count > 0) {
        otherError = "user.exists";
      }
    }

//    if((null == object.phone || object.phone.equals("")) && (null == object.mobilePhone || object.mobilePhone.equals(""))) {
//      otherError = "user.phoneFails";
//    }

    if(validation.hasErrors() || !otherError.equals("")) {
      Logger.debug("validation error: " + validation.errorsMap());
      render("Register/editUser.html", User.class, object, randomID, otherError);
    }

    boolean isRegistration = null == object.id;
    object.save();
    Cache.delete(randomID);
    if(isRegistration) {
      registrationConfirmation(object.email);
    } else {
      Users.dashboard();
    }
  }


  // TODO parametrize hard coded values
  private static void registrationConfirmation(String userEmail) throws EmailException {
    User user = User.find("byEmail", userEmail).<User>first();
    SimpleEmail email = new SimpleEmail();
    email.setFrom("ads@sun.com.py");
    email.addTo(user.email);
    email.addTo("ads@sun.com.py");
    email.setCharset("UTF-8");
//    String path = request.path;
    email.setSubject("Activar su cuenta con SUNCOM Ads");
    String msg = "Siguese este link para empezar: ";
    if(Lang.get().equals("de")) {
      email.setSubject("Bitte aktivieren Sie Ihr Benutzerkonto bei SUNCOM Ads");
      msg = "Bitte folgen Sie diesem Link um Ihr Benutzerkonto zu aktieren: ";
    } else if(Lang.get().equals("en")) {
      email.setSubject("Please activate your account with SUNCOM Ads");
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


  public static void confirmRegistration(String email, String confirmationToken) throws Throwable {
    User user = User.find("byEmail", email).<User>first();
    if(null == user) {
      Logger.fatal("user confirmation failed: " + email);
      Application.categories();
    }
    if(user.confirmationToken.equals(confirmationToken)) {
      Logger.info("activate user: " + user.id);
      user.isActive = true;
      user.save();
    } else {
      Logger.fatal("user confirmation failed (wrong token): " + email);
      Application.categories();
    }
    Secure.login();
  }
}
