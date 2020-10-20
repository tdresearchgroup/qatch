package miltos.diploma;

import java.util.Iterator;

/**
 * This class is responsible for the evaluation of all the 
 * properties of a certain object. 
 * 
 * Typically:
 * 		
 * 		1. it creates an Iterator of the Property objects 
 * 		   found in the project's PropertySet. 
 * 		2. For each property found in this set it calls its build in evaluate() method
 * 		   in order to calculate the eval field of this Property object.
 * 
 * @author Miltos
 *
 */
public class ProjectEvaluator {
	
	public void evaluateProjectProperties(Project project){
		//Iterate through the properties of this project
		Iterator<Property> iterator = project.getProperties().iterator();
		while(iterator.hasNext()){
			//Get the current property of the project
			Property property = iterator.next();
			//Evaluate this property (i.e. calculate its eval field)
			property.evaluate();
		}
	}

}
