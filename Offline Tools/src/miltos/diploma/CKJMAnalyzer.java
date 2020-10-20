package miltos.diploma;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * This class is responsible for analyzing a single project 
 * by invoking the CKJM tool.
 * 
 * This can be done by using the first and the second method
 * of this class respectively. 
 * 
 * @author Miltos
 *
 */
//TODO: CKJM should be invoked at any case because we need the total LOC of the project for normalization purposes.
public class CKJMAnalyzer extends AbstractAnalyzer{
	
	public static final String TOOL_NAME = "CKJM";
	
	/**
	 * This method is used to analyze a single project with the CKJM static analysis 
	 * tool.
	 * 
	 * ATTENTION: 
	 *  - The appropriate build.xml ant file should be placed inside the eclipse folder.
	 *  - TODO: Check if you can provide the path of the build.xml.
	 * 
	 * @param src      : The path of the folder that contains the class files of the project.
	 * @param dest     : The path where the XML file that contains the results will be placed.
	 * 
	 */
	public void analyze(String src, String dest){

		//Check the OS type
		if(System.getProperty("os.name").contains("Windows")){
			src = "\"" + src + "\"";
			dest = "\"" + dest + "\"";
		}

		//Configure the command that should be executed
		ProcessBuilder builder = new ProcessBuilder("cmd.exe","/c","ant  -Dsrc.dir="+ src +" -Ddest.dir="+ dest);
		builder.redirectErrorStream(true);

		//Execute the command
		try{
				Process p = builder.start();
				BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));

				//Print the console output for debugging purposes
				String line;
				while (true) {
					line = r.readLine();
					if (line == null) { break; }
				}

		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}

	/**
	 * This method is responsible for analyzing a single project against a set of 
	 * properties by using the CKJM Tool.
	 * 
	 * @param src      : The path of the folder that contains the class files of the desired project.
	 * @param dest     : The path where the XML file that contains the results should be placed.
	 *
	 * Typically this method does the following:
	 * 		
	 * 		1. Iterates through the PropertySet.
	 * 		2. If it finds at least one property that uses the CKJM tool then it calls the 
	 * 			simple analyze() method.
	 * 
	 * IDEA:
	 *   - All the metrics are calculated for the project and then loaded by the program.
	 *   - After that we decide which metrics to keep by iterating through the PropertySet of
	 *     the Quality Model.
	 *     
	 * It has this form in order to look the same with the PMDAnalyzer.
	 */
		public void analyze(String src, String dest, PropertySet properties){

		//Iterate through the properties of the desired PropertySet object
		Iterator<Property> iterator = properties.iterator();
		Property p = null;

		//For each property found in the desired PropertySet do...
		while(iterator.hasNext()){

			//Get the current property
			p = iterator.next();

			//Check if it is a ckjm property
			//TODO: Check this outside this function
			if(p.getMeasure().getTool().equals(CKJMAnalyzer.TOOL_NAME) && p.getMeasure().getType() == Measure.METRIC){//Redundant condition!!!

				//Analyze this project
				analyze(src, dest);
				
				//Found at least one ckjm property. Process finished.
				break;
				
			}else{
				//Print some messages for debugging purposes
				//System.out.println("* Property : " + p.getName() + " is not a CKJM Property!!");
			}
		}
	}
}
