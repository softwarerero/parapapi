package models;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 19.11.2010
 * Time: 19:32:47
 * Copyright.
 */
@Entity
public class AdSearch extends Model {

  @MaxSize(100) public String text;
  @Temporal(TemporalType.DATE) public Date postedAt;
  public BigDecimal priceFrom;
  public BigDecimal priceTo;
  public Ad.Department department = Ad.Department.ce;
  public String city;
  public String zone;
  @Enumerated public Ad.OfferType offer = Ad.OfferType.offer;
  @Enumerated public Ad.HandOver handOver = Ad.HandOver.sell;
  @Enumerated public Ad.PriceType priceType;
  @Enumerated public Ad.Language language = Ad.Language.de;
  @Enumerated public Ad.Country country = Ad.Country.py;
  @Enumerated public Ad.Currency currency = Ad.Currency.PYG;

  public String mainCategory;
  public String subCategory;
  
  @ManyToOne public User author;

  public boolean allowBids;

}
