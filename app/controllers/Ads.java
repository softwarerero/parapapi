package controllers;

import play.mvc.With;

/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 06.11.2010
 * Time: 00:44:03
 * Copyright.
 */
@Check("admin")
@With(Secure.class)
public class Ads extends CRUD {
}
