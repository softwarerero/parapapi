package jobs;

import play.jobs.*;
import play.test.*;

import models.*;

/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 04.11.2010
 * Time: 11:39:26
 * Copyright.
 */
@OnApplicationStart
public class Bootstrap extends Job {

    public void doJob() {
        // Check if the database is empty
        if(User.count() == 0) {
            Fixtures.load("bootstrap.yml");
        }
    }

}
