package controllers;

import play.mvc.With;

/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 11.12.2010
 * Time: 13:35:02
 * Copyright.
 */
@Check("admin")
@With(Secure.class)
public class AdSearches extends CRUD {

}
