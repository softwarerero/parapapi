package controllers;

import models.*;
import play.mvc.*;

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
    optionString.append("<option value=''>(None)</option>");

    if(null != id) {
      MainCategory mainCategory = MainCategory.findById(id);
      List<SubCategory> subCategories = mainCategory.children;
      List<List<Object>> objects = new ArrayList<List<Object>>();
      for(SubCategory cat: subCategories) {
        List<Object> obj = new ArrayList<Object>();
        obj.add(cat.id);
        obj.add(cat.name);
        objects.add(obj);
        optionString.append("<option value='" + cat.id + "'>" + cat.name + "</option>");
      }
    }
    System.out.println("optionString: " + optionString);
    return optionString.toString();
  }
}
