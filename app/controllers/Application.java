package controllers;

import play.Logger;
import play.Play;
import play.cache.Cache;
import play.db.jpa.Blob;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

import models.*;
import play.data.validation.*;
import play.modules.paginate.ValuePaginator;

import javax.imageio.ImageIO;


public class Application extends Controller {

  static public int pageSize = Integer.parseInt(Play.configuration.getProperty("pagesize"));


  public static void index() {
//        render();
    List<MainCategory> mainCategory = MainCategory.find(
        "order by id asc"
    ).fetch(100);
    render(mainCategory);
  }


	public static void search(@Required String searchString) {
		//if(myName.equals("")) render("nada");
		if(validation.hasErrors()) {
			flash.error("Oops, please enter your search criteria!");
			index();
		}
    flash.error("Not implemented yet, Papi!");
    index();
		//render(myName);
	}

  public static void maincategoryList(Long id) {
    MainCategory category = MainCategory.findById(id);
    List<Ad> ads = category.ads;
    ValuePaginator paginator = new ValuePaginator(ads);
    paginator.setPageSize(pageSize);
		render("Application/adList.html", category, ads, paginator);
	}

  public static void subcategoryList(Long id) {
    SubCategory category = SubCategory.findById(id);
    List<Ad> ads = category.ads;
    ValuePaginator paginator = new ValuePaginator(ads);
    paginator.setPageSize(pageSize);
    render("Application/adList.html", category, ads, paginator);
	}

  public static void adDetail(Long id) {
    Ad ad = Ad.findById(id);
		render(ad);
	}


  static int no = 0;
  public static void createAd() {
    Ad object = new Ad();
    populateTestAd(object);
    render4editAd(object);
//    List<MainCategory> mainCategories = MainCategory.findAll();
//    List<SubCategory> subCategories = object.mainCategory.children;
//    no++;
//    String randomID = Codec.UUID();
//    render("Application/editAd.html", Ad.class, object, mainCategories, subCategories, randomID);
	}


  public static void editAd(Long id) {
    Ad object = Ad.findById(id);
    render4editAd(object);
	}

  
  private static void render4editAd(Ad object) {
    List<MainCategory> mainCategories = MainCategory.findAll();
    List<SubCategory> subCategories = object.mainCategory.children;
    String randomID = Codec.UUID();
    render("Application/editAd.html", Ad.class, object, mainCategories, subCategories, randomID);
	}


  private static Ad populateTestAd(Ad object) {
    // TODO find a better user
    object.author = (User) User.findAll().get(0);
    object.price = new BigDecimal("99.77");
    object.postedAt = new Date();
    object.title = "Title " + no;
    object.content = "Content " + no;
    object.email = "test@test.de";
    object.offer = Ad.OfferType.offer;
    object.handOver = Ad.HandOver.sell;
    object.priceType = Ad.PriceType.fixedPrice;
    object.phone = "123" + no;
    object.mainCategory = (MainCategory) MainCategory.findAll().get(0);
    object.subCategory = object.mainCategory.children.get(0);
    System.out.println("object.subCategory: " + object.subCategory);
    return object;
  }

//  @Before
//  public static void addType() throws Exception {
//      CRUD.ObjectType type = CRUD.ObjectType.get(getControllerClass());
//      renderArgs.put("type", type);
//  }

  
  public static void saveAd(@Valid Ad object,
                            @Required(message="Please type the code") String code,
                            String randomID,
                            Picture picture, Picture picture1, Picture picture2, Picture picture3, Picture picture4) {

    System.out.println("params: " + params.allSimple());
    System.out.println("id: " + object.id);

    if(!Play.id.equals("test")) {
      validation.equals(code, Cache.get(randomID)).message("Invalid code. Please type it again");
    }

    if(validation.hasErrors()) {
      Logger.debug("validation error: " + validation.errorsMap());
      List<MainCategory> mainCategories = MainCategory.findAll();
      List<SubCategory> subCategories = object.mainCategory.children;
      render("Application/editAd.html", Ad.class, object, mainCategories, subCategories, randomID);
    }

    Logger.debug("about to save: " + object.title);
    object.save();

    savePicture(object, picture);
    savePicture(object, picture1);
    savePicture(object, picture2);
    savePicture(object, picture3);
    savePicture(object, picture4);

    Cache.delete(randomID);
    Users.dashboard();
  }

  
  public static void deleteAd(Long id) {
//  public static void deleteAd(Ad object) {
    Ad object = Ad.findById(id);
    Logger.debug("delete: " + object);
    object.delete();
    Users.dashboard();
  }


  private static void savePicture(Ad ad, Picture picture) {
    picture.ad = ad;
    Validation.ValidationResult res = validation.valid(picture);
    if(res.ok) {
      System.out.println("try to save picture ");
      picture.save();
    } else {
      System.out.println("no picture: " + res.error);
    }
  }


//  public static void uploadPicture(Picture picture, Long ad_id) {
//    System.out.println("try to upload picture: " + picture.getClass());
//    System.out.println("try to upload picture: " + picture);
//    System.out.println("ad_id: " + ad_id);
//    assert(null != ad_id);
//    //renderBinary(photo.image.get());
//    editAd(ad_id);
//  }

  public static void renderFirstPicture(long id) {
    Ad ad = Ad.findById(id);
    if(ad.pictures.size() > 0) {
      Picture picture = ad.pictures.get(0);
      renderBinary(picture.image.get());
    }
  }

  public static void renderPicture(Long adId, int offset) {
    Ad ad = Ad.findById(adId);
    Picture picture = ad.pictures.get(offset);
      renderBinary(picture.image.get());
  }

  public static void renderPictures(long id) throws IOException {
    Ad ad = Ad.findById(id);
    BufferedImage newImage = null;
    for(Picture picture: ad.pictures) {
      System.out.println("render3 picture: " + picture.getClass());
      //InputStream is = new InputStream();
      //Image i = new BufferedImage(picture.image.get());
      //ImageIO.
      if(null == newImage) {
          newImage = ImageIO.read(picture.image.get());
      } else {
          BufferedImage image1 = ImageIO.read(picture.image.get());
          newImage = combineImages(newImage, image1);
      }
    }
    File f = new File(Play.tmpDir.getPath() + "/newImage.png");
    ImageIO.write(newImage, "png", f);
//    renderBinary(ImageIO.createImageInputStream(newImage));
      System.out.println("f: " + f);
    renderBinary(f);
  }


  private static BufferedImage combineImages(BufferedImage image1, BufferedImage image2) {
    //BufferedImage image1 has size[100, 200]
    //BufferedImage image2 has size[150, 150]
    //BufferedImage bigImage = GraphicsUtilities.createThumbnail(ImageIO.read(file), 300);
    //ImageInputStream bigInputStream = ImageIO.createImageInputStream(bigImage);
    int w = image1.getWidth() + image2.getWidth();
    //int h = Math.max(image1.getHeight(), image2.getHeight());
    int h = image1.getHeight() + image2.getHeight();
    BufferedImage image = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
    Graphics2D g2 = image.createGraphics();
    g2.drawImage(image1, 0, 0, null);
    g2.drawImage(image2, image1.getWidth() + 1, 0, null);
    g2.dispose();
    return image;
  }

  public static void captcha(String id) {
      Images.Captcha captcha = Images.captcha();
      String code = captcha.getText("#E4EAFD");
      Cache.set(id, code, "10mn");
      renderBinary(captcha);
  }

}