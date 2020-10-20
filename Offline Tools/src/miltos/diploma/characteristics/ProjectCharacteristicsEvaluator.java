package miltos.diploma.characteristics;

import java.util.Iterator;

import miltos.diploma.Project;
import miltos.diploma.Property;

/**
 * This class is responsible for the evaluation of the characteristics
 * of a single project, based on the eval fields of its properties and
 * the weights imported from the QM.
 * 
 * This class is equivalent to ProjectEvaluator which is used for
 * the evaluation of the properties of a single project.
 * 
 * @author Miltos
 *
 */
public class ProjectCharacteristicsEvaluator {
	
	/**
	 * This method implements the whole functionality of this class.
	 * It iterates through the set of characteristics of the certain
	 * project and calls the method evaluate() of each characteristic.
	 */
	public void evaluateProjectCharacteristics(Project project){
		//Iterate through the characteristics of this project
		Iterator<Characteristic> iterator = project.getCharacteristics().iterator();
		while(iterator.hasNext()){
			//Get the current characteristic of the project
			Characteristic characteristic = iterator.next();
			//Evaluate this property (i.e. calculate its eval field)
			characteristic.evaluate(project.getProperties());
		}
	}

}
