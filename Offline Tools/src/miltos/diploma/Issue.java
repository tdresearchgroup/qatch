package miltos.diploma;

/**
 * A class that represents an issue (i.e. a violation of a rule).
 * 
 * Typically, it represents a <violation> entry of a
 * PMD result XML file.
 * 
 * @author Miltos
 *
 */
public class Issue {
	
	//One field for each attribute found in the result XML file
	private String ruleName;
	private String ruleSetName;
	private String packageName;
	private String description;
	private String externalInfoUrl;
	private int priority;
	private int beginLine;
	private int endLine;
	private int beginCol;
	private int endCol;

	//The path to the project's class that this violation belongs to
	private String classPath;

	/**
	 * Setters and getters.
     */
	public String getRuleName() {
		return ruleName;
	}
	
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	
	public String getRuleSetName() {
		return ruleSetName;
	}
	
	public void setRuleSetName(String ruleSetName) {
		this.ruleSetName = ruleSetName;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getExternalInfoUrl() {
		return externalInfoUrl;
	}
	
	public void setExternalInfoUrl(String externalInfoUrl) {
		this.externalInfoUrl = externalInfoUrl;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public int getBeginLine() {
		return beginLine;
	}
	
	public void setBeginLine(int beginLine) {
		this.beginLine = beginLine;
	}
	
	public int getEndLine() {
		return endLine;
	}
	
	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}
	
	public int getBeginCol() {
		return beginCol;
	}
	
	public void setBeginCol(int beginCol) {
		this.beginCol = beginCol;
	}
	
	public int getEndCol() {
		return endCol;
	}
	
	public void setEndCol(int endCol) {
		this.endCol = endCol;
	}

	public String getClassPath() {
		return classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}
}
