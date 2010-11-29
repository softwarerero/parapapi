package models;

import java.math.BigDecimal;
import java.util.*;

import play.data.validation.*;
import play.db.jpa.Model;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 03.11.2010
 * Time: 22:17:02
 * Copyright.
 */
@Entity
public class Ad extends Model {

  public enum OfferType { offer, search }
  public enum HandOver { sell, rent, exchange, donate }
  public enum PriceType { fixedPrice, negociable }
  public enum Language { de, en, es }
  public enum Country { py, de, en, us, fr }
  public enum Currency { PYG, USD, EUR }
  public enum Department { ce, ap, an, am, bo, ca, cz, cn, co, cd, gu, it, pa, pr , sp, ne, mi }
  // Central, Alto Paraguay,
  // Alto Paraná, Amambay, Boquerón, Caaguazú, Caazapá, Canindeyú, Concepción,
  // Cordillera, Guariá, Itapúa, Paraguarí, Presidente Hayes, San Pedro, Ñeembucú, Missiones

  @Required @MaxSize(100) public String title;
  @Temporal(TemporalType.TIMESTAMP) public Date postedAt = new Date();
  public BigDecimal price;
  @Required public Department department = Department.ce;
  public String city;
  public String zone;
  @Required public String phone;
  public String mobilePhone;
  @Required @Email public String email;
  public long noOfVisits;
  @Required @Enumerated public Ad.OfferType offer = Ad.OfferType.search;
  @Required @Enumerated public HandOver handOver = HandOver.sell;
  @Enumerated public PriceType priceType;
  @Required @Enumerated public Language language = Language.de;
  @Required @Enumerated public Country country = Country.py;
  @Required @Enumerated public Currency currency = Currency.PYG;

  @ManyToOne @Required public MainCategory mainCategory;
  @ManyToOne public SubCategory subCategory;

  @Lob @Required @MaxSize(10000) public String content;

  @ManyToOne @Required public User author;

  public boolean allowBids;
  
  @OneToMany(mappedBy="ad", cascade= CascadeType.ALL) public List<Picture> pictures;


  public String getHtmlSecuredEmail() {
    if(null == email) return null;
    return email.replace("@", "<code>@</code>");
  }


//  public String formattedPrice() {
//    if(null == price) return "";
//    String ret = price.toString();
//    if(Currency.pyg.equals(currency)) {
//      ret = Long.valueOf(price.longValue()).toString();
//    }
//    ret.replace('.', ',');
//    for(int i=1; i<ret.length()-3; i++) {
//      if(0 == i%3) {
//        ret = ret.substring(0, i) + '.' + ret.substring(i+1);
//      }
//    }
//    System.out.println("ret: " + ret);
//
//    return ret;
//  }


  public String toString() {
    return title;
  }

}
