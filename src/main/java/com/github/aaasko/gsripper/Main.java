package com.github.aaasko.gsripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

public class Main {
  
  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      System.out.println("Usage:");
      System.out.println("mvn exec:java -Dexec.mainClass=\"com.github.aaasko.gsripper.Main\" -Dexec.args=\"'C:\\urls.txt' 'C:\\target\\folder'\"");
      return;
    }
    
    String urlsFile = args[0];
    String targetFolder = args[1];
    
    String titleSelector = ".productDetail-productTitle";
    ImmutableList<String> colorSelectors = ImmutableList.of(
        ".colorSelection-name",
        ".productInfoRow-subtitle:contains(Colour) + p",
        ".productInfoRow-subtitle:contains(Color) + p"
    );
    String fabricNameSelector =
        ".productInfoRow-title:contains(Fabric) + .productInfoRow-subtitle";
 
    // thumbnails selector
    String mainImagesSelector =
        ".productDetail-thumbnails-list img.productDetail-thumbnails-image";
    
    // renaming a thumbnail URL to a full image URL
    Function<String, String> mainImageSrcTransformer =
        url -> url.replaceAll("_P4", "_P20");
    
    String productInfoImagesSelector = ".productInfoImage";
    Function<String, String> productInfoImageTransformer =
        url -> url.replaceAll("_P18", "_P20").replaceAll("_P19", "_P20");
    
    ItemRipperConfiguration conf = new ItemRipperConfiguration(
        titleSelector,
        colorSelectors,
        fabricNameSelector,
        mainImagesSelector,
        mainImageSrcTransformer,
        productInfoImagesSelector,
        productInfoImageTransformer,
        targetFolder
    );
    ItemRipper ripper = new ItemRipper(conf);
    List<String> urls = Files.readAllLines(Paths.get(urlsFile));
    
    for (String url : urls) {
      ripper.run(url);
    }
  }
  
}
