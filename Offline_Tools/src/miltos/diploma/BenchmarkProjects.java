package miltos.diploma;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

/**
 * This class represent a set of projects that will be evaluated.
 * It is just a Vector of Project objects.
 * 
 * @author Miltos
 *
 */

public class BenchmarkProjects {

	//A vector of Project objects
	Vector<Project> projects;

	/**
	 * The constructor method of the class.
	 */
	
	public BenchmarkProjects(){
		projects = new Vector<>();
	}

	/**
	 * Setters and getters.
     */
	
	public Vector<Project> getProjects() {
		return projects;
	}

	public void setProjects(Vector<Project> projects) {
		this.projects = projects;
	}

	
	/**
	 * Adds a Project object in the project vector.
	 */
	public void addProject(Project project){
		this.projects.add(project);
	}
	
	/**
	 * Returns the Project object placed in the index position
	 * of project vector.
	 */
	public Project getProject(int index){
		return projects.get(index);
	}
	
	/**
	 * Clears the vector that contains the Projects of the Benchmark.
	 */
	public void clearProjects(){
		projects.clear();
	}
	
	/**
	 * Searches for an project and returns the index of
	 * the first occurrence.
	 */
	public boolean containsProject(Project project){
		return projects.contains(project);	
	}
	
	/**
	 * Checks if the project vector is empty.
	 */
	public boolean isEmpty(){
		return projects.isEmpty();
	}
	
	/**
	 * Creates an iterator for the projects Vector.
	 */
	public Iterator<Project> iterator(){
		return projects.iterator();
	}

	/**
	 * Returns the index that a project has inside the projects vector.
     */
	public int indexOfProject(Project project){
		return projects.indexOf(project);
	}

	/**
	 * Removes a certain project from the projects vector.
     */
	public void removeProject(int index){
		projects.remove(index);
	}

	/**
	 * Removes the first occurrence of a desired project from the projects vector.
     */
	public void removeProject(Project project){
		projects.remove(project);
	}

	/**
	 * Returns the size of the projects vector.
     */
	public int size(){
		return projects.size();
	}

	/**
	 * Returns the Array representation of the projects vector.
     */
	public Project[] toArray(){
		return (Project[]) projects.toArray();
	}

	/**
	 * Returns the String representation of the projects vector.
     */
	public String toString(){
		return projects.toString();
	}
	
	/**
	 * Method for freeing memory.
	 * Deletes all the analysis results that were imported previously in the system
	 * for aggregation and evaluation.
	 */
	public void clearIssuesAndMetrics(){
		//For each project of the Benchmark Repository do ...
		for(int i = 0; i < projects.size(); i++){
			//Clear the issues and the metrics of the current project
			projects.get(i).clearIssuesAndMetrics();
		}
	}

	/**
	 * This method is responsible for sorting the projects of the projects vector
	 * in a descending order based on their "eval" field.
	 */
	public void sortProjects(){
		Project.sort("eval", projects);
	}
	
}
