package miltos.diploma.webapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class MessagesServlet
 */
@WebServlet("/MessagesServlet")
public class MessagesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//Database information - These are the default values
	public static String SERVER_NAME = "MILTOS-PC";
	public static String DB_NAME = "messages_DB";
	public static String USERNAME = "quality";
	public static String PASSWORD = "quality";

	//Fields
	private boolean success = true;
	private static Connection con;  

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//TODO: Remove these prints 
		System.out.println("I am ivoked!!!");
		System.out.println(request.getParameter("project"));
		System.out.println(request.getParameter("email"));
		
		
		if(request.getParameter("project")!=null && request.getParameter("email")!=null ){
			
			//TODO: Remove these prints 
			System.out.println("Calling the appropriate script!");	
			System.out.println("java  -jar "+ " onlineEvaluationScript.jar "+ request.getParameter("project") +" "+ request.getParameter("email") +" " + request.getParameter("model") + " " + request.getParameter("inspection"));
			
			ProcessBuilder builder = new ProcessBuilder("cmd.exe","/c","java -jar "+ " onlineEvaluationScript.jar "+ request.getParameter("project") +" "+ request.getParameter("email") +" " + request.getParameter("model") + " " + request.getParameter("inspection"));
			builder.redirectErrorStream(true);
			
			try{
					Process p = builder.start();
					BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line;
					while (true) {
						line = r.readLine();
						if (line == null) { break; }
						System.out.println(line);
						if(line.contains("Exception")){
						    success = false;
						}
					}
					
					// Success or failure??
					if(success){
						//TODO: Remove this print
						System.out.println("Successfull!!");
						
						try {
							viewResultsPage(response, request);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}else{
						success = true;
						
						//TODO: Remove this print
						System.out.println("Unsuccessfull!!");
						
						RequestDispatcher view = request.getRequestDispatcher("error.html");
						try {
							view.forward(request, response);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
			}catch(IOException e){
				System.out.println(e.getMessage());
			} 

		
			
		}else{
			System.out.println("They are null!!");
			// Redirect to the main page
			RequestDispatcher view = request.getRequestDispatcher("index.html");
			view.forward(request, response);
		}
				
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//TODO: Remove this print
		System.out.println("POST Invoked");

		// Check if the users wants to evaluate a project
		if(request.getParameter("project")!=null && request.getParameter("email")!=null ){
			System.out.println("Calling the appropriate script!");
				
			System.out.println("java  -jar "+ " onlineEvaluationScript.jar "+ request.getParameter("project") +" "+ request.getParameter("email") +" " + request.getParameter("model") + " " + request.getParameter("inspection"));
			ProcessBuilder builder = new ProcessBuilder("cmd.exe","/c","java -jar "+ " onlineEvaluationScript.jar "+ request.getParameter("project") +" "+ request.getParameter("email") +" " + request.getParameter("model") + " " + request.getParameter("inspection"));
			
			builder.redirectErrorStream(true);
			
			
			try{
					Process p = builder.start();
					BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line;
					while (true) {
						line = r.readLine();
						if (line == null) { break; }
						System.out.println(line);
						if(line.contains("Exception")){
						    success = false;
						}
					}
					
					// Success or failure??
					if(success){
						// TODO: Remove this print
						System.out.println("Successfull!!");
						
						try {
							viewResultsPage(response, request);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}else{
						success = true;
						
						// TODO: Remove this print 
						System.out.println("Unsuccessfull!!");
						
						RequestDispatcher view = request.getRequestDispatcher("error.html");
						try {
							view.forward(request, response);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
			}catch(IOException e){
				System.out.println(e.getMessage());
			} 
			
		}else{
			System.out.println("They are null!!");
		}
		
		// Check if the user wants to post a message
		if(request.getParameter("name")!=null && request.getParameter("email-us")!=null && request.getParameter("message")!=null ){
			
			// Call the interface jar in order to store the message
			ProcessBuilder builder = new ProcessBuilder("cmd.exe","/c","java -jar ./interfaces/messagesDBInterface.jar " + request.getParameter("name") + " " + request.getParameter("email-us") + " \"" + request.getParameter("message")+"\"");
			builder.redirectErrorStream(true);
			
			// TODO: Remove this print
			System.out.println("Done");
			try{
				Process p = builder.start();
				BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while (true) {
					line = r.readLine();
					if (line == null) { break; }
				}
					
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
			
		// Redirect to the main page
		RequestDispatcher view = request.getRequestDispatcher("index.html");
		view.forward(request, response);	
			
	}		
}
	
	public static void viewResultsPage(HttpServletResponse response, HttpServletRequest request) throws IOException, JSONException{
		
		// HTML document
		String htmlFile = "<!DOCTYPE html>"
				+ "<!-- saved from url=(0043)http://getbootstrap.com/examples/dashboard/ -->"
				+ "<html lang=\"en\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
				+ "<meta charset=\"utf-8\">"
				+ "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">"
				+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
				+ "<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->"
				+ "<meta name=\"description\" content=\"\">"
				+ "<meta name=\"author\" content=\"\">"
				+ "<link rel=\"icon\" href=\"http://getbootstrap.com/favicon.ico\">"
				+ "<title>Dashboard Template for Bootstrap</title>"
				+ "<!-- Bootstrap core CSS -->"
				+ "<link href=\"./Dashboard Template for Bootstrap_files/bootstrap.min.css\" rel=\"stylesheet\">"
				+ "<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->"
				+ "<link href=\"./Dashboard Template for Bootstrap_files/ie10-viewport-bug-workaround.css\" rel=\"stylesheet\">"
				+ "<!-- Custom styles for this template -->"
				+ "<link href=\"./Dashboard Template for Bootstrap_files/dashboard.css\" rel=\"stylesheet\">"
				+ "<!-- Just for debugging purposes. Don't actually copy these 2 lines! -->"
				+ "<!--[if lt IE 9]><script src=\"../../assets/js/ie8-responsive-file-warning.js\"></script><![endif]-->"
				+ "<script src=\"./Dashboard Template for Bootstrap_files/ie-emulation-modes-warning.js\"></script>"
				+ "<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->"
				+ "<!--[if lt IE 9]>"
				+ "<script src=\"https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js\"></script>"
				+ "<script src=\"https://oss.maxcdn.com/respond/1.4.2/respond.min.js\"></script>"
				+ "<![endif]-->"
				+ "</head>"
				+ "<body>"
				+ "<nav class=\"navbar navbar-inverse navbar-fixed-top\" style=\"background-color:#555\">"
				+ "<div class=\"container-fluid\">"
				+ "<div class=\"navbar-header\">"
				+ "<button type=\"button\" class=\"navbar-toggle collapsed\" data-toggle=\"collapse\" data-target=\"#navbar\" aria-expanded=\"false\" aria-controls=\"navbar\">"
				+ "<span class=\"sr-only\">Toggle navigation</span>"
				+ "<span class=\"icon-bar\"></span>"
				+ "<span class=\"icon-bar\"></span>"
				+ "<span class=\"icon-bar\"></span>"
				+ "</button>"
				+ "<a class=\"navbar-brand\" href=\"index.html\" style=\"color:#eeeeee\">Quality Evaluator</a>"
				+ "</div>"
				+ "<div id=\"navbar\" class=\"navbar-collapse collapse\">"
				+ "<ul class=\"nav navbar-nav navbar-right\">"
				+ "<li><a href=\"index.html\">Home</a></li>"
				+ "</ul>"
				+ "</div>"
				+ "</div>"
				+ "</nav>"
				+ "<div class=\"container-fluid\">"
				+ "<div class=\"row\">"
				+ "<div class=\"col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-1 main\" align=\"center\">"
				+ "<!-- Stars Section -->"
				+ "<h1 class=\"page-header\">Evaluation Results</h1>"
				+ "<!--label style=\"font-family: Calibri; font-size:40px\" >TQI</label-->"
				+ "<br>";
		
		// Load the JSON file
		System.out.println("\n\n\n\n**********************   TEST   *****************************");
		File gitRepo = new File(request.getParameter("project"));
		String remotePath = gitRepo.getAbsolutePath();
        String folderName = new File(remotePath).getName();
        String projName = folderName.replace(".git", "");
        System.out.println(projName);
        String projFullName = projName + "_" + request.getParameter("email");
        System.out.println("NAME : eval_" + projFullName + ".json");
       
     
		BufferedReader br = new BufferedReader(new FileReader("./Results/Evaluation/SingleProjectResults/eval_" + projFullName +".json"));
		
		// Parse the file		
		String malformedJson = br.readLine();
		
		// Print it
		System.out.println(malformedJson);
		
		// Get the main object
		JSONObject obj = new JSONObject(malformedJson);
		
		// Set TQI and Stars Section
		JSONObject tqi_obj = obj.getJSONObject("tqi");
		double tqi = tqi_obj.getDouble("eval");
		System.out.println("TQI = " + tqi);
		
		if(tqi >= 0.8){
			htmlFile += "<img src=\"star2.png\" width=\"100\" height=\"100\" >";
		}
		
		if(tqi >= 0.6){
			htmlFile += "<img src=\"star2.png\" width=\"100\" height=\"100\" >";
		}
		
		if(tqi >= 0.4){
			htmlFile += "<img src=\"star2.png\" width=\"100\" height=\"100\" >";
		}
		
		if(tqi >= 0.2){
			htmlFile += "<img src=\"star2.png\" width=\"100\" height=\"100\" >";
		}
		
		htmlFile += "<img src=\"star2.png\" width=\"100\" height=\"100\" >";
		
		htmlFile += "<br>"
				+ "<label style=\"font-size:40px\">(" + round(tqi, 2) + ")</label>"
				+ "<br><br><br>";
		
		/*
		 * Characteristics Section
		 */
		
		// Initialization of the section
		htmlFile += "        <!-- Characteristics Section -->    "
				+ "<h2 class=\"sub-header\" align=\"left\">Characteristics</h2>"
				+ "<div class=\"table-responsive\">"
				+ "<table class=\"table table-striped\">"
				+ "<thead>"
				+ "<tr>"
				+ "<th>#</th>"
				+ "<th>Name</th>"
				+ "<th>Value</th>"
				+ "<th>Score</th>"
				+ "</tr>"
				+ "</thead>"
				+ "<tbody>";
		
		
		// Get the characteristics
		JSONObject characteristics = obj.getJSONObject("characteristics");
		JSONArray charArray = characteristics.getJSONArray("characteristics");
		
		// For each characteristic add a row to the table
		for (int i = 0; i < charArray.length(); i++){
			String name = charArray.getJSONObject(i).getString("name");
			String standard = charArray.getJSONObject(i).getString("standard");
			double eval = charArray.getJSONObject(i).getDouble("eval");
			System.out.println(name + " value = " + eval);
			
			// Add a row to the table
			htmlFile += "<tr>"
					+ "<td>" + (i+1) +"</td>"
					+ "<td>" + name + "</td>"
					+ "<td>" + standard +"</td>"
					+ "<td>" + round(eval, 2) + "</td>"
					+ "</tr>";
		}
		
		// TODO: Why here?
		htmlFile += " </tbody>"
				+ "</table>"
				+ "</div>";
		
		/*
		 * Properties Section
		 */
		
		// Initialization of the section
		htmlFile += "        <!-- Properties Section -->    "
				+ "<h2 class=\"sub-header\" align=\"left\">Properties</h2>"
				+ "<div class=\"table-responsive\">"
				+ "<table class=\"table table-striped\">"
				+ "<thead>"
				+ "<tr>"
				+ "<th>#</th>"
				+ "<th>Name</th>"
				+ "<th>Value</th>"
				+ "<th>Score</th>"
				+ "</tr>"
				+ "</thead>"
				+ "<tbody>";
		
		// Get the properties
		JSONObject properties = obj.getJSONObject("properties");
		JSONArray propArray = properties.getJSONArray("properties");
		
		// For each properties add a row to the table
		for (int i = 0; i < propArray.length(); i++){
			String name = propArray.getJSONObject(i).getString("name");
			double eval = propArray.getJSONObject(i).getDouble("eval");
			double normValue = propArray.getJSONObject(i).getJSONObject("measure").getDouble("normValue");
			System.out.println(name + " normValue= " + normValue + " eval= " + eval);
			
			// Add a row to the table
			htmlFile += "<tr>"
					+ "<td>" + (i+1) +"</td>"
					+ "<td>" + name + "</td>"
					+ "<td>" + round(normValue, 10) +"</td>"
					+ "<td>" + round(eval, 2) + "</td>"
					+ "</tr>";
		}
		
		
		/*
		 * Close the HTML Document
		 */
		
		htmlFile += "</tbody>"
				+ "</table>"
				+ "</div>"
				+ "</div>"
				+ "</div>"
				+ "</div>"
				+ "<!-- Bootstrap core JavaScript"
				+ "================================================== -->"
				+ "<!-- Placed at the end of the document so the pages load faster -->"
				+ "<script src=\"./Dashboard Template for Bootstrap_files/jquery.min.js\"></script>"
				+ "<script>window.jQuery || document.write('<script src=\"../../assets/js/vendor/jquery.min.js\"><\\/script>')"
				+ "</script>"
				+ "<script src=\"./Dashboard Template for Bootstrap_files/bootstrap.min.js\"></script>"
				+ "<!-- Just to make our placeholder images work. Don't actually copy the next line! -->"
				+ "<script src=\"./Dashboard Template for Bootstrap_files/holder.min.js\"></script>"
				+ "<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->"
				+ "<script src=\"./Dashboard Template for Bootstrap_files/ie10-viewport-bug-workaround.js\"></script>"
				+ "</body></html>";
		
		// TODO: Remove this print
		System.out.println(htmlFile);
		
		
		// View the page
		response.setContentType("text/html");
	    PrintWriter out = response.getWriter();

	    out.println(htmlFile);
	    
	    /*
	     * Delete the results ...
	     */
	    br.close();
	    
	    System.out.println("DELETING....");
	    File f1 = new File("./Results/Evaluation/SingleProjectResults/eval_" + projFullName +".json");
        if(f1.exists()){
        	f1.delete();
        }
        
        File f2 = new File("./Results/Analysis/SingleProjectResults/" + projFullName);
        if(f2.exists()){
        	File[] files = f2.listFiles();
        	for(File file : files){
        		file.delete();
        	}
        	f2.delete();
        }
        
        File f3 = new File("./gitRepo/" + projFullName);
        if(f3.exists()){
        	delete(f3);
        }
				
	}
	
	 public static void delete(File file)
		    	throws IOException{
		 
		    	if(file.isDirectory()){
		 
		    		//directory is empty, then delete it
		    		if(file.list().length==0){
		    			
		    		   file.delete();
		    			
		    		}else{
		    			
		    		   //list all the directory contents
		        	   String files[] = file.list();
		     
		        	   for (String temp : files) {
		        	      //construct the file structure
		        	      File fileDelete = new File(file, temp);
		        		 
		        	      //recursive delete
		        	     delete(fileDelete);
		        	   }
		        		
		        	   //check the directory again, if empty then delete it
		        	   if(file.list().length==0){
		           	     file.delete();
		        	   }
		    		}
		    		
		    	}else{
		    		//if file, then delete it
		    		file.delete();
		    	}
		    }
	
	
	/**
	 * A method for keeping only the first four digits
	 * of a double number.
	 * 
	 * Used as a quick fix for precision difference spotted
	 * between the numbers calculated by Java and those received
	 * from R Analysis.
	 */
	//TODO: Find a better way of fixing this issue (i.e. increase the precision in R)
	public static double roundDown2(double d) {
	    return (long) (d * 1e2) / 1e2;
	}
	
	public static double roundDown12(double d) {
	    return (long) (d * 1e11) / 1e11;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}


}
