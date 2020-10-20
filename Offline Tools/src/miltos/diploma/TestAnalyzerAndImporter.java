package miltos.diploma;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import miltos.diploma.characteristics.CharacteristicSet;
import miltos.diploma.characteristics.QualityModel;
import miltos.diploma.characteristics.QualityModelLoader;
import miltos.diploma.characteristics.Tqi;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.google.gson.Gson;

/**
 * This class is made in order to Test the BenchmarkAnalyzer
 * and the results importer!!!
 * 
 * Basically this class contains the central script of the 
 * Threshold Extractor.
 * 
 * @author Miltos
 *
 */
public class TestAnalyzerAndImporter {

	//The path where the benchmark repository is placed...
	private static String BENCHMARK_REPO = "C:/Users/Miltos/Desktop/MyWorkspace";
	public static final String PROJ_RES_PATH = "C:/Users/Miltos/Desktop/evalResults.json";
	public static final String PROJ_RANK_PATH = "C:/Users/Miltos/Desktop/projectRanking.json";
	public static final String XLS_DATASET_PATH = "C:/Users/Miltos/Desktop/evaluationResults.xls";
	public static final boolean STATIC_ANALYSIS = true;
	public static final boolean INCLUDE_INSPECT_RES = false;
	public static final String PROPERTIES_XML = "C:/Users/Miltos/Desktop/properties3.xml";
	
	//User defined variables
	public static boolean serialAnalysis = false;
	public static boolean keepResults = false;
	private static String benchRepoPath;
	private static String qmPath;
	private static String resPath;
	private static boolean staticAnalysis;
	public static boolean benchmarkCalibration;
	public static boolean weightsElicitation;
	
