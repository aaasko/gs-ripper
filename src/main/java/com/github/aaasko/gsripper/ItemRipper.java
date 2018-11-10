package com.github.aaasko.gsripper;

import static java.util.stream.Collectors.toList;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

public class ItemRipper {

  private static final int N_ATTEMPTS = 3;
  
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
    
    String title = doc.select(config.getTitleSelector()).first().text();
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
      config.getMainImageLinksSelector()
    );
    List<String> productInfoImages = getProductInfoImagesSources(
      doc,
      config.getProductInfoImagesSelector()
    );
    
    ItemInfo itemInfo = new ItemInfo(
        title,
        color,
        fabric,
        mainImages,
        productInfoImages
    );
    String folderName = formFolderName(itemInfo);
    createFileWithLink(url, folderName);
    downloadImages(itemInfo, folderName);
  }

  private void createFileWithLink(String url, String folderName) {
    Path linkFile = formTargetFile(folderName, "link.txt");
    
    if (!Files.exists(linkFile)) {
      try {
        Files.copy(new ByteArrayInputStream(url.getBytes(StandardCharsets.UTF_8)), linkFile);
      } catch (IOException e) {
        System.err.println("Failed to write link.txt to " + folderName);
      }
    }
  }

  private List<String> getMainImagesSources(
      Document doc,
      String selector
  ) {
    return doc
      .select(selector)
      .stream()
      .map(this::getDataImageLarge)
      .map(this::fixGoogleStyleUrl)
      .collect(toList());
  }
  
  private List<String> getProductInfoImagesSources(
      Document doc,
      String selector
  ) {
    return doc
      .select(selector)
      .stream()
      .map(this::getDataSrc)
      .map(this::fixGoogleStyleUrl)
      .collect(toList());
  }
  
  private String getDataImageLarge(Element e) {
    return e.attr("data-image-large");
  }
  
  private String getDataSrc(Element e) {
    return e.attr("data-src");
  }
  
  private String fixGoogleStyleUrl(String url) {
    return url.startsWith("//") ? "http:" + url : url;
  }
  
  private void downloadImages(ItemInfo itemInfo, String folderName) {
    System.out.println(folderName);
    
    List<String> imageUrls = ImmutableList.<String>builder()
      .addAll(itemInfo.getMainImages())
      .addAll(itemInfo.getProductInfoImages())
      .build();
    
    downloadImages(imageUrls, folderName);
  }
  
  private String formFolderName(ItemInfo itemInfo) {
    List<String> folderNameParts = new ArrayList<>();
    folderNameParts.add(itemInfo.getTitle());
    
    if (itemInfo.getColor() != null) {
      folderNameParts.add(itemInfo.getColor());
    }
    
    if (itemInfo.getFabric() != null) {
      folderNameParts.add(encodeFileName(itemInfo.getFabric()));
    }
    
    return folderNameParts.stream().map(this::encodeFileName).collect(Collectors.joining(", ")).trim();
  }
  
  private void downloadImages(List<String> imageUrls, String folderName) {
    for (String imageUrl : imageUrls) {
      Path targetFile = formImageTargetFile(folderName, imageUrl);
      
      if (Files.exists(targetFile)) {
        continue;
      }
      
      InputStream resultImageResponse = null;
      
      for (int i = 0; i < N_ATTEMPTS; i++) {
        try {
          resultImageResponse = new BufferedInputStream(new URL(imageUrl).openStream());
        } catch (IOException e1) {
          System.out.println("Can't read " + imageUrl);
          continue;
        }
      }
      
      if (resultImageResponse == null) {
        System.err.println("Failed to read " + imageUrl);
        continue;
      }
      
      try {
        Files.copy(resultImageResponse, targetFile);
      } catch (IOException e) {
        System.err.println("Can't copy " + imageUrl);
      }
    }
  }
  
  private Path formImageTargetFile(String folderName, String imageUrl) {
    return formTargetFile(folderName, getFileName(imageUrl));
  }

  private Path formTargetFile(String folderName, String fileName) {
    Path targetFolder = Paths.get(config.getTargetFolder(), folderName);
    try {
      Files.createDirectories(targetFolder);
    } catch (IOException e1) {
      System.err.println("Can't create folder " + targetFolder);
    }
    
    return Paths.get(config.getTargetFolder(), folderName, fileName);
  }

  private String encodeFileName(String name) {
    return name.replace('/', '+');
  }

  private String getFileName(String url) {
    return url.substring(url.lastIndexOf('/') + 1);
  }

}
