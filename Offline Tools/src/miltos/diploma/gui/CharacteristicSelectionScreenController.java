package miltos.diploma.gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import miltos.diploma.Measure;
import miltos.diploma.Property;
import miltos.diploma.characteristics.Characteristic;

import java.io.File;

/**
 * Created by Miltos on 15/2/2016.
 */


public class CharacteristicSelectionScreenController {


    @FXML
    private TableView<CharacteristicFX> charTable;

    @FXML
    private TableColumn<CharacteristicFX, String> nameColumn;

    @FXML
    private Label nameLabel;

    @FXML
    private Label standardLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Button deleteButton;

    // Reference to the main application.
    private Main mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public CharacteristicSelectionScreenController() {
        // Do nothing ...
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file is been loaded.
     *
     * It is used for the initialization of the TableView that displays the
     * quality model's characteristics to the user.
     *
     * ATTENTION:
     * The TableView knows where to find the data because the setMainApp()
     * method is initially called.
     */
    @FXML
    private void initialize() {

        nameColumn.setCellValueFactory(
                cellData -> cellData.getValue().nameProperty());

        // Clear characteristic details.
        showCharacteristicDetails(null);

        // Listen for selection changes and show the characteristic details when changed.
        charTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showCharacteristicDetails(newValue));
    }

    /**
     * This method is called by the main application to give a reference back to itself.
     * Typically it does the following:
     *
     *  1. It sets the mainApp field to the mainApp object of this application.
     *  2. It attaches the mainApp's characteristics to the TableView of the
     *     GUI.
     *
     * It is obligatory in order to set the data that will be displayed on the
     * TableView.
     */
    public void setMainApp(Main mainApp) {
        // Set the mainApp field
        this.mainApp = mainApp;

        // Add observable list data to the table
        charTable.setItems(mainApp.getCharData());
    }


    /**
     * Fills all text fields to show details about a characteristic.
     * If the specified property is null, all text fields are cleared.
     */
    private void showCharacteristicDetails(CharacteristicFX characteristic) {
        if (characteristic != null) {

            // Fill the labels with info from the characteristic object.
            nameLabel.setText(characteristic.getName());
            standardLabel.setText(characteristic.getStandard());
            descriptionLabel.setText(characteristic.getDescription());

        } else {
            // Characteristic is null, remove all the text.
            nameLabel.setText("");
            standardLabel.setText("");
            descriptionLabel.setText("");
        }
    }

    /**
     * A method for handling the delete button.
     */
    @FXML
    private void handleDeleteCharacteristic(){
        // Get the index of the selected characteristic
        int selectedIndex = charTable.getSelectionModel().getSelectedIndex();

        // Check the index and delete the characteristic
        if(selectedIndex >= 0){
            // Delete the selected characteristic
            charTable.getItems().remove(selectedIndex);
        }else{
            // If there is nothing to delete, disable the "Delete" button
            deleteButton.setDisable(true);
        }
    }


    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new characteristic.
     */
    @FXML
    private void handleNewCharacteristic() {
        // Create a new CharacteristicFX object
        CharacteristicFX tempCharacteristic = new CharacteristicFX();

        // Show the CharacteristicEditDialog and wait until "OK" button is clicked
        boolean okClicked = mainApp.showCharacteristicEditDialog(tempCharacteristic);

        // Check if "OK" button is clicked and add the CharacteristicFX object if so
        if (okClicked) {
            mainApp.getCharData().add(tempCharacteristic);
        }

        // Enable the delete button
        deleteButton.setDisable(false);
    }

    /**
     * This method is called whenever the user wants to edit a certain
     * CharacteristicFX object that belongs to the TableView. Typically,
     * it is called whenever "Edit" button is clicked.
     */
    @FXML
    private void handleEditCharacteristic(){

        // Get the currently selected characteristic from the table view
        CharacteristicFX characteristic = charTable.getSelectionModel().getSelectedItem();

        // Check if the selected characteristic is null (i.e. "Edit" pressed with no selection)
        if(characteristic != null){
            // If it is not null (something is selected) open the dialog and wait for the answer
            boolean okClicked = mainApp.showCharacteristicEditDialog(characteristic);

            // Check the user answer
            if(okClicked){
                // If "Ok" is clicked then display the properties info
                showCharacteristicDetails(characteristic);
            }
        }else{
            // If nothing was selected inform the user
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Characteristic Selected");
            alert.setContentText("Please select a characteristic from the table and then click \"Edit\".");
            alert.showAndWait();
        }
    }

    /**
     * This method is called whenever "Next" button is clicked by the user.
     * It checks if the user defined Properties and Characteristics and if
     * yes it moves to the next screen.
     */
    @FXML
    private void handleNextButton(){

        // Check for validity
        if(validateCharacteristics() && validateProperties() && mainApp.getCharData().size() != 0){

            // Step 1 : Load the characteristics into the properties field of the QualityModel object
            loadCharacteristics();

            // Step 2 : Call the next scene
             mainApp.showWeightElicitationScreen();
        }else{
            System.out.println("The properties cannot be validated!");
            System.out.println("Please fix the issues and try again!!");
        }
    }

    /**
     * This method handles the selection of the "Back" button.
     */
    @FXML
    public void handleBackOption(){

        // Just return to the Properties Selection Screen
        mainApp.showPropertiesScreen();
    }

