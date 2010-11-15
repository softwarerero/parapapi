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

  @Required @MaxSize(180) public String title;
  @Temporal(TemporalType.DATE)
  public Date postedAt;
  public BigDecimal price;
  public String department;
  public String city;
  public String zone;
  @Required public String phone;
  public String mobile_phone;
  @Required @Email
  public String email;
  public long noOfVisits;
  @Required @Enumerated public OfferType offer; // or search
  @Required @Enumerated public HandOver handOver; // or rent
  @Enumerated public PriceType priceType; // or nogiciable

  @ManyToOne @Required
  public MainCategory mainCategory;

  @ManyToOne 
  public SubCategory subCategory;

  @Lob @Required @MaxSize(10000)
  public String content;

  @ManyToOne @Required
  public User author;
  
  @OneToMany(mappedBy="ad", cascade= CascadeType.ALL)
  public List<Picture> pictures;

  public Ad() {
    postedAt = new Date();
    //picture = new Picture();
    //  fotos = new ArrayList<File>();
  }

  public String toString() {
    return title;
  }

}
