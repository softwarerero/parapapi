package models;

import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;

import javax.persistence.*;


/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 07.11.2010
 * Time: 22:27:22
 * Copyright.
 */
@Entity
public class Picture extends Model {

  @ManyToOne
  @Required
  public Ad ad;

  @Required
  public Blob image;
}
