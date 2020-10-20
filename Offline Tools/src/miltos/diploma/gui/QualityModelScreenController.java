package miltos.diploma.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.tools.ant.DirectoryScanner;


import java.io.File;

/**
 * Created by Miltos on 15/2/2016.
 */
public class QualityModelScreenController {

    // Basic fields
    private Main mainApp;
    private Stage primaryStage;
    private TextArea descriptionTextArea;
    private String rootDir = "/";
    private File selectedDirectory;
    private boolean valid = false;
    private double progress = 0;
    private  boolean validating = false;
    String errorMessage = "";

    // GUI fields
    @FXML
    private TextField browseTextField;
    @FXML
    private TextField qmNameTextField;
    @FXML
    private Label label1;
    @FXML
    private Label label2;


    /**
     * The class'es constructor.
     */
    public QualityModelScreenController(){
        mainApp = new Main();
        primaryStage = new Stage();
    }

    /**
     * Set the main app.
     */
    public void setMainApp(Main mainApp){
        this.mainApp = mainApp;
    }

    /**
     * Set the primary stage of these scene.
     */
    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    /**
     * A method for handling the browse button
     */
    public void handleBrowse(){

        // Create a directory chooser
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Benchmark Repository");
        File file = new File(rootDir);

        // Set the root directory of the chooser
        chooser.setInitialDirectory(file);

        // Get the path of the selected directory
        selectedDirectory = chooser.showDialog(primaryStage);

        // Check if a directory is selected
        if(selectedDirectory != null){
            // Set the new root directory
            rootDir = selectedDirectory.getParent();

            // Set the text field value to the directory's path
            browseTextField.setText(selectedDirectory.getAbsolutePath());

            // Set the path of the benchmark repository
            Main.benchRepoPath =  selectedDirectory.getAbsolutePath();
        }
    }

    /**
     * A method that handles Next button.
     */
    public void handleNext() throws InterruptedException {

        // Check if the program is already validating a repository
        if(!validating){

            // Indicate that the app is validating the repository...
            validating = true;

            // Create a separate thread
            // Lambda expression used instead of inline anonymous function!!!
            Thread t = new Thread(() -> {

                // Call the appropriate method
                valid = isInpudValid();

                // Call the next page with validating true
                if(valid){
                        Platform.runLater(() -> {
                            try {
                                handleNext();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                }else{
                    // Allow next validation
                    validating = false;
                }
            });

            // Start the thread
            t.start();
        }

        // Check if the repository is valid
        if(valid){
            // Set the path of the benchmark repository
            Main.benchRepoPath =  selectedDirectory.getAbsolutePath();
            Main.qualityModel.setName(qmNameTextField.getText());

            // Open the next scene
            mainApp.showPropertiesScreen();
        }
    }

    /**
     * This method is used only for testing purposes.
     */
    public void testQM(){
        System.out.println(Main.qualityModel.getName());
        System.out.println(Main.benchRepoPath);
    }

    /**
     * A method that validates user's inputs
     */
    public boolean isInpudValid(){

        // Initialize the error message
        errorMessage = "";

        /**
         * Validate the repository.
         */
        File dir = selectedDirectory;

        // Inform the user
        System.out.println("\nValidating the repository...");
        Platform.runLater(() -> {
            label1.setText("Validating the repository...");
                });
        System.out.println("Please wait...");

        boolean exists = false;

        if(selectedDirectory != null) {
            // Check if the directory exists and if it contains the appropriate files
            if (dir.exists() && dir.isDirectory()) {

                // Get a list of the files of this directory
                File[] projects = dir.listFiles();

                // Check if the desired repository is empty
                if (projects.length == 0) {
                    //If yes, ask for a new path
                    System.out.println("The desired repository is empty..!");
                    errorMessage += "The desired repository is empty..!\n";
                } else {

                    // Create scanners and search for java and class or jar files
                    DirectoryScanner javaFileScanner = new DirectoryScanner();
                    DirectoryScanner classFileScanner = new DirectoryScanner();
                    DirectoryScanner jarFilesScanner = new DirectoryScanner();
                    String[] javaFiles = null;
                    String[] classFiles = null;
                    String[] jarFiles = null;

                    // Check each folder if it contains the appropriate files for the analysis
                    boolean problem = false;
                    boolean hasDirectory = false;

                    progress = 0;
                    System.out.println("");
                    for (File project : projects) {

                        // Check if the repository contains at least one directory (i.e. project)
                        if (project.isDirectory()) {
                            hasDirectory = true;
                        }

                        // If this file is not a directory continue to the next one
                        //TODO: Connect this check to the previous if statement by using "else"
                        if (!project.isDirectory()) {
                            continue;
                        }

                        // Inform the user for the validation's progress
                        System.out.print("* Progress : " + (int) (progress / projects.length * 100) + " %\r");
                        Platform.runLater(() -> {
                            label2.setText((int) (progress / projects.length * 100) + " %");
                        });

                        // Scan the current directory for the files needed for the analysis
                        javaFileScanner.setIncludes(new String[]{"**/*.java"});
                        javaFileScanner.setBasedir(project.getAbsolutePath());
                        javaFileScanner.setCaseSensitive(false);
                        javaFileScanner.scan();
                        javaFiles = javaFileScanner.getIncludedFiles();

                        classFileScanner.setIncludes(new String[]{"**/*.class"});
                        classFileScanner.setBasedir(project.getAbsolutePath());
                        classFileScanner.setCaseSensitive(false);
                        classFileScanner.scan();
                        classFiles = classFileScanner.getIncludedFiles();

                        jarFilesScanner.setIncludes(new String[]{"**/*.jar"});
                        jarFilesScanner.setBasedir(project.getAbsolutePath());
                        jarFilesScanner.setCaseSensitive(false);
                        jarFilesScanner.scan();
                        jarFiles = jarFilesScanner.getIncludedFiles();

                        // Check if this directory contains java and class or jar files...
                        if (javaFiles.length == 0) {
                            System.out.println("There aren't any java files inside the repository : " + project.getName());
                            errorMessage += "There aren't any java files inside the repository : " + project.getName() + "\n";
                            problem = true;
                            break;
                        } else if (classFiles.length == 0 || classFiles.length == 0) {
                            System.out.println("There aren't any class or jar files inside the repository : " + project.getName());
                            errorMessage += "There aren't any class or jar files inside the repository : " + project.getName() + "\n";
                            problem = true;
                            break;
                        }
                        // Update the progress of the process
                        progress++;
                    }

                    // Inform the user for the validation's progress
                    System.out.print("* Progress : " + (int) (progress / projects.length * 100) + " %\r");
                    Platform.runLater(() -> {
                        label2.setText((int) (progress / projects.length * 100) + " %");
                    });
                    System.out.println("");

                    // Check if the for loop finished without any violation
                    if (!problem) {
                        System.out.println("Repository successfully validated..!");

                        Platform.runLater(() -> {
                            label1.setText("Repository successfully validated..!\n");
                        });

                        exists = true;
                    }

                    if (!hasDirectory) {
                        exists = false;
                        System.out.println("there isn't any project in the desired repository");
                        errorMessage += "There isn't any project in the desired repository\n";
                    }

                }

            } else {
                System.out.println("The desired directory doesn't exist..!");
                errorMessage += "The desired directory doesn't exist..!\n";
            }

        }else{
            errorMessage += "Please provide a benchmark repository!\n";
        }

        if (qmNameTextField.getText() == null || qmNameTextField.getText().length() == 0) {
            errorMessage += "No valid quality name!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Platform.runLater(() -> {
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            label1.setText("");
            label2.setText("");
            });

            return false;
        }
    }
}
