package miltos.diploma.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Created by Miltos on 15/2/2016.
 */
public class PropertyEditDialogController {


    //Observable Lists for the ComboBoxes of the dialog
    ObservableList<String> typeComboItems = FXCollections.observableArrayList("Metric", "Finding");
    ObservableList<String> toolComboItems = FXCollections.observableArrayList("PMD", "CKJM");
    ObservableList<String> positiveComboItems = FXCollections.observableArrayList("false", "true");
    ObservableList<String> metricNameComboItems = FXCollections.observableArrayList("","WMC", "DIT", "NOC", "CBO", "RFC", "LCOM", "Ca"
            , "Ce", "NPM", "LCOM3", "LOC", "DAM", "MOA", "MFA", "CAM", "IC", "CBM", "AMC", "CC");


    /**
     * FXML GUI fields.
     */
    @FXML
    private ComboBox<String> typeCombo;

    @FXML
    private ComboBox<String> toolCombo;

    @FXML
    private ComboBox<String> metricNameCombo;

    @FXML
    private ComboBox<String> positiveCombo;

    @FXML
    private TextField nameField;

    @FXML
    private TextField rulesetPathField;

    @FXML
    private TextArea descriptionArea;

    /**
     * Useful fields.
     */
    private Stage dialogStage;
    private PropertyFX property;
    private boolean okClicked = false;
    private String rootDir = "/";


    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

        typeCombo.setValue("Metric");
        typeCombo.setItems(typeComboItems);

        toolCombo.setValue("CKJM");
        toolCombo.setItems(toolComboItems);

        metricNameCombo.setValue("");
        metricNameCombo.setItems(metricNameComboItems);

        positiveCombo.setValue("false");
        positiveCombo.setItems(positiveComboItems);
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Sets the property to be edited in the dialog.
     */
    public void setProperty(PropertyFX property) {
        this.property = property;

        nameField.setText(property.getName());
        rulesetPathField.setText(property.getRulesetPath());
        descriptionArea.setText(property.getDescription());

        // Check the type of the property
        if("Metric".equalsIgnoreCase(property.getType())){
            typeCombo.setValue("Metric");
            rulesetPathField.setDisable(true);
            rulesetPathField.setText("");

            metricNameCombo.setDisable(false);

        }else if("Finding".equalsIgnoreCase(property.getType())){
            typeCombo.setValue("Finding");
            metricNameCombo.setDisable(true);
            metricNameCombo.setValue("");

            rulesetPathField.setDisable(false);
        }else {
            System.out.println("There is not such a type!!!");
            typeCombo.setValue(property.getType());
            metricNameCombo.setValue("");
            rulesetPathField.setText("");
        }

        // Check the selected tool that should be used for the property's quantification
        if("PMD".equalsIgnoreCase(property.getType())){
            toolCombo.setValue("PMD");
        }else if("CKJM".equalsIgnoreCase(property.getType())){
            toolCombo.setValue("CJKM");
        }else {
            System.out.println("There isn't such a tool!!!");
            toolCombo.setValue(property.getTool());
        }

        metricNameCombo.setValue(property.getMetricName());
        positiveCombo.setValue(Boolean.toString(property.getPositive()));
    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     *
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }


    /**
     * This method is responsible for enabling/disabling some fields
     * according to the value of the type ComboBox.
     */
    @FXML
    public void handleTypeSelection(){

        //Get the selected value
        String selected = typeCombo.getValue();
        System.out.print(selected);

        if("Metric".equalsIgnoreCase(selected)){
            rulesetPathField.setDisable(true);
            rulesetPathField.setText("");
            metricNameCombo.setDisable(false);
        }else if("Finding".equalsIgnoreCase(selected)){
            metricNameCombo.setDisable(true);
            metricNameCombo.setValue("");
            rulesetPathField.setDisable(false);
        }else {
            typeCombo.setValue(property.getType());
        }
    }

