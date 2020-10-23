package miltos.diploma.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Miltos on 15/2/2016.
 */
public class CharacteristicFX {

    /**
     * The basic fields of the class.
     */
    private final StringProperty name;
    private final StringProperty standard;
    private final StringProperty description;

    /**
     * Default constructor.
     */
    public CharacteristicFX(){
        this.name = new SimpleStringProperty("");
        this.standard = new SimpleStringProperty("");
        this.description = new SimpleStringProperty("");
    }

    /**
     * Constructor with some initial data.
     */
    public CharacteristicFX(String name, String standard, String description) {

        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.standard = new SimpleStringProperty(standard);

    }

    /**
     * Setters and getters.
     */
    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getStandard() {
        return standard.get();
    }

    public StringProperty standardProperty() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard.set(standard);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
}
