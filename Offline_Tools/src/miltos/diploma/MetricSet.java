package miltos.diploma;

import java.util.Iterator;
import java.util.Vector;
/**
 * This class represents a set of metrics found in a result.xml
 * file of CKJM.
 * 
 * Typically, it is a Vector of Metrics objects. Each object is
 * a bundle of metrics for a specific class of the project.
 * 
 * It is the equivalent of IssueSet. However, here we pay attention
 * on the level of the analysis.
 * 
 * @author Miltos
 *
 */
public class MetricSet {
	/*
	 * There is no need of setting a Name. It is a collection of
	 * all the metrics available  for each class of a project.
	 */
	
	Vector<Metrics> metricSet;
	
	//Constructors
	public MetricSet(){
		metricSet = new Vector<>();
	}

	
	//Setters and Getters
	public Vector<Metrics> getMetricSet() {
		return metricSet;
	}

	public void setMetricSet(Vector<Metrics> metricSet) {
		this.metricSet = metricSet;
	}
	
	//Extra methods - Used in order to avoid train expressions
	public void addMetrics(Metrics metrics){
		metricSet.add(metrics);
	}
	
	public void addMetrics(int index, Metrics metrics){
		metricSet.add(index, metrics);
	}
	
	public void clearMetrics(){
		metricSet.clear();
	}

	public boolean containsMetrics(Metrics metrics){
		return metricSet.contains(metrics);	
	}
	
	public Metrics get(int index){
		return metricSet.get(index);
	}
	
	public boolean isEmpty(){
		return metricSet.isEmpty();
	}
	
	public Iterator<Metrics> iterator(){
		return metricSet.iterator();
	}
	
	public int indexOfMetrics(Metrics metrics){
		return metricSet.indexOf(metrics);
	}
	
	public void removeMetrics(int index){
		metricSet.remove(index);
	}

	public void removeMetrics(Metrics metrics){
		metricSet.remove(metrics);
	}
	
	public int size(){
		return metricSet.size();
	}
	
	public Metrics[] toArray(){
		return (Metrics[]) metricSet.toArray();
	}
	
	public String toString(){
		return metricSet.toString();
	}
	

}
