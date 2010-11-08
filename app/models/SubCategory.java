package models;

import play.data.validation.*;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 07.11.2010
 * Time: 18:06:05
 * Copyright.
 */
@Entity
public class SubCategory extends Model {
  
  @Required
  public String name;

  @ManyToOne
// @JoinColumn(name="parent")
  public MainCategory parent;

  @OneToMany(mappedBy="subCategory", cascade= CascadeType.ALL, fetch = FetchType.LAZY)
  public List<Ad> ads;

  public long adCount = 0;

  public SubCategory(String name) {
     this.name = name;
  }


  public String toString() {
    return name;
  }
}
