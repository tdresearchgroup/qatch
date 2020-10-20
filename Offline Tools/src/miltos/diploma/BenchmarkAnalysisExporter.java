package miltos.diploma;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;

/**
 * This class is responsible for exporting the results of the benchmark analysis
 * and aggregation in different file formats, so that they can be used by R in
 * order to calculate the thresholds of each property.
 * 
 * More specifically, it does the following:
 * 
 * 	1. Receives a set of projects (i.e. BenchmarkProjects object) as an input.
 * 	   (The projects of the project set should have their normValue field for 
 * 	   each Property object of their PropertySet calculated)
 *  2. It creates the appropriate file (e.g. xls, json etc.) inside the working 
 *     directory of R.
 *  3. It iterates through all the properties of each project of the project set
 *     and writes their normValue values inside the appropriate cells of the xls file.
 *     
 * ATTENTION:
 *   - The properties should be exported in the appropriate order. The order of the
 *     columns of the xls file (or the json objects of the json file) should agree 
 *     with the order of the Property objects found in the PropertySet of each project,
 *     or the total PropertySet containing the Properties of the model in general!!!
 *   
 * @author Miltos
 *
 */

public class BenchmarkAnalysisExporter {

	
	//If you want to include a column with the project Name to the xls file just set the values to 1 and true respectively
	public static final int START = 0;
	public static boolean INCLUDE_NAME = false;
	
	/**
	 * A method for exporting the normValue fields of each Project's Property objects
	 * into a XLS file. The xls file is placed inside R's working directory in order 
	 * to be processed by the appropriate R script. (see RInvoker)
	 * 
	 * The xls file exported by this method has the following form:
	 *    - Each row corresponds to a single project of the BenchmarkProjects object.
	 *    - Each column corresponds to a single Property of the model's properties
	 *    - Each cell contains the calculated normalized value (normValue) of each 
	 *    	project's property.
	 *    
	 *  ATTENTION:
	 *    - The order of the columns is equivalent to the order of the Properties of
	 *      the total PropertySet containing the model's properties.
	 */
	//TODO: Change the name to : exportToXLS()
	public void exportToCsv(BenchmarkProjects projects){
		
		//Set the path where the csv file will be stored and the name of the csv file
		String filename = RInvoker.R_WORK_DIR +  "/properties.xls";
		
		//Create the folder
		File dir = new File(RInvoker.R_WORK_DIR);
		dir.mkdir();
		
		//Create an empty workbook
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Benchmark Analysis Results");
		HSSFRow rowhead = sheet.createRow((short) 0);
		
		//Check if the names of the projects should be included in the XLS file (user defined)
		if(INCLUDE_NAME){
			rowhead.createCell(0).setCellValue("Project_Name");
		}
		
		//Create the header of the xls file
		for(int i = START; i < projects.getProject(0).getProperties().size(); i++){
			
			//Get the i-th property
			Property p = projects.getProject(0).getProperties().get(i);
			
			//Set the name of the i-th column to the name of this Property
			rowhead.createCell(i).setCellValue(p.getName());
		}
		
		//Fill the xls file with the normalized values of each prokect's properties
		int index = 0;
		Iterator<Project> iterator = projects.iterator();
		//Iterate through all the projects
		while(iterator.hasNext()){
			
			//Get the current project
			Project project = iterator.next();
			
			//Create a new row in the csv file 
			index++;
			HSSFRow row = sheet.createRow((short) index);
			
			
			//Check if the names of the projects should be included in the XLS file (user defined)
			if(INCLUDE_NAME){
				row.createCell(0).setCellValue(project.getName());
			}
			
			//Iterate thorough all the properties of the project and store their values into the csv
			for(int i = START; i < project.getProperties().size(); i++){
				
				//Get the current property
				Property p = project.getProperties().get(i);
				
				//Receive the normalized value of this property
				double normValue = p.getMeasure().getNormValue();

				//TODO: Remove the following command
				if(p.getName().equalsIgnoreCase("Volume")) normValue = p.getMeasure().getNormalizer();
				
				//Store it in the appropriate column of the csv file
				row.createCell(i).setCellValue(normValue);
				
			}
		}
		
		//Export the XLS file to the appropriate path (R's working directory)
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(filename);
			workbook.write(fileOut);
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * A method for exporting only the properties of the projects of a set of
	 * projects in a json format. Typically, it exports only the PropertySet
	 * object of each Project found in a property set (object of class BenchmarkProjects).
	 * 
	 * USAGES:
	 *   - It can be used for debugging purposes. Use it instead of exporting 
	 *     everything found in a BenchmarkProject object. (more lightweight)
	 *   - As an example (tutorial), on how to create your own json file.
	 */

	public void exportToJSON(BenchmarkProjects projects, String path){

		//Create the json parser
		Gson gson = new Gson();
		
		//Create the string containing the total json file
		String totalJson = "{\"projects\":[";
		
		//Iterate through the projects
		for(int i = 0; i < projects.size(); i++){
			
			Project project = projects.getProject(i);
			String json = gson.toJson(project.getProperties());
			totalJson += json + ",";
		}
		
		//Close the json file appropriately 
		totalJson += "]}";

		//Save the json file inside R Working Directory for Manipulation
		try{
			FileWriter writer = new FileWriter(path);
			writer.write(totalJson);
			writer.close();
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
}
