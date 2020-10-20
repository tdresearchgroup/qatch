package miltos.diploma.gui;

import javafx.beans.property.*;

import java.time.LocalDate;

/**
 * Created by Miltos on 15/2/2016.
 */
public class PropertyFX {

    // The basic fields
    private final StringProperty name;
    private final StringProperty type;
    private final BooleanProperty positive;
    private final StringProperty metricName;
    private final StringProperty rulesetPath;
    private final StringProperty tool;
    private final StringProperty description;

    /**
     * Default constructor.
     */
    public PropertyFX(){
        this(null,null);
    }

    /**
     * Constructor with some initial data.
     */
    public PropertyFX(String name, String type) {
        this.name = new SimpleStringProperty(name);
        this.type = new SimpleStringProperty(type);

        // Some initial dummy data, just for convenient testing.
        this.metricName = new SimpleStringProperty("");
        this.rulesetPath = new SimpleStringProperty("");
        this.description = new SimpleStringProperty("");
        this.tool = new SimpleStringProperty("");
        this.positive = new SimpleBooleanProperty(false);
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

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public boolean getPositive() {
        return positive.get();
    }

    public BooleanProperty positiveProperty() {
        return positive;
    }

    public void setPositive(boolean positive) {
        this.positive.set(positive);
    }

    public String getMetricName() {
        return metricName.get();
    }

    public StringProperty metricNameProperty() {
        return metricName;
    }

    public void setMetricName(String metricNme) {
        this.metricName.set(metricNme);
    }

    public String getRulesetPath() {
        return rulesetPath.get();
    }

    public StringProperty rulesetPathProperty() {
        return rulesetPath;
    }

    public void setRulesetPath(String rulesetPath) {
        this.rulesetPath.set(rulesetPath);
    }

    public String getTool() {
        return tool.get();
    }

    public StringProperty toolProperty() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool.set(tool);
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
