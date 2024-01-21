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
import lu.df.solver.StreamCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // 1. Step - Define problem

        DetectiveSolution problem = DetectiveSolution.generateData(5);
        problem.print();

        // 2. Step - Define SolverFactory

        SolverFactory<DetectiveSolution> solverFactoryFromXML = SolverFactory
                .createFromXmlResource("SolverConfig.xml");

        SolverFactory<DetectiveSolution> solverFactory = SolverFactory.create(
                new SolverConfig()
                        .withSolutionClass(DetectiveSolution.class)
                        .withEntityClasses(Detective.class, Visit.class) // CHANGEABLE thing
                        .withConstraintProviderClass(StreamCalculator.class)
                        .withTerminationConfig(new TerminationConfig()
                                .withSecondsSpentLimit(10L)) // 10 seconds for work
                        .withEnvironmentMode(EnvironmentMode.REPRODUCIBLE)
        );

        // 3. Step - Solve

        //Solver<DetectiveSolution> solver = solverFactory.buildSolver();
        Solver<DetectiveSolution> solver = solverFactoryFromXML.buildSolver();
        DetectiveSolution solution = solver.solve(problem);

        // 4. Step - Explain and print

        SolutionManager<DetectiveSolution, HardSoftScore> solutionManager = SolutionManager.create(solverFactory);
        ScoreExplanation<DetectiveSolution, HardSoftScore> scoreExplanation = solutionManager.explain(solution);
        LOGGER.info(scoreExplanation.getSummary());

        solution.print();
    }
}