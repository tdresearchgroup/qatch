package miltos.diploma;

public class Measure implements Cloneable {
	
	//TODO: Turn type field into String
	public static final int METRIC = 0;
	public static final int FINDING = 1;

	private double value;//The value of it's metric (metric or number of violations/findings)
	private double normValue;//The normalized value of it's measure (metric or findings)
	private int type;//The type of the property(METRIC or FINDING)
	private String metricName;
	private String rulesetPath;
	private String tool;
	private int normalizer;
	
	public Measure(){
		//Empty constructor in order to avoid null pointer exception during quality model import...
	}

	public Measure(int type){
		if(type == METRIC || type == FINDING){
			this.type = type;
		}else{
			System.out.println("Invalid measure type! A measure can be either METRIC or FINDING!");
		}
		
	}
	
	public Measure(int type, String metricName){
		if(type == METRIC || type == FINDING){
			this.type = type;
		}else{
			System.out.println("Invalid measure type! A measure can be either METRIC or FINDING!");
		}
		
		this.metricName = metricName;
	}
	
	public Measure(int type, String metricName, String rulesetPath){
		if(type == METRIC || type == FINDING){
			this.type = type;
		}else{
			System.out.println("Invalid measure type! A measure can be either METRIC or FINDING!");
		}
		
		this.metricName = metricName;
		this.rulesetPath = rulesetPath;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public double getNormValue() {
		return normValue;
	}
	
	public void setNormValue(double normValue) {
		this.normValue = normValue;
	}
	
	public String getTool() {
		return tool;
	}

	public Measure setTool(String tool) {
		this.tool = tool;
		return this;
	}

	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}

	public String getMetricName() {
		return metricName;
	}
	
	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public String getRulesetPath() {
		return rulesetPath;
	}
	
	public void setRulesetPath(String rulesetPath) {
		this.rulesetPath = rulesetPath;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
	    return super.clone();
	}

	public int getNormalizer() {
		return normalizer;
	}

	public void setNormalizer(int normalizer) {
		this.normalizer = normalizer;
	}
	
	/**
	 * This method calculates the normalized value of this measure.
	 * It just divides the value field by the normalizer field.
	 */
	public void calculateNormValue(){
		if(this.normalizer != 0){
			this.normValue = this.value/this.normalizer;
		}else{
			System.out.println("Devision by zero avoided!!");
		}
	}
}
