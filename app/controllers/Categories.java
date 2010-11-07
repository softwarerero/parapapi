package controllers;

import models.Category;
import play.*;
import play.mvc.*;


/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 06.11.2010
 * Time: 00:27:41
 * Copyright.
 */
@CRUD.For(Category.class)
@Check("admin")
@With(Secure.class)
public class Categories extends CRUD {
}
