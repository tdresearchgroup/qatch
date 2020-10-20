package miltos.diploma;

import java.io.File;

/** 
 * This class is responsible for importing all the results that the
 * BenchmarkAnalyzer exported in the fixed results directory.
 * 
 * Typically it does the following:
 * 	1. It searches the results directory for project results
 * 	2. For each folder it creates a Project object 
 *  3. It calls the pmd and ckjm importers in order to import
 *     the results of each project and stores them in the
 *     corresponding fields of the Project object
 *  4. It returns an object of type BenchmarkProjects. This 
 *     object is simply a Vector of Project objects containing 
 *     the imported project results. 
 * 
 * @author Miltos
 *
 */

public class BenchmarkResultImporter {
	
	/**
	 * The method that implements the whole functionality of this class.
	 */
	public BenchmarkProjects importResults(String path){
		
		//Create an empty BenchmarkProject object
		BenchmarkProjects projects = new BenchmarkProjects();
		
		//Create a simple PMD and CKJM Result Importers 
		PMDResultsImporter pmdImporter = new PMDResultsImporter();
		CKJMResultsImporter ckjmImporter = new CKJMResultsImporter();
		
		//Create a file that represents the results directory 
		System.out.println("Analysis Path : " + path);
		File resultsDir = new File(path);
		
		//Get a list of the folders that are places inside the result directory
		File[] projectDirs = resultsDir.listFiles();
		
		//Import the results
		//For each folder found in the results folder do...
		double progress = 0;
		for(File projectDir : projectDirs){
			
			//Print the progress to the console
			//TODO: Remove this print
			System.out.print("* Progress : " + (int) (progress/projectDirs.length * 100) + " %\r");
			
			//Create a new Project object and set its parent folder path
			Project project = new Project(projectDir.getName());
			project.setPath(projectDir.getAbsolutePath());
			
			//For each result file placed in the current folder do...
			File[] results = projectDir.listFiles();
			
			for(File resultFile : results){
				//Check if it is a ckjm result file
				if(!resultFile.getName().contains("ckjm")){
					//Import the issues found in this file and add them to the Project's IssueSet vector
					project.addIssueSet(pmdImporter.parseIssues(resultFile.getAbsolutePath()));
				}else{
					//Import the metrics found in the ckjm result file and store them in the Project's metrics field
					project.setMetrics(ckjmImporter.parseMetrics(resultFile.getAbsolutePath()));
				}
			}
			//Add the project to the BenchmarkProjects object that will be returned
			projects.addProject(project);	
			
			//Increment the progress counter
			progress++;
		}
		System.out.print("* Progress : " + (int) (progress/projectDirs.length * 100) + " %\r");
		
		//Return the projects
		return projects;
	}
}
