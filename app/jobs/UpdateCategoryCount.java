package jobs;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import models.*;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.jobs.*;
import py.suncom.parapapi.db.DbHelper;

import java.sql.*;
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
    Statement st = DbHelper.getStatement();
    Map categoryCountMap = getCategoryCountMap("CategoryCountMap");
    updateMainCategories(categoryCountMap, st, null);
    updateSubCategories(categoryCountMap, st, null);
    categoryCountMap = getCategoryCountMap("CategoryCountMap_de");
    updateMainCategories(categoryCountMap, st, "language=" + Ad.Language.de.ordinal());
    updateSubCategories(categoryCountMap, st, "language=" + Ad.Language.de.ordinal());
    categoryCountMap = getCategoryCountMap("CategoryCountMap_en");
    updateMainCategories(categoryCountMap, st, "language=" + Ad.Language.en.ordinal());
    updateSubCategories(categoryCountMap, st, "language=" + Ad.Language.en.ordinal());
    categoryCountMap = getCategoryCountMap("CategoryCountMap_es");
    updateMainCategories(categoryCountMap, st, "language=" + Ad.Language.es.ordinal());
    updateSubCategories(categoryCountMap, st, "language=" + Ad.Language.es.ordinal());

    count(Ad.Language.de, st);
    count(Ad.Language.en, st);
    count(Ad.Language.es, st);
    st.close();
  }

  private void count(Ad.Language lang, Statement st) throws SQLException {
    String queryString = "select count(*) from Ad where language = " + lang.ordinal();
    ResultSet rs = st.executeQuery(queryString);
    rs.first();
    Cache.set("count_" + lang.name(), rs.getObject(1));
    rs.close();
  }

  public Long getCount(Ad.Language lang) {
    return (Long) Cache.get("count_" + lang.name());
  }

  private void updateSubCategories(Map categoryCountMap, Statement st, String having) throws SQLException {
    String categoryType = "subCategory";
    execQuery(categoryCountMap, st, categoryType, having);
  }

  private void updateMainCategories(Map categoryCountMap, Statement st, String having) throws SQLException {
    String categoryType = "mainCategory";
    execQuery(categoryCountMap, st, categoryType, having);
  }

  private void execQuery(Map categoryCountMap, Statement st, String categoryType, String having) throws SQLException {
    String queryString = "select " + categoryType + ", count(id) from Ad group by " + categoryType;
    if(null != having) {
      queryString += ", language having " + having;
    }
    ResultSet rsCats = st.executeQuery(queryString);
    while(rsCats.next()) {
      categoryCountMap.put(rsCats.getObject(1), rsCats.getObject(2));
    }
    rsCats.close();
  }

  public static Map getCategoryCountMap(String name) {
    Map categoryCountMap = (Map) Cache.get(name);
    Monitor cacheMonitor = MonitorFactory.getMonitor(name + "Hit", "count");
    if(null == categoryCountMap) {
      categoryCountMap = Category.createCounterMap();
      Cache.set(name, categoryCountMap);
      cacheMonitor = MonitorFactory.getMonitor(name + "Miss", "count");
    }
    cacheMonitor.add(1);
    return categoryCountMap;
  }


}