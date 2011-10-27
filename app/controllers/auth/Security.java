package controllers.auth;

import models.User;
import controllers.Secure;

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

//  static void onAuthenticated() {
//    User user = User.find("byEmail", connected()).<User>first();
//    session.put("nickname", user.nickname);
//  }

//  static void onDisconnected() {
//    session.remove("username");
//    Application.index();
//  }

//  static boolean isConnected() {
//  	System.out.println(session.get("username"));
//  	Logger.info(session.get("username"));
//    return session.contains("username");
//  }

  static boolean check(String profile) {
    if("admin".equals(profile)) {
      User user = User.find("byEmail", connected()).<User>first();
      if(null != user) {
        return user.isAdmin;
      }
    }
    return false;
  }

  static boolean isAdmin() {
    User user = User.find("byEmail", connected()).<User>first();
    if(null != user) {
      return user.isAdmin;
    }
    return false;
  }
  
  
  static public String connected() {
  	return session.get("username");
  }

//  static String nickname() {
//      return session.get("nickname");
//  }

}
