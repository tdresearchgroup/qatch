package miltos.diploma;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class is responsible for analyzing a single project 
 * by invoking the CKJM tool.
 * 
 * This can be done by using the first and the second method
 * of this class respectively. 
 * 
 * @author Miltos
 *
 */
//TODO: CKJM should be invoked at any case because we need the total LOC of the project for normalization purposes.
public class CKJMAnalyzer extends AbstractAnalyzer{
	
	public static final String TOOL_NAME = "CKJM";

	/**
	 * This method is used to analyze a single project with the CKJM static analysis 
	 * tool.
	 * 
	 * ATTENTION: 
	 *  - The appropriate build.xml ant file should be placed inside the eclipse folder.
	 *  - TODO: Check if you can provide the path of the build.xml.
	 * 
	 * @param src      : The path of the folder that contains the class files of the project.
	 * @param dest     : The path where the XML file that contains the results will be placed.
	 * 
	 */
	public void analyze(String src, String dest){

		//Check the OS type
		if(System.getProperty("os.name").contains("Mac")){
			src = "\"" + src + "\"";
			dest = "\"" + dest + "\"";
		}

		//Configure the command that should be executed
		ProcessBuilder builder = new ProcessBuilder("sh","-c","ant -buildfile \"/Users/guribhangu/development/research/qatch/Offline_Tools/build.xml\" " +
				"-Dsrc.dir="+ src +" -Ddest.dir="+ dest);

		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + src.substring(1, src.length()-1) + "/bin");

		// dubbo module paths
		String[] dubbo_paths = {"/hystrix-contrib/hystrix-junit/src/main/java",
				"/dubbo-registry/dubbo-registry-multicast/target/classes",
				"/dubbo-registry/dubbo-registry-redis/target/classes",
				"/dubbo-registry/dubbo-registry-zookeeper/target/classes",
				"/dubbo-registry/dubbo-registry-default/target/classes",
				"/hessian-lite/target/classes",
				"/dubbo-filter/dubbo-filter-validation/target/classes",
				"/dubbo-filter/dubbo-filter-cache/target/classes",
				"/dubbo-demo/dubbo-demo-provider/target/classes",
				"/dubbo-demo/dubbo-demo-api/target/classes",
				"/dubbo-demo/dubbo-demo-consumer/target/classes",
				"/dubbo-rpc/dubbo-rpc-http/target/classes",
				"/dubbo-rpc/dubbo-rpc-rmi/target/classes",
				"/dubbo-rpc/dubbo-rpc-hessian/target/classes",
				"/dubbo-rpc/dubbo-rpc-api/target/classes",
				"/dubbo-rpc/dubbo-rpc-redis/target/classes",
				"/dubbo-rpc/dubbo-rpc-default/target/classes",
				"/dubbo-rpc/dubbo-rpc-webservice/target/classes",
				"/dubbo-rpc/dubbo-rpc-memcached/target/classes",
				"/dubbo-rpc/dubbo-rpc-thrift/target/classes",
				"/dubbo-rpc/dubbo-rpc-injvm/target/classes",
				"/dubbo-common/target/classes",
				"/dubbo-admin/target/classes",
				"/dubbo-test/dubbo-test-examples/target/classes",
				"/dubbo-test/dubbo-test-benchmark/target/classes",
				"/dubbo-remoting/dubbo-remoting-grizzly/target/classes",
				"/dubbo-remoting/dubbo-remoting-netty/target/classes",
				"/dubbo-remoting/dubbo-remoting-api/target/classes",
				"/dubbo-remoting/dubbo-remoting-netty4/target/classes",
				"/dubbo-remoting/dubbo-remoting-mina/target/classes",
				"/dubbo-remoting/dubbo-remoting-p2p/target/classes",
				"/dubbo-remoting/dubbo-remoting-zookeeper/target/classes",
				"/dubbo-remoting/dubbo-remoting-http/target/classes",
				"/dubbo-cluster/target/classes",
				"/dubbo-simple/dubbo-registry-simple/target/classes",
				"/dubbo-simple/dubbo-monitor-simple/target/classes",
				"/dubbo-monitor/dubbo-monitor-api/target/classes",
				"/dubbo-monitor/dubbo-monitor-default/target/classes",
				"/dubbo-config/dubbo-config-api/target/classes",
				"/dubbo-config/dubbo-config-spring/target/classes",
				"/dubbo-container/dubbo-container-jetty/target/classes",
				"/dubbo-container/dubbo-container-logback/target/classes",
				"/dubbo-container/dubbo-container-api/target/classes",
				"/dubbo-container/dubbo-container-spring/target/classes",
				"/dubbo-container/dubbo-container-log4j/target/classes",
				"/dubbo-plugin/dubbo-qos/target/classes",
				"/dubbo-registry/dubbo-registry-api/target/classes",
				"/dubbo-compatible/target/classes",
				"/dubbo-rpc/dubbo-rpc-rest/target/classes/",
				"/dubbo-remoting/dubbo-remoting-etcd3/target/classes",
				"/dubbo-serialization/dubbo-serialization-jdk/target/classes",
				"/dubbo-serialization/dubbo-serialization-kryo/target/classes/",
				"/dubbo-configcenter/dubbo-configcenter-api/target/classes",
				"/dubbo-metadata-report/dubbo-metadata-report-api/target/classes",
				"/dubbo-rpc/dubbo-rpc-http-invoker/target/classes",
				"/dubbo-metrics/dubbo-metrics-api/target/classes"};

