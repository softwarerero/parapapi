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
    User bob = UserTest.createBob();

    Ad ad = new Ad();
    ad.author = bob;
    ad.title = "My first post";
    ad.content = "Hello world";
    ad.save();

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
    User bob = UserTest.createBob();

    // Create a new post
    MainCategory firstCategory = new MainCategory();
    firstCategory.name = "My first mainCategory";
    firstCategory.save();

    // Post a first comment
    Ad ad = new Ad();
    ad.author = bob;
    ad.title = "Jeff";
    ad.content = "Nice post";
    ad.save();
    ad = new Ad();
    ad.author = bob;
    ad.title = "Tom";
    ad.content = "I knew that !";
    ad.save();

    // Retrieve all comments
    List<Ad> bobAds = Ad.find("byAuthor", bob).fetch();

    // Tests
    assertEquals(2, bobAds.size());

    Ad firstAd = bobAds.get(0);
    assertNotNull(firstAd);
    assertEquals("Bob", firstAd.author.nickname);
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
