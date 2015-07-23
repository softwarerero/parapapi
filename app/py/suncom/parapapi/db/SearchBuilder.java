package py.suncom.parapapi.db;

import models.Ad;
import play.templates.JavaExtensions;
import play.Logger;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 09.12.2010
 * Time: 20:36:48
 * Copyright.
 */
public class SearchBuilder {
  StringBuilder search = new StringBuilder();
  StringBuilder order = new StringBuilder();

  public SearchBuilder like(String attr, String s) {
    search.append("lower(").append(attr).append(") like ");
    search.append("'%" + (s.toLowerCase()) + "%'");
    return this;
  }

  public SearchBuilder eq(String attr, Object s) {
    if(s instanceof String) {
      s = escape((String) s);
    }
    search.append(attr).append("=").append(s);
    return this;
  }

  public SearchBuilder neq(String attr, Object s) {
    if(s instanceof String) {
      s = escape((String) s);
    }
    search.append(attr).append("<>").append(s);
    return this;
  }

  public SearchBuilder gte(String attr, Object s) {
    if(s instanceof String || s instanceof Date) {
      s = "'" + escape(((String) s)) + "'";
    }
    search.append(attr).append(">=").append(s);
    return this;
  }


  public SearchBuilder lte(String attr, Object s) {
    if(s instanceof String || s instanceof Date) {
      s = "'" + escape(((String) s)) + "'";
    }
    search.append(attr).append("<=").append(s);
    return this;
  }

  public SearchBuilder eqEnum(String attr, Enum[] values, Enum enumValue) {
    int val = -1;
    for(int i=0; i<values.length; i++) {
      Enum e = values[i];
      if(e.name().equals(enumValue.name())) {
        val = i;
      }
    }
    search.append(attr).append("=").append(val);
    return this;
  }

  public SearchBuilder and() {
    search.append(" and ");
    return this;
  }

  public SearchBuilder or() {
    search.append(" or ");
    return this;
  }

  public SearchBuilder startExpression() {
    search.append("(");
    return this;
  }

  public SearchBuilder endExpression() {
    search.append(")");
    return this;
  }

  public SearchBuilder orderBy(String orderBy) {
    order.append(" order by " + orderBy);
    return this;
  }

  public String getSearchString() {
    return search.toString() + order.toString();
  }

  public String escape(String s) {
    return JavaExtensions.escape(s).toString();
  }

  public String quote(String s) {
    return "'" + s + "'";
  }

  public List<Ad> exec() {
    return Ad.find(getSearchString()).fetch();
  }
}
