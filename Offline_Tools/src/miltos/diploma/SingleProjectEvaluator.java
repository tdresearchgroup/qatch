package miltos.diploma;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import miltos.diploma.characteristics.*;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class SingleProjectEvaluator {

    private static String QUALITY_MODEL_PATH = "/Users/guribhangu/development/research/qatch/Rules, " +
            "Models, Descriptions/Models/qualityModel.xml";
    private static String PROJECT_ROOT_PATH = "/Users/guribhangu/test_dir";
    private static String PROJECT_PATH = "/Users/guribhangu/test_dir/experiment";
    private static String PROJECT_RESULT_PATH = "/Users/guribhangu/development/research/qatch/Results";

    private static QualityModel qualityModelCreator(QualityModel qualityModel) throws InterruptedException {
        RInvoker rInvoker = new RInvoker();
        rInvoker.executeRScriptForThresholds();
        rInvoker.executeRScriptForWeightsElicitation();
//        ThresholdImporter thresholdImporter = new ThresholdImporter();
//        thresholdImporter.importThresholdsFromJSON(qualityModel.getProperties());

        return qualityModel;
    }

    private static void buildQuaityModel(QualityModel baseQualityModel) throws CloneNotSupportedException, InterruptedException {

        // get base model information
        PropertySet properties = baseQualityModel.getProperties();
        CharacteristicSet characteristics = baseQualityModel.getCharacteristics();
        Tqi tqi = baseQualityModel.getTqi();

        // Benchmark analysis
        BenchmarkAnalyzer benchmarkAnal = new BenchmarkAnalyzer();
        benchmarkAnal.setBenchRepoPath(PROJECT_ROOT_PATH);
        benchmarkAnal.setProperties(properties);
        benchmarkAnal.analyzeBenchmarkRepo();

        // import benchmark analysis results
        BenchmarkResultImporter benchmarkImporter = new BenchmarkResultImporter();
        BenchmarkProjects projects = new BenchmarkResultImporter().importResults(BenchmarkAnalyzer.BENCH_RESULT_PATH);

        // aggregate
        BenchmarkAggregator benchAggregator = new BenchmarkAggregator();
        benchAggregator.aggregateProjects(projects, properties);

        // export benchmark results
        BenchmarkAnalysisExporter exporter = new BenchmarkAnalysisExporter();
        exporter.exportToCsv(projects);

        // run thresholds R script
        RInvoker rInvoker = new RInvoker();
        rInvoker.executeRScriptForThresholds();

        // import thresholds
        ThresholdImporter thresholdImp = new ThresholdImporter();
        thresholdImp.importThresholdsFromJSON(properties);

        // assign thresholds into projects properties
        for(int i = 0; i < projects.size(); i++){
            PropertySet prop = projects.getProject(i).getProperties();
            for(int j = 0; j < prop.size(); j++){
                prop.get(j).setThresholds(properties.get(j).getThresholds().clone());
            }
        }

        // Call R script for weight elicitation
        RInvoker invoker = new RInvoker();
        invoker.executeRScriptForWeightsElicitation();

        // Import the weights from the json file
        WeightsImporter weightImporter = new WeightsImporter();
        weightImporter.importWeights(tqi, characteristics);

        // Export quality model
        QualityModelExporter qmExp = new QualityModelExporter();
        qmExp.exportQualityModelToXML(baseQualityModel, new File(PROJECT_RESULT_PATH + "/qualityModel.xml").getAbsolutePath());

        PropertiesExporter propertiesExp = new PropertiesExporter();
        propertiesExp.exportPropertiesToXML(properties, new File(PROJECT_RESULT_PATH + "/properties.xml").getAbsolutePath());
        propertiesExp.exportPropertiesToJSON(properties, new File(PROJECT_RESULT_PATH + "/properties.json").getAbsolutePath());
    }

    public static void main(String[] args) throws CloneNotSupportedException, IOException, InterruptedException {


        QualityModelLoader qualityModelLoader = new QualityModelLoader(QUALITY_MODEL_PATH);
        QualityModel baseQualityModel = qualityModelLoader.importQualityModel();


        SingleProjectEvaluator.buildQuaityModel(baseQualityModel);



//        QualityModelExporter qualityModelExporter = new QualityModelExporter();
//
//
//        QualityModel qualityModel = SingleProjectEvaluator.qualityModelCreator(baseQualityModel);

//        File projectDir = new File(PROJECT_PATH);
//
//        // Load project directory
//        Project project = new Project();
//        project.setPath(PROJECT_PATH);
//        project.setName(projectDir.getName());
//
//        PMDAnalyzer pmdAnalyzer = new PMDAnalyzer();
//        CKJMAnalyzer ckjmAnalyzer = new CKJMAnalyzer();
//
//        // Analyze projects
//        pmdAnalyzer.analyze(PROJECT_PATH, PROJECT_RESULT_PATH + "/" + project.getName(),
//                baseQualityModel.getProperties());
//        ckjmAnalyzer.analyze(PROJECT_PATH, PROJECT_RESULT_PATH + "/" + project.getName(),
//                baseQualityModel.getProperties());
//
//        // import pmd and ckjm analysis results
//        PMDResultsImporter pmdImporter = new PMDResultsImporter();
//        CKJMResultsImporter ckjmImporter = new CKJMResultsImporter();
//
//        File resultsDir = new File(PROJECT_RESULT_PATH + "/" + project.getName());
//        File[] results = resultsDir.listFiles();
//
//        for(File resultFile : results){
//
//            //Check if it is a ckjm result file
//            if(!resultFile.getName().contains("ckjm")){
//
//                //Parse the issues and add them to the IssueSet Vector of the Project object
//                project.addIssueSet(pmdImporter.parseIssues(resultFile.getAbsolutePath()));
//
//            }else{
//
//                //Parse the metrics of the project and add them to the MetricSet field of the Project object
//                project.setMetrics(ckjmImporter.parseMetrics(resultFile.getAbsolutePath()));
//            }
//        }
//
//        // Aggregate results
//        for(int i = 0; i < baseQualityModel.getProperties().size(); i++){
//            //Clone the property and add it to the PropertySet of the current project
//            Property p = (Property) baseQualityModel.getProperties().get(i).clone();
//            project.addProperty(p);
//        }
//
//        PMDAggregator pmd = new PMDAggregator();
//        CKJMAggregator ckjm = new CKJMAggregator();
//
//        pmd.aggregate(project);
//        ckjm.aggregate(project);
//
//        for(int i = 0; i < project.getProperties().size(); i++){
//            Property property =  project.getProperties().get(i);
//            property.getMeasure().calculateNormValue();
//        }
//
//        // Evaluate benchmark projects against their thresholds
//        ProjectEvaluator evaluator = new ProjectEvaluator();
//        evaluator.evaluateProjectProperties(project);
//
//        // Characteristic evaluation
//        for(int i = 0; i < baseQualityModel.getCharacteristics().size(); i++){
//            //Clone the characteristic and add it to the CharacteristicSet of the current project
//            Characteristic c = (Characteristic) baseQualityModel.getCharacteristics().get(i).clone();
//            project.getCharacteristics().addCharacteristic(c);
//        }
//
//        ProjectCharacteristicsEvaluator charEvaluator = new ProjectCharacteristicsEvaluator();
//        charEvaluator.evaluateProjectCharacteristics(project);
//
//
//        // TQI calculation
//        project.setTqi((Tqi)baseQualityModel.getTqi().clone());
//        project.calculateTQI();
//
//        System.out.println(project.getTqi());
//
//        //export result
//        EvaluationResultsExporter.exportProjectToJson(project, new File(PROJECT_RESULT_PATH + "/" + project.getName() + "_evalResults.json").getAbsolutePath());
//
//
//        JsonParser parser = new JsonParser();
//        JsonObject jsonObject = (JsonObject) parser.parse(new FileReader("/Users/guribhangu/development/research/qatch/Results/experiment_evalResults.json"));
//
//        System.out.println(jsonObject.get("properties"));
//        System.out.println(jsonObject.get("characteristics"));
//
//        System.out.println(jsonObject.get("name"));
    }
}
