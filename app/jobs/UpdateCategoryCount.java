package jobs;

import models.*;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.db.jpa.JPA;
import play.jobs.*;

import javax.persistence.Query;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 04.11.2010
 * Time: 13:56:02
 * Copyright.
 */
@OnApplicationStart
@Every("60s")
public class UpdateCategoryCount extends Job {

  public void doJob() throws Exception {
    Logger.info("Update category stats");
    Map categoryCountMap = getCategoryCountMap();
    Statement st = getStatement();
    updateMainCategories(categoryCountMap, st);
    updateSubCategories(categoryCountMap, st);
    Cache.set("categoryCountMap", categoryCountMap);
    st.close();
  }

  private void updateSubCategories(Map categoryCountMap, Statement st) throws SQLException {
    String queryStringSub = "select subCategory, count(id) from Ad group by subCategory";
    ResultSet rsSubCats = st.executeQuery(queryStringSub);
    while(rsSubCats.next()) {
//      System.out.println(" Name:" + rsSubCats.getObject(1));
//      System.out.println(" noOfAds:" + rsSubCats.getObject(2));
      categoryCountMap.put(rsSubCats.getObject(1), rsSubCats.getObject(2));
    }
    rsSubCats.close();
  }

  private void updateMainCategories(Map categoryCountMap, Statement st) throws SQLException {
    String queryString = "select mainCategory, count(id) from Ad group by mainCategory";
    ResultSet rsCats = st.executeQuery(queryString);
    while(rsCats.next()) {
//      System.out.println(" Na,e:" + rsCats.getObject(1));
//      System.out.println(" noOfAds:" + rsCats.getObject(2));
      categoryCountMap.put(rsCats.getObject(1), rsCats.getObject(2));
    }
    rsCats.close();
  }

  public static Map getCategoryCountMap() {
    //Cache.set("products", products, "30mn");
    Map categoryCountMap = (Map) Cache.get("categoryCountMap");
    if(null == categoryCountMap) {
      categoryCountMap = Category.createCounterMap();
      Cache.set("categoryCountMap", categoryCountMap);
    }
    return categoryCountMap;
  }

  private Statement getStatement() throws ClassNotFoundException, SQLException {
    Class.forName(Play.configuration.getProperty("db.driver"));
    Connection conn = DriverManager.getConnection(Play.configuration.getProperty("db.url"), Play.configuration.getProperty("db.user"), Play.configuration.getProperty("db.pass"));
    Statement st = conn.createStatement();
    return st;
  }

}