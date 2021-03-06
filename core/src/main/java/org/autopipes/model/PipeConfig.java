package org.autopipes.model;

import java.util.Map;
/**
 * Spring singleton bean containing application-wide parameters.
 * @author janh
 *
 */
public class PipeConfig {
	
	// lookup table which matches block names with various head types
	private Map<String, AreaBody.HeadInfo> headLookup;
	
	// parameters used in preparing rendering text for AutoCAD
    private int textSideShift;
    private int textVerticalUpShift1;  // main label
    private int textVerticalDownShift1; // main span
    private int textVerticalDownShift2; // main span and cut size
    private int textVerticalUpShift2_5; // branch 5in resolution
    private int textVerticalUpShift2_7; // branch 7in resolution
    private int jumpTextShift; // jump
    private double jumpTextDirection; // in multiples of PI
    private int textWidth;
    private double errorTilt;
    
    private String stdBlockName(final String blockName){
    	return blockName.replace(" ", "-").toLowerCase();
    }
    public boolean isReducerBlock(String blockName){
    	String stdName = stdBlockName(blockName);
    	return stdName.indexOf("reducer") >= 0;
    }
    public boolean isKnownBlockName(String blockName){
    	if(blockName == null){
    		return false;
    	}
    	if(blockName.equalsIgnoreCase("AREA-CONFIG")){
    		 return true;
    	}
    	String stdName = stdBlockName(blockName);
    	if(stdName.indexOf("reducer") >= 0){
    		return true;
    	}
    	if(getHeadLookup().containsKey(stdName)){
    		return true;
    	}
    	return false;
    }
    public AreaBody.HeadInfo lookupHeadTemplate(String blockName){
		return blockName == null ? null : getHeadLookup().get(stdBlockName(blockName));
    }

	public Map<String, AreaBody.HeadInfo> getHeadLookup() {
		return headLookup;
	}

	public void setHeadLookup(final Map<String, AreaBody.HeadInfo> headLookup) {
		this.headLookup = headLookup;
	}
	public int getTextSideShift() {
		return textSideShift;
	}

	public void setTextSideShift(final int textSideShift) {
		this.textSideShift = textSideShift;
	}

	public int getTextVerticalUpShift1() {
		return textVerticalUpShift1;
	}

	public void setTextVerticalUpShift1(final int textVerticalUpShift1) {
		this.textVerticalUpShift1 = textVerticalUpShift1;
	}
	public int getTextVerticalDownShift1() {
		return textVerticalDownShift1;
	}
	public void setTextVerticalDownShift1(final int textVerticalDownShift1) {
		this.textVerticalDownShift1 = textVerticalDownShift1;
	}
	public int getTextVerticalDownShift2() {
		return textVerticalDownShift2;
	}
	public void setTextVerticalDownShift2(final int textVerticalDownShift2) {
		this.textVerticalDownShift2 = textVerticalDownShift2;
	}
	public int getTextVerticalUpShift2_5() {
		return textVerticalUpShift2_5;
	}
	public void setTextVerticalUpShift2_5(final int textVerticalUpShift2_5) {
		this.textVerticalUpShift2_5 = textVerticalUpShift2_5;
	}
	public int getTextVerticalUpShift2_7() {
		return textVerticalUpShift2_7;
	}
	public void setTextVerticalUpShift2_7(final int textVerticalUpShift2_7) {
		this.textVerticalUpShift2_7 = textVerticalUpShift2_7;
	}
	public int getTextWidth() {
		return textWidth;
	}
	public void setTextWidth(final int textWidth) {
		this.textWidth = textWidth;
	}
	public double getErrorTilt() {
		return errorTilt;
	}
	public void setErrorTilt(final double errorTilt) {
		this.errorTilt = errorTilt;
	}
	public int getJumpTextShift() {
		return jumpTextShift;
	}
	public void setJumpTextShift(final int jumpTextShift) {
		this.jumpTextShift = jumpTextShift;
	}
	public double getJumpTextDirection() {
		return jumpTextDirection;
	}
	public void setJumpTextDirection(final double jumpTextDirection) {
		this.jumpTextDirection = jumpTextDirection;
	}
}
