package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.*;
import play.i18n.Messages;
import play.mvc.*;
import play.templates.JavaExtensions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 06.11.2010
 * Time: 00:27:41
 * Copyright.
 */
@CRUD.For(MainCategory.class)
@Check("admin")
@With(Secure.class)
public class MainCategories extends CRUD {


}
