package miltos.diploma;

import miltos.diploma.characteristics.QualityModel;
import miltos.diploma.characteristics.QualityModelLoader;

import java.io.File;

public class SingleProjectEvaluator {

    private static String QUALITY_MODEL_PATH = "/Users/guribhangu/development/research/qatch/Rules, " +
            "Models, Descriptions/Models/qualityModel.xml";
    private static String PROJECT_PATH = "/Users/guribhangu/test_dir/experiment";
    private static String PROJECT_RESULT_PATH = "/Users/guribhangu/development/research/qatch/Results/";

    private void qualityModelCreator(){

    }

    public static void main(String[] args) {
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
        pmdAnalyzer.analyze(PROJECT_PATH, PROJECT_RESULT_PATH + project.getName(),
                qualityModel.getProperties());
        ckjmAnalyzer.analyze(PROJECT_PATH, PROJECT_RESULT_PATH + project.getName(),
                qualityModel.getProperties());


    }
}
