package controllers;

import models.Ad;
import models.Category;
import play.mvc.With;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 06.11.2010
 * Time: 00:45:02
 * Copyright.
 */
@Check("admin")
@With(Secure.class)
public class Users extends CRUD {

  public static void dashboard() {
    List<Ad> ads = Ad.find("author.email", Security.connected()).fetch();
    render(ads);
  }
}
