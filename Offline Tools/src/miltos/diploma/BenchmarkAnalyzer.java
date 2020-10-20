package miltos.diploma;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;

import java.io.File;
/**
 * This class is responsible for analyzing all the projects that are stored in the
 * desired folder (e.g. Benchmark Repository) against:
 * 		
 * 		1. all the supported tools of the system (e.g. PMD, CKJM etc.) and 
 * 		2. all the Properties of the Quality Model (e.g. Comprehensibility etc.)
 * 
 * The results are stored in a fixed results directory of known structure. 
 * This directory is automatically created by the BenchmarkAnalyzer based on
 * the structure of the benchmark directory.
 * 
 * Typically, it creates a different folder for each project found in the benchmark 
 * repository and places all the result files concerning this project in this folder.
 * 
 * @author Miltos
 *
 */
public class BenchmarkAnalyzer {
	
	// Useful static fields
	public static String BASE_DIR = new File(System.getProperty("user.dir")).getAbsolutePath();
	public static String BENCH_RESULT_PATH = new File(BASE_DIR + "/Results/Analysis/BenchmarkResults").getAbsolutePath();
	public static String WORKSPACE_RESULT_PATH = new File(BASE_DIR + "/Results/Analysis/WorkspaceResults").getAbsolutePath();
	public static String SINGLE_PROJ_RESULT_PATH = new File(BASE_DIR + "/Results/Analysis/SingleProjectResults").getAbsolutePath();

	private String benchRepoPath;
	private PropertySet properties;
	private static String resultsPath = BENCH_RESULT_PATH;

	// Easy fix for gui
	// TODO : Find another way
	private ProgressBar prog;
	private ProgressIndicator progInd;


	/**
	 * This method sets the ProgressBar and ProgressIndicator objects that belong
	 * to the GUI's main console so that they can be updated by this class.
     */
	public void setGUIObjects(ProgressBar prog, ProgressIndicator progInd){
		this.prog = prog;
		this.progInd = progInd;
	}

	/**
	 * The basic constructors of the class.
	 */
	public BenchmarkAnalyzer(){
		this.benchRepoPath = null;
		this.properties = null;
	}

	/**
	 * The second constructor of the class.
     */
	public BenchmarkAnalyzer(String benchRepoPath){
		this.benchRepoPath = benchRepoPath;
	}

	/**
	 * The third constructor of the class.
     */
	public BenchmarkAnalyzer(String benchRepoPath, PropertySet properties){
		this.benchRepoPath = benchRepoPath;
		this.properties = properties;
	}
	
	
	/**
	 * Setters and Getters.
	 */

	public String getBenchRepoPath() {
		return benchRepoPath;
	}
	
	public void setBenchRepoPath(String benchRepoPath) {
		this.benchRepoPath = benchRepoPath;
	}

	public PropertySet getProperties() {
		return properties;
	}

	public void setProperties(PropertySet properties) {
		this.properties = properties;
	}

	public String getResultsPath() {
		return resultsPath;
	}

	public void setResultsPath(String resultsPath) {
		this.resultsPath = resultsPath;
	}

	
	/**
	 * This method is responsible for analyzing the desired benchmark
	 * repository according to the user defined properties.
	 * 
	 * Its algorithm is pretty straightforward if you read the comments.
	 */
	public void analyzeBenchmarkRepo(){
		
		//Instantiate the available single project analyzers of the system
		PMDAnalyzer pmd = new PMDAnalyzer();
		CKJMAnalyzer ckjm = new CKJMAnalyzer();
		
		//List the projects of the repository
		File baseDir = new File(benchRepoPath);
		File[] projects = baseDir.listFiles();

		/* Basic Solution */
		// Analyze all the projects of the benchmark repository
		double progress = 0;
		prog.setProgress(progress);
		progInd.setProgress(progress);

		//For each project in the benchmark repo do...
		for(File project : projects){
			//Print the progress to the console
			//TODO: Remove this print
			ProgressDemo.updateProgress((progress/projects.length));
			prog.setProgress((progress/projects.length));
			progInd.setProgress((progress/projects.length));

			//System.out.print("* Progress : " + Math.ceil(()*) + " %\r" );
			//Call the single project analyzers sequentially
			if(project.isDirectory()){
				pmd.analyze(project.getAbsolutePath(), resultsPath + "/" +project.getName(), properties);
				ckjm.analyze(project.getAbsolutePath(), resultsPath + "/" +project.getName(), properties);
			}
			progress++;	
		}

		//Print the progress to the console
		//TODO: Remove this print
		ProgressDemo.updateProgress((progress/projects.length));
		prog.setProgress((progress/projects.length));
		progInd.setProgress((progress/projects.length));

		
		System.out.println();

		//TODO: REMOVE!!!!
		progInd.setRotate(0);
		Text text = (Text) progInd.lookup(".percentage");
		text.getStyleClass().add("percentage-null");
		progInd.setLayoutY(progInd.getLayoutY()+8);
		progInd.setLayoutX(progInd.getLayoutX()-7);
	}
	
	
	/**
	 * This method prints the structure of the benchmark directory to
	 * the console. 
	 * 
	 * It should be used for debugging purposes only.
	 */
	public void printBenchmarkRepoContents(){
		//List all the directories included inside the repository
		File baseDir = new File(benchRepoPath);
		System.out.println("Benchmark repository : " + baseDir.getAbsolutePath());
		File[] projects = baseDir.listFiles();
		for(int i = 0; i < projects.length; i++){
			if(projects[i].isDirectory()){
				System.out.println("Directory : " + projects[i].getName());
			}else{
				System.out.println("File : " + projects[i].getName());
			}
		}
		System.out.println("");
	}
}
