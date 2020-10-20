package miltos.diploma.characteristics;

import java.util.Iterator;
import java.util.Vector;

/**
 * This class represents the total quality index of a project.
 * It is used for the evaluation (quality assessment - estimation) of a 
 * certain project or a benchmark of projects.
 * 
 * @author Miltos
 *
 */
public class Tqi {
	
	//The basic fields of the class
	private double eval;				//The total quality index (total quality score) of a project
	private Vector<Double> weights;		//The weights used for the calculation of the TQI from the evals of the QM's characteristics
	
	//Constructors
	public Tqi(){
		this.weights = new Vector();
	}
	
	public Tqi(Vector<Double> weights){
		this.weights = weights;
	}

	//Setters and Getters
	public double getEval() {
		return eval;
	}

	public void setEval(double eval) {
		this.eval = eval;
	}

	public Vector<Double> getWeights() {
		return weights;
	}

	public void setWeights(Vector<Double> weights) {
		this.weights = weights;
	}
	

	/**
	 * This method calculates the Total Quality Index (TQI), i.e. the total eval (quality score),
	 * of the project based on:
	 * 	- The values of "eval" fields of the characteristics of the model
	 *  - The values of the weights that the model assigns to each characteristic
	 *  
	 *  ATTENTION:
	 *   - The order of the Characteristic objects that are placed in the CharacteristicSet of the
	 *     quality model matters, because the weights are placed in the weights Vector depending on 
	 *     this order!
	 *   - Do not change the auto-generated order neither in the Quality Model xml file nor elsewhere 
	 *     inside the program.
	 * @param characteristics : The CharacteristicSet object with the eval fields calculated received 
	 *                          from the Project object.
	 */
	public void calculateTQI(CharacteristicSet characteristics){
		double sum = 0;
		for(int i = 0; i < characteristics.size(); i++){
			//The number of weights is equal to the number of the model's characteristics
			//The sequence of the characteristics matters!!!!!!
			sum += characteristics.get(i).getEval() * this.weights.get(i).doubleValue();
		}
		this.eval = sum;
	}
	
	
	//Vector methods
	public void addWeight(Double weight){
		weights.add(weight);
	}
	
	public void addWeight(int index, Double weight){
		weights.add(index, weight);
	}
	
	public void clearProperties(){
		weights.clear();
	}
	
	// Returns the index of the first occurrence
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
	
	// Removes the first occurrence
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
	
	//TODO: Deep Cloning - Check PropertySet class (and Property, Measure)
	@Override
	protected Object clone() throws CloneNotSupportedException {
	    Tqi cloned = new Tqi();
	    for(int i = 0; i < this.weights.size(); i++){
	    	cloned.weights.add((Double) this.getWeights().get(i));
	    }
		return cloned;
	}

}
