package miltos.diploma.characteristics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.google.gson.Gson;

import miltos.diploma.AbstractAnalyzer;
import miltos.diploma.BenchmarkAggregator;
import miltos.diploma.BenchmarkAnalysisExporter;
import miltos.diploma.BenchmarkAnalyzer;
import miltos.diploma.BenchmarkEvaluator;
import miltos.diploma.BenchmarkProjects;
import miltos.diploma.BenchmarkResultImporter;
import miltos.diploma.EvaluationResultsExporter;
import miltos.diploma.OptimalParallelBenchmarkAnalyzer;
import miltos.diploma.ProgressDemo;
import miltos.diploma.Project;
import miltos.diploma.ProjectRanker;

import miltos.diploma.Property;
import miltos.diploma.PropertySet;


/**
 * This class contains the main script for evaluating all the projects
 * of a workspace based on a desired Quality Model.
 * 
 * It gives you the option to analyze the project or to use the results 
 * of the previous analysis.
 * 
 * @author Miltos
 *
 */

public class TestEvaluationProcess {
	
	//User defined fields
	public static String workspacePath;
	public static String qmPath;
	public static String resPath;
	public static boolean includeInspectRes = false;
	public static boolean staticAnalysis = false;
	public static boolean serialAnalysis = true;
	public static boolean keepResults = false;
	public static boolean useBenchmarksResultDir = false;
	public static String analysisResPath;
	
