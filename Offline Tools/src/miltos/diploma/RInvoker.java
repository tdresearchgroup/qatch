package miltos.diploma;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.nio.file.*;
	
/**
 * This class is responsible for executing R scripts.
 * 
 * Basically, it contains methods that:
 * 
 * 	  1. Call R for a certain R script.
 *    2. Wait until R finishes the processing and create or 
 *       modify a file in the desired folder.
 */	
public class RInvoker {
	
	//Fixed paths
	public static final String BASE_DIR = System.getProperty("user.dir");
	public static final String R_WORK_DIR = new File(BASE_DIR + "/R_Working_Directory").getAbsolutePath();
	public static final String R_THRES_SCRIPT = new File(R_WORK_DIR + "/thresholdsExtractor.R").getAbsolutePath();
	public static final String R_AHP_SCRIPT = new File(R_WORK_DIR + "/ahpWeightElicitation.R").getAbsolutePath();
	public static final String R_FAHP_SCRIPT = new File(R_WORK_DIR + "/fahpWeightElicitator.R").getAbsolutePath();
	public static String weightsScript = R_AHP_SCRIPT;
	public static String R_BIN_PATH = "";
	
	/**
	 * A method for executing a certain R script...
	 */
	public void executeRScript(String rPath, String scriptPath, String args){
		try {
			if(System.getProperty("os.name").contains("Windows")){
				scriptPath = "\"" + scriptPath + "\"";
				args = "\"" + args + "\"";
				
			}
			
		Runtime.getRuntime().exec(rPath + "/RScript "  + scriptPath + " " + args);
			
	/*		String line;
			Process p = Runtime.getRuntime().exec(rPath + "/RScript "  + scriptPath + " " + args);
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			  while ((line = input.readLine()) != null) {
				    System.out.println(line);
				  }
			  input.close();
	*/
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * A method for executing the R script that calculates the thresholds 
	 * of the properties.
	 */
	public void executeRScriptForThresholds() throws InterruptedException{
		
		//TODO: Remove this prints
		System.out.println("* R_BIN_PATH= "+ R_BIN_PATH);
		System.out.println("* R_AHP_SCRIPT= "+ R_THRES_SCRIPT);
		System.out.println("* DIR= "+ BASE_DIR);
		System.out.println("* ");
		
		//Invoke the appropriate R script for threshold extraction - Use the fixed paths
		executeRScript(RInvoker.R_BIN_PATH, RInvoker.R_THRES_SCRIPT, R_WORK_DIR);
		
		//Wait for the RScript.exe to finish the analysis by polling the directory for changes
		Path resultsPath = Paths.get(R_WORK_DIR);
		
		try {
			//Create a directory watcher that watches for certain events
			WatchService watcher = resultsPath.getFileSystem().newWatchService();
			resultsPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY );
			WatchKey watchKey = watcher.take();
			
			//Poll the directory for certain events
			//Wake up the thread when a directory or a file is created, modified or deleted in the desired directory
			List<WatchEvent<?>> events = watchKey.pollEvents();
			for(WatchEvent event : events){
				if(event.kind() == StandardWatchEventKinds.ENTRY_CREATE){
					System.out.println("* Created: " + event.context().toString());
				}
				if(event.kind() == StandardWatchEventKinds.ENTRY_DELETE){
					System.out.println("* Deleted: " + event.context().toString());
				}
				if(event.kind() == StandardWatchEventKinds.ENTRY_MODIFY){
					System.out.println("* Modified: " + event.context().toString());
				}
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * A method for executing the R script that calculates the weights 
	 * of the model.
	 */
	public void executeRScriptForWeightsElicitation() throws InterruptedException{

		//Invoke the appropriate R script for threshold extraction - Use the fixed paths
		//TODO: Remove this prints
		System.out.println("* R_BIN_PATH= "+ R_BIN_PATH);
		System.out.println("* R_AHP_SCRIPT= "+ weightsScript);
		System.out.println("* DIR= "+ BASE_DIR);
		System.out.println("* ");
		
		executeRScript(RInvoker.R_BIN_PATH, RInvoker.weightsScript, BASE_DIR);

		//Wait for the RScript.exe to finish the analysis by polling the directory for changes
		Path resultsPath = Paths.get(R_WORK_DIR);
		
		try {
			//Create a directory watcher that watches for certain events
			WatchService watcher = resultsPath.getFileSystem().newWatchService();
			resultsPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY );
			WatchKey watchKey = watcher.take();
			
			//Poll the directory for certain events
			//Wake up the thread when a directory or a file is created, modified or deleted in the desired directory
			List<WatchEvent<?>> events = watchKey.pollEvents();
			for(WatchEvent event : events){
				if(event.kind() == StandardWatchEventKinds.ENTRY_CREATE){
					System.out.println("* Created: " + event.context().toString());
				}
				if(event.kind() == StandardWatchEventKinds.ENTRY_DELETE){
					System.out.println("* Deleted: " + event.context().toString());
				}
				if(event.kind() == StandardWatchEventKinds.ENTRY_MODIFY){
					System.out.println("* Modified: " + event.context().toString());
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
		
	/**
	 * A method that loads the path of the RScript executable
	 */
	public static String loadRScriptExecutablePath(){
		
		String rPath = null;
		
		try {
			FileReader fw = new FileReader(new File(R_WORK_DIR + "/RScript_Path.txt").getAbsolutePath());
			BufferedReader bw = new BufferedReader(fw);
			
			rPath = bw.readLine();
			
			bw.close();
			fw.close();
			
			if(rPath.contains(" ")){
				rPath = "\"" + rPath + "\"";
			}
			
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e){
			System.out.println(e.getMessage());
		}
		
		return rPath;
	}

}
