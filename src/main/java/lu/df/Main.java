package lu.df;

import ai.timefold.solver.core.api.score.ScoreExplanation;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.solver.SolutionManager;
import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.api.solver.SolverManager;
import ai.timefold.solver.core.config.solver.EnvironmentMode;
import ai.timefold.solver.core.config.solver.SolverConfig;
import ai.timefold.solver.core.config.solver.termination.TerminationConfig;
import lu.df.domain.*;
import lu.df.solver.StreamCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // 1. Step - Define problem

        DetectiveSolution problem = DetectiveSolution.generateData(15);

        // 2. Step - Geography

        Router ghRouter = Router.getDefaultRouterInstance();
        ghRouter.setDistanceTimeMap(problem.getLocationList());

        problem.print();

        // 3. Step - Define SolverFactory

        SolverFactory<DetectiveSolution> solverFactoryFromXML = SolverFactory
                .createFromXmlResource("SolverConfig.xml");

        /*
        SolverFactory<DetectiveSolution> solverFactory = SolverFactory.create(
                new SolverConfig()
                        .withSolutionClass(DetectiveSolution.class)
                        .withEntityClasses(Detective.class, Visit.class) // CHANGEABLE thing
                        .withConstraintProviderClass(StreamCalculator.class)
                        .withTerminationConfig(new TerminationConfig()
                                .withSecondsSpentLimit(10L)) // 10 seconds for work
                        .withEnvironmentMode(EnvironmentMode.REPRODUCIBLE)
        );
         */

        // 4. Step - Solve

        Solver<DetectiveSolution> solver = solverFactoryFromXML.buildSolver();
        DetectiveSolution solution = solver.solve(problem);

        // 5. Step - Explain and print

        SolutionManager<DetectiveSolution, HardSoftScore> solutionManager = SolutionManager.create(solverFactoryFromXML);
        ScoreExplanation<DetectiveSolution, HardSoftScore> scoreExplanation = solutionManager.explain(solution);
        LOGGER.info(scoreExplanation.getSummary());

        solution.print();
    }
}