	public static void main(String[] args) throws CloneNotSupportedException, InterruptedException {
	
		System.out.println("\n\n******************************  Model Designer *******************************");
		System.out.println("*");
		System.out.println("*");
		System.out.println("*");

		
		//Get the configuration
		getConfig();
		
		//Get the inputs of the user
		getUserInputs();
		
		/*
		 * Step 0: Import the user defined properties
		 */
		//TODO: Remove this class and use QMLoader - Get the configuration through xml file.
		//PropertySet properties = SamplePropertySetGenerator.samplePropertySet();
		
		QualityModel qualityModel = new QualityModel();
		PropertiesAndCharacteristicsLoader qmLoader = new PropertiesAndCharacteristicsLoader(qmPath);
		qualityModel = qmLoader.importQualityModel();
		PropertySet properties = qualityModel.getProperties();
		CharacteristicSet characteristics = qualityModel.getCharacteristics();
		Tqi tqi = qualityModel.getTqi();

	BenchmarkProjects projects = null;
	BenchmarkAnalysisExporter exporter = null;
	
	if(benchmarkCalibration){
		/*
		 * Step 1 : Analyze the projects in the desired Benchmark Repository
		 */

		if(staticAnalysis){
			
			//Check if the results directory exists and if not create it. Clear it's contents as well.
			checkCreateClearDirectory(BenchmarkAnalyzer.BENCH_RESULT_PATH);
	
			
			System.out.println("\n**************** STEP 1 : Benchmark Analyzer *************************");
			System.out.println("*");
			System.out.println("* Analyzing the projects of the desired benchmark repository");
			System.out.println("* Please wait...");
			System.out.println("*");
		
			//TODO: Remove the time calculation
			long startTime = System.currentTimeMillis();
			
			//TODO: Create a parent Benchmark Analyzer so that you can use it to move the common commands outside the if statement
			if(serialAnalysis){
				//Instantiate the serial benchmark analyzer
				BenchmarkAnalyzer benchmarkAnal = new BenchmarkAnalyzer();
				
				//Set the repository and the desired properties to the benchmark analyzer
				benchmarkAnal.setBenchRepoPath(benchRepoPath);
				benchmarkAnal.setProperties(properties);
				
				//Start the analysis of the benchmark repository 
				benchmarkAnal.analyzeBenchmarkRepo();
			}else{
				//Instantiate the parallel benchmark analyzer
				OptimalParallelBenchmarkAnalyzer benchmarkAnal = new OptimalParallelBenchmarkAnalyzer();
				
				//Set the repository and the desired properties to the benchmark analyzer
				benchmarkAnal.setBenchRepoPath(benchRepoPath);
				benchmarkAnal.setProperties(qualityModel.getProperties());
				
				//Start the analysis of the workspace
				benchmarkAnal.analyzeBenchmarkRepo();
			}
			
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			
			//Print some messages to the user
			System.out.println("*");
			System.out.println("* The analysis is finished..!");
			System.out.println("* You can find the results at : " + new File(BenchmarkAnalyzer.BENCH_RESULT_PATH).getAbsolutePath());
			System.out.println("* Total time is : " + totalTime + " ms");
			System.out.println();
		}
		

		/*
		 * Step 2 : Import the results of the analysis and store them into different objects
		 */
		
		System.out.println("\n**************** STEP 2 : Benchmark Importer *************************");
		System.out.println("*");
		System.out.println("* Importing the results of the analysis...");
		System.out.println("* Please wait...");
		System.out.println("*");
		
		// Create an empty BenchmarkImporter
		BenchmarkResultImporter benchmarkImporter = new BenchmarkResultImporter();
		
		// Start importing the project results
		projects = new BenchmarkProjects();
		projects = benchmarkImporter.importResults(BenchmarkAnalyzer.BENCH_RESULT_PATH);
		
		// Print some informative messages to the console
		System.out.println("*");
		System.out.println("* The results are successfully imported..! ");

		
		
		/*
		 * Step 3 : Aggregate the results of each project
		 */
		
		System.out.println("\n**************** STEP 3: Aggregation Process *************************");
		System.out.println("*");
		System.out.println("* Aggregating the results of each project...");
		System.out.println("* I.e. Calculating the normalized values of their properties...");
		System.out.println("* Please wait...");
		System.out.println("*");
		
		//Create an empty BenchmarkAggregator and aggregate the metrics of the project
		BenchmarkAggregator benchAggregator = new BenchmarkAggregator();
		benchAggregator.aggregateProjects(projects, properties);
		
		System.out.println("*");
		System.out.println("* Aggregation process finished..!");
		
		
		/*
		 * Step 4 : Export the benchmark analysis results for the R - Analysis
		 */
		
		System.out.println("\n**************** STEP 4: Properties exportation for R analysis *******");
		System.out.println("*");

		//Create an analysis exporter and export the Properties in a xls form
	    exporter = new BenchmarkAnalysisExporter();
		exporter.exportToCsv(projects);
		System.out.println("*");
		System.out.println("* The xls file with the properties is successfully exported \n and placed into R's working directory!");
		

		/*
		 * Step 5 : Invoke R analysis for the threshold calculation
		 */
		
		System.out.println("\n**************** STEP 5: Threshold extraction ************************");
		System.out.println("*");
		System.out.println("* Calling R for threshold extraction...");
		System.out.println("* This will take a while...");
		System.out.println("*");
		
		//Create an Empty R Invoker and execute the threshold extraction script
		RInvoker rInvoker = new RInvoker();
		rInvoker.executeRScriptForThresholds();
		
		System.out.println("*");
		System.out.println("* R analysis finished..!");
		System.out.println("* Thresholds exported in a JSON file..!");
		
		/*
		 * Step 6 : Import the thresholds and assign them to each property of the QM
		 */
		System.out.println("\n**************** STEP 6: Importing the thresholds ********************");
		System.out.println("*");
		System.out.println("* Importing the thresholds from the JSON file into JVM...");
		System.out.println("* This will take a while...");
		System.out.println("*");
		
		//Create an empty Threshold importer and import the thresholds from the json file exported by R 
		//The file is placed in the R working directory (fixed)
		ThresholdImporter thresholdImp = new ThresholdImporter();
		thresholdImp.importThresholdsFromJSON(properties);
		
		System.out.println("* Thresholds successfully imported..!");
		
		/*
		 * Step 7 : Copy the thresholds to each projects' properties
		 */
		System.out.println("\n**************** STEP 7: Loading thresholds to Projects  *************");
		System.out.println("*");
		System.out.println("* Seting the thresholds to the properties of the existing projects...");
		System.out.println("* This will take a while...");
		System.out.println("*");
		
		double progress = 0;
		for(int i = 0; i < projects.size(); i++){
			System.out.print("* Progress : " + (int) (progress/projects.size()* 100)  + " %\r");
			PropertySet prop = projects.getProject(i).getProperties();
			for(int j = 0; j < prop.size(); j++){
				prop.get(j).setThresholds(properties.get(j).getThresholds().clone());
			}
			progress++;
		}
		
		System.out.print("* Progress : " + (int) (progress/projects.size()) * 100 + " %\r");
		
		System.out.println("*");
		System.out.println("* Thresholds successfully loaded to the projects' properties..!");
	}	
	
	//Weight elicitation process
	if(weightsElicitation){
		
		System.out.println("\n*********************************** Weight Elicitation *************************************");
		System.out.println("*");
		System.out.println("*");
		
		//Ask if the user wants simple AHP or fuzzy AHP
		String answer = selectMethod();
		
		//Create a console scanner
		Scanner console = new Scanner(System.in);
		
		boolean fuzzy = false;
		
		System.out.println("*");
		if("simple".equalsIgnoreCase(answer)){
			System.out.println("* AHP technique selected!");
			System.out.println("*");
			System.out.println("* The comparison matrices can be found at:\n* " + new File(ComparisonMatricesCreator.COMP_MATRICES).getAbsolutePath());
			System.out.println("* Please fulfill the comparison matricies with numbers between 1 and 9.");
			System.out.println("*");
			System.out.println("* When you are ready hit \"Enter\"");
			
		}else{
			System.out.println("* Fuzzy AHP tecnique selected!");
			System.out.println("*");
			System.out.println("* The comparison matrices can be found at:\n* " + new File(ComparisonMatricesCreator.COMP_MATRICES).getAbsolutePath());
			System.out.println("*");
			System.out.println("* Please fulfill each cell of the comparison matricies with one of the following linguistic\n* variables:  Very Low, Low, Moderate, High, Very High");
			System.out.println("*");
			System.out.println("* If you wish you can define how sure you are for your choice by providing one of the letters:\n* U, D, C next to your judgement, seperated by comma (U: Uncertain, D: Default, C: Certain)");
			System.out.println("* ");
			System.out.println("* Please check your spelling! If you misspell a choice then the default values will be\n* automatically taken (i.e. Moderate and D)");
			System.out.println("* ");
			System.out.println("* When you are ready hit \"Enter\"");
			fuzzy = true;
			
		}
		
		//Create the appropriate comparison matrices
		ComparisonMatricesCreator.generateCompMatrices(characteristics, properties, fuzzy);
		
		//Wait until the user is ready
		console.nextLine();
		
		//Call R script for weight elicitation
		RInvoker invoker = new RInvoker();
		invoker.executeRScriptForWeightsElicitation();
		
		//Import the weights from the json file 
		WeightsImporter weightImporter = new WeightsImporter();
		weightImporter.importWeights(tqi, characteristics);
	}
	
	
		/*
		 * STEP 8 : Export the properties in an XML (or JSON) File
		 */
		
		System.out.println("\n**************** STEP 8: Exporting Results ***********************");
		System.out.println("*");
		System.out.println("* Exporting the results to XML, JSON files and JDOM Element object...");
		System.out.println("* This will take a while...");
		System.out.println("*");
		
		//Check and create the predefined results directory 
		checkCreateClearDirectory(PropertiesExporter.BENCH_RESULT_PATH);
		
		//Check what should be exported 
		if(benchmarkCalibration && weightsElicitation){
			
			//Export the Quality Model
			QualityModelExporter qmExp = new QualityModelExporter();
			qmExp.exportQualityModelToXML(qualityModel, new File(resPath + "/qualityModel.xml").getAbsolutePath());
			qmExp.exportQualityModelToXML(qualityModel, new File(PropertiesExporter.BENCH_RESULT_PATH + "/qualityModel.xml").getAbsolutePath());
		
		}else if(benchmarkCalibration){
			
			//Export the properties to XML and JSON format to the predefined directory
			PropertiesExporter propertiesExp = new PropertiesExporter();
			propertiesExp.exportPropertiesToXML(properties, new File(PropertiesExporter.BENCH_RESULT_PATH + "/properties.xml").getAbsolutePath());
			propertiesExp.exportPropertiesToJSON(properties, new File(PropertiesExporter.BENCH_RESULT_PATH + "/properties.json").getAbsolutePath());
		
			//Export the properties to XML and JSON format to the user defined directory
			propertiesExp.exportPropertiesToXML(properties, new File(resPath + "/properties.xml").getAbsolutePath());
			propertiesExp.exportPropertiesToJSON(properties, new File(resPath + "/properties.json").getAbsolutePath());
			
			//TODO: Remove this ...
			exporter.exportToJSON(projects, new File(resPath + "/projects.json").getAbsolutePath());
			
		}else if(weightsElicitation){
			
			//Export the quality model's characteristics
			CharacteristicsExporter charExporter = new CharacteristicsExporter();
			charExporter.exportCharacteristicsToXML(characteristics, new File(resPath + "/characteristics.xml").getAbsolutePath());
			charExporter.exportCharacteristicsToXML(characteristics, new File(PropertiesExporter.BENCH_RESULT_PATH + "/characteristics.xml").getAbsolutePath());
			
			//Export the quality model's TQI object
			TqiExporter tqiExp = new TqiExporter();
			tqiExp.exportTqiToXML(tqi, new File(resPath + "/tqi.xml").getAbsolutePath());
			tqiExp.exportTqiToXML(tqi, new File(PropertiesExporter.BENCH_RESULT_PATH  + "/tqi.xml").getAbsolutePath());
		
		}else{
			System.out.println("Something went wrong!! Nothing to export");
		}
		
		System.out.println("* Results successfully exported..!");
		System.out.println("*");
		System.out.println("* You can find the results at        : " + new File(resPath).getAbsolutePath());
		System.out.println("* You can find the ranked results at : " + new File(PropertiesExporter.BENCH_RESULT_PATH).getAbsolutePath());
		
	}
	
