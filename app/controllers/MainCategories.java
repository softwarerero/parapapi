package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.*;
import play.i18n.Messages;
import play.mvc.*;
import play.templates.JavaExtensions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 06.11.2010
 * Time: 00:27:41
 * Copyright.
 */
@CRUD.For(MainCategory.class)
@Check("admin")
@With(Secure.class)
public class MainCategories extends CRUD {

  public static void getSubCategories(Long id) {
    renderText(getOptionString4Category(id));
	}


  public static String getOptionString4Category(Long id) {
    StringBuilder optionString = new StringBuilder();
    optionString.append("<option value=''>").append(Messages.get("option.none")).append("</option>");

    if(null != id) {
      MainCategory mainCategory = MainCategory.findById(id);
      List<SubCategory> subCategories = mainCategory.children;
      for(SubCategory cat: subCategories) {
        String name = JavaExtensions.noAccents(cat.name).replaceAll(" / ", "_").replaceAll(" ", "_");
        name = Messages.get(name);
        optionString.append("<option value='" + cat.id + "'>" + name + "</option>");
      }
    }
    System.out.println("optionString: " + optionString);
    return optionString.toString();
  }

}
