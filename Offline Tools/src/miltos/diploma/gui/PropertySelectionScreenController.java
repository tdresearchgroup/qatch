package miltos.diploma.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import miltos.diploma.Measure;
import miltos.diploma.Property;

import java.io.File;

/**
 * Created by Miltos on 15/2/2016.
 */
public class PropertySelectionScreenController {

    @FXML
    private TableView<PropertyFX> propertyTable;

    @FXML
    private TableColumn<PropertyFX, String> nameColumn;

    @FXML
    private Label nameLabel;

    @FXML
    private Label typeLabel;

    @FXML
    private Label positiveLabel;

    @FXML
    private Label metricNameLabel;

    @FXML
    private Label rulesetPathLabel;

    @FXML
    private Label toolLabel;

    @FXML
    private Label decrLabel;

    @FXML
    private Button deleteButton;

    @FXML
    private Label refreshLabel;

    // Reference to the main application.
    private Main mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public PropertySelectionScreenController() {
        // Do nothing!
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

        nameColumn.setCellValueFactory(
                cellData -> cellData.getValue().nameProperty());

        // Clear property details
        showPropertyDetails(null);

        // Listen for selection changes and show the property details when changed
        propertyTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showPropertyDetails(newValue));
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        propertyTable.setItems(mainApp.getPropertyData());
    }

    /**
     * Fills all text fields to show details about a property.
     * If the specified property is null, all text fields are cleared.
     */
    private void showPropertyDetails(PropertyFX property) {
        if (property != null) {
            // Fill the labels with info from the property object.
            nameLabel.setText(property.getName());
            toolLabel.setText(property.getTool());
            typeLabel.setText(property.getType());
            positiveLabel.setText(Boolean.toString(property.getPositive()));
            metricNameLabel.setText(property.getMetricName());
            rulesetPathLabel.setText(property.getRulesetPath());
            decrLabel.setText(property.getDescription());

        } else {
            // Property is null, remove all the text.
            nameLabel.setText("");
            toolLabel.setText("");
            typeLabel.setText("");
            positiveLabel.setText("");
            metricNameLabel.setText("");
            rulesetPathLabel.setText("");
            decrLabel.setText("");
        }
    }

    /**
     * A method for handling the delete button.
     */
    @FXML
    private void handleDeleteProperty(){

        // Get the selected property
        int selectedIndex = propertyTable.getSelectionModel().getSelectedIndex();

        // Check if a property is selected
        if(selectedIndex >= 0){
            // Remove the selected property
            propertyTable.getItems().remove(selectedIndex);
        }else{
            // If there is nothing to delete - disable the button (empty table)
            deleteButton.setDisable(true);
        }
    }

    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new property.
     */
    @FXML
    private void handleNewProperty() {
        // Open the PropertyEditDialog
        PropertyFX tempProperty = new PropertyFX();
        boolean okClicked = mainApp.showPropertyEditDialog(tempProperty);
        if (okClicked) {
            mainApp.getPropertyData().add(tempProperty);
        }
        // Enable the "Delete" button
        deleteButton.setDisable(false);
    }

    /**
     * This method is called whenever the "Edit" button is clicked. It allows
     * the user to edit the details of a desired property.
     */
    @FXML
    private void handleEditProperty(){

        // Get the currently selected property from the table view
        PropertyFX property = propertyTable.getSelectionModel().getSelectedItem();

        // Check if the selected property is null (i.e. "Edit" pressed with no selection)
        if(property != null){
            // If it is not null (something selected) open the dialog and wait for the answer
            boolean okClicked = mainApp.showPropertyEditDialog(property);

            // Check the user answer
            if(okClicked){
                // If Ok is clicked then display the properties info
                showPropertyDetails(property);
            }
        }else{
            // If nothing was selected inform the user
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Property Selected");
            alert.setContentText("Please select a property from the table and then click \"Edit\".");

            alert.showAndWait();
        }
    }

    /**
     * This method is called whenever the "Next" button is clicked. It validates the
     * properties and moves to the next scene if and only if the validation is
     * successful.
     */
    @FXML
    private void handleNextButton(){

        // Check for validity
        if(validateProperties() && mainApp.getPropertyData().size() != 0){

            // Step 1 : Load the properties to the properties field of the QualityModel object
            // TODO: Quick fix for "Back" Button
            // If there is a property object and it already contains some staff remove them!
            if ( Main.qualityModel.getProperties() != null && Main.qualityModel.getProperties().size() != 0){
                Main.qualityModel.getProperties().clearProperties();
            }
            loadProperties();

            //TODO: New Analysis Quick Fix
            Main.success = true;

            // Step 2 : Call the next scene
            if(Main.weightsElicitation){
                mainApp.showCharacteristicsScreen();
            }else{
                mainApp.showLastConfigScreen();
            }
        }else{
            System.out.println("The properties cannot be validated!");
            System.out.println("Please fix the issues and try again!!");
        }

    }

    /**
     * This method is responsible for parsing the basic info of the properties
     * of the desired Quality Model, to the PropertySet object of the object
     * QualityModel, that will be used in the main script.
     */
    public void loadProperties(){

        // Get the properties from the table view
        ObservableList<PropertyFX> propData = mainApp.getPropertyData();

        for(int i = 0; i < propData.size(); i ++){

            // Create a new Property object
            Property property = new Property();

            // Parse the results from the PropertyFX objects
            property.setName(propData.get(i).getName());
            property.setDescription(propData.get(i).getDescription());
            property.setPositive(propData.get(i).getPositive());

            property.getMeasure().setMetricName(propData.get(i).getMetricName());
            property.getMeasure().setRulesetPath(propData.get(i).getRulesetPath());
            property.getMeasure().setTool(propData.get(i).getTool());

            if("Metric".equalsIgnoreCase(propData.get(i).getType())){
                property.getMeasure().setType(Measure.METRIC);
            }else{
                property.getMeasure().setType(Measure.FINDING);
            }

            // Add the property to the PropertySet of the QualityModel
            Main.qualityModel.getProperties().addProperty(property);
        }
    }

    /**
     * This method checks the user defined properties for validation.
     * This step is obligatory because we give the opportunity to the user
     * to load the properties from an external xml file.
     */
    public boolean validateProperties(){

        // Initialize the basic local variables
        boolean validated = true;
        String errorProperty = "";

        // Retrieve the list with the properties
        ObservableList<PropertyFX> propData = mainApp.getPropertyData();
        // System.out.println("SIZE : " + propData.size());

        // Validate each property ....
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

            // Check if there is any error
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
     * This method is used in order to refresh the table containing the model's properties.
     * @param table
     * @param propList
     */
    public static void refreshTable(final TableView<PropertyFX> table, final ObservableList<PropertyFX> propList){
        table.setItems(null);
        table.layout();
        table.setItems(propList);
    }

    /**
     * This method is called whenever the "Refresh" label is clicked by the user.
     */
    @FXML
    private void handleRefreshLabelClicked(){
        refreshTable(propertyTable, mainApp.getPropertyData());
        deleteButton.setDisable(false);
    }

}


