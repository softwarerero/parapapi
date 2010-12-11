package py.suncom.parapapi.db;

import play.Logger;
import play.Play;
import play.cache.Cache;

import java.io.File;
import java.sql.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 11.12.2010
 * Time: 13:36:53
 * Copyright.
 */
public class DbHelper {

  public static Statement getStatement() throws ClassNotFoundException, SQLException {
    Class.forName(Play.configuration.getProperty("db.driver"));
    Connection conn = DriverManager.getConnection(Play.configuration.getProperty("db.url"), Play.configuration.getProperty("db.user"), Play.configuration.getProperty("db.pass"));
    Statement st = conn.createStatement();
    return st;
  }


  public static void backup() throws ClassNotFoundException, SQLException {
//    String backupPathName = "/opt/parapapi/beta"
    String backupPathName = Play.configuration.getProperty("db.backup.path");
    String datePart = getDatePart();
    String backupFileName = backupPathName + "/" + datePart + ".parapapi.h2.sql";

    Statement st = DbHelper.getStatement();
    Logger.info("st: " + st);
    assert null == st;
    String queryString = "BACKUP TO 'backup.parapapi.zip'";
    String url = Play.configuration.getProperty("db.url");
    String user = Play.configuration.getProperty("db.user");
    String pw = Play.configuration.getProperty("db.pass");
    org.h2.tools.Script.main("-url", url, "-user", user, "-password", pw, "-script", backupFileName); //-url jdbc:h2:~/test -user sa -script test.zip -options compression zip

    Logger.info("backuped to: " + backupFileName);
    st.close();
  }


  private static String getDatePart() {
    Calendar cal = new GregorianCalendar();
    String datePart = String.valueOf(cal.get(Calendar.YEAR));
    datePart += String.valueOf(cal.get(Calendar.MONTH));
    datePart += String.valueOf(cal.get(Calendar.DATE));
    datePart += "_" + String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
    datePart += String.valueOf(cal.get(Calendar.MINUTE));
    return datePart;
  }

}
