package miltos.diploma;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import miltos.diploma.characteristics.Characteristic;
import miltos.diploma.characteristics.CharacteristicSet;
import miltos.diploma.characteristics.Tqi;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.google.gson.Gson;

public class TqiExporter {
	
	/**
	 * This method returns the DOM Tree representation of the tqi node of the
	 * Quality Model's XML file.
	 * 
	 * This method is used for the construction of the Quality Model's XML file,
	 * together with the DOM Tree representation of the Properties and the
	 * Characteristics of the Quality Model.
	 * 
	 * @param tqi : The Tqi object containing the weights of the TQI.
	 * @return    : An Element object that correspond to the DOM Tree 
	 * 			    representation of the TQI object.
	 */
	public Element createJDOMRepresentation(Tqi tqi){
		
		//Create an empty "root" element
		Element root = new Element("tqi");
		root.setName("tqi");
		
		//Create a node (element) containing the weights of the tqi object
		Element weights = new Element("weights");
		for(int i = 0; i < tqi.getWeights().size(); i++){

			//Create a weight Element
			Element t = new Element("weight");
			
			//Set the appropriate value of the weight
			t.setText(String.valueOf(tqi.getWeights().get(i)));
			
			//Attach the current weight element to the element named "weights"
			weights.addContent(t);
		}

		//Attach the "weights" element to the "tqi" parent element
		root.addContent(weights);
		
		//Return the "root" element 
		return root;	
	}

	/**
	 * This method exports the JDOM Tree representation of the Tqi object
	 * created by the createJDOMRepresentation() method into an XML file
	 * to the desired path.
	 * 
	 * Basically:
	 * 
	 * 	1. It calls the createJDOMRepresentation() in order to receive the
	 * 	   DOM tree representation of the desired Tqi object.
	 *  2. Stores the DOM tree representation into an XML file in to the
	 *     desired path.
	 * 
	 */
	public void exportTqiToXML(Tqi tqi, String xmlPath){
		
		try {

			//Call the method to get the root element of this document
			Element root = createJDOMRepresentation(tqi);
			
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
	 * This method exports the desired Tqi object into JSON format 
	 * to the desired path.
	 */
	public void exportTqiToJSON(Tqi tqi, String jsonPath){
		
		//Create a Gson json parser
		Gson gson = new Gson();
		
		//Parse your tqi into a json String representation
		String json = gson.toJson(tqi);
		
		try{
			FileWriter writer2 = new FileWriter(jsonPath);
			writer2.write(json);
			writer2.close();
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
}
