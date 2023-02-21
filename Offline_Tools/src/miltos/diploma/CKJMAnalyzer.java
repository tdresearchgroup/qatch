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
import java.util.List;
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
		builder = new ProcessBuilder("sh","-c","ant -buildfile \"/Users/research/IdeaProject/qatch/Offline_Tools/build.xml\" " +
				"-Dsrc.dir="+ src +" -Ddest.dir="+ dest);

		if (src.toLowerCase().contains("guava")){
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length()-1) + "/guava/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length()-1) + "/guava-gwt/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length()-1) + "/guava-testlib/target/classes");
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
			if (listOfFiles != null) {
				for (File file: listOfFiles) {
					builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
							+ ":" + src.substring(1, src.length()-1) + "/classes/" + file.getName());
				}
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
//					+ ":" + "/Users/research/Documents/classFiles/dubbo/DD/open_versions" +
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

		if (src.toLowerCase().contains("retrofit")) {
			for (int i = 0; i < retrofit_paths.length; i++) {
				builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
						+ ":" + src.substring(1, src.length()-1) + retrofit_paths[i]);
			}
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
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + "/Users/guribhangu/some_stuff/RxJava-rxjava-0.11.0/classes/rxjava-core");
		}

		if (src.toLowerCase().contains("netty")) {
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + "/Users/research/Documents/classFiles/netty/DD/" +
					"in_between_versions/netty-netty-4.1.18.Final/codec-haproxy/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + "/Users/research/Documents/classFiles/netty/" +
					"DD/in_between_versions/netty-netty-4.1.21.Final/codec-redis/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + "/Users/research/Documents/classFiles/netty/" +
					"DD/in_between_versions/netty-netty-4.1.21.Final/codec-smtp/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + "/Users/research/Documents/classFiles/netty/" +
					"NDD/in_between_versions/netty-netty-4.1.21.Final/transport-native-unix-common/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + "/Users/research/Documents/classFiles/netty/" +
					"NDD/in_between_versions/netty-netty-4.1.21.Final/transport-native-epoll/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + "/Users/research/Documents/classFiles/netty/" +
					"NDD/in_between_versions/netty-netty-4.1.21.Final/codec-haproxy/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + "/Users/research/Documents/classFiles/netty/" +
					"NDD/in_between_versions/netty-netty-4.1.21.Final/codec-haproxy/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + "/Users/research/Documents/classFiles/netty/" +
					"NDD/in_between_versions/netty-netty-4.1.21.Final/codec-haproxy/target/classes");

			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/handler/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/codec-http/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/transport/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/example/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/codec/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/common/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/buffer/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/codec-socks/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/transport-rxtx/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/transport-udt/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/transport-sctp/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/microbench/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/codec-memcache/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/handler-proxy/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/resolver/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/codec-xml/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/codec-stomp/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/codec-haproxy/tarcodec-mqttget/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/codec-http2/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/codec-mqtt/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/resolver-dns/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/testsuite/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/codec-dns/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/codec-mqtt/target/classes");
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + src.substring(1, src.length() - 1) + "/transport-native-unix-common-tests");
		}

		if (src.toLowerCase().contains("selenium") || src.toLowerCase().contains("springboot")
				|| src.toLowerCase().contains("mockito")) {
			try (Stream<Path> paths = Files.walk(Paths.get(src.substring(1, src.length() - 1) + "/classes"))) {
				paths.filter(Files::isDirectory).forEach(this::addToClasspath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (src.toLowerCase().contains("spring-framework")) {
			try (Stream<Path> paths = Files.walk(Paths.get(src.substring(1, src.length() - 1) + "/classes"))) {
				paths.filter(Files::isDirectory).forEach(this::addToClasspath);
			} catch (IOException e) {
				e.printStackTrace();
			}
			builder.environment().put("CLASSPATH", builder.environment().get("CLASSPATH")
					+ ":" + "/Users/research/Documents/classFiles/springframework/NDD/close_versions/" +
					"spring-framework-4.0.0.RC1/classes/spring-websocket");
		}



		try (Stream<Path> paths = Files.walk(Paths.get("/Users/guribhangu/java_dependencies"))) {
			paths.filter(Files::isRegularFile).forEach(this::addToClasspath);
		} catch (IOException e) {
			e.printStackTrace();
		}
//		try (Stream<Path> paths = Files.walk(Paths.get("/Users/guribhangu/java_dependencies_2"))) {
//			paths.filter(Files::isRegularFile).forEach(this::addToClasspath);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		try (Stream<Path> paths = Files.walk(Paths.get("/Users/guribhangu/java_dependencies_springboot"))) {
//			paths.filter(Files::isRegularFile).forEach(this::addToClasspath);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		builder.redirectErrorStream(true);
		//Execute the command
		try{
				Process p = builder.start();
				BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
				// Print the console output for debugging purposes
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

	static public List<File> getDirs(File parent, int level){
		List<File> dirs = new ArrayList<File>();
		File[] files = parent.listFiles();
		if (files == null) return dirs; // empty dir
		for (File f : files){
			if (f.isDirectory()) {
				if (level == 0) dirs.add(f);
				else if (level > 0) dirs.addAll(getDirs(f,level-1));
			}
		}
		return dirs;
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

	/**
	 * Recursively takes a filepath, looks through all the subfolders and files held therein,
	 * and finds every directory containing a .class file
	 * @param basePath: the current filepath to check through recursively
	 * @return a List of strings, each being the directory path to a folder contianing .class files
	 * It is my hope that this can replace about 300 lines of hardcoded directories up top in here ~Bex
	 */
	public static List getClassPaths(File basePath){
		// get a list of all the files in this directory
		File[] flist = basePath.listFiles();
		List<String> dirList = new ArrayList<String>();
		if(flist != null) for(File file : flist) {
			// if this file is another directory, recurse into it
			if(file.isDirectory()){
				// recurse and keep looking
				dirList.addAll(getClassPaths(file));
			} else if(file.isFile() && file.getName().endsWith(".class")){
				// if the file is a file AND ends in .class
				// check to see if basepath's string is in the list, if it isn't, add
				if(!dirList.contains(basePath.getAbsolutePath())){
					dirList.add(basePath.getAbsolutePath());
				}
			}
		} else {
			System.out.println("Whoops! Nothing there");
		}
		return dirList;
	}
}
