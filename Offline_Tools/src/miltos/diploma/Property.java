package miltos.diploma;

import java.math.*;

public class Property implements Cloneable {

	//TODO: Remove this constants - Use the Analyzers constants instead!
	public static final String PMD = "PMD";
	public static final String CKJM = "CKJM";
	
	/* The breakpoint of the Utility Function used for the evaluation of the Property */
	private static final double BREAKPOINT = 0.5;
	
	/* Static constants that belong to this class */
	public static final int THRESHOLDS_NUM = 3;
	
	private String name;//The name of the property
	private String description;//A brief description of the property (optional)
	private double[] thresholds;//The three thresholds of the property metric, needed for the evaluation
	private double eval;//The quality score of the property
	private double profile[];//The profile of this Property (SIG Model for metrics - just violation counter per severity category for PMD)
	private boolean positive;//If this field is true then the metric has a positive impact on the property
	//private String tool;//The tool used for quantifying it (PMD, CKJM etc.)
	
	private Measure measure;
	
	
	public Property(){
		// Just create the thresholds array.
		thresholds = new double[THRESHOLDS_NUM];
		measure = new Measure();
	}
	
	public Property(Measure measure){
		thresholds = new double[THRESHOLDS_NUM];
		this.measure = measure;
	}
	

	public boolean isPositive() {
		return positive;
	}

	public void setPositive(boolean positive) {
		this.positive = positive;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public double[] getThresholds() {
		return thresholds;
	}
	
	public void setThresholds(double[] thresholds) {
		this.thresholds = thresholds;
	}
	
	public double getEval() {
		return eval;
	}
	
	public void setEval(double eval) {
		this.eval = eval;
	}
	
	public double[] getProfile() {
		return profile;
	}

	public void setProfile(double[] profile) {
		this.profile = profile;
	}

	public boolean isMetric(){
		return measure.getType() == Measure.METRIC;
	}
	
	//TODO: Remove - Redundant
	public boolean isFinding(){
		return measure.getType() == Measure.FINDING;
	}
	
	public void setMeasure(Measure measure){
		this.measure = measure;
	}
	
	public Measure getMeasure(){
		return measure;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
	    Property cloned = (Property)super.clone();
	    cloned.setMeasure((Measure)cloned.getMeasure().clone());
	    return cloned;
	}

	
	/**
	 * A method for evaluating a Property object (i.e this property). 
	 * In other words, this method calculates the eval field
	 * of a property based on the values of the following fields:
	 * 
	 * 		- positive   : User defined or received by the QM.xml
	 * 		- normValue  : Calculated by the aggregator.
	 * 		- thresholds : Imported by ThresholdImporter or by the
	 * 					   QM.xml file.
	 * 
	 * Typically, it simulates the Utility Function that is used
	 * for the evaluation of the properties.
	 */
	public void evaluate(){
		/*
		 * Check the sign of the impact that this property has on the total quality
		 * and choose  the monotony of the utility function.
		 */
		
		if(positive){
			//If the metric has a positive impact on quality -> Ascending utility function
			if(this.measure.getNormValue() <= this.thresholds[0]){
				//Lower Group
				this.eval = 0;
			}else if(this.measure.getNormValue() <= this.thresholds[1]){
				//Middle Group
				this.eval = (0.5/(thresholds[1]-thresholds[0]))*(this.getMeasure().getNormValue() - thresholds[0]);
			}else if(this.measure.getNormValue() <= this.thresholds[2]){
				//Upper Group
				this.eval = 1 - (0.5/(thresholds[2]-thresholds[1]))*(thresholds[2] - this.getMeasure().getNormValue());
			}else{
				//Saturation
				this.eval = 1;
			}
		}else{
			//If the metric has a negative impact on quality -> Descending utility function
			if(roundDown4(this.measure.getNormValue()) <= this.thresholds[0]){
				//Lower Group
				this.eval = 1;
			}else if(roundDown4(this.measure.getNormValue()) <= this.thresholds[1]){
				//Middle Group
				this.eval = 1 - (0.5/(thresholds[1]-thresholds[0]))*(this.getMeasure().getNormValue() - thresholds[0]);
			}else if(roundDown4(this.measure.getNormValue()) <= this.thresholds[2]){
				//Upper Group
				this.eval = (0.5/(thresholds[2]-thresholds[1]))*(thresholds[2] - roundDown4(this.getMeasure().getNormValue()));
			}else{
				//Saturation
				this.eval = 0;
			}
		}
	}
	
	/**
	 * A method for keeping only the first four digits
	 * of a double number.
	 * 
	 * Used as a quick fix for precision difference spotted
	 * between the numbers calculated by Java and those received
	 * from R Analysis.
	 */
	//TODO: Find a better way of fixing this issue (i.e. increase the precision in R)
	public static double roundDown4(double d) {
	    return (long) (d * 1e4) / 1e4;
	}

}
