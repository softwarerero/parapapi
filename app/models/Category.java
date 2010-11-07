package models;

import play.data.validation.*;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 03.11.2010
 * Time: 22:34:11
 * Copyright.
 */
@Entity
public class Category extends Model {
  @Required
  public String name;

  @ManyToOne// @JoinColumn(name="parent")
  public Category parent;

  @OneToMany(mappedBy="parent")
  public List<Category> children;

  @OneToMany(mappedBy="category", cascade= CascadeType.ALL, fetch = FetchType.LAZY)
  public List<Ad> ads;

  public long adCount = 0;

  public Category(String name) {
     this.name = name;
  }


  public String toString() {
    return name;
  }
}
