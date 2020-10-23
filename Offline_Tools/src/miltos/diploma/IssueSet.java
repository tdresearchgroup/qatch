package miltos.diploma;

import java.util.Iterator;
import java.util.Vector;
/**
 * A class that represents a set of violations
 * of a certain file (i.e. class) or a whole project in general.
 * 
 * For the benchmarking we do not pay attention to each class separately,
 * but to the total number of the violations for the whole project.
 * 
 * Typically it is a vector of issues. An IssueSet holds all the
 * violations (i.e. issues) of a certain Property (i.e Rule Set)
 * of the total Project.
 *
 * @author Miltos
 *
 */
public class IssueSet {
	
	//Basic Fields
	private String propertyName;
	private Vector<Issue> issues = null;		// An IssueSet is just a Vector of Issue objects

	//TODO: Remove these fields - We will not follow the class level approach
	private String fileName;
	private String filePath;

	//Constructors 
	
	public IssueSet(){
		issues = new Vector<>();
	}
	
	
	//Setters and Getters ...
	
	public String getPropertyName() {
		return propertyName;
	}


	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public String getFilePath() {
		return filePath;
	}


	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	// Redundant : There is a method called setIssuesVector() below!
	public void setIssues(Vector<Issue> issues) {
		this.issues = issues;
	}

	public Vector<Issue> getIssues(){
		return issues;
	}
	
	public void setIssueVector(Vector<Issue> issues){
		this.issues=issues;
	}
	
	public void addIssue(Issue issue){
		issues.add(issue);
	}
	
	public void addIssue(int index, Issue issue){
		issues.add(index, issue);
	}
	
	public void clearIssues(){
		issues.clear();
	}

	public boolean containsIssue(Issue issue){
		return issues.contains(issue);	
	}
	
	public Issue get(int index){
		return issues.get(index);
	}
	
	public boolean isEmpty(){
		return issues.isEmpty();
	}
	
	public Iterator<Issue> iterator(){
		return issues.iterator();
	}
	
	public int indexOfIssue(Issue issue){
		return issues.indexOf(issue);
	}
	
	public void removeIssue(int index){
		issues.remove(index);
	}

	public void removeIssue(Issue issue){
		issues.remove(issue);
	}
	
	public int size(){
		return issues.size();
	}
	
	public Issue[] toArray(){
		return (Issue[]) issues.toArray();
	}
	
	public String toString(){
		return issues.toString();
	}
}
