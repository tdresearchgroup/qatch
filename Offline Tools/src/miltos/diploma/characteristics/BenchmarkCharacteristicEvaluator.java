package miltos.diploma.characteristics;

import java.util.Iterator;

import miltos.diploma.BenchmarkProjects;
import miltos.diploma.Project;
import miltos.diploma.Property;

/**
 * This class is responsible for the evaluation of the characteristics
 * (i.e. calculation of the eval field) of a set of projects.
 * 
 * Typically, this class:
 * 
 * 	1. Clones the quality model's CharacteristicSet into the CharacteristicSet
 *     of each project.
 *  2. Invokes a single project evaluator for the evaluation of the characteristics
 *     of each project found in the desired set of projects.
 *     
 * @author Miltos
 * 
 * TODO: Remove the clone method - Add a Cloner class!!!
 */

public class BenchmarkCharacteristicEvaluator {
	
	/**
	 * This method implements the whole functionality of this class.
	 * Its algorithm is pretty straightforward if you read the comments.
	 */
	public void evaluateProjects(BenchmarkProjects projects, CharacteristicSet characteristics) throws CloneNotSupportedException{
		
		//Clone the quality model's characteristics to the CharacteristicSet of each project
		cloneCharacteristics(projects, characteristics);
		
		//Create a single project characteristic evaluator
		ProjectCharacteristicsEvaluator evaluator = new ProjectCharacteristicsEvaluator();
		
		//Iterate through the set of projects
		Iterator<Project> iterator = projects.iterator();
		double progress = 0;
		while(iterator.hasNext()){
			//TODO: Remove this print...
			System.out.print("* Progress : " + (int) (progress/projects.size() * 100) + " %\r");
			//Get the current project
			Project project = iterator.next();
			//Evaluate all its characteristics
			evaluator.evaluateProjectCharacteristics(project);
			//Increment the progress counter
			progress++;
		}
		System.out.print("* Progress : " + (int) (progress/projects.size() * 100) + " %\r");
	}
	
	/**
	 * This method is responsible for copying the CharacteristicSet that contains
	 * all the characteristics of the Quality Model, into the CharacteristicSet of 
	 * each project of the BenchmarkProject. 
	 * 
	 * It is equivalent with the method cloneProperties() that can be found in the
	 * class BenchmarkAggregator and that is used for coping the quality Model's 
	 * properties into the PropertySet of each project.
	 * 
	 */
	public void cloneCharacteristics(BenchmarkProjects projects, CharacteristicSet characteristics) throws CloneNotSupportedException{
		
		//Create an iterator of the available projects
		Iterator<Project> iterator = projects.iterator();
		
		while(iterator.hasNext()){
			
			//Get the current project
			Project project = iterator.next();
			
			//For each characteristic do...
			for(int i = 0; i < characteristics.size(); i++){
				
				//Clone the characteristic and add it to the CharacteristicSet of the current project
				Characteristic c = (Characteristic) characteristics.get(i).clone();
				project.getCharacteristics().addCharacteristic(c);
			}
		}
		
	}

}
