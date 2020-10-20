package miltos.diploma;

import java.util.Vector;

/**
 * Equivalent to issues. Representation of CKJM results in the
 * java program.
 * 
 * Typically, this class holds all the metrics of a certain class
 * of the whole project.
 * 
 * This metrics can be found inside ckjmResult.xml between the tags
 * <class> and </class>.
 * 
 */

//TODO: Instead of different fields replace it with a Vector of Metric objects
public class Metrics {
	
	private String className;
	private int wmc;
	private int dit;
	private int noc;
	private int cbo;
	private int rfc;
	private int lcom;
	private int ca;
	private int ce;
	private int npm;
	private double lcom3;
	private int loc;
	private double dam;
	private int moa;
	private double mfa;
	private double cam;
	private int ic;
	private int cbm;
	private double amc;
	//The list of methods that can be found in the class
	private Vector<MethodLevelAttributes> methods;

	//Getters and Setters
	
	public String getClassName() {
		return className;
	}
	
	public void setName(String className) {
		this.className = className;
	}
	
	public int getCbo() {
		return cbo;
	}

	public void setCbo(int cbo) {
		this.cbo = cbo;
	}
	
	public int getWmc() {
		return wmc;
	}
	
	public void setWmc(int wmc) {
		this.wmc = wmc;
	}
	
	public int getDit() {
		return dit;
	}
	
	public void setDit(int dit) {
		this.dit = dit;
	}
	
	public int getNoc() {
		return noc;
	}
	
	public void setNoc(int noc) {
		this.noc = noc;
	}
	
	public int getRfc() {
		return rfc;
	}
	
	public void setRfc(int rfc) {
		this.rfc = rfc;
	}
	
	public int getLcom() {
		return lcom;
	}
	
	public void setLcom(int lcom) {
		this.lcom = lcom;
	}
	
	public int getCa() {
		return ca;
	}
	
	public void setCa(int ca) {
		this.ca = ca;
	}
	
	public int getCe() {
		return ce;
	}
	
	public void setCe(int ce) {
		this.ce = ce;
	}
	
	public int getNpm() {
		return npm;
	}
	
	public void setNpm(int npm) {
		this.npm = npm;
	}
	
	public double getLcom3() {
		return lcom3;
	}
	
	public void setLcom3(double lcom3) {
		this.lcom3 = lcom3;
	}
	
	public int getLoc() {
		return loc;
	}
	
	public void setLoc(int loc) {
		this.loc = loc;
	}
	
	public double getDam() {
		return dam;
	}
	
	public void setDam(double dam) {
		this.dam = dam;
	}
	
	public int getMoa() {
		return moa;
	}
	
	public void setMoa(int moa) {
		this.moa = moa;
	}
	
	public double getMfa() {
		return mfa;
	}
	
	public void setMfa(double mfa) {
		this.mfa = mfa;
	}
	
	public double getCam() {
		return cam;
	}
	
	public void setCam(double cam) {
		this.cam = cam;
	}
	
	public int getIc() {
		return ic;
	}
	
	public void setIc(int ic) {
		this.ic = ic;
	}
	
	public int getCbm() {
		return cbm;
	}
	
	public void setCbm(int cbm) {
		this.cbm = cbm;
	}
	
	public double getAmc() {
		return amc;
	}
	
	public void setAmc(double amc) {
		this.amc = amc;
	}
	
	public Vector<MethodLevelAttributes> getMethods() {
		return methods;
	}
	
	public void setMethods(Vector<MethodLevelAttributes> methods) {
		this.methods = methods;
	}
	
	//TODO: Remove the clone() method
	public Metrics clone(){
		return this.clone();
	}
}
