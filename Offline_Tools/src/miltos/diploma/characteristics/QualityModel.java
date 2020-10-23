package miltos.diploma.characteristics;

import miltos.diploma.PropertySet;

/**
 * This class encapsulates all the appropriate information that describe a 
 * Quality Model that can be used for the evaluation of a single project or 
 * a benchmark of projects.
 * 
 * Typically, it is used in order to load the PropertySet and the CharacteristicSet
 * of the XML file that describes the quality model and assign their values to the 
 * project (or projects) that we want to evaluate.
 * 
 * @author Miltos
 *
 */
public class QualityModel {

	//A representation of quality model inside JVM
	private String name;						//The name of the QM found in the XML file
	private PropertySet properties;				//The PropertySet containing all the properties of the Quality Model
	private CharacteristicSet characteristics;  //The CharacteristicSet containing all the characteristics of the Quality Model
	private Tqi tqi;							//The configuration for the calculation of the TQI of a project
	
	//Constructors
	public QualityModel(){
		this.name = "";
		this.properties = new PropertySet();
		this.characteristics = new CharacteristicSet();
		this.tqi = new Tqi();
	}
	
	public QualityModel(String name){
		this.name = name;
		this.properties = new PropertySet();
		this.characteristics = new CharacteristicSet();
		this.tqi = new Tqi();
	}
	
	public QualityModel(String name, PropertySet properties, CharacteristicSet characteristics, Tqi tqi){
		this.name = name;
		this.properties = properties;
		this.characteristics = characteristics;
		this.tqi = tqi;
	}
	
	//Setters and Getters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PropertySet getProperties() {
		return properties;
	}

	public void setProperties(PropertySet properties) {
		this.properties = properties;
	}

	public CharacteristicSet getCharacteristics() {
		return characteristics;
	}

	public void setCharacteristics(CharacteristicSet characteristics) {
		this.characteristics = characteristics;
	}
	
	public Tqi getTqi() {
		return tqi;
	}

	public void setTqi(Tqi tqi) {
		this.tqi = tqi;
	}
	
	//TODO: Add extra methods for avoiding train expressions...
	
}
