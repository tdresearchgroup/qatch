package miltos.diploma.characteristics;

import java.util.Iterator;
import java.util.Vector;

/**
 * This class represents a set of characteristics that are used for the
 * evaluation of a project.
 * 
 * Typically, it is a Vector of Characteristic objects. 
 * It is used in order to load all the information for the characteristics
 * found in the Quality Model's XML file.
 * 
 * @author Miltos
 *
 */
public class CharacteristicSet {
	
	private Vector<Characteristic> characteristics;		//The set of the quality model's characteristics
	
	//Constructor
	public CharacteristicSet(){
		this.characteristics = new Vector<>();
	}

	//Setters and Getters
	public Vector<Characteristic> getCharacteristics() {
		return characteristics;
	}

	public void setCharacteristics(Vector<Characteristic> characteristics) {
		this.characteristics = characteristics;
	}
	
	//Vector methods
	//Override vectors basic methods
	public void addCharacteristic(Characteristic c){
		characteristics.add(c);
	}
	
	public void addCharacteristic(int index, Characteristic c){
		characteristics.add(index, c);
	}
	
	public void clearCharacteristics(){
		characteristics.clear();
	}

	public boolean containsCharacteristic(Characteristic c){
		return characteristics.contains(c);	
	}
	
	public Characteristic get(int index){
		return characteristics.get(index);
	}
	
	public boolean isEmpty(){
		return characteristics.isEmpty();
	}
	
	public Iterator<Characteristic> iterator(){
		return characteristics.iterator();
	}
	
	public int indexOfCharacteristic(Characteristic c){
		return characteristics.indexOf(c);
	}
	
	public void removeCharacteristic(int index){
		characteristics.remove(index);
	}

	public void removeCharacteristic(Characteristic c){
		characteristics.remove(c);
	}
	
	public int size(){
		return characteristics.size();
	}
	
	public Characteristic[] toArray(){
		return (Characteristic[]) characteristics.toArray();
	}
	
	public String toString(){
		return characteristics.toString();
	}
}