    /**
     * This method is responsible for parsing the basic info of the characteristics
     * of the desired Quality Model, to the CharacteristicSet object of the object
     * QualityModel, that will be used in the main script.
     */
    public void loadCharacteristics(){

        // Get the characteristics from the table view
        ObservableList<CharacteristicFX> charData = mainApp.getCharData();

        for(int i = 0; i < charData.size(); i ++){

            // Create a new Characteristic object
            Characteristic characteristic = new Characteristic();

            // Parse the results from the CharacteristicFX objects
            characteristic.setName(charData.get(i).getName());
            characteristic.setDescription(charData.get(i).getDescription());
            characteristic.setStandard(charData.get(i).getStandard());

            // Add the Characteristic to the CharacteristicSet of the QualityModel
            Main.qualityModel.getCharacteristics().addCharacteristic(characteristic);
        }
    }


    /**
     * This method checks the user defined characteristics for validity.
     * This step is obligatory because we give the oportunity to the user
     * to load the properties from an external xml file.
     * @return
     */
    public boolean validateCharacteristics(){

        // Initialize the basic local variables
        boolean validated = true;
        String errorCharacteristic = "";

        // Retrieve the list with the properties
        ObservableList<CharacteristicFX> charData = mainApp.getCharData();

        // Validate each characteristic
        String errorMessage = "";
        CharacteristicFX characteristic = null;
        for(int i = 0; i < charData.size(); i++){

            // Initialize the error message string for this property
            errorMessage = "";

            // Get the current CharacteristicFX object
            characteristic = charData.get(i);

            if (characteristic.getName() == null || characteristic.getName().length() == 0) {
                errorMessage += "The name of the characteristic is not valid!\n";
            }

            if (characteristic.getStandard() == null) {
                errorMessage += "The standard cannot be null!\n";
            }

            if (characteristic.getDescription() == null) {
                errorMessage += "The description cannot be null!\n";
            }


            if (errorMessage.length() == 0) {
                // This characteristic passed the validation
            } else {
                // This characteristic cannot be validated
                // Stop the process
                errorCharacteristic = characteristic.getName();
                validated = false;
                break;
            }
        }

        // Check which is the reason for the loop termination
        if(validated){
            return true;
        }else {
            // Show the error message.
            errorMessage += "Characteristic Name : " + errorCharacteristic;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("Invalid Characteristic");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();

            return false;
        }
    }


    /**
     * This method checks the user defined properties for validation.
     * This step is obligatory because we give the opportunity to the user
     * to load the properties from an external xml file.
     * @return
     */
    public boolean validateProperties(){

        // Initialize the basic local variables
        boolean validated = true;
        String errorProperty = "";

        // Retrieve the list with the properties
        ObservableList<PropertyFX> propData = mainApp.getPropertyData();

        // Validate each property
        String errorMessage = "";
        PropertyFX property = null;
        for(int i = 0; i < propData.size(); i++){

            // Initialize the error message string for this property
            errorMessage = "";

            // Get the current propertyFX object
            property = propData.get(i);

            if (property.getName() == null || property.getName().length() == 0) {
                errorMessage += "The name of the property is not valid!\n";
            }
            if (property.getType() == null || (!"Metric".equalsIgnoreCase(property.getType()) && !"Finding".equalsIgnoreCase(property.getType())) ) {
                errorMessage += "The type of the property is not valid!\n";
            }
            if (property.getTool() == null || (!"PMD".equalsIgnoreCase(property.getTool()) && !"CKJM".equalsIgnoreCase(property.getTool()))) {
                errorMessage += "The type of the tool is not valid!\n";
            }

            if("Metric".equalsIgnoreCase(property.getType())) {
                // It is a metric
                if(property.getMetricName() == null || property.getMetricName().length() == 0) {
                    errorMessage += "The name of the metric is not setted!\n";
                }

                if("PMD".equalsIgnoreCase(property.getTool())){
                    errorMessage += "You can not use PMD for a metric calculation!\n";
                }
            }else {
                // It is a finding

                if("CKJM".equalsIgnoreCase(property.getTool())){
                    errorMessage += "You can not use CKJM for a violation detection!\n";
                }

                // Check if the path is setted
                if(property.getRulesetPath() == null || property.getRulesetPath().length() == 0) {
                    errorMessage += "The ruleset path is not setted!\n";
                }else {
                    // The path is setted!!
                    // Check if it is an xml file...!!!
                    File rulesetXMLFile = new File(property.getRulesetPath());
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

                if((property.getPositive() != true) && (property.getPositive() != false)) {
                    errorMessage += "You must specify the impact of this property on the Software's Quality!";
                }

                if(property.getDescription() == null) {
                    errorMessage += "You must provide a brief description of the property!";
                }
            }


            if (errorMessage.length() == 0) {
                // This property passed the validation
            } else {
                // This property cannot be validated
                // Stop the process
                errorProperty = property.getName();
                validated = false;
                break;
            }
        }

        // Check which is the reason for the loop termination
        if(validated){
            return true;
        }else {
            // Show the error message.
            errorMessage += "Property Name : " + errorProperty;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("Invalid Property");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();

            return false;
        }
    }

    /**
     * This method is used in order to refresh the TableView properly.
     */
    public static void refreshTable(final TableView<CharacteristicFX> table, final ObservableList<CharacteristicFX> charList){
        table.setItems(null);
        table.layout();
        table.setItems(charList);
    }

    /**
     * This method is called whenever "Refresh" label is clicked. It is
     * used in order to manually refresh the table with the properties.
     */
    @FXML
    private void handleRefreshLabelClicked(){

        refreshTable(charTable, mainApp.getCharData());
        deleteButton.setDisable(false);
    }
}
