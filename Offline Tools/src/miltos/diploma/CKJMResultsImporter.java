package miltos.diploma;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * This class is responsible for importing all the metrics that the ckjm
 * tool calculates for a certain project, into a MetricSet object.
 * 
 * Each object of the MetricSet contains all the metrics of a certain 
 * class of the whole project. Typically, it contains all the data
 * found between the tags <class> and </class> of the ckjmResults XML
 * file.
 * 
 * @author Miltos
 *
 */
public class CKJMResultsImporter {
	
	//The delimiter that the OS uses for the urls and the paths
	private final static char PATH_SEPARATOR = '\\';
	
	/**
	 * The method that is used to parse the calculated metrics from
	 * the XML file containing the results of the CKJM tool for the
	 * desired project.
	 * 
	 * @param path : The exact path to the ckjmResults.xml file
	 */
	public MetricSet parseMetrics(String path){
		
		//A MetricSet object for storing the calculated metrics
		MetricSet metricSet = new MetricSet();
		
		try {	
				//Import the desired xml file with the ckjm results and create the tree representation
				SAXBuilder builder = new SAXBuilder();
				Document doc = builder.build(new File(path));
				Element root = (Element) doc.getRootElement();
			
				//Create a list of all the classes evaluated by the CKJM
				List<Element> classList = root.getChildren();
								
				//Iterate through the classes and parse their metrics
				for(Element el : classList){
					
					//Create a Metrics Object
					Metrics metrics = new Metrics();
					
					//Get the list of metrics of the class node
					List<Element> list = el.getChildren();
					
					//Store the metrics
					metrics.setName(list.get(0).getText());
					metrics.setWmc(Integer.parseInt(list.get(1).getText()));
					metrics.setDit(Integer.parseInt(list.get(2).getText()));
					metrics.setNoc(Integer.parseInt(list.get(3).getText()));
					metrics.setCbo(Integer.parseInt(list.get(4).getText()));
					metrics.setRfc(Integer.parseInt(list.get(5).getText()));
					metrics.setLcom(Integer.parseInt(list.get(6).getText()));
					metrics.setCa(Integer.parseInt(list.get(7).getText()));
					metrics.setCe(Integer.parseInt(list.get(8).getText()));
					metrics.setNpm(Integer.parseInt(list.get(9).getText()));
					metrics.setLcom3(Double.parseDouble(list.get(10).getText()));
					metrics.setLoc(Integer.parseInt(list.get(11).getText()));
					metrics.setDam(Double.parseDouble(list.get(12).getText()));
					metrics.setMoa(Integer.parseInt(list.get(13).getText()));
					metrics.setMfa(Double.parseDouble(list.get(14).getText()));
					metrics.setCam(Double.parseDouble(list.get(15).getText()));
					metrics.setIc(Integer.parseInt(list.get(16).getText()));
					metrics.setCbm(Integer.parseInt(list.get(17).getText()));
					metrics.setAmc(Double.parseDouble(list.get(18).getText()));
					
					//Store the methods as well
					List<Element> methodList = list.get(19).getChildren();
					
					//Iterate through each method found in the XML file
					Vector<MethodLevelAttributes> methods = new Vector<>();
					for(Element methodNode : methodList){

						//Create an object for each method
						MethodLevelAttributes method = new MethodLevelAttributes();
						
						//Set the name and the cyclomatic complexity value of this method
						method.setMethodName(methodNode.getAttributeValue("name"));
						method.setCyclComplexity(Integer.parseInt(methodNode.getText()));
						
						//Add the method to the methods vector
						methods.add(method);
					}
					
					//Add the metrics to the MetricSet
					metrics.setMethods(methods);
					metricSet.addMetrics(metrics);
				}
				
			} catch (JDOMException e) {
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		
		//Return the MetricSet object containing the metrics of each class of the project under evaluation
		return metricSet;
	}
}
