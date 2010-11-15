package controllers;

import models.Ad;
import models.Picture;
import play.Logger;
import play.data.validation.Validation;
import play.db.jpa.Blob;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.Controller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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


  static void savePicture(Ad ad, Picture picture) throws IOException {
    picture.ad = ad;
    Blob blob = picture.image;
    if(null != blob) {
      System.out.println("blob save1: " + blob.getFile().getName());
      File newThumbnail = resize(blob, 50, 38);
      picture.thumbnail50 = new Blob();
      picture.thumbnail50.set(new FileInputStream(newThumbnail), blob.type());
      File newFile = resize(blob, 430, 380);
      // delete old and probably big upload file
      blob.getFile().delete();
      picture.image = new Blob();
      picture.image.set(new FileInputStream(newFile), blob.type());
      System.out.println("blob save: " + blob.get());
      System.out.println("blob newFile: " + newFile.getName());
      System.out.println("blob save2: " + blob.getFile().getName());
      Validation.ValidationResult res = validation.valid(picture);
      if(res.ok) {
        picture.save();
      } else {
        Logger.warn("could not save picture (2nd pass): " + res.error);
      }
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


  public static void renderPicture(Long adId, int offset) throws IOException {
    Ad ad = Ad.findById(adId);
    Picture picture = ad.pictures.get(offset);
    Blob blob = picture.image;
    renderBinary(blob.get());
  }


  public static void renderThumbnail(Long adId, int offset) throws IOException {
    Ad ad = Ad.findById(adId);

    if(ad.pictures.isEmpty()) {
      renderBinary(new File(PUBLIC_IMAGES_FAVICON01_PNG));
    }
    Picture picture = ad.pictures.get(offset);
    Blob blob = picture.thumbnail50;
    renderBinary(blob.get());
  }


  public static void delete(Long adId, int offset) throws IOException {
    Ad ad = Ad.findById(adId);
    Picture picture = ad.pictures.get(offset);
    picture.ad.pictures.remove(picture);
    picture.ad.save();
    picture.delete();
    picture.thumbnail50.getFile().delete();
    picture.image.getFile().delete();
    //renderBinary(new File(PUBLIC_IMAGES_CROSS_PNG));
    renderText("deleted");
  }


  private static File resize(Blob blob, int maxSizeW, int maxSizeH) throws IOException {
    BufferedImage image = ImageIO.read(blob.get());
    int w = image.getWidth();
    int h = image.getHeight();
    int max = Math.max(w, h);
    //int maxSize = h > maxSizeH ? maxSizeH : maxSizeW;
    if(w > maxSizeW || h > maxSizeH) {
      String UUID = Codec.UUID();
      File newFile = new File(blob.getStore(), UUID);
      int w2 = 0, h2 = 0;
      int q = 0;
      if(w > h) {
        q = h * 100 / w;
        System.out.println(q);
        w2 = maxSizeW;
        h2 = maxSizeW * q / 100;
      } else {
        q = w * 100 / h;
        h2 = maxSizeH;
        w2 = maxSizeH * q / 100;
      }
      System.out.println("w: " + w + " h: " + h + " w2: " + w2 + " h2: " + h2 + " q: " + q);
      Images.resize(blob.getFile(), newFile, w2, h2);
      long size = blob.getFile().length();
      System.out.println("orgi size: " + size + " new size: " + newFile.length());
      return newFile;
    }
    return blob.getFile();
  }


//  public static void renderPictures(long id) throws IOException {
//    Ad ad = Ad.findById(id);
//    BufferedImage newImage = null;
//    for(Picture picture: ad.pictures) {
//      System.out.println("render3 picture: " + picture.getClass());
//      //InputStream is = new InputStream();
//      //Image i = new BufferedImage(picture.image.get());
//      //ImageIO.
//      if(null == newImage) {
//          newImage = ImageIO.read(picture.image.get());
//      } else {
//          BufferedImage image1 = ImageIO.read(picture.image.get());
//          newImage = combineImages(newImage, image1);
//      }
//    }
//    File f = new File(Play.tmpDir.getPath() + "/newImage.png");
//    ImageIO.write(newImage, "png", f);
////    renderBinary(ImageIO.createImageInputStream(newImage));
//      System.out.println("f: " + f);
//    renderBinary(f);
//  }
//
//
//  private static BufferedImage combineImages(BufferedImage image1, BufferedImage image2) {
//    //BufferedImage image1 has size[100, 200]
//    //BufferedImage image2 has size[150, 150]
//    //BufferedImage bigImage = GraphicsUtilities.createThumbnail(ImageIO.read(file), 300);
//    //ImageInputStream bigInputStream = ImageIO.createImageInputStream(bigImage);
//    int w = image1.getWidth() + image2.getWidth();
//    //int h = Math.max(image1.getHeight(), image2.getHeight());
//    int h = image1.getHeight() + image2.getHeight();
//    BufferedImage image = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
//    Graphics2D g2 = image.createGraphics();
//    g2.drawImage(image1, 0, 0, null);
//    g2.drawImage(image2, image1.getWidth() + 1, 0, null);
//    g2.dispose();
//    return image;
//  }
}
