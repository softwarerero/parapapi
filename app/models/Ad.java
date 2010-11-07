package models;

import java.io.File;
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

  public enum Offer { offer, search }
  

  @Required public String title;
  @Temporal(TemporalType.DATE)
  public Date postedAt;
  public BigDecimal price;
  public BigDecimal prize;
  public String department;
  public String city;
  public String zone;
  @Required public String phone;
  public String mobile_phone;
  @Required @Email
  public String email;
  public long noOfVisits;
  @Required @Enumerated public Offer offer; // or search
  @Required public boolean sell; // or rent
  @Required public boolean fixedPrice; // or nogiciable

  @ManyToOne @Required
  public Category category;
  
  @Lob @Required @MaxSize(10000)
  public String content;

  @ManyToOne
  public User author;
  
  //List<File> fotos;

  public Ad() {
    postedAt = new Date();
    //  fotos = new ArrayList<File>();
  }

  public String toString() {
    return title;
  }

}
