package controllers;

import com.google.gson.GsonBuilder;
import models.City;
import play.mvc.Controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 20.11.2010
 * Time: 15:12:19
 * Copyright.
 */
public class Cities extends Controller {

  public static String getAll() {
    List<City> all = City.findAll();
//    GsonBuilder  gson = new GsonBuilder();
//    return gson.create().toJson(all);
    //return "hi";
    StringBuilder cityNames = new StringBuilder();
    cityNames.append('[');
    for(City city: all) {
      cityNames.append(city.name).append(',');
    }
    cityNames.append(']');
    return cityNames.toString();
  }
}
