package miltos.diploma.characteristics;

import java.util.Iterator;
import java.util.Vector;

import miltos.diploma.Property;
import miltos.diploma.PropertySet;

/**
 * This class represents a quality characteristic of the 
 * Quality Model that is used in order to evaluate a 
 * project or a set of projects.
 * 
 * @author Miltos
 *
 */
public class Characteristic {
	
	private String name;				//The name of the characteristic
	private String standard;			//The standard from which this characteristic derives 
	private String description;			//A brief description of the characteristic
	private Vector<Double> weights;	    //The vector holding the weights for the evaluation of the characteristic.

	private double eval;				//The quality score of this characteristic (derives from the weighted average of the eval fields of the QM's properties)
	

	/*
	 * Constructors...
	 */
	public Characteristic(){
		this.name = "";
		this.description = "";
		this.standard = "";
		this.weights = new Vector<>();
	}
	
	public Characteristic(String name, String standard, String description){
		this.name = name;
		this.description = standard;
		this.standard = description;
		this.weights = new Vector<>();
	}
	
	public Characteristic(String name, String standard, String description, Vector<Double> weights){
		this.name = name;
		this.description = standard;
		this.standard = description;
		this.weights = weights;
	}
	
	/*
	 * Setters and Getters...
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStandard() {
		return standard;
	}

	public void setStandard(String standard) {
		this.standard = standard;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String desription) {
		this.description = desription;
	}

	public Vector<Double> getWeights() {
		return weights;
	}

	public void setWeights(Vector<Double> weights) {
		this.weights = weights;
	}
	
	public double getEval() {
		return eval;
	}

	public void setEval(double eval) {
		this.eval = eval;
	}

	/*
	 * Overridden Vector's basic methods
	 */

	public void addWeight(Double weight){
		weights.add(weight);
	}
	
	public void addWeight(int index, Double weight){
		weights.add(index, weight);
	}
	
	public void clearProperties(){
		weights.clear();
	}
	
	public boolean containsWeight(Double weight){
		return weights.contains(weight);	
	}
	
	public Double get(int index){
		return weights.get(index);
	}
	
	public boolean isEmpty(){
		return weights.isEmpty();
	}
	
	public Iterator<Double> iterator(){
		return weights.iterator();
	}
	
	public int indexOfWeight(Double weight){
		return weights.indexOf(weight);
	}
	
	public void removeWeight(int index){
		weights.remove(index);
	}
	
	public void removeWeight(Double weight){
		weights.remove(weight);
	}
	
	public int size(){
		return weights.size();
	}
	
	public Double[] toArray(){
		return (Double[]) weights.toArray();
	}
	
	public String toString(){
		return weights.toString();
	}

	/**
	 * This method is used in order to calculate the eval field of this Characteristic
	 * object, based
	 *  1. on the eval fields of the properties that have an impact on this
	 *     characteristic and 
	 *  2. the weights that quantify those impacts.
	 * 
	 * Typically, it calculates the weighted average of the values of the eval fields 
	 * of the project properties and stores it to the eval field of this characteristic.
	 * 
	 * ATTENTION:
	 *  - The order in which the weights are placed inside the weight vector corresponds 
	 *    to the order of the Properties of the Quality Model.
	 */
	public void evaluate(PropertySet properties){
		double sum = 0;
		for(int i = 0; i < weights.size(); i++){
			sum += properties.get(i).getEval() * weights.get(i).doubleValue();
		}
		this.eval = sum;
	}
	
	//TODO: Deep Cloning - Check PropertySet class (and Property, Measure)
	@Override
	protected Object clone() throws CloneNotSupportedException {
	    Characteristic cloned = new Characteristic();
	    cloned.setDescription(this.description);
	    cloned.setName(this.name);
	    cloned.setStandard(this.standard);
	    for(int i = 0; i < this.weights.size(); i++){
	    	cloned.weights.add((Double) this.getWeights().get(i));
	    }
		return cloned;
	}

	
}
