package miltos.diploma;

import java.util.Collections;
import java.util.Comparator;

/**
 * This class is responsible for sorting the a set of projects
 * in a descending order, based on the field "eval".
 * 
 * @author Miltos
 *
 */
public class ProjectRanker {
	/**
	 * Specify the comparator for the sorting process...
	 */
	 static final Comparator<Project> TQI_ORDER = new Comparator<Project>() {
		 public int compare(Project p1, Project p2) {
			 return Double.compare(p2.getTqi().getEval(), p1.getTqi().getEval());
		 }
	 };
	 
	 /**
	  * A method for implementing the desired sorting...
	  */
	 public static void rank(BenchmarkProjects projects){
		 Collections.sort(projects.getProjects(), TQI_ORDER);
	 }
}
