package miltos.diploma;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import miltos.diploma.characteristics.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class SingleProjectEvaluator {

    private static String QUALITY_MODEL_PATH = "/Users/guribhangu/development/research/qatch/Rules, " +
            "Models, Descriptions/Models/qualityModel.xml";
    private static String PROJECT_PATH = "/Users/guribhangu/test_dir/experiment";
    private static String PROJECT_RESULT_PATH = "/Users/guribhangu/development/research/qatch/Results";

    private void qualityModelCreator(){

    }

    public static void main(String[] args) throws CloneNotSupportedException, FileNotFoundException {
        QualityModelLoader qualityModelLoader = new QualityModelLoader(QUALITY_MODEL_PATH);

        QualityModel qualityModel = qualityModelLoader.importQualityModel();

        File projectDir = new File(PROJECT_PATH);

        // Load project directory
        Project project = new Project();
        project.setPath(PROJECT_PATH);
        project.setName(projectDir.getName());


        PMDAnalyzer pmdAnalyzer = new PMDAnalyzer();
        CKJMAnalyzer ckjmAnalyzer = new CKJMAnalyzer();

        // Analyze projects
        pmdAnalyzer.analyze(PROJECT_PATH, PROJECT_RESULT_PATH + "/" + project.getName(),
                qualityModel.getProperties());
        ckjmAnalyzer.analyze(PROJECT_PATH, PROJECT_RESULT_PATH + "/" + project.getName(),
                qualityModel.getProperties());

        // import pmd and ckjm analysis results
        PMDResultsImporter pmdImporter = new PMDResultsImporter();
        CKJMResultsImporter ckjmImporter = new CKJMResultsImporter();

        File resultsDir = new File(PROJECT_RESULT_PATH + "/" + project.getName());
        File[] results = resultsDir.listFiles();

        for(File resultFile : results){

            //Check if it is a ckjm result file
            if(!resultFile.getName().contains("ckjm")){

                //Parse the issues and add them to the IssueSet Vector of the Project object
                project.addIssueSet(pmdImporter.parseIssues(resultFile.getAbsolutePath()));

            }else{

                //Parse the metrics of the project and add them to the MetricSet field of the Project object
                project.setMetrics(ckjmImporter.parseMetrics(resultFile.getAbsolutePath()));
            }
        }

        // Aggregate results
        for(int i = 0; i < qualityModel.getProperties().size(); i++){
            //Clone the property and add it to the PropertySet of the current project
            Property p = (Property) qualityModel.getProperties().get(i).clone();
            project.addProperty(p);
        }

        PMDAggregator pmd = new PMDAggregator();
        CKJMAggregator ckjm = new CKJMAggregator();

        pmd.aggregate(project);
        ckjm.aggregate(project);

        for(int i = 0; i < project.getProperties().size(); i++){
            Property property =  project.getProperties().get(i);
            property.getMeasure().calculateNormValue();
        }

        // Evaluate benchmark projects against their thresholds
        ProjectEvaluator evaluator = new ProjectEvaluator();
        evaluator.evaluateProjectProperties(project);

        // Characteristic evaluation
        for(int i = 0; i < qualityModel.getCharacteristics().size(); i++){
            //Clone the characteristic and add it to the CharacteristicSet of the current project
            Characteristic c = (Characteristic) qualityModel.getCharacteristics().get(i).clone();
            project.getCharacteristics().addCharacteristic(c);
        }

        ProjectCharacteristicsEvaluator charEvaluator = new ProjectCharacteristicsEvaluator();
        charEvaluator.evaluateProjectCharacteristics(project);


        // TQI calculation
        project.setTqi((Tqi)qualityModel.getTqi().clone());
        project.calculateTQI();

        System.out.println(project.getMetrics());
        System.out.println(project.getTqi());

        //export result
        EvaluationResultsExporter.exportProjectToJson(project, new File(PROJECT_RESULT_PATH + "/" + project.getName() + "_evalResults.json").getAbsolutePath());


        JsonParser parser = new JsonParser();

        JsonElement a = parser.parse(new FileReader("/Users/guribhangu/development/research/qatch/Results/experiment_evalResults.json"));

//        System.out.println(a);
    }
}
