package lu.df;

import ai.timefold.solver.core.api.score.ScoreExplanation;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.solver.SolutionManager;
import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.EnvironmentMode;
import ai.timefold.solver.core.config.solver.SolverConfig;
import ai.timefold.solver.core.config.solver.termination.TerminationConfig;
import lu.df.domain.Detective;
import lu.df.domain.DetectiveSolution;
import lu.df.domain.Visit;
import lu.df.solver.ScoreCalculator;
import lu.df.solver.StreamCalculator;
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
                        .withEntityClasses(Detective.class, Visit.class) // CHANGEABLE thing
                        //.withEasyScoreCalculatorClass(ScoreCalculator.class)
                        .withConstraintProviderClass(StreamCalculator.class)
                        .withTerminationConfig(new TerminationConfig()
                                .withSecondsSpentLimit(10L)) // 10 seconds for work
                        .withEnvironmentMode(EnvironmentMode.FULL_ASSERT)
        );

        Solver<DetectiveSolution> solver = solverFactory.buildSolver();
        DetectiveSolution solution = solver.solve(problem);

        SolutionManager<DetectiveSolution, HardSoftScore> solutionManager = SolutionManager.create(solverFactory);
        ScoreExplanation<DetectiveSolution, HardSoftScore> scoreExplanation = solutionManager.explain(solution);
        LOGGER.info(scoreExplanation.getSummary());

        solution.print();
    }
}