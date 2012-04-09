package models;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 01.12.2010
 * Time: 20:31:22
 * Copyright.
 * THINK: Bad about this class is that it is not easy to insert new categories.
 */
public class Category {

  public static final String[] main = {"farming", "contact", "education", "electronics", "job", "business", "event", 
  	"leisure", "household", "realEstate", "other", "service", "transport", "life" };
  public static final String[] transport = {"auto", "autoparts", "airplane", "bicycle", "bike", "water" };
  public static final String[] realEstate = {"house", "commercial", "apartment", "twinHouse", "room", "rural"};
  public static final String[] electronics = {"supply", "computer", "printer", "notebook", "software", "phone", "tv", "video"};
  public static final String[] household = {"antique", "appliance", "furniture" };
  public static final String[] life = {"baby", "cosmetic", "toy", "nutrition","gift", "wear", "health"};
  public static final String[] leisure = {"hunt", "sport", "foto", "home", "musicalInstrument", "garden", "optic", "fishery"};
  public static final String[] service = {"animation", "construction", "eventTechnology", "finance", "multimedia", "healthservice", "security", "workshop", "transportservice", "tourism"};
  public static final String[] job = {"educationjob", "householdjob", "industry", "office", "healthjob"};
  public static final String[] farming = {"animal", "equipment", "plant"};
  public static final String[] business = {"officesupply", "machine", "officefurniture"};
  public static final String[] event = {"cinema", "concert", "sportevent", "fair", "party", "theatre"};
  public static final String[] contact = {"friendship", "venture", "club", "heSeeksHer", "sheSeeksHim"};
  public static final String[] education = {"art", "sportcourse", "languages", "it", "book", "driving", "academics"};
  public static final String[] other = {"collection", "pet"};

  public static final Map<String, String[]> all = new HashMap() {{
    put("transport", transport);
    put("realEstate", realEstate);
    put("electronics", electronics);
    put("household", household);
    put("life", life);
    put("leisure", leisure);
    put("service", service);
    put("job", job);
    put("farming", farming);
    put("business", business);
    put("event", event);
    put("contact", contact);
    put("education", education);
    put("other", other);
  }};

  public static String[] getSubCategory(String name) {
    String[] ret = all.get(name);
    return ret;
  }


  public static Map createCounterMap() {
    Map<String, Long> map = new HashMap<String, Long>();
    List l = new ArrayList();
    for(String key: main) { map.put(key, 0L); }
    for(String key: transport) { map.put(key, 0L); }
    for(String key: realEstate) { map.put(key, 0L); }
    for(String key: household) { map.put(key, 0L); }
    for(String key: life) { map.put(key, 0L); }
    for(String key: leisure) { map.put(key, 0L); }
    for(String key: electronics) { map.put(key, 0L); }
    for(String key: service) { map.put(key, 0L); }
    for(String key: job) { map.put(key, 0L); }
    for(String key: farming) { map.put(key, 0L); }
    for(String key: business) { map.put(key, 0L); }
    for(String key: event) { map.put(key, 0L); }
    for(String key: contact) { map.put(key, 0L); }
    for(String key: education) { map.put(key, 0L); }
    for(String key: other) { map.put(key, 0L); }
    return map;
  }
}
