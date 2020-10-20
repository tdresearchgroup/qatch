package miltos.diploma;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom.*;
import org.jdom.input.SAXBuilder;

import java.util.Vector;

/**
 * This class is responsible for importing all the violations of
 * a certain XML file, containing the results of a static analysis 
 * exported by the PMD tool.
 * 
 * @author Miltos
 *
 */

public class PMDResultsImporter {
	
	
	/**
	 * This method is used to parse all the issues (i.e. violations) of a single 
	 * result XML PMD file, that corresponds to a certain property, into a single 
	 * IssueSet object.
	 */
	public IssueSet parseIssues(String path){

		//The IssueSet object used to store all the violations of this property
		IssueSet tempIssues = new IssueSet();
		
		try {
			
			//Import the desired xml file with the violations and create the tree representation
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new File(path));
			Element root = (Element) doc.getRootElement();
			
			//Create a list of all the "files" evaluated by the PMD
			List<Element> fileList = root.getChildren();
			
			//Create the IssueSet that will be returned
			IssueSet issues = new IssueSet();
			
			//Set the property name of the IssueSet
			
			//The OS Separator should be defined for Filename class
			//TODO: Find a better way
			char separator;
			if(System.getProperty("os.name").contains("Windows")){
				separator = '\\';
			}else{
				separator = '/';
			}

			Filename propName = new Filename(path,separator,'.');
			issues.setPropertyName(propName.filename());
			
			//Create an empty list in order to store temporary the violations of each file
			List<Element> list = null;
			
			//Iterate through the list of files 
			for(Element el : fileList){
				
				//Get the violations of the current file
				list = el.getChildren();
				
				/* 
				 * Iterate through the list of violation for this certain 
				 * file and store them in the appropriate objects 
				 */
				for(Element viol : list){
					//Create an empty Issue Object
					Issue issue = new Issue();
					
					//Set the path of the class (file) that this violation belongs to
					issue.setClassPath(el.getAttributeValue("name"));
					
					//Get all the information for this issue from the xml file
					issue.setBeginLine(Integer.parseInt(viol.getAttributeValue("beginline")));
					issue.setEndLine(Integer.parseInt(viol.getAttributeValue("endline")));
					issue.setBeginCol(Integer.parseInt(viol.getAttributeValue("begincolumn")));
					issue.setEndCol(Integer.parseInt(viol.getAttributeValue("endcolumn")));
					issue.setRuleName(viol.getAttributeValue("rule"));
					issue.setRuleSetName(viol.getAttributeValue("ruleset"));
					issue.setPackageName(viol.getAttributeValue("package"));
					issue.setExternalInfoUrl(viol.getAttributeValue("externalInfoUrl"));
					issue.setPriority(Integer.parseInt(viol.getAttributeValue("priority")));
					issue.setDescription(viol.getText());
					
					//Add the issue to the IssueSet
					issues.addIssue(issue);
				}	
			}
			
			tempIssues = issues;
		
		} catch (JDOMException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
		//Return the IssueSet
		return tempIssues;
	}

	//TODO: Check if we should follow this approach - If not remove this method
	public Vector<IssueSet> parseIssuesPerFile(String path){

		Vector<IssueSet> tempIssues = new Vector<>();

		try {

			//The OS Separator should be defined for Filename class
			//TODO: Find a better way
			char separator;
			if(System.getProperty("os.name").contains("Windows")){
				separator = '\\';
			}else{
				separator = '/';
			}

			/* Import the desired xml file with the violations and create the tree representation */
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new File(path));
			Element root = (Element) doc.getRootElement();

			/* Create a list of all the "files" evaluated by the PMD */
			List<Element> fileList = root.getChildren();

			/* Print the number of files contained in this project for debugging purposes */
			System.out.println(" PMD found and evaluated : " + fileList.size() + " classes in this project.");



			/* Create an empty list in order to store temporary the violation nodes of each file node */
			List<Element> list = null;

			/* Create the IssueSet to store the issues of the current file*/
			//IssueSet issues = new IssueSet();

			/* Create an empty Issue Object */
			// Issue issue = new Issue();

			/* Iterate through the list of files */
			for(Element el : fileList){
					/* Create the IssueSet to store the issues of the current file*/
				IssueSet issues = new IssueSet();
				/* Save the name and the absolute path of the file */
				Filename fileName = new Filename(el.getAttributeValue("name"),separator,'.');
				issues.setFileName(fileName.filename());
				issues.setFilePath(fileName.path());

				Filename propName = new Filename(path,separator,'.');
				issues.setPropertyName(propName.filename());


				/* Get the violations of the current file */
				list = el.getChildren();
				System.out.println("Found : " + list.size() + " violations for the file : " + el.getAttributeValue("name"));

				/* Iterate through the list of violation for this certain file and store them in the appropriate objects */
				for(Element viol : list){
					/* Create an empty Issue Object */
					Issue issue = new Issue();
					/* Get all the information for this issue from the xml file */
					issue.setBeginLine(Integer.parseInt(viol.getAttributeValue("beginline")));
					issue.setEndLine(Integer.parseInt(viol.getAttributeValue("endline")));
					issue.setBeginCol(Integer.parseInt(viol.getAttributeValue("begincolumn")));
					issue.setEndCol(Integer.parseInt(viol.getAttributeValue("endcolumn")));
					issue.setRuleName(viol.getAttributeValue("rule"));
					issue.setRuleSetName(viol.getAttributeValue("ruleset"));
					issue.setPackageName(viol.getAttributeValue("package"));
					issue.setExternalInfoUrl(viol.getAttributeValue("externalInfoUrl"));
					issue.setPriority(Integer.parseInt(viol.getAttributeValue("priority")));
					issue.setDescription(viol.getText());

					/* Add the issue to the IssueSet */
					issues.addIssue(issue);
				}
				/* Add the current IssueSet to the IssueSet Vector */
				tempIssues.add(issues);
			}

		} catch (JDOMException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		/* Return the IssueSet */
		return tempIssues;
	}

}
