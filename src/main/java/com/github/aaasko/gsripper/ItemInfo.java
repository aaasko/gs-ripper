package com.github.aaasko.gsripper;

import java.util.List;

public class ItemInfo {

  private final String title;
  private final String color;
  private final String fabric;
  private final List<String> mainImages;
  private final List<String> productInfoImages;

  public ItemInfo(String title, String color, String fabric, List<String> mainImages, List<String> productInfoImages) {
    this.title = title;
    this.color = color;
    this.fabric = fabric;
    this.mainImages = mainImages;
    this.productInfoImages = productInfoImages;
  }

  public String getTitle() {
    return title;
  }

  public String getColor() {
    return color;
  }

  public String getFabric() {
    return fabric;
  }

  public List<String> getMainImages() {
    return mainImages;
  }

  public List<String> getProductInfoImages() {
    return productInfoImages;
  }
  
}
