package com.github.aaasko.gsripper;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class ItemRipperConfiguration {

  private final String titleSelector;
  private final List<String> colorSelectors;
  private final String fabricNameSelector;
  private final String mainImageLinksSelector;
  private final String productInfoImagesSelector;
  private final String targetFolder;

  public ItemRipperConfiguration(
    String titleSelector,
    List<String> colorSelectors,
    String fabricNameSelector,
    String mainImageLinksSelector,
    String productInfoImagesSelector,
    String targetFolder
  ) {
    this.titleSelector = titleSelector;
    this.colorSelectors = ImmutableList.copyOf(colorSelectors);
    this.fabricNameSelector = fabricNameSelector;
    this.mainImageLinksSelector = mainImageLinksSelector;
    this.productInfoImagesSelector = productInfoImagesSelector;
    this.targetFolder = targetFolder;
  }

  public String getTitleSelector() {
    return titleSelector;
  }

  public List<String> getColorSelectors() {
    return colorSelectors;
  }

  public String getFabricNameSelector() {
    return fabricNameSelector;
  }

  public String getMainImageLinksSelector() {
    return mainImageLinksSelector;
  }

  public String getProductInfoImagesSelector() {
    return productInfoImagesSelector;
  }

  public String getTargetFolder() {
    return targetFolder;
  }

}
