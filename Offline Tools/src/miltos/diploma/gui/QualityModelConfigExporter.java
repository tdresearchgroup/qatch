package miltos.diploma.gui;

import miltos.diploma.Property;
import miltos.diploma.PropertySet;
import miltos.diploma.characteristics.Characteristic;
import miltos.diploma.characteristics.CharacteristicSet;
import miltos.diploma.characteristics.QualityModel;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by Miltos on 16/2/2016.
 */
public class QualityModelConfigExporter {

    /**
     * This method stores the basic information that is needed for the derivation of a QualityModel
     * into an XML file.
     * @param qualityModel
     * @param path
     */
    public static void exportQualityModelConfig(QualityModel qualityModel, String path){

        // Create the root of the Quality Model definition XML file
        Element root = new Element("quality_model");
        root.setAttribute("name", qualityModel.getName());

        // Get the two sub nodes
        Element charRoot = createCharJDOMRepresentation(qualityModel.getCharacteristics());
        Element propRoot = createPropJDOMRepresentation(qualityModel.getProperties());

        // Attach the nodes to the parent root
        root.addContent(charRoot);
        root.addContent(propRoot);

        // Export the XML File
        try {
            //Create an XML Outputter
            XMLOutputter outputter = new XMLOutputter();

            //Set the format of the outputted XML File
            Format format = Format.getPrettyFormat();
            outputter.setFormat(format);

            //Output the XML File to standard output and the desired file
            FileWriter filew = new FileWriter(path);
            outputter.output(root, filew);

        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * This method is used to create the JDOM Tree representation of the node
     * <characteristics> of the XML file that can be used for the importation of the
     * properties and the characteristics for the desired QualityModel.
     *
     * Typically, it is an alternative to the createJDOMRepresentation() method
     * that can be found at CharacteristicsExporter class.
     *
     * Difference : Weights are not exported
     */
    public static Element createCharJDOMRepresentation(CharacteristicSet characteristics){

        //Create an empty "root" element
        Element root = new Element("characteristics");
        root.setName("characteristics");

        //Iterate through the characteristics of this CharacteristicSet
        Iterator<Characteristic> iterator = characteristics.iterator();
        while(iterator.hasNext()){

            //Get the current Characteristic
            Characteristic characteristic = iterator.next();

            //Create a new Element representing this characteristic
            Element charNode = new Element("characteristic");

            //Add the appropriate attributes
            charNode.setAttribute("name", characteristic.getName());
            charNode.setAttribute("standard", characteristic.getStandard());
            charNode.setAttribute("description", characteristic.getDescription());

            //Attach the "characteristic" element to the "characteristics" parent element
            root.addContent(charNode);
        }

        //Return the "root" element of the characteristics
        return root;

    }

    /**
     * This method is used to create the JDOM Tree representation of the node
     * <properties> of the XML file that can be used for the importation of the
     * properties and the characteristics for the desired QualityModel.
     *
     * Typically, it is an alternative to the createJDOMRepresentation() method
     * that can be found at PropertiesExporter class.
     *
     * Difference : thresholds are not exported.
     */
    public static Element createPropJDOMRepresentation(PropertySet properties){

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

            //Attach the "property" element to the "properties" parent element
            rootProp.addContent(prop);
        }

        //Return the "root" element of the properties
        return rootProp;
    }
}