		for (int i = 0; i < dubbo_paths.length; i++) {
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length()-1) + dubbo_paths[i]);
		}
		
		// third party dependencies
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/log4j-1.2.17.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/spring-context-support-1.0.11.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/org.springframework.beans-3.1.2.release.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/org.springframework.context.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/nacos-api-1.4.0.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/dubbo-metadata-api-2.7.6.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/gson-2.8.2.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/jedis-3.0.0-m1.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/jedis-3.0.0-m1.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/commons-pool2-2.4.2.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/grizzly-framework-3.0.0-M1.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/javax.servlet.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/mina-core-1.1.0.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/netty-3.2.6.Final.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/netty-3.4.6.final.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/netty-all-4.0.0.final.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/netty-all-4.1.54.Final.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/grpc-all-0.13.2.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/hessian-4.0.6.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/swagger-jaxrs-1.6.2.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/citrus-webx-all-3.1.0.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/kryo-2.21.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/spring-remoting-2.0.8.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/org.springframework.core-3.1.0.release.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/experiement/dubbo-dubbo-2.6.2/hessian-lite/target/classes");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/protobuf-java-2.5.0.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/com.google.protobuf-2.4.0.jar");
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + "/Users/guribhangu/java_dependencies/protobuf-java-3.14.0-rc-3.jar");


		builder.redirectErrorStream(true);
		//Execute the command
		try{
				Process p = builder.start();
				BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));

				//Print the console output for debugging purposes
				String line;
				while (true) {
					line = r.readLine();
					System.out.println(line);
					if (line == null) { break; }
				}

		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}

	/**
	 * This method is responsible for analyzing a single project against a set of 
	 * properties by using the CKJM Tool.
	 * 
	 * @param src      : The path of the folder that contains the class files of the desired project.
	 * @param dest     : The path where the XML file that contains the results should be placed.
	 *
	 * Typically this method does the following:
	 * 		
	 * 		1. Iterates through the PropertySet.
	 * 		2. If it finds at least one property that uses the CKJM tool then it calls the 
	 * 			simple analyze() method.
	 * 
	 * IDEA:
	 *   - All the metrics are calculated for the project and then loaded by the program.
	 *   - After that we decide which metrics to keep by iterating through the PropertySet of
	 *     the Quality Model.
	 *     
	 * It has this form in order to look the same with the PMDAnalyzer.
	 */
		public void analyze(String src, String dest, PropertySet properties){
		//Iterate through the properties of the desired PropertySet object
		Iterator<Property> iterator = properties.iterator();
		Property p = null;

		//For each property found in the desired PropertySet do...
		while(iterator.hasNext()){

			//Get the current property
			p = iterator.next();

			//Check if it is a ckjm property
			//TODO: Check this outside this function
			if(p.getMeasure().getTool().equals(CKJMAnalyzer.TOOL_NAME) && p.getMeasure().getType() == Measure.METRIC){//Redundant condition!!!

				//Analyze this project
				analyze(src, dest);
				
				//Found at least one ckjm property. Process finished.
				break;
				
			}else{
				//Print some messages for debugging purposes
				//System.out.println("* Property : " + p.getName() + " is not a CKJM Property!!");
			}
		}
	}
}
