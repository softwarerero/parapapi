package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 20.11.2010
 * Time: 14:33:38
 * Copyright.
 */
@Entity
public class City extends Model {

  @Required public Ad.Country country = Ad.Country.py;
  @Required public Ad.Department department = Ad.Department.ce;
  @Required public String name;

  public static String toJSON() {
    List<City> all = City.findAll();
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    boolean start = true;
    for(City c : all) {
      if(start) {
        start = false;
      } else {
        sb.append(',');
      }
      sb.append("\"").append(c.name).append("\"");
    }
    sb.append("]");
    return sb.toString();
  }
}
