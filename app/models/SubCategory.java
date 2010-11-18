package models;

import com.google.gson.annotations.Expose;
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
  
  @Required @Expose
  public String name;

  @ManyToOne
  public MainCategory parent;

  @OneToMany(mappedBy="subCategory", cascade= CascadeType.ALL, fetch = FetchType.LAZY)
  public List<Ad> ads;

  @Expose
  public long adCount = 0;

  public SubCategory(String name) {
     this.name = name;
  }


  public String toString() {
    return name;
  }
}
