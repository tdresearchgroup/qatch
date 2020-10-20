package miltos.diploma;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * This class is responsible for analyzing a single project 
 * against:
 * 	
 * 	1. a certain ruleset (i.e. property) or
 *  2. a set of rulesets (i.e. properties) 
 * by invoking the PMD tool.
 *
 * @author Miltos
 *
 */
public class PMDAnalyzer extends AbstractAnalyzer{
	
	public static final String TOOL_NAME = "PMD";

	/**
	 * This method is used in order to analyze a single project against a certain ruleset (property)
	 * by calling the PMD tool through the command line with the appropriate configuration.
	 * 
	 * ATTENTION: 
	 *  - The appropriate build.xml ant file should be placed inside the base directory.
	 */
	public void analyze(String src, String dest, String ruleset, String filename) {

		//Set the path delimiter based on the OS that is used
		if(System.getProperty("os.name").contains("Windows")){
			src = "\"" + src + "\"";
			dest = "\"" + dest + "\"";
			ruleset = "\"" + ruleset + "\"";
		}
		
		//Create the command that should be executed
		ProcessBuilder builder = new ProcessBuilder("cmd.exe","/c","ant -buildfile pmd_build.xml -Dsrc.dir=" + src +" -Ddest.dir="+ dest + " -Druleset.path=" + ruleset + " -Dfilename=" + filename);
		builder.redirectErrorStream(true);

		//Execute the command
		try{
				Process p = builder.start();
				BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
			    //Print the messages to the console for debugging purposes
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
	 * properties (i.e. PMD rulesets) by using the PMD Tool.
	 * 
	 * @param src        : The path of the folder that contains the sources of the project.
	 * @param dest       : The path where the XML files with the results will be placed.
	 * @param properties : The set of properties against which the project will be analyzed.
	 *
	 * Typically this method does the following:
	 * 		
	 * 		1. Iterates through the PropertySet
	 * 		2. For each Property object the method calls the analyze() method in order to 
	 * 	       analyze the project against this single property. 
	 */
	
	public void analyze(String src, String dest, PropertySet properties){

		//Create an Iterator in order to iterate through the properties of the desired PropertySet object
		Iterator<Property> iterator = properties.iterator();
		Property p = null;

		//For each property found in the PropertySet do...
		while(iterator.hasNext()){

			//Get the current property
			p = iterator.next();

			//Check if it is a PMD Property
			//TODO: Check this outside this function
			if(p.getMeasure().getTool().equals(PMDAnalyzer.TOOL_NAME) && p.getMeasure().getType() == Measure.FINDING){//TODO: Remove redundant condition!!!
				//Analyze the project against this property
				analyze(src, dest, p.getMeasure().getRulesetPath(), p.getName());
			}else{
				//System.out.println("* Property : " + p.getName() + " is not a PMD Property!!");
			}
		}
	}
	
}
