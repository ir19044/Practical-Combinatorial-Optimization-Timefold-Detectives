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

        //PlannerBenchmarkFactory benchmarkFactoryFromXML = PlannerBenchmarkFactory
        //        .createFromXmlResource("BenchmarkConfig.xml");

        DetectiveSolution problem = DetectiveSolution.generateData();

        DetectiveSolutionJsonIO detectiveSolutionJsonIO = new DetectiveSolutionJsonIO();
        detectiveSolutionJsonIO.write(DetectiveSolution.generateData(),
                new File("data/classExample5.json"));

        PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark(problem);

        benchmark.benchmarkAndShowReportInBrowser();

    }
}