	public static void main(String[] args) throws CloneNotSupportedException, InterruptedException{
		
		System.out.println("\n\n******************************  Workspace Evaluator *******************************");
		System.out.println();
		
		//Get the default configuration
		getConfig();
		
		//Receive the appropriate configuration from the user through terminal
		getUserInputs();
		
		/*
		 * Step 0 : Load the desired Quality Model from the appropriate XML file 
		 */
		
		System.out.println("**************** STEP 0 : Quality Model Loader ***********************");
		System.out.println("*");
		System.out.println("* Loading the desired Quality Model...");
		System.out.println("* Please wait...");
		System.out.println("*");
		
		//Create an empty QualityModel object 
		QualityModel qualityModel = new QualityModel();
		
		//Instantiate the Quality Model importer
		QualityModelLoader qmImporter = new QualityModelLoader(qmPath);
		qualityModel = qmImporter.importQualityModel();
		
		System.out.println("* Quality Model successfully loaded..!");
		
		/*
		 * Step 1: Analyze all the projects of your workspace
		 */
		
		//Check if the user wants to analyze the projects...
		
		if(useBenchmarksResultDir){
			analysisResPath = BenchmarkAnalyzer.BENCH_RESULT_PATH;
		}else{
			analysisResPath = BenchmarkAnalyzer.WORKSPACE_RESULT_PATH;
		}
		
		if(staticAnalysis){
			
			//Check if the results directory exists and if not create it. Clear it's contents as well.
			if(useBenchmarksResultDir){
				checkCreateClearDirectory(BenchmarkAnalyzer.BENCH_RESULT_PATH);
			}else{
				checkCreateClearDirectory(BenchmarkAnalyzer.WORKSPACE_RESULT_PATH);
			}
			
			
			//Snippet copied from the generic script of the Model Designer
			System.out.println("\n**************** STEP 1 : Workspace Analyzer *************************");
			System.out.println("*");
			System.out.println("* Analyzing the projects of your workspace");
			System.out.println("* Please wait...");
			System.out.println("*");
		
			
			//TODO: Remove the time calculation
			long startTime = System.currentTimeMillis();
			
			//TODO: Create a parent Benchmark Analyzer so that you can use it to move the common commands outside the if statement
			if(serialAnalysis){
				//Instantiate the serial benchmark analyzer
				BenchmarkAnalyzer benchmarkAnal = new BenchmarkAnalyzer();
				
				//Set the repository and the desired properties to the benchmark analyzer
				benchmarkAnal.setBenchRepoPath(workspacePath);
				benchmarkAnal.setProperties(qualityModel.getProperties());
				benchmarkAnal.setResultsPath(analysisResPath);
				
				//Start the analysis of the workspace
				benchmarkAnal.analyzeBenchmarkRepo();
			}else{
				//Instantiate the parallel benchmark analyzer
				OptimalParallelBenchmarkAnalyzer benchmarkAnal = new OptimalParallelBenchmarkAnalyzer();
				
				//Set the repository and the desired properties to the benchmark analyzer
				benchmarkAnal.setBenchRepoPath(workspacePath);
				benchmarkAnal.setProperties(qualityModel.getProperties());
				benchmarkAnal.setResultsPath(analysisResPath);
				
				//Start the analysis of the workspace
				benchmarkAnal.analyzeBenchmarkRepo();
			}
			
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			
			//Print some messages to the user
			System.out.println("*");
			System.out.println("* The analysis is finished..!");
			System.out.println("* You can find the results at : " + new File(analysisResPath).getAbsolutePath());
			System.out.println("* Total time is : " + totalTime + " ms");
			System.out.println();
		}
		
		/*
		 * Step 2: Import the results of the static analysis tools
		 */
		
		System.out.println("\n**************** STEP 2 : Benchmark Importer *************************");
		System.out.println("*");
		System.out.println("* Importing the results of the analysis...");
		System.out.println("* Please wait...");
		System.out.println("*");
		
		// Create an empty BenchmarkImporter
		BenchmarkResultImporter benchmarkImporter = new BenchmarkResultImporter();
		
		// Start importing all the results of the static analysis found in the results folder
		BenchmarkProjects projects = new BenchmarkProjects();
		if(useBenchmarksResultDir){
			projects = benchmarkImporter.importResults(BenchmarkAnalyzer.BENCH_RESULT_PATH);
		}else{
			projects = benchmarkImporter.importResults(BenchmarkAnalyzer.WORKSPACE_RESULT_PATH);
		}
		// Print some informative messages to the console
		System.out.println("*");
		System.out.println("* The results are successfully imported..! ");
		
		/*
		 * Step 3 : Aggregate the results and Evaluate the properties
		 */
		
		System.out.println("\n**************** STEP 3: Aggregation Process *************************");
		System.out.println("*");
		System.out.println("* Aggregating the results of each project...");
		System.out.println("* I.e. Calculating the normalized values of their properties...");
		System.out.println("* Please wait...");
		System.out.println("*");
		
		//Create an empty BenchmarkAggregator and aggregate the metrics of the project
		BenchmarkAggregator benchAggregator = new BenchmarkAggregator();
		benchAggregator.aggregateProjects(projects, qualityModel.getProperties());
		
		System.out.println("*");
		System.out.println("* Aggregation process finished..!");
		
		/*
		 * STEP 4 : Evaluate all the benchmark projects against their thresholds. 
		 */
		
		System.out.println("\n**************** STEP 4: Properties Evaluation ***********************");
		System.out.println("*");
		System.out.println("* Evaluating the projects' properties against the calculated thresholds...");
		System.out.println("* This will take a while...");
		System.out.println("*");
		
		//Create an empty Benchmark Evaluator
		BenchmarkEvaluator benchEval = new BenchmarkEvaluator();
		benchEval.evaluateProjects(projects);
		
		System.out.println("*");
		System.out.println("* The projects' properties are successfully evaluated..!");
		
		/*
		 * Step 5 : Evaluate characteristics 
		 */
		
		System.out.println("\n**************** STEP 5: Characteristics Evaluation ******************");
		System.out.println("*");
		System.out.println("* Evaluating the projects' characteristics based on the eval values of their properties...");
		System.out.println("* This will take a while...");
		System.out.println("*");
		
		BenchmarkCharacteristicEvaluator benchCharEval = new BenchmarkCharacteristicEvaluator();
		benchCharEval.evaluateProjects(projects, qualityModel.getCharacteristics());
		
		System.out.println("*");
		System.out.println("* The projects' characteristics are successfully evaluated..!");
		
		/*
		 * Step 6 : Calculate the TQI of the projects
		 */
		
		System.out.println("\n**************** STEP 6: TQI Calculation *****************************");
		System.out.println("*");
		System.out.println("* Calgculating the TQI of each project ...");
		System.out.println("* This will take a while...");
		System.out.println("*");
		
		BenchmarkTqiEvaluator tqiEvaluator = new BenchmarkTqiEvaluator();
		tqiEvaluator.evaluateProjects(projects, qualityModel.getTqi());
		
		System.out.println("*");
		System.out.println("* The TQI of the projects successfully calculated..!");
		
		/*
		 * Step 8 : Export the projects data and properties to a json file 
		 */	
		
		System.out.println("\n**************** STEP 7: Exporting Evaluation Results ****************");
		System.out.println("*");
		System.out.println("* Exporting the results of the project evaluation...");
		System.out.println("* This will take a while...");
		System.out.println("*");
		
		
		//Clear Issues and metrics for more lightweight solution
		if(!includeInspectRes){
			
			Iterator<Project> iterator = projects.iterator();
			while(iterator.hasNext()){
				Project project = iterator.next();
				project.clearIssuesAndMetrics();
			}
			
		}

		//Export the evaluation results
		EvaluationResultsExporter.exportProjectsToJson(projects, new File(resPath + "/evaluationResults.json").getAbsolutePath());
		EvaluationResultsExporter.exportPropValuesAndTqiToXls(projects, new File(resPath + "/evaluationResults.xls").getAbsolutePath(), true, true, true, true, true);
	
		//Export the results to the predefined directory as well
		checkCreateClearDirectory(EvaluationResultsExporter.WORKSPACE_RESULT_PATH);
		EvaluationResultsExporter.exportProjectsToJson(projects, new File(EvaluationResultsExporter.WORKSPACE_RESULT_PATH + "/evaluationResults.json").getAbsolutePath());
		EvaluationResultsExporter.exportPropValuesAndTqiToXls(projects, new File(EvaluationResultsExporter.WORKSPACE_RESULT_PATH + "/evaluationResults.xls").getAbsolutePath(), true, true, true, true, true);		
		
		//Export a project ranking...
		ProjectRanker.rank(projects);
		EvaluationResultsExporter.exportProjectsToJson(projects, new File(resPath + "/rankedEvaluationResults.json").getAbsolutePath());
		EvaluationResultsExporter.exportProjectsToJson(projects, new File(EvaluationResultsExporter.WORKSPACE_RESULT_PATH + "/rankedEvaluationResults.json").getAbsolutePath());
		
		
		
		System.out.println("* Results successfully exported..!");
		System.out.println("* You can find the results at        : " + new File(resPath).getAbsolutePath());
		System.out.println("* You can find the ranked results at : " + new File(EvaluationResultsExporter.WORKSPACE_RESULT_PATH).getAbsolutePath());
	}

