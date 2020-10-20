package miltos.diploma.gui;/**
 * Created by Miltos on 15/2/2016.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import miltos.diploma.*;
import miltos.diploma.characteristics.Characteristic;

import miltos.diploma.characteristics.CharacteristicSet;
import miltos.diploma.characteristics.QualityModel;
import miltos.diploma.characteristics.Tqi;
import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class Main extends Application {

    // The basic fields of the app
    private Stage primaryStage;
    private static BorderPane rootLayout;
    private static int HEIGHT = 400;
    private static int WIDTH = 600;

    // Script's fields
    public static boolean benchmarkCalibration;
    public static boolean weightsElicitation;
    public static String benchRepoPath;
    public static boolean serialAnalysis = false;
    public static String resPath;
    public static boolean staticAnalysis = true;
    public static boolean keepResults;
    public static boolean inBenchAnalysis = false;
    public static QualityModel qualityModel;
    public static  CharacteristicSet characteristics;
    public static PropertySet properties;

    // TODO: New Analysis Quick Fix
    public static boolean success = false;

    // The ObservableList objects that are needed for the TableView objects.
    private static ObservableList<PropertyFX> propertyData = FXCollections.observableArrayList();
    private static ObservableList<CharacteristicFX> charData = FXCollections.observableArrayList();

    /**
     * Setters and getters of the ObservableList objects.
     */

    public ObservableList<PropertyFX> getPropertyData(){
        return propertyData;
    }

    public void setPropertyData(ObservableList<PropertyFX> propertyData){
        this.propertyData = propertyData;
    }

    public static ObservableList<CharacteristicFX> getCharData() {
        return charData;
    }

    public static void setCharData(ObservableList<CharacteristicFX> charData) {
        Main.charData = charData;
    }

    /**
     * The constructor method of the class.
     */
    public Main(){
        qualityModel = new QualityModel();
    }

    /**
     * A method for retrieving the primary stage of the app
     */
    public Stage getPrimaryStage(){
        return this.primaryStage;
    }

    /**
     * The main method of the application
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The basic method of the application's primary stage
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        // Get the default configuration from the config.xml file
        getConfig();

        // Set the primary stage (window) of the application
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Model Designer");

        // Set the application icon
        this.primaryStage.getIcons().add(new Image("file:resources/images/quality-education.png"));

        // Initialize the main layout of the application
        initRootLayout();

        // Set the first scene of the application
        showFirstScene();
    }


    /**
     * Initialize the root layout.
     */
    public void initRootLayout(){

        try {
            // Load the desired layout
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("MainWindow.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Create a simple scene and add the layout to it
            Scene scene = new Scene(rootLayout);

            // TODO: REMOVE
            scene.getStylesheets().add("file:resources/stylesheets/progressBar.css");

            // Get the controller of the desired layout
            Controller controller = loader.getController();
            controller.setMainApp(this);

            // Handle the close request - Quick Fix
            primaryStage.setOnCloseRequest(e -> {
                try {
                    e.consume();
                    controller.handleCloseMenuItem();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });

            // Set the scene to the main window and show the window
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Show the first scene of the application.
     */
    public void showFirstScene(){

        try{

            // Load the layout of the scene
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("WelcomeScreen.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Set this layout to the main layout
            rootLayout.setCenter(page);

            // Give the controller access to the main app
            WelcomeScreenController controller = new WelcomeScreenController();
            controller.setMainApp(this);

        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Show the Screen for the definition of the quality model
     */
    public void showQualityModelScreen(){
        try{

            // Load the layout of the scene
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("QualityModelScreen.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Set this layout to the main layout
            rootLayout.setCenter(page);

            // Give the controller access to the main app
            QualityModelScreenController controller = new QualityModelScreenController();
            controller.setPrimaryStage(this.primaryStage);

        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Show the Screen for the definition of the quality model's properties.
     */
    public void showPropertiesScreen(){
        try{

            // Load the layout of the scene
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("PropertySelectionScreen.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Set this layout to the main layout
            rootLayout.setCenter(page);

            // Give the controller access to the main app
            PropertySelectionScreenController controller = loader.getController();
            controller.setMainApp(this);

        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Show the Screen for the editing of the quality model's properties.
     *
     * It returns boolean because we want to retrieve the answer to the
     * parent Stage.
     */
    public boolean showPropertyEditDialog(PropertyFX property){
        try{

            // Load the layout of the scene that belongs to this pop up window
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("PropertyEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create a new dialog Stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Property");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);

            // Create the scene and set this layout
            Scene scene = new Scene(page);

            //Set the scene to the dialog window
            dialogStage.setScene(scene);

            // Set the property into the controller
            PropertyEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setProperty(property);

            // Show and wait
            dialogStage.showAndWait();

            return controller.isOkClicked();

        }catch (IOException e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Show the Screen for the definition of the quality model's characteristics.
     */
    public void showCharacteristicsScreen(){
        try{

            // Load the layout of the scene
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("CharacteristicSelectionScreen.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Set this layout to the main layout
            rootLayout.setCenter(page);

            // Give the controller access to the main app
            CharacteristicSelectionScreenController controller = loader.getController();
            controller.setMainApp(this);

        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Show the Screen for the editing of the quality model's characteristics.
     *
     * It returns boolean because we want to retrieve the answer to the
     * parent Stage.
     */
    public boolean showCharacteristicEditDialog(CharacteristicFX characteristic){
        try{

            // Load the layout of the scene that belongs to this pop up window
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("CharacteristicEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create a new dialog Stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Characteristic");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);

            // Create the scene and set this layout
            Scene scene = new Scene(page);

            //Set the scene to the dialog window
            dialogStage.setScene(scene);

            // Set the property into the controller
            CharacteristicEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setCharacteristic(characteristic);

            // Show and wait
            dialogStage.showAndWait();

            return controller.isOkClicked();

        }catch (IOException e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Show the Weight Elicitation Screen
     */
    public void showWeightElicitationScreen(){
        try{

            // Load the layout of the scene
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("WeightElicitationScreen.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Set this layout to the main layout
            rootLayout.setCenter(page);

            // Give the controller access to the main app
            WeightElicitationScreenController controller = loader.getController();
            controller.setMainApp(this);

        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Show the Last Configuration Screen
     */
    public void showLastConfigScreen(){
        try{

            // Load the layout of the scene
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("LastConfigScreen.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Set this layout to the main layout
            rootLayout.setCenter(page);

            // Give the controller access to the main app
            LastConfigScreenController controller = loader.getController();
            controller.setMainApp(this);

        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Show the Last Config Screen
     */
    public void showCentralConsole(){
        try{

            // Load the layout of the scene
            AnchorPane page;
            FXMLLoader loader;

            if(OptimalParallelBenchmarkAnalyzer.MAX_DEPTH != 3 || serialAnalysis){
                loader = new FXMLLoader();
                loader.setLocation(Main.class.getResource("Console.fxml"));
                page = (AnchorPane) loader.load();
            }else{
                loader = new FXMLLoader();
                loader.setLocation(Main.class.getResource("Console2.fxml"));
                page = (AnchorPane) loader.load();
            }

            // Set this layout to the main layout
            rootLayout.setCenter(page);

            // Give the controller access to the main app
            if(OptimalParallelBenchmarkAnalyzer.MAX_DEPTH != 3 || serialAnalysis) {
                CentralConsoleController controller = loader.getController();
                controller.setMainApp(this);
            }else{
                CentralConsoleController2 controller = loader.getController();
                controller.setMainApp(this);
            }

        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * This method implements the total process of the quality model derivation.
     */
    public void executeAnalysisScript(Vector<ProgressBar> progBars, Vector<ProgressIndicator> progIndicators, TextArea console, ProgressIndicator totalProgress) throws CloneNotSupportedException, InterruptedException {

        System.out.println("\n\n******************************  Model Designer *******************************");
        System.out.println("*");
        System.out.println("* Starting Analysis...");
        System.out.println("* Loading Quality Model...");

        CentralConsoleController c = new CentralConsoleController();

        Platform.runLater(() -> {
        console.appendText("**************************  Model Designer ***************************\n");
        console.appendText("*\n");
        console.appendText("* Starting Analysis...\n");
        console.appendText("* Loading Quality Model...\n");
        });


        // Set the properties and the characteristics
        PropertySet properties = qualityModel.getProperties();
        CharacteristicSet characteristics = qualityModel.getCharacteristics();
        Tqi tqi = qualityModel.getTqi();

        Platform.runLater(() -> {
        totalProgress.setProgress(0.05);
        console.appendText("* Quality Model successfully loaded...!\n");
        console.appendText("\n");
        });

        System.out.println("* Quality Model successfully loaded...!");
        System.out.println("*");

        BenchmarkProjects projects = null;
        BenchmarkAnalysisExporter exporter = null;

        // Check if the user wants to execute a benchmark calibration
        if(benchmarkCalibration){
		/*
		 * Step 1 : Analyze the projects found in the desired Benchmark Repository
		 */
            // Check if the user wants to perform a new static analysis
            if(staticAnalysis){

                // Check if the results directory exists and if not create it. Clear it's contents as well.
                checkCreateClearDirectory(BenchmarkAnalyzer.BENCH_RESULT_PATH);


                System.out.println("\n**************** STEP 1 : Benchmark Analyzer *************************");
                System.out.println("*");
                System.out.println("* Analyzing the projects of the desired benchmark repository");
                System.out.println("* Please wait...");
                System.out.println("*");

                Platform.runLater(() -> {
                    console.appendText("**************** STEP 1 : Benchmark Analyzer *************************\n");
                    console.appendText("*\n");
                    console.appendText("* Analyzing the projects of your benchmark repository...\n");
                    totalProgress.setProgress(0.1);
                });

                long startTime = System.currentTimeMillis();

                //TODO: REMOVE THIS EASY FIX FOR THREAD TERMINATION
                inBenchAnalysis = true;

                //TODO: Create a parent Benchmark Analyzer so that you can use it to move the common commands outside the if statement
                if(serialAnalysis){
                    //Instantiate the serial benchmark analyzer
                    BenchmarkAnalyzer benchmarkAnal = new BenchmarkAnalyzer();

                    //Set the GUI objects
                    //TODO: Find a better way
                    benchmarkAnal.setGUIObjects(progBars.get(0), progIndicators.get(0));

                    Platform.runLater(() -> {
                        console.appendText("* Serial analysis is selected...\n");
                        console.appendText("* Please wait...\n");
                    });

                    //Set the repository and the desired properties to the benchmark analyzer
                    benchmarkAnal.setBenchRepoPath(benchRepoPath);
                    benchmarkAnal.setProperties(properties);

                    //Start the analysis of the benchmark repository
                    benchmarkAnal.analyzeBenchmarkRepo();

                }else{
                    //Instantiate the parallel benchmark analyzer
                    OptimalParallelBenchmarkAnalyzer benchmarkAnal = new OptimalParallelBenchmarkAnalyzer();

                    //Set the GUI objects
                    benchmarkAnal.setGUIObjects(progBars, progIndicators, console);
                    Platform.runLater(() -> {
                        console.appendText("* Parallel analysis is selected...\n");
                        console.appendText("* Please wait...\n");
                    });

                    //Set the repository and the desired properties to the benchmark analyzer
                    benchmarkAnal.setBenchRepoPath(benchRepoPath);
                    benchmarkAnal.setProperties(qualityModel.getProperties());

                    //Start the analysis of the workspace
                    benchmarkAnal.analyzeBenchmarkRepo();
                }

                long endTime   = System.currentTimeMillis();
                long totalTime = endTime - startTime;

                //Print some messages to the user
                Platform.runLater(() -> {
                    console.appendText("*\n");
                    console.appendText("* The analysis is finished..!\n");
                    console.appendText("* You can find the results at : " + new File(BenchmarkAnalyzer.BENCH_RESULT_PATH).getAbsolutePath() + "\n");
                    console.appendText("* Total time is : " + totalTime + " ms\n");
                });

                System.out.println("*");
                System.out.println("* The analysis is finished..!");
                System.out.println("* You can find the results at : " + new File(BenchmarkAnalyzer.BENCH_RESULT_PATH).getAbsolutePath());
                System.out.println("* Total time is : " + totalTime + " ms");
                System.out.println();
            }
            Platform.runLater(() -> {
            totalProgress.setProgress(0.50);
            });
            //TODO: REMOVE THIS EASY FIX FOR THREAD TERMINATION
            inBenchAnalysis = false;

		/*
		 * Step 2 : Import the results of the analysis and store them into different objects
		 */

            System.out.println("\n**************** STEP 2 : Benchmark Importer *************************");
            System.out.println("*");
            System.out.println("* Importing the results of the analysis...");
            System.out.println("* Please wait...");
            System.out.println("*");

            Platform.runLater(() -> {
                console.appendText("\n**************** STEP 2 : Benchmark Importer *************************\n");
                console.appendText("*\n");
                console.appendText("* Importing the results of the analysis\n");
                console.appendText("* Please wait ...\n");
                console.appendText("*\n");
            });

            // Create an empty BenchmarkImporter
            BenchmarkResultImporter benchmarkImporter = new BenchmarkResultImporter();

            // Start importing the project results
            projects = new BenchmarkProjects();
            projects = benchmarkImporter.importResults(BenchmarkAnalyzer.BENCH_RESULT_PATH);

            // Print some informative messages to the console
            System.out.println("*");
            System.out.println("* The results are successfully imported..! ");

            Platform.runLater(() -> {
                console.appendText("*\n");
                console.appendText("* The results are successfully imported..!\n");
                totalProgress.setProgress(0.6);
             });

		/*
		 * Step 3 : Aggregate the results of each project
		 */

            System.out.println("\n**************** STEP 3: Aggregation Process *************************");
            System.out.println("*");
            System.out.println("* Aggregating the results of each project...");
            System.out.println("* I.e. Calculating the normalized values of their properties...");
            System.out.println("* Please wait...");
            System.out.println("*");

            Platform.runLater(() -> {
                console.appendText("\n**************** STEP 3: Aggregation Process *************************\n");
                console.appendText("*\n");
                console.appendText("* Aggregating the results of each project...\n");
                console.appendText("* I.e. Calculating the normalized values of their properties...\n");
                console.appendText("* Please wait...\n");
                console.appendText("*\n");
            });

            // Create an empty BenchmarkAggregator and aggregate the metrics of the project
            BenchmarkAggregator benchAggregator = new BenchmarkAggregator();
            benchAggregator.aggregateProjects(projects, properties);

            System.out.println("*");
            System.out.println("* Aggregation process finished..!");

            Platform.runLater(() -> {
                console.appendText("*\n");
                console.appendText("* Aggregation process finished..!\n");
                totalProgress.setProgress(0.7);
            });

		/*
		 * Step 4 : Export the benchmark analysis results for the R - Analysis
		 */

            System.out.println("\n**************** STEP 4: Properties exportation for R analysis *******");
            System.out.println("*");

            Platform.runLater(() -> {
                console.appendText("\n**************** STEP 4: Properties exportation for R analysis *******\n");
                console.appendText("*\n");
                console.appendText("* Exporting properties for R analysis...\n");
            });

            // Create an analysis exporter and export the Properties in a xls form
            exporter = new BenchmarkAnalysisExporter();
            exporter.exportToCsv(projects);

            System.out.println("*");
            System.out.println("* The xls file with the properties is successfully exported \n and placed into R's working directory!");

            Platform.runLater(() -> {
                console.appendText("*\n");
                console.appendText("* The xls file with the properties is successfully exported \n" +
                        " and placed into R's working directory!\n");
                 totalProgress.setProgress(0.75);
            });

		/*
		 * Step 5 : Invoke R analysis for the threshold calculation
		 */

            System.out.println("\n**************** STEP 5: Threshold extraction ************************");
            System.out.println("*");
            System.out.println("* Calling R for threshold extraction...");
            System.out.println("* This will take a while...");
            System.out.println("*");

            Platform.runLater(() -> {
                console.appendText("\n**************** STEP 5: Threshold extraction ************************\n");
                console.appendText("*\n");
                console.appendText("* Calling R for threshold extraction...\n");
                console.appendText("* This will take a while...\n");
            });

            // Create an Empty R Invoker and execute the threshold extraction script
            RInvoker rInvoker = new RInvoker();
            rInvoker.executeRScriptForThresholds();

            System.out.println("*");
            System.out.println("* R analysis finished..!");
            System.out.println("* Thresholds exported in a JSON file..!");

            Platform.runLater(() -> {
                console.appendText("*\n");
                console.appendText("* R analysis finished..!\n");
                console.appendText("* Thresholds exported in a JSON file..!\n");
                totalProgress.setProgress(0.8);
            });

		/*
		 * Step 6 : Import the thresholds and assign them to each property of the Quality Model
		 */
            System.out.println("\n**************** STEP 6: Importing the thresholds ********************");
            System.out.println("*");
            System.out.println("* Importing the thresholds from the JSON file into JVM...");
            System.out.println("* This will take a while...");
            System.out.println("*");

            Platform.runLater(() -> {
                console.appendText("\n**************** STEP 6: Importing the thresholds ********************\n");
                console.appendText("*\n");
                console.appendText("* Importing the thresholds from the JSON file into JVM...\n");
                console.appendText("* This will take a while...\n");
            });

            // Create an empty Threshold importer and import the thresholds from the json file exported by R
            // The file is placed into the R working directory (fixed)
            ThresholdImporter thresholdImp = new ThresholdImporter();
            thresholdImp.importThresholdsFromJSON(properties);

            System.out.println("* Thresholds successfully imported..!");

            Platform.runLater(() -> {
                console.appendText("* Thresholds successfully imported..!\n");
            });

		/*
		 * Step 7 : Copy the thresholds to each projects' properties
		 */

            System.out.println("\n**************** STEP 7: Loading thresholds to Projects  *************");
            System.out.println("*");
            System.out.println("* Seting the thresholds to the properties of the existing projects...");
            System.out.println("* This will take a while...");
            System.out.println("*");

            Platform.runLater(() -> {
                console.appendText("\n**************** STEP 7: Loading thresholds to Projects  *************\n");
                console.appendText("*\n");
                console.appendText("* Seting the thresholds to the properties of the existing projects...\n");
                console.appendText("* This will take a while...\n");
            });

            double progress = 0;
            for(int i = 0; i < projects.size(); i++){
                System.out.print("* Progress : " + (int) (progress/projects.size()* 100)  + " %\r");

                PropertySet prop = projects.getProject(i).getProperties();
                for(int j = 0; j < prop.size(); j++){
                    prop.get(j).setThresholds(properties.get(j).getThresholds().clone());
                }
                progress++;
            }

            System.out.print("* Progress : " + (int) (progress/projects.size()) * 100 + " %\r");

            System.out.println("*");
            System.out.println("* Thresholds successfully loaded to the projects' properties..!");

            Platform.runLater(() -> {
                console.appendText("*\n");
                console.appendText("* Thresholds successfully loaded to the projects' properties..!\n");
            });
        }

        Platform.runLater(() -> {
             totalProgress.setProgress(0.90);
        });

        // Check if the user wants to execute weights elicitation
        if(weightsElicitation){

            System.out.println("\n*********************************** Weight Elicitation *************************************");
            System.out.println("*");
            System.out.println("*");

            Platform.runLater(() -> {
                console.appendText("\n*********************** Weight Elicitation ***************************\n");
                console.appendText("*\n");
                console.appendText("* Calling R for weight elicitation...\n");
                console.appendText("* Please wait...\n");
            });

            // Call R script for weight elicitation
            RInvoker invoker = new RInvoker();
            invoker.executeRScriptForWeightsElicitation();

            // Import the weights from the json file
            WeightsImporter weightImporter = new WeightsImporter();
            weightImporter.importWeights(tqi, characteristics);
        }

        Platform.runLater(() -> {
            console.appendText("* Weights successfully calculated..!\n");
            totalProgress.setProgress(0.95);
        });

		/*
		 * STEP 8 : Export the properties in an XML (or JSON) File
		 */

        System.out.println("\n**************** STEP 8: Exporting Results ***********************");
        System.out.println("*");
        System.out.println("* Exporting the results to XML, JSON files and JDOM Element object...");
        System.out.println("* This will take a while...");
        System.out.println("*");

        Platform.runLater(() -> {
            console.appendText("\n**************** STEP 8: Exporting Results ***********************\n");
            console.appendText("*\n");
            console.appendText("* Exporting the results to XML, JSON files and JDOM Element object...\n");
            console.appendText("* This will take a while...\n");
            console.appendText("*\n");
        });

        // Check and create the predefined results directory
        checkCreateClearDirectory(PropertiesExporter.BENCH_RESULT_PATH);

    // TODO: Quick fix for New Analysis
    if(success) {
        // Check what should be exported
        if (benchmarkCalibration && weightsElicitation) {

            // Export the Quality Model
            QualityModelExporter qmExp = new QualityModelExporter();
            qmExp.exportQualityModelToXML(qualityModel, new File(resPath + "/qualityModel.xml").getAbsolutePath());
            qmExp.exportQualityModelToXML(qualityModel, new File(PropertiesExporter.BENCH_RESULT_PATH + "/qualityModel.xml").getAbsolutePath());

        } else if (benchmarkCalibration) {

            // Export the properties to XML and JSON format to the predefined directory
            PropertiesExporter propertiesExp = new PropertiesExporter();
            propertiesExp.exportPropertiesToXML(properties, new File(PropertiesExporter.BENCH_RESULT_PATH + "/properties.xml").getAbsolutePath());
            propertiesExp.exportPropertiesToJSON(properties, new File(PropertiesExporter.BENCH_RESULT_PATH + "/properties.json").getAbsolutePath());

            // Export the properties to XML and JSON format to the user defined directory
            propertiesExp.exportPropertiesToXML(properties, new File(resPath + "/properties.xml").getAbsolutePath());
            propertiesExp.exportPropertiesToJSON(properties, new File(resPath + "/properties.json").getAbsolutePath());

        } else if (weightsElicitation) {

            // Export the quality model's characteristics
            CharacteristicsExporter charExporter = new CharacteristicsExporter();
            charExporter.exportCharacteristicsToXML(characteristics, new File(resPath + "/characteristics.xml").getAbsolutePath());
            charExporter.exportCharacteristicsToXML(characteristics, new File(PropertiesExporter.BENCH_RESULT_PATH + "/characteristics.xml").getAbsolutePath());

            // Export the quality model's TQI object
            TqiExporter tqiExp = new TqiExporter();
            tqiExp.exportTqiToXML(tqi, new File(resPath + "/tqi.xml").getAbsolutePath());
            tqiExp.exportTqiToXML(tqi, new File(PropertiesExporter.BENCH_RESULT_PATH + "/tqi.xml").getAbsolutePath());

        } else {
            System.out.println("Something went wrong!! Nothing to export");
        }

        System.out.println("* Results successfully exported..!");
        System.out.println("*");
        System.out.println("* You can find the results at        : " + new File(resPath).getAbsolutePath());
        System.out.println("* You can find the ranked results at : " + new File(PropertiesExporter.BENCH_RESULT_PATH).getAbsolutePath());

        Platform.runLater(() -> {
            console.appendText("* Results successfully exported..!\n");
            console.appendText("*\n");
            console.appendText("* You can find the results at        : " + new File(resPath).getAbsolutePath() + "\n");
            console.appendText("* You can find the ranked results at : " + new File(PropertiesExporter.BENCH_RESULT_PATH).getAbsolutePath() + "\n");

            totalProgress.setProgress(1);
            totalProgress.setRotate(0);

            Text text = (Text) totalProgress.lookup(".percentage");
            text.getStyleClass().add("percentage-null");
            totalProgress.setLayoutY(totalProgress.getLayoutY() + 8);
            totalProgress.setLayoutX(totalProgress.getLayoutX() - 7);
        });
    }
}

    /**
     * A method that checks the predefined directory structure, creates the
     * directory tree if it doesn't exists and clears it's contents for the
     * new analysis (optional).
     */

    public static void checkCreateClearDirectory(String path){

        File dir = new File(path);

        // Check if the directory exists
        if(!dir.isDirectory() || !dir.exists()){
            dir.mkdirs();
        }

        // Clear previous results
        if(!keepResults){
            try {
                FileUtils.cleanDirectory(dir);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * A method that parses the configuration xml file in order to set up
     * some fixed (default) user defined parameters.
     */
    public static void getConfig(){

        try {
            // Import the desired xml configuration file
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new File(System.getProperty("user.dir") + "/config.xml").getAbsoluteFile());
            Element root = (Element) doc.getRootElement();

            // Create a list of all the its elements
            List<Element> elements = root.getChildren();

            // Iterate through the elements of the file and parse their values
            for(Element el : elements){
                if("keepResults".equalsIgnoreCase(el.getName())){
                    keepResults = Boolean.parseBoolean(el.getText());
                }else if("rScriptPath".equalsIgnoreCase(el.getName())){
                    String path = el.getText();
                    RInvoker.R_BIN_PATH = path;
                }else if("threadDepth".equalsIgnoreCase(el.getName())){
                    OptimalParallelBenchmarkAnalyzer.MAX_DEPTH = Integer.parseInt(el.getText());
                }
            }
        }  catch (JDOMException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void testConfig(PropertySet properties, CharacteristicSet characteristics){
        System.out.println("Benchmark ? : " + benchmarkCalibration);
        System.out.println("Weight ? : " + weightsElicitation);
        if(benchmarkCalibration) {
            System.out.println("Benchmark Repo Path : " + benchmarkCalibration);
        }
        System.out.println("Keep Results ? : " + keepResults);
        System.out.println("Serial Analysis : " + serialAnalysis);
        System.out.println("Static Analysis ? : " + staticAnalysis);
        System.out.println("RInvoker : " + RInvoker.weightsScript);
        System.out.println("Respath : " + resPath);

        System.out.println(" Number of Characteristics : " + characteristics.size());
        System.out.println(" Number of Properties : " + properties.size());

        System.out.println("MAX DEPTH = " + OptimalParallelBenchmarkAnalyzer.MAX_DEPTH);
    }
}
