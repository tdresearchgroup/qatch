package miltos.diploma;

import java.util.Arrays;
import java.util.Iterator;

public class PMDAggregator {
	
	//The weights representing the relative importance of each PMD rule category
	//TODO: Define the values of the weights
	public static final int[] WEIGHT = {1, 1, 1, 1, 1};

	/**
	 * This method is responsible for the aggregation of the
	 * issues of a single project.
	 * 
	 */
	public void aggregate(Project project){
		
		//Create an array for storing the number of issues per severity level
		double[] num = new double[5];
		
		//Iterate through the different IsuueSets of the project (i.e. result sets)
		Iterator<IssueSet> iterator = project.issueSetIterator();
		while(iterator.hasNext()){
			
			//Clear the num array
			Arrays.fill(num, 0);
			
			//Get the current IssueSet
			IssueSet issueSet = iterator.next();
			//Iterate through the issues of this IssueSet and count their number per severity
			for(int i = 0; i < issueSet.size(); i++){
				switch (issueSet.get(i).getPriority()){
				  case 1:
					  num[0]++;
				      break;
				  case 2:
					  num[1]++;
					  break;
				  case 3:
					  num[2]++;
					  break;
				  case 4:
					  num[3]++;
					  break;
				  case 5:
					  num[4]++;
					  break;
				}
			}

			//Calculate the value of this issue set
		    int value = 0;
		    for(int i = 0; i < num.length; i++){
		    	value += WEIGHT[i] * num[i];
		    }

		    //Find the property and set its value and its profile ...
		    for(int i = 0; i < project.getProperties().size(); i++){
		    	Property property = project.getProperties().get(i);
		    	if(issueSet.getPropertyName().contains(property.getName())){
					property.getMeasure().setValue(value);
		    		property.setProfile(num.clone());
		    		break;
		    	}
		    }
		    
		}
	}

}
