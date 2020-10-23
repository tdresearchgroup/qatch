package miltos.diploma.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by Miltos on 15/2/2016.
 */
public class CharacteristicEditDialogController {

    /**
     * The FXML GUI fields.
     */
    @FXML
    private TextField nameField;

    @FXML
    private TextField standardField;

    @FXML
    private TextArea descriptionArea;

    /**
     * Useful fields.
     */
    private Stage dialogStage;
    private CharacteristicFX characteristic;
    private boolean okClicked = false;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Do nothing ...
    }

    /**
     * Sets the stage of this dialog.
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Sets the characteristic to be edited in the dialog.
     */
    public void setCharacteristic(CharacteristicFX characteristic) {
        this.characteristic = characteristic;

        nameField.setText(characteristic.getName());
        standardField.setText(characteristic.getStandard());
        descriptionArea.setText(characteristic.getDescription());
    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     */
    public boolean isOkClicked() {

        return okClicked;
    }

    /**
     * This method is executed whenever the user clicks the OK button
     * of the dialog pop up window.
     *
     * Typically it does the following:
     *   1. It checks if the inputs are valid
     *   2. If yes, it stores the data and closes the window
     *   3. If no, it does nothing
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {

            // Store the data into the CharacteristicFX object
            characteristic.setName(nameField.getText());
            characteristic.setStandard(standardField.getText());
            characteristic.setDescription(descriptionArea.getText());

            // Close the window and set okClicked to true
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

        // Initialize the error message
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().length() == 0) {
            errorMessage += "The name of the characteristic is not valid!\n";
        }

        if (standardField.getText() == null) {
            errorMessage += "The standard cannot be null!\n";
        }

        if (descriptionArea.getText() == null) {
            errorMessage += "The description cannot be null!\n";
        }

        // Check if there where errors
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
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
