package miltos.diploma;

/** 
 * This is an abstract class that describes the minimum
 * functionality that an Analyzer should have in order
 * to work properly.
 *
 * e.g. PMDAnalyzer, CKJMAnalyzer etc.
 */

public abstract class AbstractAnalyzer {
	
	public static String TOOL_NAME = "Abstract";
	
	/**
	 * This method is responsible for the execution of the desired static analysis
	 * on the desired project.
	 * @param src : The path to the project to be analyzed
	 * @param dest : The path to the destination folder where the results will be placed.
	 * 
	 */
	public void analyze(String src, String dest){
		//Implement your simple analyzer...
	}
	
	//Alternative
	//public abstract void analyze(String src, String dest, String ruleset, String filename);
	
	public abstract void analyze(String src, String dest, PropertySet properties);
}
