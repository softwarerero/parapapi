package controllers;

import models.Ad;
import models.Picture;
import play.Logger;
import play.Play;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.Controller;
import py.suncom.parapapi.db.DbHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: sun
 * Date: 10.11.2010
 * Time: 21:55:40
 * Copyright.
 */
public class Pictures extends Controller  {
  
  private static final String PUBLIC_IMAGES_FAVICON01_PNG = "public/images/favicon01.png";
  private static final String PUBLIC_IMAGES_CROSS_PNG = "public/images/cross.png";
  private static final String THUMB = "_thumb";
  private static final String MIDDLE = "_middle";
  private static final String BIG = "_big";
//  private static final String IMAGE_PATTERN = "[-|.|\\w|\\d|\\s]*(\\.)(jpg|png|gif|bmp)$";
  private static final String IMAGE_PATTERN = "(?i)(.)*(\\.)(jpg|png|gif|bmp)$";


  static void savePicture(Ad ad, File file) throws Exception {
    if(null == file) return;
    validation.match(file.getName(), IMAGE_PATTERN).message(Messages.get("validation.badPicture", file.getName()));
    if(validation.hasErrors()) {
      return;
    }

    Picture picture = new Picture();
    picture.ad = ad;

    String todayPath = getTodadysPicturePath();
    String ending = file.getName().substring(file.getName().lastIndexOf('.'));
    String UUID = Codec.UUID();
    String fileName = todayPath + File.separator + UUID + THUMB + ending;
    File newFile = new File(getPicturePath(), fileName);
    resize(file, newFile, 50, 38);
    picture.thumbnail50 = fileName;

    fileName = todayPath + File.separator + UUID + MIDDLE + ending;
    newFile = new File(getPicturePath(), fileName);
    resize(file, newFile, 200, 150);
    picture.thumbnail72 = fileName;

    fileName = todayPath + File.separator + UUID + BIG + ending;
    newFile = new File(getPicturePath(), fileName);
    resize(file, newFile, 520, 450);
    picture.image = fileName;

    Validation.ValidationResult res = validation.valid(picture);
    if(res.ok) {
      picture.save();
    } else {
      Logger.warn("could not save picture: " + res.error);
//      throw new Exception("could not save picture");
    }
  }


  //TODO probably cache
  public static String getTodadysPicturePath() {
    Calendar date = new GregorianCalendar();
    int day = (date.get(Calendar.YEAR) + 21 - 2000) * 10000 + date.get(Calendar.DAY_OF_YEAR);
    File path = new File(getPicturePath() + '/' + day);
    if(!path.exists()) {
        path.mkdirs();
    }
    return String.valueOf(day);
  }


  static String picturePath = null;

  public static String getPicturePath() {
    if(null == picturePath) {
      String name = Play.configuration.getProperty("pictures.path");
      if(null == name || "".equals(name)) name = "pictures";
      if(new File(name).isAbsolute()) {
          picturePath = name;
      } else {
          picturePath = ".." + File.separator + name;
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
    File file = new File(getPicturePath(), picture.image);
    renderBinary(file);
  }


  public static void renderThumbnail(Long adId, int offset) throws IOException {
    Ad ad = Ad.findById(adId);

    if(ad.pictures.isEmpty()) {
      renderBinary(new File(PUBLIC_IMAGES_FAVICON01_PNG));
    }
    Picture picture = ad.pictures.get(offset);
    File file = new File(getPicturePath(), picture.thumbnail50);
    renderBinary(file);
  }


  public static void renderThumbnail72(Long adId, int offset) throws IOException {
    Ad ad = Ad.findById(adId);

    if(ad.pictures.isEmpty()) {
      renderBinary(new File(PUBLIC_IMAGES_FAVICON01_PNG));
    }
    Picture picture = ad.pictures.get(offset);
    File file = new File(getPicturePath(), picture.thumbnail72);
    renderBinary(file);
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
    renderText("X ");
  }


  private static File resize(File originalFile, File newFile, int maxSizeW, int maxSizeH) throws Exception {
    BufferedImage image = ImageIO.read(originalFile);
    if(null == image) {
      validation.required(image).message(Messages.get("validation.badPicture", originalFile));
    }
    int w = image.getWidth();
    int h = image.getHeight();
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
      Logger.info("save picture: " + newFile);
      return newFile;
    }
    originalFile.renameTo(newFile);
    Logger.info("save picture: " + newFile);
    return newFile;
  }


  public static void pictureUpdate() throws Exception {
    List pictures = Picture.all().fetch();
    for(Object object: pictures) {
      Picture picture = (Picture) object;
      String todayPath = getTodadysPicturePath();
      File bigFile = new File(getPicturePath(), picture.image);
      if(bigFile.exists()) {
        Logger.info("found bigFile: " + bigFile);
      } else {
        Logger.info("not found bigFile: " + bigFile);
        continue;
      }
      String UUID = Codec.UUID();
      String ending = bigFile.getName().substring(bigFile.getName().lastIndexOf('.'));
      String fileName72 = todayPath + File.separator + UUID + MIDDLE + ending;
      File file72 = new File(getPicturePath(), fileName72);
      resize(bigFile, file72, 200, 150);
      picture.thumbnail72 = fileName72;
      picture.save();
    }
  }


  public static void backup() throws IOException {
    String backupPathName = Play.configuration.getProperty("db.backup.path");
    String datePart = DbHelper.getDatePart();
    String backupFileName = backupPathName + "/" + datePart + ".parapapi.pictures.zip";
    String picturePath = getPicturePath();

    String cmd = "zip -r " + backupFileName + " /var/www/parapapi/pictures/";

    Process p = Runtime.getRuntime().exec(cmd);
    String line;

    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
    while ((line = input.readLine()) != null) {
     System.out.println(line);
    }
    input.close();

    Logger.info("picture backup to: " + backupFileName);
  }

}
