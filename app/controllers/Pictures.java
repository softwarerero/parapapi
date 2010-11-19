package controllers;

import models.Ad;
import models.Picture;
import play.Logger;
import play.Play;
import play.data.validation.Validation;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.Controller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 10.11.2010
 * Time: 21:55:40
 * Copyright.
 */
public class Pictures extends Controller {
  
  private static final String PUBLIC_IMAGES_FAVICON01_PNG = "public/images/favicon01.png";
  private static final String PUBLIC_IMAGES_CROSS_PNG = "public/images/cross.png";
  private static final String THUMB = "_thumb";
  private static final String WIDTH430 = "430";


  static void savePicture(Ad ad, File file) throws IOException {
    if(null == file) return;

    Picture picture = new Picture();
    picture.ad = ad;

    File picturePath = getTodadysPicturePath();
    String ending = file.getName().substring(file.getName().lastIndexOf('.'));
    String UUID = Codec.UUID();
    File newFile = new File(picturePath, UUID + THUMB + ending);
    File newThumbnail = resize(file, newFile, 50, 38);
    picture.thumbnail50 = newThumbnail.getCanonicalPath();

    newFile = new File(picturePath, UUID + WIDTH430 + ending);
    File newImage = resize(file, newFile, 430, 380);
    picture.image = newImage.getCanonicalPath();

    Validation.ValidationResult res = validation.valid(picture);
    if(res.ok) {
      picture.save();
    } else {
      Logger.warn("could not save picture (2nd pass): " + res.error);
    }
  }


  //TODO probably cache
  public static File getTodadysPicturePath() {
  Calendar date = new GregorianCalendar();
  int day = (date.get(Calendar.YEAR) + 21 - 2000) * 10000 + date.get(Calendar.DAY_OF_YEAR);
    File path = new File(getPicturePath() + File.separator + day);
    if(!path.exists()) {
        path.mkdirs();
    }
    return path;
  }


  static String picturePath = null;

  public static String getPicturePath() {
    if(null == picturePath) {
      String name = Play.configuration.getProperty("pictures.path");
      if(null == name || "".equals(name)) name = "pictures";
      if(new File(name).isAbsolute()) {
          picturePath = name;
      } else {
          picturePath = Play.applicationPath.getAbsolutePath() + File.separator + ".." + File.separator + name;
      }
    }
    return picturePath;
  }


//  public static void uploadPicture(Picture picture, Long ad_id) {
//    System.out.println("try to upload picture: " + picture.getClass());
//    System.out.println("try to upload picture: " + picture);
//    System.out.println("ad_id: " + ad_id);
//    assert(null != ad_id);
//    //renderBinary(photo.image.get());
//    editAd(ad_id);
//  }


  public static void renderPicture(Long adId, int offset) throws IOException {
    Ad ad = Ad.findById(adId);
    Picture picture = ad.pictures.get(offset);
    renderBinary(new File(picture.image));
  }


  public static void renderThumbnail(Long adId, int offset) throws IOException {
    Ad ad = Ad.findById(adId);

    if(ad.pictures.isEmpty()) {
      renderBinary(new File(PUBLIC_IMAGES_FAVICON01_PNG));
    }
    Picture picture = ad.pictures.get(offset);
    renderBinary(new File(picture.thumbnail50));
  }


  public static void delete(Long adId, int pictureId) throws IOException {
    Ad ad = Ad.findById(adId);
    Picture picture = null;
    for(Picture pic: ad.pictures) {
      if(pictureId == pic.id) {
        picture = pic;
      }
    }

    new File(picture.thumbnail50).delete();
    new File(picture.image).delete();
    picture.ad.pictures.remove(picture);
    picture.ad.save();
    picture.delete();
    renderText("deleted");
  }


  private static File resize(File originalFile, File newFile, int maxSizeW, int maxSizeH) throws IOException {
    BufferedImage image = ImageIO.read(originalFile);
    int w = image.getWidth();
    int h = image.getHeight();
    int max = Math.max(w, h);
    if(w > maxSizeW || h > maxSizeH) {
      int w2 = 0, h2 = 0;
      int q = 0;
      if(w > h) {
        q = h * 100 / w;
        w2 = maxSizeW;
        h2 = maxSizeW * q / 100;
      } else {
        q = w * 100 / h;
        h2 = maxSizeH;
        w2 = maxSizeH * q / 100;
      }
//      System.out.println("w: " + w + " h: " + h + " w2: " + w2 + " h2: " + h2 + " q: " + q);
//      System.out.println("originalFile: " + originalFile);
//      System.out.println("newFile: " + newFile);
      Images.resize(originalFile, newFile, w2, h2);
      long size = originalFile.length();
//      System.out.println("orgi size: " + size + " new size: " + newFile.length());
      return newFile;
    }
    return originalFile;
  }
}
