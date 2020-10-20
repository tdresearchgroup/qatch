package miltos.diploma;

/**
 * This class contains attributes that are at method level
 *
 * Typically, this class represents a method in the field
 * of Quality Evaluation, I.e. it represents a
 * method and contains all its quality attributes that 
 * can be used to characterize the quality of the method 
 * (e.g LOC, Cyclomatic Compexity etc.)
 * 
 * @author Miltos
 *
 */
public class MethodLevelAttributes {
	
	private String methodName;
	private int cyclComplexity;
	private int loc;

	//Setters and Getters
	public String getMethodName() {
		return methodName;
	}
	
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public int getCyclComplexity() {
		return cyclComplexity;
	}
	
	public void setCyclComplexity(int cyclComplexity) {
		this.cyclComplexity = cyclComplexity;
	}
	
	public int getLoc() {
		return loc;
	}
	
	public void setLoc(int loc) {
		this.loc = loc;
	}
}
