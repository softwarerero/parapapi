package controllers;

import models.MainCategory;
import play.mvc.With;

/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 07.11.2010
 * Time: 18:08:16
 * Copyright.
 */
@CRUD.For(MainCategory.class)
@Check("admin")
@With(Secure.class)
public class SubCategories extends CRUD {
}