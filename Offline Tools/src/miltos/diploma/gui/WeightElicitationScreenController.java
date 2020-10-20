package miltos.diploma.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import miltos.diploma.ComparisonMatricesCreator;
import miltos.diploma.RInvoker;

import java.io.File;

/**
 * Created by Miltos on 16/2/2016.
 */
public class WeightElicitationScreenController {

    @FXML
    TextArea textArea;

    @FXML
    RadioButton radioAHP;

    @FXML
    RadioButton radioFAHP;

    @FXML
    ToggleGroup radioMenu;

    @FXML
    Button nextButton;

    private Main mainApp;


    /**
     * Set the main app.
     */
    public void setMainApp(Main mainApp){
        this.mainApp = mainApp;
    }

    /**
     * This method is called whenever "Generate" button is clicked by the user.
     * It is responsible for the generation of the appropriate comparison matrices
     * that they are needed for the weights elicitation.
     */
    @FXML
    public void handleGenerateClicked(){

        // Get the user's choice
        Toggle toggle = radioMenu.getSelectedToggle();

        // Check which AHP technique the user chose
        if(toggle == radioAHP){

            // Enable Next Button
            nextButton.setDisable(false);

            // Generate crisp comparison matrices and inform the user
            textArea.setText("");
            textArea.appendText("AHP technique selected!\n");
            textArea.appendText("The comparison matrices can be found at:" + new File(ComparisonMatricesCreator.COMP_MATRICES).getAbsolutePath());
            textArea.appendText("\nPlease fulfill the comparison matricies with numbers between 1 and 9.\n");
            textArea.appendText("When you are ready click \"Next\"" );

            // Set the path of the desired script that should be executed for the weights elicitation
            RInvoker.weightsScript = RInvoker.R_AHP_SCRIPT;

            // Create the appropriate comparison matrices
            ComparisonMatricesCreator.generateCompMatrices(mainApp.qualityModel.getCharacteristics(), mainApp.qualityModel.getProperties(), true);

        }else if(toggle == radioFAHP){

            // Enable Next Button
            nextButton.setDisable(false);

            // Generate fuzzy comparison matrices and inform the user
            textArea.setText("");
            textArea.appendText("Fuzzy AHP technique selected!\n");
            textArea.appendText("The comparison matrices can be found at:\n" + new File(ComparisonMatricesCreator.COMP_MATRICES).getAbsolutePath());
            textArea.appendText("\n\nPlease fulfill each cell of the comparison matricies with one of the following linguistic variables:\n Very Low, Low, Moderate, High, Very High\n");
            textArea.appendText("\nIf you wish you can define how sure you are for your choice by providing one of the letters:\nU, D, C next to your judgement, seperated by comma (U: Uncertain, D: Default, C: Certain)" );
            textArea.appendText("\n\nPlease check your spelling! If you misspell a choice then the default values will be\nautomatically taken (i.e. Moderate and D)\n");
            textArea.appendText("When you are ready click \"Next\"" );

            // Set the path of the desired script that should be executed for the weights elicitation
            RInvoker.weightsScript = RInvoker.R_FAHP_SCRIPT;

            //Create the appropriate comparison matrices
            ComparisonMatricesCreator.generateCompMatrices(mainApp.qualityModel.getCharacteristics(), mainApp.qualityModel.getProperties(), true);


        }else{
            //Do nothing because the user didn't provide a choice
        }
    }

    /**
     * This method is called when the "Next" button is clicked. Typically, it
     * opens the next scene based on the user choices.
     */
    public void handleNextButtonClicked(){
        // Check if the user wants to execute a benchmark analysis
        if(Main.benchmarkCalibration){
            // Go to the LastConfigScreen in order to define the type of the analysis
            mainApp.showLastConfigScreen();
        }else{
            // Just jump to the main console
            mainApp.showCentralConsole();
        }
    }

}
