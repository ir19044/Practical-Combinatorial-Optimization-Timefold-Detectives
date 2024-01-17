package lu.df;

import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.SolverConfig;
import ai.timefold.solver.core.config.solver.termination.TerminationConfig;
import lu.df.domain.Detective;
import lu.df.domain.DetectiveSolution;
import lu.df.solver.ScoreCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        LOGGER.info("Hello world from Logger!");
        LOGGER.debug("Hello from debug!");

        DetectiveSolution problem = DetectiveSolution.generateData();
        problem.print();

        SolverFactory<DetectiveSolution> solverFactory = SolverFactory.create(
                new SolverConfig()
                        .withSolutionClass(DetectiveSolution.class)
                        .withEntityClasses(Detective.class) // CHANGEABLE thing
                        .withEasyScoreCalculatorClass(ScoreCalculator.class)
                        .withTerminationConfig(new TerminationConfig()
                                .withSecondsSpentLimit(10L)) // 10 seconds for work
        );

        Solver<DetectiveSolution> solver = solverFactory.buildSolver();
        DetectiveSolution solution = solver.solve(problem);

        solution.print();
    }
}