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
  @Required
  public String email;
  @Required
  public String password;
  @Required
  public String fullname;
  public boolean isAdmin;

//@Equals("passwordConfirmation") String password

  public User(String email, String password, String fullname) {
      this.email = email;
      this.password = password;
      this.fullname = fullname;
  }


  public static User connect(String email, String password) {
      return find("byEmailAndPassword", email, password).first();
  }


  public String toString() {
    return fullname;
  }
}
