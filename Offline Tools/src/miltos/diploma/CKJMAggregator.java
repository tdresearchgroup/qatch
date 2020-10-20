package miltos.diploma;

/**
 * This class is responsible for calculating the value of the
 * properties that are quantified by the CKJM static analysis
 * tool.
 * 
 * Typically,
 * 	- It calculates the weighted sum of each metric against all
 *    the classes of the project (classe's loc is used as weight)
 *  - It keeps only the values of the properties that we are 
 *    interested in.
 */

public class CKJMAggregator {
	
	/**
	 * The method that implements the whole functionality of this class.
	 */
	public void aggregate(Project project) {
		
		//Get the MetricSet of the project
		MetricSet metricSet = new MetricSet();
		metricSet = project.getMetrics();
		
		//Initialize the appropriate counters
		int totalLoc = 0;
		int[] metr = new int[18];
		
		for(int i = 0; i < metr.length; i++){
			metr[i] = 0;
		}

		//For each Metrics object (i.e. class) do...
		for(int i = 0; i < metricSet.size(); i++){
			//Get the metrics of the i-th class of the Project
			Metrics metrics = metricSet.get(i);
			
			//Aggregate all the metrics
			int loc = metrics.getLoc();
			totalLoc += loc;
			
			metr[0]  += metrics.getWmc()  * loc;
			metr[1]  += metrics.getDit()  * loc;
			metr[2]  += metrics.getNoc()  * loc;
			metr[3]  += metrics.getCbo()  * loc;
			metr[4]  += metrics.getRfc()  * loc;
			metr[5]  += metrics.getLcom() * loc;
			metr[6]  += metrics.getCa()   * loc;
			metr[7]  += metrics.getCe()   * loc;
			metr[8]  += metrics.getNpm()  * loc;
			metr[9]  += metrics.getLcom3()* loc;
			metr[10] += metrics.getLoc();
			metr[11] += metrics.getDam()  * loc;
			metr[12] += metrics.getMoa()  * loc;
			metr[13] += metrics.getMfa()  * loc;
			metr[14] += metrics.getCam()  * loc;
			metr[15] += metrics.getIc()   * loc;
			metr[16] += metrics.getCbm()  * loc;
			metr[17] += metrics.getAmc()  * loc;
			//TODO: Decide what to do with the Cyclomatic Complexity metric
		    //metr[18] += metrics.getCc() * loc;
		}

		 //Find the property, set its normalizer to the totalLoc and set its value if it is a CKJM property
	    for(int i = 0; i < project.getProperties().size(); i++){
	    	
	    	//Get the current property
	    	Property property = project.getProperties().get(i);
	    	
	    	//Set the property's field normalizer to the totalLOC of the project
	    	property.getMeasure().setNormalizer(totalLoc);
	    	
	    	//Check if this property is quantified by the CKJM tool
	    	if(property.getMeasure().getTool().equalsIgnoreCase("ckjm")){
	    		Measure measure = property.getMeasure();
	    		
	    		//Get the index of the metr array that corresponds to this metric
	    		int index = 0;
	    		index = findIndex(measure.getMetricName());
	    		
	    		//Set the value of the property to the appropriate value of the metr array
	    		measure.setValue(metr[index]);
	    	}
	    }
	}
	
	/**
	 * This method is responsible for finding the index of the array
	 * that corresponds to the desirable measureName
	 */
	//TODO: Turn it to a switch statement
	public int findIndex(String measureName){
		
		int index = -1;
		
		if("WMC".equalsIgnoreCase(measureName)){
			index = 0;
		}else if("DIT".equalsIgnoreCase(measureName)){
			index = 1;
		}else if("NOC".equalsIgnoreCase(measureName)){
			index = 2;
		}else if("CBO".equalsIgnoreCase(measureName)){
			index = 3;
		}else if("RFC".equalsIgnoreCase(measureName)){
			index = 4;
		}else if("LCOM".equalsIgnoreCase(measureName)){
			index = 5;
		}else if("Ca".equalsIgnoreCase(measureName)){
			index = 6;
		}else if("Ce".equalsIgnoreCase(measureName)){
			index = 7;
		}else if("NPM".equalsIgnoreCase(measureName)){
			index = 8;
		}else if("LCOM3".equalsIgnoreCase(measureName)){
			index = 9;
		}else if("LOC".equalsIgnoreCase(measureName)){
			index = 10;
		}else if("DAM".equalsIgnoreCase(measureName)){
			index = 11;
		}else if("MOA".equalsIgnoreCase(measureName)){
			index = 12;
		}else if("MFA".equalsIgnoreCase(measureName)){
			index = 14;
		}else if("CAM".equalsIgnoreCase(measureName)){
			index = 15;
		}else if("IC".equalsIgnoreCase(measureName)){
			index = 16;
		}else if("CBM".equalsIgnoreCase(measureName)){
			index = 17;
		}else if("AMC".equalsIgnoreCase(measureName)){
			index = 18;
		}else if("CC".equalsIgnoreCase(measureName)){
			index = 19;
		}else{
			System.out.println("Not a valid name!!!");
		}
		
		return index;
	}
}
