package miltos.diploma.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import miltos.diploma.Property;
import miltos.diploma.PropertySet;
import miltos.diploma.characteristics.QualityModel;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Miltos on 15/2/2016.
 */
public class QmDefinitionFXLoader {

    // Basic fields
    private String xmlPath;
    private Main mainApp;
    private ObservableList<PropertyFX> propertyData = FXCollections.observableArrayList();
    private ObservableList<CharacteristicFX> charData = FXCollections.observableArrayList();;

    // Constructors
    public QmDefinitionFXLoader(){
        this.xmlPath = "";
    }

    public QmDefinitionFXLoader(String xmlPath, ObservableList<PropertyFX> propertyData){
        this.xmlPath = xmlPath;
        this.propertyData = propertyData;
    }

    public QmDefinitionFXLoader(String xmlPath, Main mainApp){
        this.xmlPath = xmlPath;
        this.mainApp = mainApp;
    }

    // Setters and Getters
    public String getXmlPath() {
        return xmlPath;
    }

    public void setXmlPath(String xmlPath) {
        this.xmlPath = xmlPath;
    }

    public ObservableList<PropertyFX> getPropertyData() {
        return propertyData;
    }

    public void setPropertyData(ObservableList<PropertyFX> propertyData) {
        this.propertyData = propertyData;
    }

    public Main getMainApp() {
        return mainApp;
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * This method is called in order to import the information of the desired quality model
     * from it's XML file. Typically, it parses the XML file and places the information into
     * the appropriate Java objects.
     */
    public void importQualityModel(){

        try {

            // Import the XML file that contains the description of the Quality Model
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new File(xmlPath));
            Element root = (Element) doc.getRootElement();

            // Create a list of the two nodes contained into the xml file
            List<Element> children = root.getChildren();

            // Parse <characteristics> node
            charData = loadCharacteristicsNode(children.get(0));

            // Parse <properties> node
            propertyData = loadPropertiesNode(children.get(1));

            // Save the characteristics and the properties in Main App
            mainApp.setPropertyData(propertyData);
            mainApp.setCharData(charData);

        } catch (JDOMException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * This method parses the <properties> node of the quality model XML file
     * and returns a PropertySet object that contains all the properties
     * found in the XML file.
     */
    private ObservableList<PropertyFX> loadPropertiesNode(Element propertiesNode) {

            // Create a new ObservableList object in order to store the properties of the model
            ObservableList<PropertyFX> properties = FXCollections.observableArrayList();

            // Get a list with all the characteristic nodes of the xml file
            List<Element> propNodes = propertiesNode.getChildren();

            // For each property node do ...
            for(Element propNode : propNodes){

                // Create a new Property object
                PropertyFX property = new PropertyFX();

                // Set the metadata of the current characteristic
                property.setName(propNode.getAttributeValue("name"));
                property.setPositive(Boolean.parseBoolean(propNode.getAttributeValue("positive_impact")));
                property.setDescription(propNode.getAttributeValue("description"));
                property.setMetricName(propNode.getAttributeValue("metricName"));
                property.setRulesetPath(propNode.getAttributeValue("ruleset"));
                int type = Integer.parseInt(propNode.getAttributeValue("type"));
                if(type == 0) {
                    property.setType("Metric");
                }else {
                    property.setType("Finding");
                }
                property.setTool(propNode.getAttributeValue("tool"));


                // Add this property to the ObservableList
                properties.add(property);
            }

            // Return the properties of the quality model
            return properties;
        }

    /**
     * This method parses the <charateristics> node of the quality model XML file
     * and returns an ObservableList  object that contains all the characteristics
     * found in the XML file.
     */
    private ObservableList<CharacteristicFX> loadCharacteristicsNode(Element characteristicNode) {

        // Create a new ObservableList object in order to store the characteristics
        ObservableList<CharacteristicFX> characteristics = FXCollections.observableArrayList();

        // Get a list with all the characteristic nodes of the xml file
        List<Element> charNodes = characteristicNode.getChildren();

        // For each characteristic node do ...
        for(Element charNode : charNodes){
            // Create a new CharacteristicFX object
            CharacteristicFX characteristic = new CharacteristicFX();

            // Set the metadata of the current characteristic
            characteristic.setName(charNode.getAttributeValue("name"));
            characteristic.setDescription(charNode.getAttributeValue("description"));
            characteristic.setStandard(charNode.getAttributeValue("standard"));

            // Add this CharacteristicFX object to the characteristics ObservableList
            characteristics.add(characteristic);
        }
        // Return the characteristics of the quality model
        return characteristics;
    }
}

