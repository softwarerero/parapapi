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

  @Email
  @Required @javax.persistence.Column(unique = true) 
  public String email;
  @Required @Equals("passwordConfirmation")
  public String password;
  @Required
  public String passwordConfirmation;
  @Required @javax.persistence.Column(unique = true) 
  public String nickname;
  public String firstNames;
  public String lastNames;
  public String postCode;
  public String street;
  public String city;
  public String country;
  public String mobilePhone;
  public String landLine;
  public boolean isAdmin = false;
  public boolean isActive = false;
  public String confirmationToken;
  

  public User() {
  }

  public User(String email, String password, String nickname) {
      this.email = email;
      this.password = password;
      this.nickname = nickname;
  }


  public static User connect(String email, String password) {
    User user = find("byEmailAndPassword", email, password).first();
//    if(null != user && !user.isActive) {
//      return null;
//    }
    return user;
  }


  public String toString() {
    return nickname;
  }
}