    /**
     * This method is responsible for handling the click of the
     * ruleset field.
     */
    @FXML
    public void handleMouseClicked(){
        // Call the handler if and only if the text field is not disabled
        if(!rulesetPathField.isDisabled()){
            handleBrowse();
        }else{
           //System.out.println("The field is disabled!!!");
        }
    }

    /**
     * A method for handling the importation of the ruleset XML file.
     */
    public void handleBrowse(){

        // Create a new file chooser
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open ruleset file");
        chooser.setInitialDirectory(new File(rootDir));

        // Open the dialog and retrieve the path of the xml file
        File selectedFile = chooser.showOpenDialog(dialogStage);

        //Set the text field value
        if(selectedFile != null){
            //Set the new root directory and update the appropriate text field
            rootDir = selectedFile.getParent();
            rulesetPathField.setText(selectedFile.getAbsolutePath());
        }
    }


    /**
     * This method is called whenever the "OK" button is clicked.
     */
    @FXML
    private void handleOk() {
        // Check if the input is valid
        if (isInputValid()) {
            // Set the information of the new property
            property.setName(nameField.getText());
            property.setType(typeCombo.getValue());
            property.setTool(toolCombo.getValue());
            property.setMetricName(metricNameCombo.getValue());
            property.setRulesetPath(rulesetPathField.getText());
            property.setPositive(Boolean.parseBoolean(positiveCombo.getValue()));
            property.setDescription(descriptionArea.getText());

            // Set the flag and close the dialog window
            okClicked = true;
            dialogStage.close();
        }
    }

    /**
     * Validates the user input in the text fields.
     *
     * @return true if the input is valid
     */
    private boolean isInputValid() {

        // The error message
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().length() == 0) {
            errorMessage += "The name of the property is not valid!\n";
        }
        if (typeCombo.getValue() == null || (!"Metric".equalsIgnoreCase(typeCombo.getValue()) && !"Finding".equalsIgnoreCase(typeCombo.getValue())) ) {
            errorMessage += "The type of the property is not valid!\n";
        }
        if (toolCombo.getValue() == null || (!"PMD".equalsIgnoreCase(toolCombo.getValue()) && !"CKJM".equalsIgnoreCase(toolCombo.getValue()))) {
            errorMessage += "The type of the tool is not valid!\n";
        }

        if("Metric".equalsIgnoreCase(typeCombo.getValue())) {
            // It is a metric
            if(metricNameCombo.getValue() == null || metricNameCombo.getValue().length() == 0) {
                errorMessage += "The name of the metric is not setted!\n";
            }

            if("PMD".equalsIgnoreCase(toolCombo.getValue())){
                errorMessage += "You can not use PMD for a metric calculation!\n";
            }
        }else {
            // It is a finding
            if("CKJM".equalsIgnoreCase(toolCombo.getValue())){
                errorMessage += "You can not use CKJM for a violation detection!\n";
            }

            // Check if the path is set
            if(rulesetPathField.getText() == null || rulesetPathField.getText().length() == 0) {
                errorMessage += "The ruleset path is not setted!\n";
            }else {
                // The path is set
                // Check if it is an xml file...!!!
                File rulesetXMLFile = new File(rulesetPathField.getText());
                if(!rulesetXMLFile.exists() || !rulesetXMLFile.isFile()){
                    System.out.println("The desired file doesn't exist..!");
                    errorMessage += "The desired ruleset file doesn't exist..!\n";
                }else if(!rulesetXMLFile.getName().contains(".xml")){
                    System.out.println("The desired file is not an XML file..!");
                    errorMessage += "The desired file is not an XML file..!\n";
                }else{
                    //All is fine..!!
                }
            }

            if(positiveCombo.getValue() == null) {
                errorMessage += "You must specify the impact of this property on the Software's Quality!";
            }

            if(descriptionArea == null) {
                errorMessage += "You must provide a brief description of the property!";
            }
        }

        // Check if there was any error
        if (errorMessage.length() == 0) {
            // No error. Inputs are valid.
            return true;
        } else {
            // There is at least one error. Show the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();

            return false;
        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}





