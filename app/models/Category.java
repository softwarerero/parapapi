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

  public static final String[] main = {"transport", "realEstate", "household", "life",
    "leisure", "electronics", "service", "job",
    "farming", "business", "event", "contact", "education", "other" };
  public static final String[] transport = {"auto", "bike", "bicycle", "water", "autoparts", "airplane"};
  public static final String[] realEstate = {"house", "apartment", "twinHouse", "room", "commercial", "rural"};
  public static final String[] household = {"furniture", "antique", "appliance"};
  public static final String[] life = {"health", "wear", "cosmetic", "gift", "toy", "baby", "nutrition"};
  public static final String[] leisure = {"home", "garden", "foto", "optic", "sport", "hunt", "fishery", "musicalInstrument"};
  public static final String[] electronics = {"computer", "notebook", "software", "printer", "supply", "phone", "tv", "video"};
  public static final String[] service = {"healthservice", "workshop", "finance", "transport", "construction", "animation", "eventTechnology", "multimedia", "security", "tourism"};
  public static final String[] job = {"healthjob", "education", "industry", "office", "household"};
  public static final String[] farming = {"equipment", "plant", "animal"};
  public static final String[] business = {"machine", "officefurniture", "officesupply"};
  public static final String[] event = {"fair", "party", "cinema", "theatre", "concert", "sportevent"};
  public static final String[] contact = {"friendship", "venture", "club", "sheSeeksHim", "heSeeksHer"};
  public static final String[] education = {"it", "academics", "sportcourse", "languages", "art", "driving"};
  public static final String[] other = {"collection", "pet"};

  public static final Map<String, String[]> all = new HashMap() {{
    put("transport", transport);
    put("realEstate", realEstate);
    put("household", household);
    put("life", life);
    put("leisure", leisure);
    put("electronics", electronics);
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

}
