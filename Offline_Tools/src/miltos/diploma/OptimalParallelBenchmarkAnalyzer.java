package miltos.diploma;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class OptimalParallelBenchmarkAnalyzer {

	//The fixed path of the directory where the results should be stored after the analyses.
	//TODO: Create an Abstract Benchmark Analyzer and place this constant there.
	public static String BASE_DIR = new File(System.getProperty("user.dir")).getAbsolutePath();
	public static String BENCH_RESULT_PATH = new File(BASE_DIR + "/Results/Analysis/BenchmarkResults").getAbsolutePath();
	public static String WORKSPACE_RESULT_PATH = new File(BASE_DIR + "/Results/Analysis/WorkspaceResults").getAbsolutePath();
	public static String SINGLE_PROJ_RESULT_PATH = new File(BASE_DIR + "/Results/Analysis/SingleProjectResults").getAbsolutePath();

	public static int MAX_DEPTH = 1;
	public static Vector<Thread> threads;
	
	private static File[] projects;
	private static PMDAnalyzer pmd;

	//Basic fields
	private String benchRepoPath;		//The path of the benchmark repository
	private PropertySet properties;		//The set of properties against which the project should be analyzed
	
	private static String  resultsPath = BENCH_RESULT_PATH;

	// GUI Objects
	//TODO: REMOVE THIS!
	private static Vector<ProgressBar> progBars;
	private static Vector<ProgressIndicator> progIndicators;
	private static TextArea console;
	public static int index = 0;

	/**
	 * This method is used in order to pass the GUI objects as arguments to this class
	 * so that they can be processed and updated by its methods.
     */
	public void setGUIObjects(Vector<ProgressBar> progBars, Vector<ProgressIndicator> progIndicators, TextArea console){
		this.progBars = progBars;
		this.progIndicators = progIndicators;
		this.console = console;
	}
	
	/**
	 * The default constructor of the class.
	 */
	public OptimalParallelBenchmarkAnalyzer(){
		this.benchRepoPath = null;
		this.properties = null;
		threads = new Vector<>();
	}

	/**
	 * The basic constructor of the class.
	 * @param benchRepoPath
     */
	public OptimalParallelBenchmarkAnalyzer(String benchRepoPath){
		this.benchRepoPath = benchRepoPath;
	}

	/**
	 * The complete constructor of the class.
     */
	public OptimalParallelBenchmarkAnalyzer(String benchRepoPath, PropertySet properties){
		this.benchRepoPath = benchRepoPath;
		this.properties = properties;
	}
	
	/*
	 * Setters and Getters...
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
	 * This method is responsible for analyzing the projects of the desired benchmark
	 * repository, according to the user defined properties. 
	 * 
	 * Its algorithm is pretty straightforward if you read the comments.
	 */
	public void analyzeBenchmarkRepo() throws InterruptedException, CloneNotSupportedException{

		//Instantiate the single project analyzers supported by the system
		pmd = new PMDAnalyzer();
		CKJMAnalyzer ckjm = new CKJMAnalyzer();
		
		//List the projects of the repository
		File baseDir = new File(benchRepoPath);
		projects = baseDir.listFiles();
		
		/*
		 * Separate the property set into two different property sets one for each tool
		 */

		//Create one property set in order to store the properties that belong to each tool
		PropertySet ckjmPropSet = new PropertySet();
		PropertySet pmdPropSet = new PropertySet();
		
		//Iterate through all the model's properties
		Iterator<Property> iterator = properties.iterator();
		//For each property do ...
		while(iterator.hasNext()){

			//Get the current property
			Property property = iterator.next();
			
			//Add it to the appropriate PropertySet based on the tool that should be used for its quantification
			if(property.getMeasure().getTool().equals(PMDAnalyzer.TOOL_NAME)){
				pmdPropSet.addProperty((Property) property.clone());
			}else if(property.getMeasure().getTool().equals(CKJMAnalyzer.TOOL_NAME)){
				ckjmPropSet.addProperty((Property) property.clone());
			}else{
				System.out.println("This property doesn't belong to any tool supported by the system!!");
			}
		}
		
		//Print some messages for debugging purposes ...
		System.out.println("* Splitting work...");
		System.out.println("*");
		System.out.println("* Total properties found      : " + properties.size());
		System.out.println("* Properties assigned to CKJM : " + ckjmPropSet.size());
		System.out.println("* Properties assigned to PMD  : " + pmdPropSet.size());
		System.out.println("*");
		
		/* 
		 * Split the PMD property set on the desired number of lists and feed them to the corresponding number of threads 
		 */
	
		System.out.println("*** Thread Created ***  ");
		System.out.println("* Thread Size : " + ckjmPropSet.size());
		System.out.println("* Thread Contents : ");
		for(int i = 0; i < ckjmPropSet.size(); i++){
			System.out.println("*   Property Name : " + ckjmPropSet.get(i).getName());
		}

		Platform.runLater(() -> {

			console.appendText("* Splitting work...\n");
			console.appendText("*\n");
			console.appendText("* Total properties found           : " + properties.size() + "\n");
			console.appendText("* Properties assigned to CKJM : " + ckjmPropSet.size() + "\n");
			console.appendText("* Properties assigned to PMD  : " + pmdPropSet.size() + "\n");
			console.appendText("*\n");

			console.appendText("*** Thread Created ***  \n");
			console.appendText("* Thread Size : " + ckjmPropSet.size() + "\n");
			console.appendText("* Thread Contents : \n");
			for(int i = 0; i < ckjmPropSet.size(); i++){
				console.appendText("*   Property Name : " + ckjmPropSet.get(i).getName() + "\n");
			}
		});

		ProgressBar prog = progBars.get(0);
		ProgressIndicator progInd = progIndicators.get(0);


		Thread ckjmThread = new Thread(new Runnable(){
			double progress = 0;
			public void run(){
				Platform.runLater(() -> {
					prog.setProgress(progress);
					progInd.setProgress(progress);
				});
					for (File project : projects) {
						Platform.runLater(() -> {
						prog.setProgress((progress / projects.length));
						progInd.setProgress((progress / projects.length));
						});
						if (project.isDirectory()) {
							ckjm.analyze(project.getAbsolutePath(), resultsPath + "/" + project.getName(), ckjmPropSet);
						}
						progress++;
					}
				Platform.runLater(() -> {
					prog.setProgress((progress / projects.length));
					progInd.setProgress((progress / projects.length));

					//TODO: REMOVE!!!!
					progInd.setRotate(0);
					Text text = (Text) progInd.lookup(".percentage");
					text.getStyleClass().add("percentage-null");
					progInd.setLayoutY(progInd.getLayoutY() + 8);
					progInd.setLayoutX(progInd.getLayoutX() - 7);
				});

			}
		});
		
		
		//Start the thread
		ckjmThread.start();

		//Create the PMD threads
		pmdThreadCreator(pmdPropSet, 0, MAX_DEPTH);

		//Inform the user
		System.out.println("* ");
		System.out.println("* Please wait...");

		Platform.runLater(() -> {
			console.appendText("* \n");
			console.appendText("* Please wait...\n");
		});

		//Make the main thread wait for the other threads to be finished
		for(int i = 0; i < threads.size(); i++){
			threads.get(i).join();
		}
		
		ckjmThread.join();
	}

	/**
	 * This method is responsible for splitting the work into multiple threads based on
	 * the divide and conquer logic.
     */
	public static void pmdThreadCreator(PropertySet pmdPropSet,int depth, int threshold) throws InterruptedException{
		//First division is done
		depth++;
		
		//1. Divide Step
		int half;
		if(pmdPropSet.size() %2 == 0){
			half = (int) pmdPropSet.size()/2;
		}else{
			half = (int) Math.ceil(pmdPropSet.size()/2);
		}

		//Create two separate lists of PMD properties
		List<Property> l1 = pmdPropSet.getPropertyVector().subList(0, half);
		List<Property> l2 = pmdPropSet.getPropertyVector().subList(half, pmdPropSet.size());

		Vector<Property> p = new Vector<>(l1);
		PropertySet ps1 = new PropertySet();
		ps1.setPropertyVector(p);
		
		Vector<Property> s = new Vector<>(l2);
		PropertySet ps2 = new PropertySet();
		ps2.setPropertyVector(s);

		//Check if the value of depth field reached the threshold
		if(depth != threshold){
			//If no, call the same method for each list respectively
			pmdThreadCreator(ps1, depth, threshold);
			pmdThreadCreator(ps2, depth, threshold);
		}else{
			//If yes, create the threads

			//Print some descriptive messages
			System.out.println("\n*** Thread Created ***  ");
			System.out.println("* Thread Size : " + ps1.size());
			System.out.println("* Thread Contents : ");

			for(int i = 0; i < ps1.size(); i++){
				System.out.println("*   Property Name : " + ps1.get(i).getName());
			}

			Platform.runLater(() -> {
				console.appendText("\n*** Thread Created ***  \n");
				console.appendText("* Thread Size : " + ps1.size() + "\n");
				console.appendText("* Thread Contents : \n");
				for(int i = 0; i < ps1.size(); i++){
					console.appendText("*   Property Name : " + ps1.get(i).getName() + "\n");
				}
			});
			
			System.out.println("*");
			System.out.println("\n*** Thread Created ***  ");
			System.out.println("* Thread Size : " + ps2.size());
			System.out.println("* Thread Contents : ");
			for(int i = 0; i < ps2.size(); i++){
				System.out.println("*   Property Name : " + ps2.get(i).getName());
			}

			Platform.runLater(() -> {
				console.appendText("\n*** Thread Created ***  \n");
				console.appendText("* Thread Size : " + ps2.size() + "\n");
				console.appendText("* Thread Contents : \n");
				for(int i = 0; i < ps2.size(); i++){
					console.appendText("*   Property Name : " + ps2.get(i).getName() + "\n");
				}
			});

			index++;
			ProgressBar prog = progBars.get(index);
			ProgressIndicator progInd = progIndicators.get(index);

			//Create the first thread
			Thread thread1 = new Thread(new Runnable(){

				double  progress = 0;

				public void run(){

					Platform.runLater(() -> {
						prog.setProgress(progress);
						progInd.setProgress(progress);
					});

						for (File project : projects) {
							Platform.runLater(() -> {
							prog.setProgress((progress / projects.length));
							progInd.setProgress((progress / projects.length));
							});
							if (project.isDirectory()) {

								pmd.analyze(project.getAbsolutePath(), resultsPath + "/" + project.getName(), ps1);
							}
							progress++;

						}

					Platform.runLater(() -> {
						prog.setProgress((progress / projects.length));
						progInd.setProgress((progress / projects.length));

						//TODO: REMOVE!!!!
						progInd.setRotate(0);
						Text text = (Text) progInd.lookup(".percentage");
						text.getStyleClass().add("percentage-null");
						progInd.setLayoutY(progInd.getLayoutY() + 8);
						progInd.setLayoutX(progInd.getLayoutX() - 7);
					});

				}


			});

			index++;

			ProgressBar prog2 = progBars.get(index);
			ProgressIndicator progInd2 = progIndicators.get(index);

			//Create the second thread
			Thread thread2 = new Thread(new Runnable(){
				double progress = 0;
				public void run(){
					Platform.runLater(() -> {
					prog2.setProgress(progress);
					progInd2.setProgress(progress);
					});
					for(File project : projects){
						Platform.runLater(() -> {
						prog2.setProgress((progress/projects.length));
						progInd2.setProgress((progress/projects.length));
						});
						if(project.isDirectory()){
							pmd.analyze(project.getAbsolutePath(), resultsPath + "/"+project.getName(), ps2);
						}
						progress++;
					}
					Platform.runLater(() -> {
					prog2.setProgress((progress/projects.length));
					progInd2.setProgress((progress/projects.length));

					//TODO: REMOVE!!!!
					progInd2.setRotate(0);
					Text text = (Text) progInd2.lookup(".percentage");
					text.getStyleClass().add("percentage-null");
					progInd2.setLayoutY(progInd2.getLayoutY()+8);
					progInd2.setLayoutX(progInd2.getLayoutX()-7);
					});

				}
			});
			
			threads.add(thread1);
			threads.add(thread2);
			thread1.start();
			thread2.start();
		}
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
	}
}
