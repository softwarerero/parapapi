package models;

import org.junit.*;
import java.util.*;
import play.test.*;

/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 03.11.2010
 * Time: 22:10:04
 * Copyright.
 */
public class UserTest extends UnitTest {

  @Before
  public void setup() {
    Fixtures.deleteAll();
  }


  @Test
  public void createAndRetrieveUser() {
    createBob();
    User bob;

    // Retrieve the user with e-mail address bob@gmail.com
    bob = User.find("byEmail", "bob@gmail.com").first();

    // Test
    assertNotNull(bob);
    assertEquals("Bob", bob.nickname);
  }

  public static User createBob() {
    User bob = new User();
    bob.email = "bob@gmail.com";
    bob.password = "secret";
    bob.nickname = "Bob";
    bob.isActive = true;
    bob.save();
    return bob;
  }


  @Test
  public void tryConnectAsUser() {
    createBob();

    // Test
    assertNotNull(User.connect("bob@gmail.com", "secret"));
    assertNull(User.connect("bob@gmail.com", "badpassword"));
    assertNull(User.connect("tom@gmail.com", "secret"));
  }

}
