package com.github.aaasko.gsripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
        ".productInfoRow-subtitle:contains(Color) + p",
        ".productInfoRow-title:contains(Fabric) ~ .productInfoRow-subtitle:nth-of-type(2)"
    );
    String fabricNameSelector =
        ".productInfoRow-title:contains(Fabric) + .productInfoRow-subtitle";
 
    String mainImageLinksSelector =
        ".productDetail-thumbnails-list .productDetail-thumbnails-listItem a";
    
    String productInfoImagesSelector = ".productInfoImage";
    
    ItemRipperConfiguration conf = new ItemRipperConfiguration(
        titleSelector,
        colorSelectors,
        fabricNameSelector,
        mainImageLinksSelector,
        productInfoImagesSelector,
        targetFolder
    );
    ItemRipper ripper = new ItemRipper(conf);
    List<String> urls = Files.readAllLines(Paths.get(urlsFile));
    
    for (String url : urls) {
      ripper.run(url);
    }
  }
  
}
