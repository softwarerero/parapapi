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
  public enum Country { py }
  public enum Currency { pyg, usd, eur }

  @Required @MaxSize(100) public String title;
  @Temporal(TemporalType.DATE)
  public Date postedAt;
  public BigDecimal price;
  public String department;
  public String city;
  public String zone;
  @Required public String phone;
  public String mobilePhone;
  @Required @Email
  public String email;
  public long noOfVisits;
  @Required @Enumerated public OfferType offer;
  @Required @Enumerated public HandOver handOver;
  @Enumerated public PriceType priceType;
  @Required @Enumerated public Language language = Language.de;
  @Required @Enumerated public Country country = Country.py;
  @Required @Enumerated public Currency currency = Currency.pyg;

  @ManyToOne @Required
  public MainCategory mainCategory;

  @ManyToOne 
  public SubCategory subCategory;

  @Lob @Required @MaxSize(10000)
  public String content;

  @ManyToOne @Required
  public User author;

  public boolean allowBids;
  
  @OneToMany(mappedBy="ad", cascade= CascadeType.ALL)
  public List<Picture> pictures;

  public Ad() {
    postedAt = new Date();
    //picture = new Picture();
    //  fotos = new ArrayList<File>();
  }


  public String getHtmlSecuredEmail() {
    if(null == email) return null;
    return email.replace("@", "<code>@</code>");
  }


  public String toString() {
    return title;
  }

}
