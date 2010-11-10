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
    System.out.println("getSubCategories: " + id);
//    List<SubCategory> subCategories = SubCategory.find(
//        "parent_id = ? order by id asc", id
//    ).fetch();
//    MainCategory category = MainCategory.findById(id);
    //List<SubCategory> subCategories = mainCategory.children;
//    List<List<Object>> objects = new ArrayList<List<Object>>();
//    for(SubCategory cat: subCategories) {
//      List<Object> obj = new ArrayList<Object>();
//      obj.add(cat.id);
//      obj.add(cat.name);
//      objects.add(obj);
//    }
//    System.out.println("objects: " + objects);
		//rendeJSON(objects);
          //  #{crud.relationField name: "subCategory", value: object.subCategory, field: currentType.getField('subCategory') /}

//    ObjectType currentType = null;
//    try {
//      currentType = ObjectType.forClass(MainCategory.class.getName());
//    } catch(ClassNotFoundException e) {
//      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//    }
//    System.out.println("currentType: " + currentType.getClass());
//    System.out.println("currentType: " + currentType.getFields());
//    for(ObjectType.ObjectField field: currentType.getFields()) System.out.println("field: " + field.name);
//
//    //_module_crud__app_views_tags_crud_relationField
//    Map options = new HashMap();
//    options.put("_name", "subCategory");
//    options.put("_value", "");
//    options.put("_field", currentType.getField("children"));
////    renderTemplate("/app/views/tags/crud/relationField.html", "subCategory", "", currentType);
//    renderTemplate("/app/views/tags/crud/relationField.html", options);

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
