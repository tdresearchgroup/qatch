package miltos.diploma.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

/**
 * Created by Miltos on 15/2/2016.
 */
public class WelcomeScreenController {

    @FXML
    private ToggleGroup basicMenu;
    @FXML
    private Toggle benchmarkCal;
    @FXML
    private Toggle weightElicit;
    @FXML
    private Toggle modelDeriv;
    @FXML
    private Button nextButton;

    private Main mainApp;

    public WelcomeScreenController(){
        mainApp = new Main();
    }

    /**
     * Set the main app.
     */
    public void setMainApp(Main mainApp){
        this.mainApp = mainApp;
    }

    /**
     * This method is called when the "Next" button is clicked. Typically, it
     * opens the next scene based on the user choices.
     */
    public void handleNext(){

        // Get the user's choice
        Toggle toggle = basicMenu.getSelectedToggle();

        // Check the user's choice and open the desired scene
        if(toggle == benchmarkCal){

            // Benchmark Calibration only
            Main.benchmarkCalibration = true;
            Main.weightsElicitation = false;

            // Go to the next scene
            mainApp.showQualityModelScreen();

        }else if(toggle == weightElicit){

            // Weight Elicitation only
            Main.benchmarkCalibration = false;
            Main.weightsElicitation = true;

            // Go to the CharacteristicDefinition scene
            mainApp.showPropertiesScreen();

        }else if(toggle == modelDeriv){

            // Execute both benchmark calibration and weight elicitation
            Main.benchmarkCalibration = true;
            Main.weightsElicitation = true;

            // Go to the next scene
            mainApp.showQualityModelScreen();

        }else{
            //Do nothing because the user didn't provide a choice
        }
    }

    /**
     * This method is called when the user clicks one of the radio buttons.
     * It just enables the "Next" button.
     */
    public void handleChoice(){
        // Enable the next button
        nextButton.setDisable(false);
    }
}
