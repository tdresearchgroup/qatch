package miltos.diploma.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Miltos on 14/2/2016.
 */
public class ConfirmBox {


    public Text text;
    static boolean answer = false;
    public static Stage window;

    /**
     * A method for displaying the alert box...
     * @return
     * @throws IOException
     */
    public boolean display() throws IOException{

        //Create a new Stage
        window = new Stage();

        //Load the layout from an FXML file
        Parent root = FXMLLoader.load(getClass().getResource("ConfirmBoxScreen.fxml"));

        //Set the title and the modality of the window
        window.setTitle("Warning");
        window.initModality(Modality.APPLICATION_MODAL);

        //Show the window
        window.setScene(new Scene(root, 440, 118));
        window.showAndWait();

        //Return the user's amswer
        return answer;
    }

    public void yesButtonClicked(){
        answer = true;
        window.close();
    }

    public void noButtonClicked(){
        answer = false;
        window.close();
    }
}
