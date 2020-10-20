package miltos.diploma.characteristics;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.tools.ant.DirectoryScanner;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import miltos.diploma.BenchmarkAggregator;
import miltos.diploma.BenchmarkAnalysisExporter;
import miltos.diploma.BenchmarkAnalyzer;
import miltos.diploma.BenchmarkEvaluator;
import miltos.diploma.BenchmarkProjects;
import miltos.diploma.BenchmarkResultImporter;
import miltos.diploma.CKJMAggregator;
import miltos.diploma.CKJMAnalyzer;
import miltos.diploma.CKJMResultsImporter;
import miltos.diploma.EvaluationResultsExporter;
import miltos.diploma.PMDAggregator;
import miltos.diploma.PMDAnalyzer;
import miltos.diploma.PMDResultsImporter;
import miltos.diploma.Project;
import miltos.diploma.ProjectEvaluator;
import miltos.diploma.Property;

import com.google.gson.Gson;
/**
 * TODO: Bring the cloning processes together at the beginning of the class
 * TODO: Add a parallel analysis option...
 * TODO: Extract a jar 
 * TODO: Document the code
 * @author Miltos
 *
 */
public class TestSingleProjectEvaluator {

	//User defined fields
	public static String projectPath;
	public static String qmPath;
	public static String resPath;
	public static boolean includeInspectRes = false;
	public static boolean staticAnalysis = false;
	public static boolean keepResults = false;
	
	
	public static void main(String[] args) throws CloneNotSupportedException, InterruptedException{
				
		System.out.println("******************************  Project Evaluator *******************************");
		System.out.println();
		
		//Get the configuration
		getConfig();
		//Receive the appropriate configuration from the user through terminal
		getUserInputs();
		
		/*
		 * Step 0 : Load the desired Quality Model 
		 */
		
		System.out.println("**************** STEP 0: Quality Model Loader ************************");
		System.out.println("*");
		System.out.println("* Loading the desired Quality Model...");
		System.out.println("* Please wait...");
		System.out.println("*");
		
		//Instantiate a new QualityModel object
		QualityModel qualityModel = new QualityModel();
		
		//Instantiate the Quality Model importer
		QualityModelLoader qmImporter = new QualityModelLoader(qmPath);
		
		//Load the desired quality model
		qualityModel = qmImporter.importQualityModel();
		
		System.out.println("* Quality Model successfully loaded..!");
		
		/*
		 * Step 1: Create the Project object that simulates the desired project
		 */
		
		System.out.println("\n**************** STEP 1: Project Loader ******************************");
		System.out.println("*");
		System.out.println("* Loading the desired project...");
		System.out.println("* Please wait...");
		System.out.println("*");
		
		//Get the directory of the project
		File projectDir = new File(projectPath);
		
		//Create a Project object to store the results of the static analysis and the evaluation of this project...
		Project project = new Project();
		
		//Set the absolute path and the name of the project
		project.setPath(projectPath);
		project.setName(projectDir.getName());
		
		System.out.println("* Project Name : " + project.getName());
		System.out.println("* Project Path : " + project.getPath());
		System.out.println("*");
		System.out.println("* Project successfully loaded..!");
		
		/*
		 * Step 2: Analyze the desired project against the selected properties
		 */
		
		if(staticAnalysis){
			
			//Check if the results directory exists and if not create it. Clear it's contents as well.
			checkCreateClearDirectory(BenchmarkAnalyzer.SINGLE_PROJ_RESULT_PATH);

			//Print some messages...
			System.out.println("\n**************** STEP 2: Project Analyzer ****************************");
			System.out.println("*");
			System.out.println("* Analyzing the desired project");
			System.out.println("* Please wait...");
			System.out.println("*");
			
			//Instantiate the available single project analyzers of the system ...
			PMDAnalyzer pmd = new PMDAnalyzer();
			CKJMAnalyzer ckjm = new CKJMAnalyzer();
			
			//Analyze the project against the desired properties of each tool supported by the system...
			pmd.analyze(projectPath, BenchmarkAnalyzer.SINGLE_PROJ_RESULT_PATH+"/"+project.getName(), qualityModel.getProperties());
			ckjm.analyze(projectPath, BenchmarkAnalyzer.SINGLE_PROJ_RESULT_PATH+"/"+project.getName(), qualityModel.getProperties());
			
			//Print some messages to the user
			System.out.println("* The analysis is finished");
			System.out.println("* You can find the results at : " + BenchmarkAnalyzer.SINGLE_PROJ_RESULT_PATH);
			System.out.println();
		}
		
		/*
		 * Step 3: Import the results of the static analysis tools
		 */
		
		System.out.println("\n**************** STEP 3: Results Importer ****************************");
		System.out.println("*");
		System.out.println("* Importing the results of the analysis...");
		System.out.println("* Please wait...");
		System.out.println("*");
		
		//Create a simple PMD and CKJM Result Importers 
		PMDResultsImporter pmdImporter = new PMDResultsImporter();
		CKJMResultsImporter ckjmImporter = new CKJMResultsImporter();
		
		//Get the directory with the results of the analysis
		File resultsDir = new File(BenchmarkAnalyzer.SINGLE_PROJ_RESULT_PATH+"/"+project.getName());
		File[] results = resultsDir.listFiles();
		
		//For each result file found in the directory do...
		for(File resultFile : results){
			
			//Check if it is a ckjm result file
			if(!resultFile.getName().contains("ckjm")){
				
				//Parse the issues and add them to the IssueSet Vector of the Project object
				project.addIssueSet(pmdImporter.parseIssues(resultFile.getAbsolutePath()));
				
			}else{
				
				//Parse the metrics of the project and add them to the MetricSet field of the Project object
				project.setMetrics(ckjmImporter.parseMetrics(resultFile.getAbsolutePath()));
			}
		}
		
		// Print some informative messages to the console
		System.out.println("*");
		System.out.println("* The results of the static analysis are successfully imported ");
		
		/*
		 * Step 4 : Aggregate the static analysis results of the desired project
		 */
		
		System.out.println("\n**************** STEP 4: Aggregation Process *************************");
		
		//Print some messages
		System.out.println("*");
		System.out.println("* Aggregating the results of the project...");
		System.out.println("* I.e. Calculating the normalized values of its properties...");
		System.out.println("* Please wait...");
		System.out.println("*");
		
		//Clone the properties of the quality model to the properties of the certain project
		for(int i = 0; i < qualityModel.getProperties().size(); i++){
			//Clone the property and add it to the PropertySet of the current project
			Property p = (Property) qualityModel.getProperties().get(i).clone();
			project.addProperty(p);
		}
		
		//Create an empty PMDAggregator and CKJMAggregator
		PMDAggregator pmd = new PMDAggregator();
		CKJMAggregator ckjm = new CKJMAggregator();
		
		//Aggregate all the analysis results
		pmd.aggregate(project);
		ckjm.aggregate(project);
		
		//Normalize their values
		for(int i = 0; i < project.getProperties().size(); i++){
			Property property =  project.getProperties().get(i);
			property.getMeasure().calculateNormValue();
		}
		
		System.out.println("*");
		System.out.println("* Aggregation process finished..!");
		
		/*
		 * STEP 5 : Evaluate all the benchmark projects against their thresholds. 
		 */
		
		System.out.println("\n**************** STEP 5: Properties Evaluation ***********************");
		System.out.println("*");
		System.out.println("* Evaluating the project's properties against the calculated thresholds...");
		System.out.println("* This will take a while...");
		System.out.println("*");
		
		//Create a single project property evaluator
		ProjectEvaluator evaluator = new ProjectEvaluator();
		
		//Evaluate all its properties
		evaluator.evaluateProjectProperties(project);
		
		System.out.println("*");
		System.out.println("* The project's properties successfully evaluated..!");
		
		/*
		 * Step 6 : Evaluate the project's characteristics 
		 */
		
		System.out.println("\n**************** STEP 6: Characteristics Evaluation ******************");
		System.out.println("*");
		System.out.println("* Evaluating the project's characteristics based on the eval values of its properties...");
		System.out.println("* This will take a while...");
		System.out.println("*");
		
		//Clone the quality model characteristics inside the project
		//For each quality model's characteristic do...
		for(int i = 0; i < qualityModel.getCharacteristics().size(); i++){
			//Clone the characteristic and add it to the CharacteristicSet of the current project
			Characteristic c = (Characteristic) qualityModel.getCharacteristics().get(i).clone();
			project.getCharacteristics().addCharacteristic(c);
		}
		
		//Create a single project property evaluator
		ProjectCharacteristicsEvaluator charEvaluator = new ProjectCharacteristicsEvaluator();
		
		//Evaluate the project's characteristics
		charEvaluator.evaluateProjectCharacteristics(project);
		
		System.out.println("*");
		System.out.println("* The project's characteristics successfully evaluated..!");
		
		/*
		 * Step 7 : Calculate the TQI of the project
		 */
		
		System.out.println("\n**************** STEP 7: TQI Calculation *****************************");
		System.out.println("*");
		System.out.println("* Calgculating the TQI of the project ...");
		System.out.println("* This will take a while...");
		System.out.println("*");
		
		//Copy the TQI object of the QM to the tqi field of this project
		project.setTqi((Tqi)qualityModel.getTqi().clone());
		
		//Calculate the project's TQI
		project.calculateTQI();
		
		System.out.println("*");
		System.out.println("* The TQI of the project successfully evaluated..!");
		
		/*
		 * Step 8 : Export the project's data and properties in a json file 
		 */	
		
		System.out.println("\n**************** STEP 8: Exporting Evaluation Results ****************");
		System.out.println("*");
		System.out.println("* Exporting the results of the project evaluation...");
		System.out.println("* This will take a while...");
		System.out.println("*");
	
		//Clear Issues and metrics for more lightweight solution
		//TODO: Remove this ... For debugging purposes only
		if(!includeInspectRes){
			project.clearIssuesAndMetrics();
		}
		
		EvaluationResultsExporter.exportProjectToJson(project, new File(resPath + "/" + project.getName() + "_evalResults.json").getAbsolutePath());
		
		System.out.println("* Results successfully exported..!");
		System.out.println("* You can find the results at : " + new File(resPath).getAbsolutePath());
		
		/*
		 * Step 9 : Export the results to the predefined path as well
		 */
		
		checkCreateClearDirectory(EvaluationResultsExporter.SINGLE_PROJ_RESULT_PATH);
		
		//Export the results
		EvaluationResultsExporter.exportProjectToJson(project, new File(EvaluationResultsExporter.SINGLE_PROJ_RESULT_PATH + "/" + project.getName() + "_evalResults.json").getAbsolutePath());
		System.out.println("* You can find the results at : " + new File(EvaluationResultsExporter.SINGLE_PROJ_RESULT_PATH).getAbsolutePath() + " as well..!");
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
		
		File dir = null;
		while(!exists){
			System.out.println("\nPlease provide the path of the desired project that you would like to assess its quality : ");
			dir = new File(console.nextLine());
			
			if(dir.exists() && dir.isDirectory()){
				
				DirectoryScanner scanner = new DirectoryScanner();
				scanner.setIncludes(new String[]{"**/*.java"});
				scanner.setBasedir(dir.getAbsolutePath());
				scanner.setCaseSensitive(false);
				scanner.scan();
				String[] javaFiles = scanner.getIncludedFiles();
				
				scanner.setIncludes(new String[]{"**/*.class"});
				scanner.setBasedir(dir.getAbsolutePath());
				scanner.setCaseSensitive(false);
				scanner.scan();
				String[] classFiles = scanner.getIncludedFiles();
				
				scanner.setIncludes(new String[]{"**/*.jar"});
				scanner.setBasedir(dir.getAbsolutePath());
				scanner.setCaseSensitive(false);
				scanner.scan();
				String[] jarFiles = scanner.getIncludedFiles();

				if(javaFiles.length == 0){
					System.out.println("There are no java files inside the desired directory!");
				}else if(classFiles.length == 0 || classFiles.length == 0){
					System.out.println("There are no class or jar files inside the desired directory!");
				}else{
					exists = true;
					projectPath = dir.getAbsolutePath();
				}
			}else{
				System.out.println("The desired directory doesn't exist..!");
			}
		}
		
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
			System.out.println("\nPlease provide the path where you would like the results of the analysis to be placed : ");
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
		while((!"yes".equalsIgnoreCase(answer)) &&! ("no".equalsIgnoreCase(answer))){
			if(!wrongAnswer){
				System.out.println("\nWould you like to include the inspection results as well? (yes/no): ");
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
		while((!"yes".equalsIgnoreCase(answer)) &&! ("no".equalsIgnoreCase(answer))){
			if(!wrongAnswer){
				System.out.println("\nWould you like to run a new static analysis ? (yes/no): ");
				System.out.println("(If no then the results of the previous analysis will be used for the project's evaluation) ");
			}
		    answer = console.nextLine();
		    
		    if("yes".equalsIgnoreCase(answer)){
		    	staticAnalysis = true;
			}else if("no".equalsIgnoreCase(answer)){
				staticAnalysis = false;
			}else{
				System.out.println("\nWrong answer. Please answer with yes or no : ");
			} 
		    
		    //If the user doesn't want a new analysis check if there are results for the desired project
		    if("no".equalsIgnoreCase(answer)){
		    	
		    	File resDir = new File(BenchmarkAnalyzer.SINGLE_PROJ_RESULT_PATH + "/" + dir.getName());
		    	if(!resDir.isDirectory() || !resDir.exists() ){
		    		System.out.println("\nThe aren't any previous results for this project..! ");
		    		answer = "";
		    	}
		    }
		}
	}
}
