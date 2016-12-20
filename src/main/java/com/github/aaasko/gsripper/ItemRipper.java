package com.github.aaasko.gsripper;

import static java.util.stream.Collectors.toList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

public class ItemRipper {

  private final ItemRipperConfiguration config;

  public ItemRipper(ItemRipperConfiguration config) {
    this.config = config;
  }

  public void run(String url) {
    System.out.println("Ripping " + url);
    
    Document doc;
    try {
      doc = Jsoup.connect(url).timeout(3000).get();
    } catch (IOException e) {
      System.err.println("Can't download " + url);
      return;
    }
    
    String title = doc.select(config.getTitleSelector()).text();
    String color = "";
    for (String colorSelector : config.getColorSelectors()) {
      color = doc.select(colorSelector).text();
      if (!Strings.isNullOrEmpty(color)) {
        break;
      }
    }
    String fabric = doc.select(config.getFabricNameSelector()).text();
    List<String> mainImages = getMainImagesSources(
      doc,
      config.getMainImagesSelector(),
      config.getMainImageSrcTransformer()
    );
    List<String> productInfoImages = getProductInfoImagesSources(
      doc,
      config.getProductInfoImagesSelector(),
      config.getProductInfoImageTransformer()
    );
    
    ItemInfo itemInfo = new ItemInfo(
        title,
        color,
        fabric,
        mainImages,
        productInfoImages
    );
    download(itemInfo);
  }

  private List<String> getMainImagesSources(
      Document doc,
      String selector,
      Function<String, String> transformer) {
    return doc
      .select(selector)
      .stream()
      .map(this::getSrc)
      .map(this::fixGoogleStyleUrl)
      .map(transformer::apply)
      .collect(toList());
  }
  
  private List<String> getProductInfoImagesSources(
      Document doc,
      String selector,
      Function<String, String> transformer) {
    return doc
      .select(selector)
      .stream()
      .map(this::getDataSrc)
      .map(this::fixGoogleStyleUrl)
      .map(transformer::apply)
      .collect(toList());
  }
  
  private String getSrc(Element e) {
    return e.attr("src");
  }
  
  private String getDataSrc(Element e) {
    return e.attr("data-src");
  }
  
  private String fixGoogleStyleUrl(String url) {
    return url.startsWith("//") ? "http:" + url : url;
  }
  
  private void download(ItemInfo itemInfo) {
    StringBuilder sb = new StringBuilder();
    sb.append(itemInfo.getTitle());
    if (itemInfo.getColor() != null) {
      sb.append(", ").append(itemInfo.getColor().replace('/', '+'));
    }
    if (itemInfo.getFabric() != null) {
      sb.append(", ").append(itemInfo.getFabric().replace('/', '+'));
    }
    String folderName = sb.toString().trim();
    
    System.out.println(folderName);
    
    List<String> imageUrls = ImmutableList.<String>builder()
      .addAll(itemInfo.getMainImages())
      .addAll(itemInfo.getProductInfoImages())
      .build();
    
    for (String imageUrl : imageUrls) {
      Response resultImageResponse;
      try {
        resultImageResponse = Jsoup.connect(imageUrl).ignoreContentType(true).execute();
      } catch (IOException e1) {
        System.err.println("Can't read " + imageUrl);
        continue;
      }
      String fileName = getFileName(imageUrl);
      Path targetFolder = Paths.get(config.getTargetFolder(), folderName);
      try {
        Files.createDirectories(targetFolder);
      } catch (IOException e1) {
        System.err.println("Can't create folder " + targetFolder);
      }
      Path targetFile = Paths.get(config.getTargetFolder(), folderName, fileName);
      try {
        Files.copy(new ByteArrayInputStream(resultImageResponse.bodyAsBytes()), targetFile);
      } catch (IOException e) {
        System.err.println("Can't copy " + imageUrl);
      }
    }
  }

  private String getFileName(String url) {
    return url.substring(url.lastIndexOf('/') + 1);
  }

}
