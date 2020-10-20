package miltos.diploma;

import java.io.FileWriter;
import java.io.IOException;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.google.gson.Gson;

import miltos.diploma.characteristics.QualityModel;

/**
 * This class is responsible for the exportation of the XML file that
 * represents the Quality Model, as a result of calibration and weight
 * elicitation.
 * 
 * Typically, it combines the three individual exporters in order to 
 * export all the information concerning the Quality Model, into a single 
 * XML or JSON file.
 * 
 * @author Miltos
 *
 */
public class QualityModelExporter {
	
	/**
	 * A method that creates the JDOM representation of the Quality Model and exports
	 * it in XML format, recognizable by the Evaluator subsystem.
	 * 
	 */
	public void exportQualityModelToXML(QualityModel qualityModel, String xmlPath){
		
		//Create the individual exporters
		PropertiesExporter prExp = new PropertiesExporter();
		CharacteristicsExporter charExp = new CharacteristicsExporter();
		TqiExporter tqiExp = new TqiExporter();
		
		//Retrieve the JDOM Representation of each Quality Model's node
		Element tqiRoot = tqiExp.createJDOMRepresentation(qualityModel.getTqi());
		Element charRoot = charExp.createJDOMRepresentation(qualityModel.getCharacteristics());
		Element propRoot = prExp.createJDOMRepresentation(qualityModel.getProperties());
		
		//Create the root of the Quality Model XML file
		Element root = new Element("quality_model");
		root.setAttribute("name", qualityModel.getName());
		
		//Add the three elements to the root element
		root.addContent(tqiRoot);
		root.addContent(charRoot);
		root.addContent(propRoot);
		
		//Export the QualityModel
		try {
			//Create an XML Outputter
			XMLOutputter outputter = new XMLOutputter();
			
			//Set the format of the outputted XML File
			Format format = Format.getPrettyFormat();
			outputter.setFormat(format);
			
			//Output the XML File to standard output and the desired file
			FileWriter filew = new FileWriter(xmlPath);
			outputter.output(root, filew);
			
		} catch (IOException e){
			System.out.println(e.getMessage());
		}
	}

	/**
	 * A method that exports the Quality Model in JSON format.
	 */
	public void exportQualityModelToJSON(QualityModel qualityModel, String jsonPath){
				//Create a Gson json parser
				Gson gson = new Gson();
				
				//Parse your tqi into a json String representation
				String json = gson.toJson(qualityModel);
				
				try{
					FileWriter writer = new FileWriter(jsonPath);
					writer.write(json);
					writer.close();
				}catch(IOException e){
					System.out.println(e.getMessage());
				}
	}

}
