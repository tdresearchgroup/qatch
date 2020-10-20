package miltos.diploma.gui;

import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import miltos.diploma.OptimalParallelBenchmarkAnalyzer;

import java.io.File;
import java.util.Vector;

/**
 * Created by Miltos on 16/2/2016.
 */
public class CentralConsoleController {

    @FXML
    private TextField resultTextField;

    @FXML
    private  TextArea console;

    @FXML
    private Button startButton;

    @FXML
    public ProgressBar prog1;
    @FXML
    public ProgressBar prog2;
    @FXML
    public ProgressBar prog3;
    @FXML
    public ProgressBar prog4;
    @FXML
    public ProgressBar prog5;
    @FXML
    public ProgressBar prog6;

    @FXML
    private ProgressIndicator progInd1;
    @FXML
    private ProgressIndicator progInd2;
    @FXML
    private ProgressIndicator progInd3;
    @FXML
    private ProgressIndicator progInd4;
    @FXML
    private ProgressIndicator progInd5;
    @FXML
    private ProgressIndicator progInd6;

    @FXML
    private ProgressIndicator totalProgInd;

    @FXML
    private Label doneLabel;

    /**
     * Useful fields
     */
    private Main mainApp;
    private static String rootDir = "/";
    private Vector<ProgressBar> progBars;
    private Vector<ProgressIndicator> progIndicators;
    private static int thresholds;
    private static boolean first = true;

    public CentralConsoleController(){
      // An empty constructor ...
    }

    /**
     * Initialize the basic GUI objects
     */
    public void initialize() throws InterruptedException {

        // Create the vectors and add the objects to them
        progBars = new Vector<>();
        progIndicators = new Vector();

        progBars.add(prog1);
        progBars.add(prog2);
        progBars.add(prog3);
        progBars.add(prog4);
        progBars.add(prog5);
        progBars.add(prog6);

        progIndicators.add(progInd1);
        progIndicators.add(progInd2);
        progIndicators.add(progInd3);
        progIndicators.add(progInd4);
        progIndicators.add(progInd5);
        progIndicators.add(progInd6);

        // Initialize the GUI Objects properly
        for(int i = 0; i < progBars.size() ; i++){
            progBars.get(i).setDisable(true);
            progIndicators.get(i).setDisable(true);
        }

        // Set the threshold of the dept
        int threshold;
        if(Main.serialAnalysis){
            threshold = 1;
        }else{
            if(OptimalParallelBenchmarkAnalyzer.MAX_DEPTH == 1){
                threshold = 3;
            }else if(OptimalParallelBenchmarkAnalyzer.MAX_DEPTH == 2){
                threshold = 5;
            }else{
                threshold = 6;
            }
        }

        // Show only the necessary

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                for(int i = 0; i < threshold; i++){

                    progBars.get(i).setDisable(false);
                    progIndicators.get(i).setDisable(false);

                    // Rotate the text and initially set it to invisible
                    Text text = (Text) progIndicators.get(i).lookup(".percentage");
                }
            }
    });

    t.start();
    t.join();

    // Initialize the total progress indicator
    Text text = (Text) totalProgInd.lookup(".percentage");
    }

    /**
     * Set the main app.
     */
    public void setMainApp(Main mainApp){
        this.mainApp = mainApp;
    }

    /**
     * Browse button handler
     */
    @FXML
    public void handleBrowseButtonClicked(){

        //Create a directory chooser
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Save");
        File file = new File(rootDir);

        // Set the initial directory of the chooser
        chooser.setInitialDirectory(file);
        File selectedDirectory = chooser.showDialog(mainApp.getPrimaryStage());

        //Set the text field value
        if(selectedDirectory != null){

            //Set the new root directory
            rootDir = selectedDirectory.getParent();

            resultTextField.setText(selectedDirectory.getAbsolutePath());
            Main.resPath = selectedDirectory.getAbsolutePath();

        }else{
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("No directory is selected");
            alert.showAndWait();
        }
    }

    /**
     * Start button handler.
     * @throws InterruptedException
     * @throws CloneNotSupportedException
     */
    @FXML
    public void handleStartButtonClicked() throws InterruptedException, CloneNotSupportedException {
        // Execute the analysis
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    // Disable the start button...
                    startButton.setDisable(true);

                    // Start the analysis...
                    mainApp.executeAnalysisScript(progBars, progIndicators, console, totalProgInd);

                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Make the done label visible
                doneLabel.setVisible(true);
            }
        });

        t.start();
    }
}
