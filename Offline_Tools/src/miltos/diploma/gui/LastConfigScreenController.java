package miltos.diploma.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import miltos.diploma.OptimalParallelBenchmarkAnalyzer;

/**
 * Created by Miltos on 16/2/2016.
 */
public class LastConfigScreenController {

    /**
     * FXML Gui fields.
     */
    @FXML
    private ToggleGroup radioMenu;

    @FXML
    private ToggleGroup radioMenu2;

    @FXML
    private RadioButton radioSerial;
    @FXML
    private RadioButton radioParallel;
    @FXML
    private RadioButton radioLow;
    @FXML
    private RadioButton radioHigh;
    @FXML
    private RadioButton radioMedium;
    @FXML
    private Text text;

    @FXML
    private Button nextButton;

    private Main mainApp;

    /**
     * Set the main app.
     */
    public void setMainApp(Main mainApp){
        this.mainApp = mainApp;
    }

    /**
     * This method handles the user's choice. It is invoked whenever the
     * user selects one of the scene's radio buttons that have to do with
     * serial or parallel analysis selection.
     */
    public void handleChoice(){
        // Enable the next button
        nextButton.setDisable(false);

        Toggle toggle = radioMenu.getSelectedToggle();

        if(toggle == radioSerial){
            // Disable the second question
            text.setOpacity(0.5);
            radioLow.setDisable(true);
            radioMedium.setDisable(true);
            radioHigh.setDisable(true);

            // Set the "serial" flag
            Main.serialAnalysis = true;

        }else if (toggle == radioParallel){
            // Enable the second question
            text.setOpacity(1);
            radioLow.setDisable(false);
            radioMedium.setDisable(false);
            radioHigh.setDisable(false);

            // Unset the "serial" flag
            Main.serialAnalysis = false;

        }else{
            // Do nothing
        }
    }

    /**
     * This method handles the selection of the depth of the parallelism.
     * Typically it is used in order to select the number of threads that
     * the work should be splited to.
     */
    public void handleDepthChoice(){

        Toggle toggle = radioMenu2.getSelectedToggle();

        if(toggle == radioLow){
            // Set the depth
            OptimalParallelBenchmarkAnalyzer.MAX_DEPTH = 1;

        }else if (toggle == radioMedium){
            // Set the depth
            OptimalParallelBenchmarkAnalyzer.MAX_DEPTH = 2;

        }else{
            // Set the depth
            OptimalParallelBenchmarkAnalyzer.MAX_DEPTH = 3;
        }

        //System.out.println("DEPTH : " + OptimalParallelBenchmarkAnalyzer.MAX_DEPTH );
    }

    public void handleNextButtonClicked(){
        // Just open the main console
        mainApp.showCentralConsole();
    }



}
