package miltos.diploma.characteristics;

import java.io.File;
import java.io.IOException;
import java.util.List;

import miltos.diploma.Filename;
import miltos.diploma.Issue;
import miltos.diploma.IssueSet;
import miltos.diploma.PropertySet;
import miltos.diploma.Property;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * This class is responsible for loading the Quality Model from 
 * the appropriate XML file.
 * 
 * @author Miltos
 *
 *TODO: Remove the elements <weights> and <thresholds> from the XML file.
 *		Place the <weight> and <threshold> elements directly inside 
 *      characteristic and property nodes.
 */
public class QualityModelLoader {
	
	private String xmlPath;				//The exact path where the XML file that contains the QM description is placed
	
	
	//Constructors
	public QualityModelLoader(){
		this.xmlPath = "";
	}
	
	public QualityModelLoader(String xmlPath){
		this.xmlPath = xmlPath;
	}
	
	//Setters and Getters
	public String getXmlPath() {
		return xmlPath;
	}

	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}

	//Other Methods
	/**
	 * This method is responsible for importing the desired Quality Model
	 * by parsing the XML file that contains the description of the Quality
	 * Model.
	 * 
	 * Typically, this method:
	 * 	1. Creates an empty QualityModel object
	 *  2. Fetches the 3 basic elements of the xml file (i.e. <tqi>, 
	 *     <characteristics> and <properties>)
	 *  3. Passes each element to the appropriate build-in method that is 
	 *     responsible for the parsing of a specific node
	 *  4. Sets the fields of the QualityModel object to the values returned
	 *     by these methods.
	 *  5. Returns the QualityModel object
	 *  
	 * @return : A QualityModel object containing the object oriented representation 
	 *          of the desired quality model.
	 */
	public QualityModel importQualityModel(){
		//Create an empty QualityModel Object
		QualityModel qualityModel = new QualityModel();
		
		try {
			
			// Import the XML file that contains the description of the Quality Model
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new File(xmlPath));
			Element root = (Element) doc.getRootElement();
			
			// Create a list of the three nodes contained into the xml file
			List<Element> children = root.getChildren();
			
			//Set the name of the QualityModel object
			qualityModel.setName(root.getAttributeValue("name"));
			
			//TODO:Remove this prints
			// System.out.println("* Quality Model Name    : " +  root.getAttributeValue("name"));
			// System.out.println("* Number of child nodes : " + children.size());
			
			// int i = 0;
			// for(Element e : children){
			// 	System.out.println("* Element : " + (i+1) + " name : " + e.getName() );
			// 	i++;
			// }
			
			//Parse <tqi> node
			qualityModel.setTqi(loadTqiNode(children.get(0)));
			
			//Parse <characteristics> node
			qualityModel.setCharacteristics(loadCharacteristicsNode(children.get(1)));
			
			//Parse <properties> node
			qualityModel.setProperties(loadPropertiesNode(children.get(2)));
			
			
			} catch (JDOMException e) {
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		
		//Return the imported Quality Model
		return qualityModel;
	}
	
	/**
	 * This method parses the <tqi> node of the quality model XML file 
	 * and returns a Tqi object that contains the parsed information.
	 */
	public static Tqi loadTqiNode(Element tqiNode){
		
		//Create a new Tqi object
		Tqi tqi = new Tqi();
		
		//Create a list with the weight nodes of the tqi node
		List<Element> weightsNode = tqiNode.getChildren();
		List<Element> weights = weightsNode.get(0).getChildren();
		
		//For each <weight> node do...
		for(Element weight : weights){
			tqi.addWeight(Double.parseDouble(weight.getText()));
		}
		
		//Return the tqi object
		return tqi;
	}
	
	
	/**
	 * This method parses the <characteristics> node of the quality model XML file 
	 * and returns a CharacteristicSet object that contains all the characteristics
	 * found in the XML file.
	 */
	public static CharacteristicSet loadCharacteristicsNode(Element characteristicsNode){
		
		//Create a new CharacteristicSet object
		CharacteristicSet characteristics = new CharacteristicSet();
		
		//Get a list with all the characteristic nodes of the xml file
		List<Element> charNodes = characteristicsNode.getChildren();
		
		//For each characteristic node do ...
		for(Element charNode : charNodes){
			
			//Create a new Characteristic object
			Characteristic characteristic = new Characteristic();
			
			//Set the metadata of the current characteristic
			characteristic.setName(charNode.getAttributeValue("name"));
			characteristic.setStandard(charNode.getAttributeValue("standard"));
			characteristic.setDescription(charNode.getAttributeValue("description"));
			
			//Get the list of the characteristic's weights
			//TODO: Remove the <weights> node from the xml - list the weights directly inside <characteristic> element
			List<Element> weightsNode = charNode.getChildren();
			List<Element> weights = weightsNode.get(0).getChildren();
			//For each weight node do ...
			for(Element weight : weights){
				//Get the current weight and add it to the characteristic
				characteristic.addWeight(Double.parseDouble(weight.getText()));
			}
			
			//Add the characteristic to the CharacteristicSet
			characteristics.addCharacteristic(characteristic);
		}
		
		//Return the characteristics of the quality model
		return characteristics;
	}
	
	/**
	 * This method parses the <properties> node of the quality model XML file 
	 * and returns a PropertySet object that contains all the properties
	 * found in the XML file.
	 */
	public static PropertySet loadPropertiesNode(Element propertiesNode){
		
		//Create a new PropertySet object
		PropertySet properties = new PropertySet();
		
		//Get a list with all the characteristic nodes of the xml file
		List<Element> propNodes = propertiesNode.getChildren();
		
		//For each property node do ...
		for(Element propNode : propNodes){
			//Create a new Property object
			Property property = new Property();
			
			//Set the metadata of the current characteristic
			property.setName(propNode.getAttributeValue("name"));
			property.setPositive(Boolean.parseBoolean(propNode.getAttributeValue("positive_impact")));
			property.setDescription(propNode.getAttributeValue("description"));
			property.getMeasure().setMetricName(propNode.getAttributeValue("metricName"));
			property.getMeasure().setRulesetPath(propNode.getAttributeValue("ruleset"));
			property.getMeasure().setType(Integer.parseInt(propNode.getAttributeValue("type")));
			property.getMeasure().setTool(propNode.getAttributeValue("tool"));
			
			//Get the thresholds of the property
			//Get the list of the property's thresholds
			//TODO: Remove the <thresholds> node from the xml - list the thresholds directly inside <threshold> element
			List<Element> thresholdsNode = propNode.getChildren();
			List<Element> thresholds = thresholdsNode.get(0).getChildren();
			
			//For each threshold node do ...
			int i = 0;
			for(Element threshold : thresholds){
				//Get the current threshold and add it to the property
				property.getThresholds()[i] = Double.parseDouble(threshold.getText());
				i++;
			}
			
			//Add the property to the PropertySet
			properties.addProperty(property);
			
		}
		
		//Return the properties of the quality model
		return properties;
	}

}
