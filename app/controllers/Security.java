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

  static void onDisconnected() {
      Application.index();
  }

  static boolean check(String profile) {
      if("admin".equals(profile)) {
          return User.find("byEmail", connected()).<User>first().isAdmin;
      }
      return false;
  }
  
}