	/**
	 * This method is responsible for asking the user which Multi-Criteria decision making
	 * technique he/she would like to use for the weight elicitation process.
	 * 
	 * Typically, it sets the appropriate R Script that will be executed and returns the 
	 * user's answer.
	 */
	private static String selectMethod() {
		
		//Create a console scanner
		Scanner console = new Scanner(System.in);
		
		String answer = "";
		boolean wrongAnswer = false;
		boolean asked = false;
		
		//While the correct answer is not provided by the user do...
		while((!"simple".equalsIgnoreCase(answer)) && (!"fuzzy".equalsIgnoreCase(answer))){
			//Ask the basic question only once
			if(!wrongAnswer && !asked){
				System.out.println("* Would you like to use simple AHP of fuzzy AHP for the weights elicitation? (simple/fuzzy): ");
				System.out.println("*");
				asked = true;
			}
			System.out.print("* ");
			
			//Retrieve the user's answer
		    answer = console.nextLine();
		    
		    //Check if the answer is valid and set the path to the appropriate R script
		    if("simple".equalsIgnoreCase(answer)){
		    	RInvoker.weightsScript = RInvoker.R_AHP_SCRIPT;
			}else if("fuzzy".equalsIgnoreCase(answer)){
				RInvoker.weightsScript = RInvoker.R_FAHP_SCRIPT;
			}else{
				//If the answer is not valid, inform the user and wait for a new answer...
				System.out.println("* ");
				System.out.println("* Wrong answer. Possible answers :  simple / fuzzy ");
			}   
		}
		
	//Return the user's valid answer 
	return answer;
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
					keepResults = Boolean.parseBoolean(el.getText()); 
				}else if("rScriptPath".equalsIgnoreCase(el.getName())){
					String path = el.getText();
					
					//Check if it contains spaces
					//TODO: Check the OS as well...
				//	if(path.contains(" ")){
				//		path = "\"" + path + "\"";
				//	}
					RInvoker.R_BIN_PATH = path;
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
		
		//The main menu of the program
		System.out.println("* What would you like to perform?");
		System.out.println("* 1. Benchmark Calibration (Threshold Extraction)");
		System.out.println("* 2. Weights Elicitation");
		System.out.println("* 3. General Quality Model derivation");
		System.out.println("*");
		
		boolean valid = false;
		while(!valid){
			
			System.out.println("* Please provide the number of your choice :");
			
			String choice = console.nextLine();
			
			switch(choice){
			case "1":
				benchmarkCalibration = true;
				valid = true;
				break;
			case "2":
				weightsElicitation = true;
				valid = true;
				break;
			case "3":
				benchmarkCalibration = true;
				weightsElicitation = true;
				valid = true;
				break;
			default:
				System.out.println("Invalid option!");
				break;
			}
			
		}
		
		//Questions that concern the threshold extraction process
		if(benchmarkCalibration){
			
			File dir = null;
			while(!exists){
				System.out.println("\nPlease provide the path of the desired benchmark repository that you would like \nto use for the calculation of the quality model's thresholds: ");
				
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
						
						//Check if the repository contains at least one directory (i.e. project)
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
							System.out.println("There aren't any java files inside the repository : " + project.getName());
							problem = true;
							break;
						}else if(classFiles.length == 0 || classFiles.length == 0){
							System.out.println("There aren't any class or jar files inside the repository : " + project.getName());
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
							benchRepoPath = dir.getAbsolutePath();
					}
					
					if(!hasDirectory){
						exists = false;
						System.out.println("there isn't any project in the desired repository");
					}
					
				}else{
					System.out.println("The desired directory doesn't exist..!");
				}
			}
			
