package miltos.diploma.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import miltos.diploma.OptimalParallelBenchmarkAnalyzer;
import miltos.diploma.characteristics.QualityModel;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Miltos on 15/2/2016.
 */
public class Controller {

    private Main mainApp;
    private String rootDir = "/";
    private String qmPath = null;
    boolean validFile = false;


    public void setMainApp(Main mainApp){
        this.mainApp = mainApp;
    }


    /**
     * "Close" menu item handler.
     * @throws IOException
     */
    @FXML
    public void handleCloseMenuItem() throws IOException {

        //Ask user if he is sure for his choice
        ConfirmBox c = new ConfirmBox();
        boolean answer = c.display();

        //Check the answer
        if(answer){
            //Check if there are running threads
            Vector<Thread> threads = OptimalParallelBenchmarkAnalyzer.threads;
            if(Main.inBenchAnalysis){
                for(int i = 0; i < threads.size(); i++){
                    //TODO: Find a better (non deprecated solution)
                    threads.get(i).stop();
                }
            }
            //Close the application
            System.exit(0);
        }else{
            System.out.println("User rejected his initial option....!!!!");
        }
    }

    /**
     * "Load Model" menu item handler. This method loads the quality model's description
     * from the desired XML file.
     */
    @FXML
    public void handleLoadModelMenuItem(){
        // Open a file chooser in order to load the quality model XML file
        if(validFile = handleBrowse()){
            // Load the quality model's info
            QmDefinitionFXLoader qmLoader = new QmDefinitionFXLoader(qmPath, mainApp);
            qmLoader.importQualityModel();
        }else{
            System.out.println("Nothing loaded!!");
        }
    }

    /**
     * A method for handling the browse button.
     */
    public boolean handleBrowse(){

        // Initialize the flag
        validFile = false;

        // Create a new file chooser
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open model file");
        chooser.setInitialDirectory(new File(rootDir));

        // Open the dialog and retrieve the path of the xml file
        File selectedFile = chooser.showOpenDialog(mainApp.getPrimaryStage());

        // Check the file
        String errorMessage = "";
        if(selectedFile != null){
            // Set the new root directory
            rootDir = selectedFile.getParent();

            // Check if it exists and if it is an xml file
            File rulesetXMLFile = new File(selectedFile.getAbsolutePath());
            if(!rulesetXMLFile.exists() || !rulesetXMLFile.isFile()){
                System.out.println("The desired file doesn't exist..!");
                errorMessage += "The desired ruleset file doesn't exist..!\n";
            }else if(!rulesetXMLFile.getName().contains(".xml")){
                System.out.println("The desired file is not an XML file..!");
                errorMessage += "The desired file is not an XML file..!\n";
            }else{
                //All is fine..!
                validFile = true;
            }

            if(errorMessage.length() == 0){
                qmPath = selectedFile.getAbsolutePath();
                validFile = true;
            }else{
                validFile = false;
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initOwner(mainApp.getPrimaryStage());
                alert.setTitle("Invalid File");
                alert.setHeaderText("Please correct invalid fields");
                alert.setContentText(errorMessage);
                alert.showAndWait();
            }
        }
        return validFile;
    }

    /**
     * This method is used in order to save the quality model's description into a XML file.
     * It is called whenever the "Save Model" menu item is clicked.
     */
    public void handleSaveModelMenuItem(){
        // Check if there is something to save
        if((Main.qualityModel.getCharacteristics().size() != 0) || (Main.qualityModel.getProperties().size() != 0)){

            // Create a file chooser
            FileChooser fileChooser = new FileChooser();

            //Set extension filter
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
            fileChooser.getExtensionFilters().add(extFilter);

            //Show save file dialog
            File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

            if(file != null){
                SaveFile(Main.qualityModel, file);
            }

        }else{

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("Warning");
            alert.setHeaderText("There is nothing to save!");
            alert.showAndWait();

        }
    }

    private void SaveFile(QualityModel qualityModel, File file) {
        // If there are data to save
        // Create a QualityModel Exporter
        QualityModelConfigExporter.exportQualityModelConfig(qualityModel, file.getAbsolutePath());
    }

    /**
     * This method starts a new analysis. It is called whenever the "New Analysis" menu
     * item is clicked.
     * @throws IOException
     */
    public void newAnalysisMenuItemClicked() throws IOException {

        // Check if there are running threads
        Vector<Thread> threads = OptimalParallelBenchmarkAnalyzer.threads;
        if(Main.inBenchAnalysis){

            //TODO: New Analysis Quick Fix
            Main.success = false;

            for(int i = 0; i < threads.size(); i++) {
                //TODO: Find a better (non deprecated solution)
                threads.get(i).stop();
            }
        }

        if (threads != null && threads.size() != 0){
            System.out.println("Clear all threads!!!");
            threads.clear();
            System.out.println("Reset Index");
            OptimalParallelBenchmarkAnalyzer.index=0;
        }

        // Load the configuration again
        Main.getConfig();

        // Show the Welcome Screen
        mainApp.showFirstScene();
    }

    /**
     * Not used. It should e used in order to restart the application
     */
    public void restartApplication() throws URISyntaxException, IOException {

        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        final ArrayList<String> command = new ArrayList<String>();
        command.add(javaBin);
        command.add("-jar");
        command.add(currentJar.getPath());

        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
        System.exit(0);
    }
}
