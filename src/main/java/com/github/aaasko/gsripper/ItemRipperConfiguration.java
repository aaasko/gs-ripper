package com.github.aaasko.gsripper;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

public class ItemRipperConfiguration {

  private final String titleSelector;
  private final List<String> colorSelectors;
  private final String fabricNameSelector;
	private final String mainImagesSelector;
	private final Function<String, String> mainImageSrcTransformer;
	private final String productInfoImagesSelector;
	private final Function<String, String> productInfoImageTransformer;
  private final String targetFolder;

	public ItemRipperConfiguration(
	    String titleSelector,
	    List<String> colorSelectors,
	    String fabricNameSelector,
			String mainImagesSelector,
			Function<String, String> mainImageSrcTransformer,
			String productInfoImagesSelector,
			Function<String, String> productInfoImageTransformer,
			String targetFolder) {
		this.titleSelector = titleSelector;
    this.colorSelectors = ImmutableList.copyOf(colorSelectors);
    this.fabricNameSelector = fabricNameSelector;
    this.mainImagesSelector = mainImagesSelector;
		this.mainImageSrcTransformer = mainImageSrcTransformer;
		this.productInfoImagesSelector = productInfoImagesSelector;
		this.productInfoImageTransformer = productInfoImageTransformer;
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
	
	public String getMainImagesSelector() {
		return mainImagesSelector;
	}
	
	public Function<String, String> getMainImageSrcTransformer() {
		return mainImageSrcTransformer;
	}
	
	public String getProductInfoImagesSelector() {
		return productInfoImagesSelector;
	}
	
	public Function<String, String> getProductInfoImageTransformer() {
    return productInfoImageTransformer;
  }
	
	public String getTargetFolder() {
    return targetFolder;
  }
	
}