	/**
	 * A method that parses the configuration xml file in order to set up 
	 * some fixed user defined parameters.
	 */
	public static void getConfig(){
		try {	
			// Import the desired xml configuration file
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new File(System.getProperty("user.dir") + "/config.xml").getAbsoluteFile());
			Element root = (Element) doc.getRootElement();
		
			// Create a list of all the its elements
			List<Element> elements = root.getChildren();
							
			//Iterate through the elements of the file and parse their values
			for(Element el : elements){
				if("keepResults".equalsIgnoreCase(el.getName())){
					keepResults =Boolean.parseBoolean(el.getText()); 
				}else if("useBenchmarksResultsDir".equalsIgnoreCase(el.getName())){
					useBenchmarksResultDir = Boolean.parseBoolean(el.getText()); 
				}else if("threadDepth".equalsIgnoreCase(el.getName())){
					OptimalParallelBenchmarkAnalyzer.MAX_DEPTH = Integer.parseInt(el.getText());
				}
			}
		}  catch (JDOMException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * A method that checks the predefined directory structure, creates the 
	 * directory tree if it doesn't exists and clears it's contents for the
	 * new analysis (optional).
	 */
	
	public static void checkCreateClearDirectory(String path){
		
		File dir = new File(path);
		
		//Check if the directory exists
		if(!dir.isDirectory() || !dir.exists()){
			dir.mkdirs();
		}
		
		//Clear previous results
		if(!keepResults){
			try {
				FileUtils.cleanDirectory(dir);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}		
	}
	/**
	 * A method that implements the CMD User Interface of the script.
	 * TODO: Add more checks - e.g. Check the validity of the xml file
	 */
	public static void getUserInputs(){
		
		//Create a Scanner object in order to read data from the command line
		Scanner console = new Scanner(System.in);
		
		boolean exists = false;
		boolean problem = false;
		boolean hasDirectory = false;
		
		File dir = null;
		while(!exists){
			System.out.println("\nPlease provide the path of the desired workspace that you would like to assess the \nquality of its projects : ");
			
			//Read the user's input
			dir = new File(console.nextLine());
			System.out.println("\nValidating the repository...");
			System.out.println("Please wait...");
			
			//Check if the directory exists and if it contains the appropriate files
			if(dir.exists() && dir.isDirectory()){
				
				//Get a list of files of this directory
				File[] projects = dir.listFiles();
				
				//Check if the desired repository is empty 
				if(projects.length == 0){
					//If yes, ask for a new path
					System.out.println("The desired repository is empty..!");
					continue;
				}
				
				//Create scanners and search for java and class or jar files
				DirectoryScanner javaFileScanner = new DirectoryScanner();
				DirectoryScanner classFileScanner = new DirectoryScanner();
				DirectoryScanner jarFilesScanner = new DirectoryScanner();
				String[] javaFiles = null;
				String[] classFiles = null;
				String[] jarFiles = null;
				
				//Check each file if it contains the appropriate files for the analysis
				problem = false;
				
				double progress = 0;
				System.out.println("");
				for(File project : projects){
					
					if(project.isDirectory()){
						hasDirectory = true;
					}
					//If this file is not a directory continue to the next one
					if(!project.isDirectory()){
						continue;
					}
					
					System.out.print("* Progress : " + (int) (progress/projects.length * 100) + " %\r");
					//Scan the current directory for the files needed for the analysis
					javaFileScanner.setIncludes(new String[]{"**/*.java"});
					javaFileScanner.setBasedir(project.getAbsolutePath());
					javaFileScanner.setCaseSensitive(false);
					javaFileScanner.scan();
					javaFiles = javaFileScanner.getIncludedFiles();
					
					classFileScanner.setIncludes(new String[]{"**/*.class"});
					classFileScanner.setBasedir(project.getAbsolutePath());
					classFileScanner.setCaseSensitive(false);
					classFileScanner.scan();
					classFiles = classFileScanner.getIncludedFiles();
					
					jarFilesScanner.setIncludes(new String[]{"**/*.jar"});
					jarFilesScanner.setBasedir(project.getAbsolutePath());
					jarFilesScanner.setCaseSensitive(false);
					jarFilesScanner.scan();
					jarFiles = jarFilesScanner.getIncludedFiles();
					
					//Check if this directory contains java and class or jar files...
					if(javaFiles.length == 0){
						System.out.println("There are no java files inside the directory : " + project.getName());
						problem = true;
						break;
					}else if(classFiles.length == 0 || classFiles.length == 0){
						System.out.println("There are no class or jar files inside the directory : " + project.getName());
						problem = true;
						break;
					}
					
					progress++;
				}
				
				System.out.print("* Progress : " + (int) (progress/projects.length * 100) + " %\r");
				System.out.println("");
				
				//Check if the for loop finished without any violation
				if(!problem){
						exists = true;
						workspacePath = dir.getAbsolutePath();
				}
				
				if(!hasDirectory){
					exists = false;
					System.out.println("there isn't any project in the desired directory");
				}
				
			}else{
				System.out.println("The desired directory doesn't exist..!");
			}
		}
		System.out.println("Repository successfully validated..!");
		File qmXMLFile = null;
		exists = false;
		while(!exists){
			System.out.println("\nPlease provide the path of the XML file that contains the desired Quality Model : ");
			qmXMLFile = new File(console.nextLine());
			if(!qmXMLFile.exists() || !qmXMLFile.isFile()){
				System.out.println("The desired file doesn't exist..!");
			}else if(!qmXMLFile.getName().contains(".xml")){
				System.out.println("The desired file is not an XML file..!");
			}else{
				qmPath = qmXMLFile.getAbsolutePath();
				exists = true;
			}
		}
		
		File resDirPath = null;
		exists = false;
		while(!exists){
			System.out.println("\nPlease provide the path where you would like the results of the analysis to be \nplaced : ");
			resDirPath = new File(console.nextLine());
			if(resDirPath.exists() && resDirPath.isDirectory()){
				resPath = resDirPath.getAbsolutePath();
				exists = true;
			}else{
				System.out.println("The destination folder doesn't exist..!");
			}
		}
		
		String answer = "";
		boolean wrongAnswer = false;
		boolean asked = false;
		while((!"yes".equalsIgnoreCase(answer)) &&! ("no".equalsIgnoreCase(answer))){
			if(!wrongAnswer && !asked){
				System.out.println("\nWould you like to include the inspection results as well? (yes/no): ");
				asked = true;
			}
		    answer = console.nextLine();
		    
		    if("yes".equalsIgnoreCase(answer)){
				includeInspectRes = true;
			}else if("no".equalsIgnoreCase(answer)){
				includeInspectRes = false;
			}else{
				System.out.println("\nWrong answer. Please answer with yes or no : ");
				wrongAnswer = true;
			} 
		}
			
		answer = "";
		wrongAnswer = false;
		asked = false;
		while((!"yes".equalsIgnoreCase(answer)) &&! ("no".equalsIgnoreCase(answer))){
			if(!wrongAnswer && !asked){
				System.out.println("\nWould you like to run a new static analysis ? (yes/no): ");
				System.out.println("(If no then the results of the previous analysis will be used for the project's \nevaluation) ");
				asked = true;
			}
		    answer = console.nextLine();
		    
		    if("yes".equalsIgnoreCase(answer)){
		    	staticAnalysis = true;
			}else if("no".equalsIgnoreCase(answer)){
				staticAnalysis = false;
			}else{
				System.out.println("\nWrong answer. Please answer with yes or no : ");
			}
		    
		  //If the user doesn't want a new analysis check if there are any results  
		    if("no".equalsIgnoreCase(answer)){
		    	String path = null;
		    	if(useBenchmarksResultDir){
					path = BenchmarkAnalyzer.BENCH_RESULT_PATH;
				}else{
					path = BenchmarkAnalyzer.WORKSPACE_RESULT_PATH;
				}
		    	File resDir = new File(path);
		    	if(!resDir.isDirectory() || !resDir.exists() || resDir.listFiles().length == 0 ){
		    		System.out.println("\nThe aren't any previous results..! ");
		    		answer = "";
		    	}
		    }
		    
		}
		
		if(staticAnalysis){
			
			answer = "";
			wrongAnswer = false;
			asked = false;
			while((!"serial".equalsIgnoreCase(answer)) && (!"parallel".equalsIgnoreCase(answer))){
				if(!wrongAnswer && !asked){
					System.out.println("\nWould you like to perform a serial or a parallel analysis? (serial/parallel): ");
					asked = true;
				}
			    answer = console.nextLine();
			    
			    if("serial".equalsIgnoreCase(answer)){
			    	serialAnalysis = true;
				}else if("parallel".equalsIgnoreCase(answer)){
					serialAnalysis = false;
				}else{
					System.out.println("\nWrong answer. Possible answers :  serial / parallel ");
				}   
			}
		}
	}
}