			System.out.println("Repository successfully validated..!");
			
		}
		
		
		//Questions that concern both features
		
		//Ask for the path of the XML file that contains the desired properties and characteristics of the model
		File qmXMLFile = null;
		exists = false;
		while(!exists){
			System.out.println("\nPlease provide the path of the XML file that contains the desired properties and \ncharacteristics of the Quality Model: ");
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
		
		//Destination folder
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
	
	//Questions that concern the benchmark calibration process
	if(benchmarkCalibration){
		
		//Static Analysis or just threshold extraction with the previous results???	
		String answer = "";
		boolean wrongAnswer = false;
		while((!"yes".equalsIgnoreCase(answer)) &&! ("no".equalsIgnoreCase(answer))){
			if(!wrongAnswer){
				System.out.println("\nWould you like to run a new static analysis ? (yes/no): ");
				System.out.println("(If no then the results of the previous analysis will be used for the threshold \nextraction) ");
			}
		    answer = console.nextLine();
		    
		    if("yes".equalsIgnoreCase(answer)){
		    	staticAnalysis = true;
			}else if("no".equalsIgnoreCase(answer)){
				staticAnalysis = false;
			}else{
				System.out.println("\nWrong answer. Please answer with yes or no : ");
			}
		    
		  //If the user doesn't want a new analysis check if there are any static analysis results  
		    if("no".equalsIgnoreCase(answer)){
		    	String path = BenchmarkAnalyzer.BENCH_RESULT_PATH;
		    	File resDir = new File(path);
		    	if(!resDir.isDirectory() || !resDir.exists() || resDir.listFiles().length == 0 ){
		    		System.out.println("\nThe aren't any previous benchmark analysis results results..! ");
		    		answer = "";
		    	}
		    }
		    
		}
		
		//Serial or Parallel???
		if(staticAnalysis){
			answer = "";
			wrongAnswer = false;
			boolean asked = false;
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

}
