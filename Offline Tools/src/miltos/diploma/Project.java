package miltos.diploma;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import miltos.diploma.characteristics.CharacteristicSet;
import miltos.diploma.characteristics.Tqi;

/**
 * This class represents a project under evaluation.
 * 
 * Basically, it contains all the data (metrics,
 * violations, properties etc.) that belong to a certain 
 * project.
 *
 * @author Miltos
 *
 */
public class Project{
	
	//Basic Fields
	private String name;
	private String path; //The original path where the sources of the project are stored (with or without the name)
	
	private Vector<IssueSet> issues;
	private MetricSet metrics;

	//Fields for the higher level evaluation of the project
	private PropertySet properties;
	private CharacteristicSet characteristics;
	private Tqi tqi;

	
	/*
	 * The constructor methods of this class.
	 */
	
	public Project(){
		this.issues = new Vector<>();
		this.metrics = new MetricSet();
		this.properties = new PropertySet();
		this.characteristics = new CharacteristicSet();
		this.tqi = new Tqi();
	}
	
	public Project(String name){
		this.name = name;
		this.issues = new Vector<>();
		this.metrics = new MetricSet();
		this.properties = new PropertySet();
		this.characteristics = new CharacteristicSet();
		this.tqi = new Tqi();
	}
	
	
	/*
	 * Getters and setters.
	 */
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public PropertySet getProperties() {
		return properties;
	}

	public void setProperties(PropertySet properties) {
		this.properties = properties;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public Vector<IssueSet> getIssues() {
		return issues;
	}
	
	public void setIssues(Vector<IssueSet> issues) {
		this.issues = issues;
	}
	
	public MetricSet getMetrics() {
		return metrics;
	}
	
	public void setMetrics(MetricSet metrics) {
		this.metrics = metrics;
	}
	
	public CharacteristicSet getCharacteristics() {
		return characteristics;
	}

	public void setCharacteristics(CharacteristicSet characteristics) {
		this.characteristics = characteristics;
	}

	public Tqi getTqi() {
		return tqi;
	}

	public void setTqi(Tqi tqi) {
		this.tqi = tqi;
	}

	//Other
	
	/**
	 * Adds an IssueSet in the issues vector.
	 */
	public void addIssueSet(IssueSet issueSet){
		this.issues.add(issueSet);
	}
	
	/**
	 * Returns the IssueSet placed in the index position
	 * of issues vector.
	 */
	public IssueSet getIssueSet(int index){
		return issues.get(index);
	}
	
	/**
	 * Adds a Metrics object in the metrics vector.
	 */
	public void addMetrics(Metrics m){
		this.metrics.addMetrics(m);
	}
	
	/**
	 * Return the Metrics Object placed in the index 
	 * position of the MetricSet.
	 */
	public Metrics getMetrics(int index){
		return this.metrics.get(index);
	}
	
	
	//Secondary methods
	
	/**
	 * Clears the vector that contains the IssueSets of the Project.
	 */
	public void clearIssues(){
		issues.clear();
	}
	
	/**
	 * Searches for an issueSet and returns the index of
	 * the first occurrence.
	 */
	public boolean containsIssueSet(IssueSet issueSet){
		return issues.contains(issueSet);	
	}
	
	/**
	 * Checks if the issues vector is empty.
	 */
	public boolean isEmpty(){
		return issues.isEmpty();
	}
	
	/**
	 * Creates an iterator for the issuesSet.
	 */
	public Iterator<IssueSet> issueSetIterator(){
		return issues.iterator();
	}
	
	public int indexOfIssueSet(IssueSet issueSet){
		return issues.indexOf(issueSet);
	}
	
	public void removeIssueSet(int index){
		issues.remove(index);
	}
	
	// Removes the first occurrence
	public void removeIssueSet(Issue issueSet){
		issues.remove(issueSet);
	}
	
	public int size(){
		return issues.size();
	}
	
	public IssueSet[] toArray(){
		return (IssueSet[]) issues.toArray();
	}
	
	public String toString(){
		return issues.toString();
	}
	
	public void addProperty(Property property){
		this.properties.getPropertyVector().add(property); 
	}
	
	/**
	 * A method that calculates the total evaluation (TQI) of 
	 * this Project.
	 */
	public void calculateTQI(){
		/*
		 * Just call the calculateTQI() method of the Tqi object in order to
		 * calculate the value of the TQI from the eval field of the model's
		 * characteristics
		 */
		this.tqi.calculateTQI(this.characteristics);
	}
	
	/**
	 * Method for freeing memory. Typically, it deletes the contents of the metrics 
	 * and issues objects of this project. I.e. it deletes the results of the 
	 * static analysis concerning this project.
	 */
	public void clearIssuesAndMetrics(){
		metrics = null;
	   	issues = null;
	}

	/**
	 * This method is responsible for sorting a group of projects according to a desired
	 * field in a descending order. Typically, it is used in order to sort the projects
	 * under evaluation according to their TQI.
     */
	public static void sort(final String field, Vector<Project> projects) {
		
	    Collections.sort(projects, new Comparator<Project>() {
	        @Override
	        public int compare(Project p1, Project p2) {
	            if(field.equals("eval")) {
	                return Double.compare(p1.getTqi().getEval(), p2.getTqi().getEval());
	            }else{
	            	return 1;
	            }
	        }           
	    });
	}


}
