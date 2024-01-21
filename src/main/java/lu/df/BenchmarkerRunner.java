package lu.df;

import ai.timefold.solver.benchmark.api.PlannerBenchmark;
import ai.timefold.solver.benchmark.api.PlannerBenchmarkFactory;
import lu.df.domain.DetectiveSolution;
import lu.df.domain.DetectiveSolutionJsonIO;

import java.io.File;

public class BenchmarkerRunner {

    public static void main(String[] args) {
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory
                .createFromSolverConfigXmlResource("SolverConfig.xml");

        PlannerBenchmarkFactory benchmarkFactoryFromXML = PlannerBenchmarkFactory
                .createFromXmlResource("BenchmarkConfig.xml");

        //DetectiveSolution problem = DetectiveSolution.generateData();

        DetectiveSolutionJsonIO detectiveSolutionJsonIO = new DetectiveSolutionJsonIO();
        detectiveSolutionJsonIO.write(DetectiveSolution.generateData(5),
                new File("data/classExample5.json"));


        PlannerBenchmark benchmark = benchmarkFactoryFromXML.buildPlannerBenchmark();

        /*
        PlannerBenchmark benchmark = benchmarkFactoryFromXML.buildPlannerBenchmark(
                DetectiveSolution.generateData(5),
                DetectiveSolution.generateData(10),
                DetectiveSolution.generateData(20),
                DetectiveSolution.generateData(35),
                DetectiveSolution.generateData(50)
        );

         */

        benchmark.benchmarkAndShowReportInBrowser();
    }
}
