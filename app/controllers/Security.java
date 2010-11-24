package controllers;

import models.User;

/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 06.11.2010
 * Time: 15:43:44
 * Copyright.
 */
public class Security extends Secure.Security {

  static boolean authenticate(String username, String password) {
    return User.connect(username, password) != null;
  }

  static void onAuthenticated() {
    session.put("nickname", User.find("byEmail", connected()).<User>first().nickname);
  }

  static void onDisconnected() {
    session.remove("nickname");
    Application.index();
  }

  static boolean check(String profile) {
    if("admin".equals(profile)) {
      User user = User.find("byEmail", connected()).<User>first();
      if(null != user) {
        return user.isAdmin;
      }
    }
    return false;
  }
  
}
