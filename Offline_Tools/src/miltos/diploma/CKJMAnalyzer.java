package miltos.diploma;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

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

	ProcessBuilder builder;

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
		builder = new ProcessBuilder("sh","-c","ant -buildfile \"/Users/guribhangu/development/research/qatch/Offline_Tools/build.xml\" " +
				"-Dsrc.dir="+ src +" -Ddest.dir="+ dest);

		if (src.toLowerCase().contains("guava")){
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length()-1) + "/guava/target/classes");
//			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
//					+ ":" + src.substring(1, src.length()-1) + "/guava-gwt/target/classes");
//			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
//					+ ":" + src.substring(1, src.length()-1) + "/guava-testlib/target/classes");
		}

		if (src.toLowerCase().contains("elasticsearch")){
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length()-1) + "/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + "/Users/guribhangu/research/source_code/elasticsearch/nonZero/open_versions" +
					"/elasticsearch-6.0.0/classes");
		}
		if (src.toLowerCase().contains("commons-lang")){
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length()-1) + "/target/classes");
		}

		if (src.toLowerCase().contains("hystrix")){
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length()-1) + "/classes");
			File folder = new File(src.substring(1, src.length()-1)+"/classes");
			File[] listOfFiles = folder.listFiles();
			for (File file: listOfFiles) {
				builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
						+ ":" + src.substring(1, src.length()-1) + "/classes/" + file.getName());
			}
		}

		if (src.toLowerCase().contains("glide")){
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length()-1) + "/classes");
		}


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
				"/dubbo-metrics/dubbo-metrics-api/target/classes",
				"/dubbo-serialization/dubbo-serialization-hessian2/target/classes"};

//		for (int i = 0; i < dubbo_paths.length; i++) {
//			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
//					+ ":" + src.substring(1, src.length()-1) + dubbo_paths[i]);
//		}

		//		// third party dependencies
//		if (src.contains("dubbo-dubbo-2.7") || (src.contains("dubbo-dubbo-2.6") && !src.contains("dubbo-dubbo-2.6.0") && !src.contains("dubbo-dubbo-2.6.1")
//				&& !src.contains("dubbo-dubbo-2.6.2"))) {
//			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
//					+ ":" + "/Users/guribhangu/research/source_code/dubbo/nonZero/open_versions" +
//					"/dubbo-dubbo-2.6.2/hessian-lite/target/classes");
//		}

		String[] retrofit_paths = {
				"/retrofit/target/classes",
				"/retrofit-samples/github-client/target/classes",
				"/retrofit-mock/target/classes",
				"/retrofit/target/classes",
				"/samples/target/classes",
				"/retrofit-adapters/java8/target/classes",
				"/retrofit-adapters/rxjava2/target/classes",
				"/retrofit-adapters/guava/target/classes",
				"/retrofit-adapters/scala/target/classes",
				"/retrofit-adapters/rxjava/target/classes",
				"/retrofit-converters/java8/target/classes",
				"/retrofit-converters/gson/target/classes",
				"/retrofit-converters/wire/target/classes",
				"/retrofit-converters/guava/target/classes",
				"/retrofit-converters/scalars/target/classes",
				"/retrofit-converters/jaxb/target/classes",
				"/retrofit-converters/moshi/target/classes",
				"/retrofit-converters/simplexml/target/classes",
				"/retrofit-converters/protobuf/target/classes",
				"/retrofit-converters/jackson/target/classes",
				"/retrofit/build/classes",
				"/retrofit/build/classes/java/main"
		};

		for (int i = 0; i < retrofit_paths.length; i++) {
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length()-1) + retrofit_paths[i]);
		}

		String[] rxjava_paths = {"rxjava-android",
				"rxjava-android-samples-build-wrapper",
				"rxjava-apache-http",
				"rxjava-async-util",
				"rxjava-clojure",
				"rxjava-computation-expressions",
				"rxjava-contrib",
				"rxjava-core",
				"rxjava-debug",
				"rxjava-examples",
				"rxjava-groovy",
				"rxjava-ios",
				"rxjava-joins",
				"rxjava-jruby",
				"rxjava-kotlin",
				"rxjava-math",
				"rxjava-quasar",
				"rxjava-scala",
				"rxjava-scalaz",
				"rxjava-string",
				"rxjava-swing"};

		if (src.toLowerCase().contains("rjava")) {
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/classes");
			for (int i = 0; i < rxjava_paths.length; i++) {
				builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
						+ ":" + src.substring(1, src.length()-1) + "/classes/" + rxjava_paths[i]);
			}
		}


		try (Stream<Path> paths = Files.walk(Paths.get("/Users/guribhangu/java_dependencies"))) {
			paths.filter(Files::isRegularFile).forEach(this::addToClasspath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		builder.redirectErrorStream(true);
		//Execute the command
		try{
				Process p = builder.start();
				BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
				//Print the console output for debugging purposes
				String line;
				while (true) {
					line = r.readLine();
					if (line == null) { break; }
					System.out.println(line);
				}

		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}

	private void addToClasspath(Path filePath) {
		builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
				+ ":" + filePath.toString());
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
