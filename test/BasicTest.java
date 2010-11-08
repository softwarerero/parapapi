import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

  @Before
  public void setup() {
    Fixtures.deleteAll();
  }
  
  @Test
  public void fullTest() {
      Fixtures.load("data.yml");

      // Count things
      assertEquals(2, User.count());
      assertEquals(2, MainCategory.count());
      assertEquals(1, Ad.count());

      // Try to connect as users
      assertNotNull(User.connect("bob@gmail.com", "secret"));
      assertNotNull(User.connect("jeff@gmail.com", "secret"));
      assertNull(User.connect("jeff@gmail.com", "badpassword"));
      assertNull(User.connect("tom@gmail.com", "secret"));

      // Find all of Bob's posts
      List<MainCategory> cat1List = MainCategory.find("parent.name", "firstCat").fetch();
      assertEquals(1, cat1List.size());

      // Find all comments related to Bob's posts
      List<Ad> bobAds = Ad.find("author.email", "bob@gmail.com").fetch();
      assertEquals(1, bobAds.size());

      // Find the most recent post
      MainCategory cat2 = MainCategory.find("order by name desc").first();
      assertNotNull(cat2);
      assertNotNull(cat2.parent);
      assertEquals("firstCat", cat2.parent.name);

      // Check that this post has two comments
      assertEquals("secondCat", cat2.name);
      assertEquals(1, cat2.ads.size());

      // Post a new comment
//      cat2.addAd("Jim", "Hello guys");
//      assertEquals(3, cat2.comments.size());
//      assertEquals(4, Comment.count());
  }

    

}
