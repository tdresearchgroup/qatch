package miltos.diploma;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.google.gson.Gson;

/**
 * This class is responsible for exporting the properties of a PropertySet
 * into different formats (e.g. XML, JSON etc.). It is used primarily 
 * in order to export the properties of the Quality Model into different 
 * formats for debugging purposes.
 * 
 * Basically, it is used for exporting the DOM Tree representation of the
 * property set that, together with the DOM Tree representation of the 
 * CharacteristicSet, contracts the Quality Model XML file.
 * 
 * @author Miltos
 *
 */
public class PropertiesExporter {
	
	//Predefined - recommended paths for storing the XML and JSON files with the Quality Model's properties
	//public static final String PROPERTY_XML_PATH = new File(BenchmarkAnalyzer).getAbsolutePath();
	//public static final String PROPERTY_JSON_PATH = "C:/Users/Miltos/Desktop/properties.json";
	
	public static String BASE_DIR = new File(System.getProperty("user.dir")).getAbsolutePath();
	public static String BENCH_RESULT_PATH = new File(BASE_DIR + "/Results/Evaluation/BenchmarkResults").getAbsolutePath();
	
	/**
	 * This method returns the DOM Tree representation of a set of properties
	 * (i.e. PropertySet object) that receives as an argument.
	 * 
	 * This method is used for the contraction of the Quality Model XML file, 
	 * together with the DOM Tree representation of the Characteristics of 
	 * the model.
	 * 
	 * @param properties : The properties that should be turned into DOM Tree 
	 * 				       representation
	 * @return           : An Element object that correspond to the DOM Tree 
	 * 					   representation of this PropertySet object.
	 * 				       
	 */
	public Element createJDOMRepresentation(PropertySet properties){
		
		//Create an empty "root" element
		Element rootProp = new Element("properties");
		rootProp.setName("properties");
		
		//Iterate through the properties of this PropertySet
		Iterator<Property> iterator = properties.iterator();
		while(iterator.hasNext()){
			
			//Get the current Property
			Property property = iterator.next();
			
			//Create a new Element representing this property
			Element prop = new Element("property");
			
			//Add the appropriate attributes
			prop.setAttribute("name", property.getName());
			prop.setAttribute("type",String.valueOf(property.getMeasure().getType()));
			prop.setAttribute("description", property.getDescription());
			prop.setAttribute("positive_impact", String.valueOf(property.isPositive()));
			
			//TODO: Check how to get rid of this if statements - IDEA: Initialize these fields at value ""
			if(property.getMeasure().getMetricName() != null){
				prop.setAttribute("metricName", property.getMeasure().getMetricName());
			}else{
				prop.setAttribute("metricName", "");
			}
			
			if(property.getMeasure().getRulesetPath() != null){
				prop.setAttribute("ruleset", property.getMeasure().getRulesetPath());
			}else{
				prop.setAttribute("ruleset", "");
			}
			prop.setAttribute("tool", property.getMeasure().getTool());
			//Save the thresholds as well (ascending order) 
			//Create a node (element) containing the thresholds of the property
			Element thresholds = new Element("thresholds");
			for(int i = 0; i < property.getThresholds().length; i++){
				
				//Create a threshold Element
				Element t = new Element("threshold");
				
				//Set the appropriate value of the threshold
				t.setText(String.valueOf(property.getThresholds()[i]));
				
				//Attach the current threshold element to the element named "thresholds"
				thresholds.addContent(t);
			}
			
			//Attach the "thresholds" sub element to the "property" element
			prop.addContent(thresholds);
			
			//Attach the "property" element to the "properties" parent element
			rootProp.addContent(prop);
		}
		
		//Return the "root" element of the properties
		return rootProp;	
	}
	
	/**
	 * This method exports the JDOM Tree representation of the PropertySet
	 * created by the createJDOMRepresentation() method into an XML file
	 * to the desired path.
	 * 
	 * Basically:
	 * 
	 * 	1. It calls the createJDOMRepresentation() in order to receive the
	 * 	   DOM tree representation of the desired PropertySet.
	 *  2. Stores the DOM tree representation into an XML file in to the
	 *     desired path.
	 * 
	 * @param properties  : The properties to be exported in a XML file
	 * @param xmlPath     : The exact path where the XML file will be saved
	 */
	public void exportPropertiesToXML(PropertySet properties, String xmlPath){
		
		
		try {

			//Call the method to get the root element of this document
			Element root = createJDOMRepresentation(properties);
			
			//Create an XML Outputter
			XMLOutputter outputter = new XMLOutputter();
			
			//Set the format of the outputted XML File
			Format format = Format.getPrettyFormat();
			outputter.setFormat(format);
			
			//Output the XML File to standard output and the desired file
			FileWriter filew = new FileWriter(xmlPath);
			//outputter.output(root, System.out);
			outputter.output(root, filew);
			
		} catch (IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * This method exports the desired PropertySet into JSON format 
	 * to the desired path.
	 * 
	 * @param properties
	 * @param jsonPath
	 */
	public void exportPropertiesToJSON(PropertySet properties, String jsonPath){
		
		//Create a Gson json parser
		Gson gson = new Gson();
		
		//Parse your properties into a json String representation
		String json = gson.toJson(properties);
		
		try{
			FileWriter writer2 = new FileWriter(jsonPath);
			writer2.write(json);
			writer2.close();
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
}
