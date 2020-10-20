package miltos.diploma;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import miltos.diploma.characteristics.Characteristic;
import miltos.diploma.characteristics.CharacteristicSet;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.google.gson.Gson;

public class CharacteristicsExporter {
	
	public static String BASE_DIR = new File(System.getProperty("user.dir")).getAbsolutePath();
	public static String BENCH_RESULT_PATH = new File(BASE_DIR + "/Results/Evaluation/BenchmarkResults").getAbsolutePath();
	
	/**
	 * This method returns the JDOM Tree representation of a set of characteristics
	 * (i.e. CharacteristicSet object) that it receives as an argument.
	 * 
	 * This method is used for the construction of the Quality Model XML file, 
	 * together with the JDOM Tree representation of the model's characteristics.
	 * 
	 * @param characteristics : The characteristics that should be turned into JDOM Tree
	 * 				       		representation
	 * @return          	  : An Element object that correspond to the JDOM Tree
	 * 					  	    representation of this CharacteristicSet object.
	 * 				       
	 */
	public Element createJDOMRepresentation(CharacteristicSet characteristics){
		
		//Create an empty "root" element
		Element root = new Element("characteristics");
		root.setName("characteristics");
		
		//Iterate through the characteristics of this CharacteristicSet
		Iterator<Characteristic> iterator = characteristics.iterator();
		while(iterator.hasNext()){
			
			//Get the current Characteristic object
			Characteristic characteristic = iterator.next();
			
			//Create a new Element representing this characteristic
			Element charNode = new Element("characteristic");
			
			//Add the appropriate attributes
			charNode.setAttribute("name", characteristic.getName());
			charNode.setAttribute("standard", characteristic.getStandard());
			charNode.setAttribute("description", characteristic.getDescription());
		

			//Create a node (element) containing the weights of the characteristic
			Element weights = new Element("weights");
			for(int i = 0; i < characteristic.getWeights().size(); i++){
				
				//Create a weight Element
				Element t = new Element("weight");
				
				//Set the appropriate value of the weight
				t.setText(String.valueOf(characteristic.getWeights().get(i)));
				
				//Attach the current weight element to the element named "weights"
				weights.addContent(t);
			}
			
			//Attach the "weights" sub element to the "characteristic" element
			charNode.addContent(weights);
			
			//Attach the "characteristic" element to the "characteristics" parent element
			root.addContent(charNode);
		}
		
		//Return the "root" element of the characteristics
		return root;	
	}
	
	/**
	 * This method exports the JDOM Tree representation of the CharacteristicSet
	 * created by the createJDOMRepresentation() method into an XML file
	 * to the desired path.
	 * 
	 * Basically:
	 * 
	 * 	1. It calls the createJDOMRepresentation() in order to receive the
	 * 	   JDOM tree representation of the desired CharacteristicSet.
	 *  2. Stores the JDOM tree representation into an XML file in to the
	 *     desired path.
	 * 
	 */
	public void exportCharacteristicsToXML(CharacteristicSet characteristics, String xmlPath){
		
		try {

			//Call the method to get the root element of this document
			Element root = createJDOMRepresentation(characteristics);
			
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
	 * This method exports the desired CharacteristicSet into JSON format 
	 * to the desired path.
	 */
	public void exportCharacteristicsToJSON(CharacteristicSet characteristics, String jsonPath){
		
		//Create a Gson json parser
		Gson gson = new Gson();
		
		//Parse your properties into a json String representation
		String json = gson.toJson(characteristics);
		
		try{
			FileWriter writer2 = new FileWriter(jsonPath);
			writer2.write(json);
			writer2.close();
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
}
