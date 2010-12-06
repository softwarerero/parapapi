package models;

import java.util.*;
import javax.persistence.*;

import play.data.validation.*;
import play.db.jpa.*;
                            

/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 03.11.2010
 * Time: 21:59:03
 * Copyright.
 */
@Entity
public class User extends Model {

  public enum DocumentType { CID, PASSPORT }

  @Required @javax.persistence.Column(unique = true) public String nickname;
  @Email @Required @javax.persistence.Column(unique = true) public String email;
  @Required @Equals("passwordConfirmation") @Password public String password;
  @Required @Password public String passwordConfirmation;
  @Required public String firstNames;
  @Required public String lastNames;
  @Required public Ad.Currency currency = Ad.Currency.PYG;
  public String documentNo;
  public DocumentType documentType;
  @Required public Ad.Country country = Ad.Country.py;
  @Required public Ad.Department department;
  @Required public String city = "Asunción";
  public String postCode;
  public String street;
  public String zone = "Centro";
  public String mobilePhone;
  public String phone;
  public boolean isAdmin = false;
  public boolean isActive = false;
  public String confirmationToken;
  @Required @IsTrue public boolean acceptConditions = false;
//  @IsTrue public boolean wantNotifications = false;


  public User() {
  }

//  public User(String email, String password, String nickname) {
//      this.email = email;
//      this.password = password;
//      this.nickname = nickname;
//  }


  public static User connect(String email, String password) {
    User user = find("byEmailAndPassword", email, password).first();
    if(null != user && !user.isActive) {
      return null;
    }
    return user;
  }


  public String toString() {
    return nickname;
  }
}
