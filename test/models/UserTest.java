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
    // Create a new user and save it
    new User("bob@gmail.com", "secret", "Bob").save();

    // Retrieve the user with e-mail address bob@gmail.com
    User bob = User.find("byEmail", "bob@gmail.com").first();

    // Test
    assertNotNull(bob);
    assertEquals("Bob", bob.fullname);
  }
  

  @Test
  public void tryConnectAsUser() {
    // Create a new user and save it
    new User("bob@gmail.com", "secret", "Bob").save();

    // Test
    assertNotNull(User.connect("bob@gmail.com", "secret"));
    assertNull(User.connect("bob@gmail.com", "badpassword"));
    assertNull(User.connect("tom@gmail.com", "secret"));
  }

}
