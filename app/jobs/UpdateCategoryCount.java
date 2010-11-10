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
//@OnApplicationStart
//@Every("60s")
public class UpdateCategoryCount extends Job {

    public void doJob() {
      Logger.info("Update mainCategory count");

      Query query = JPA.em().createNativeQuery("update MainCategory set adCount = 0");
      query.executeUpdate();
      List<MainCategory> mainCategories = MainCategory.findAll();
      for(MainCategory category: mainCategories) {
        long adCount = Ad.count("mainCategory = ?", category);
        category.adCount += adCount;
        category.save();
      }

      query = JPA.em().createNativeQuery("update SubCategory set adCount = 0");
      query.executeUpdate();
      List<SubCategory> subCategories = SubCategory.findAll();
      for(SubCategory category: subCategories) {
        long adCount = Ad.count("subCategory = ?", category);
        category.adCount += adCount;
        category.save();
      }
    }

}