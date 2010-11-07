package jobs;

import models.*;
import play.Logger;
import play.db.jpa.JPA;
import play.jobs.*;

import javax.persistence.Query;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 04.11.2010
 * Time: 13:56:02
 * Copyright.
 */
@OnApplicationStart
@Every("60s")
public class UpdateCategoryCount extends Job {

    public void doJob() {
      Logger.info("Update category count");

      Query query = JPA.em().createNativeQuery("update Category set adCount = 0");
      query.executeUpdate();
      // TODO: this works only for the first parent level
      List<Category> categories = Category.findAll();
      for(Category category: categories) {
        long adCount = Ad.count("category = ?", category);
        category.adCount += adCount;
        category.save();
        if(null != category.parent) {
          category.parent.adCount += adCount;
          category.parent.save();
        }
      }
    }

}