package models;

import org.junit.*;
import play.test.*;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 03.11.2010
 * Time: 22:28:58
 * Copyright.
 */
public class AdTest extends UnitTest {

  @Before
  public void setup() {
    Fixtures.deleteAll();
  }

  @Test
  public void createAd() {
    // Create a new user and save it
    User bob = new User("bob@gmail.com", "secret", "Bob").save();

    // Create a new post
    new Ad(bob, "My first post", "Hello world").save();

    // Test that the post has been created
    assertEquals(1, Ad.count());

    // Retrieve all posts created by Bob
    List<Ad> bobPosts = Ad.find("byAuthor", bob).fetch();

    // Tests
    assertEquals(1, bobPosts.size());
    Ad firstAd = bobPosts.get(0);
    assertNotNull(firstAd);
    assertEquals(bob, firstAd.author);
    assertEquals("My first post", firstAd.title);
    assertEquals("Hello world", firstAd.content);
    assertNotNull(firstAd.postedAt);
  }


  @Test
  public void postAds() {
    // Create a new user and save it
    User bob = new User("bob@gmail.com", "secret", "Bob").save();

    // Create a new post
    Category firstCategory = new Category("My first category").save();

    // Post a first comment
    new Ad(bob, "Jeff", "Nice post", firstCategory).save();
    new Ad(bob, "Tom", "I knew that !").save();

    // Retrieve all comments
    List<Ad> bobAds = Ad.find("byAuthor", bob).fetch();

    // Tests
    assertEquals(2, bobAds.size());

    Ad firstAd = bobAds.get(0);
    assertNotNull(firstAd);
    assertEquals("Bob", firstAd.author.fullname);
    assertEquals("Jeff", firstAd.title);
    assertEquals("Nice post", firstAd.content);
    assertNotNull(firstAd.postedAt);

    Ad secondAd = bobAds.get(1);
    assertNotNull(secondAd);
    assertEquals("Tom", secondAd.title);
    assertEquals("I knew that !", secondAd.content);
    assertNotNull(secondAd.postedAt);
  }

}
