package lu.df.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.calculator.EasyScoreCalculator;
import lu.df.domain.Detective;
import lu.df.domain.DetectiveSolution;

public class ScoreCalculator implements EasyScoreCalculator<DetectiveSolution, HardSoftScore> {

    @Override
    public HardSoftScore calculateScore(DetectiveSolution detectiveSolution) {
        int hard = 0;
        int soft = 0;

        Double totalDistance = 0.0;

        for (Detective detective: detectiveSolution.getDetectiveList()){
            totalDistance += detective.getTotalDistance();
        }

        soft = (int) Math.round(totalDistance * 1000);

        return HardSoftScore.of(- hard,- soft); // default solve MAXIMISATION task => we solve MINIMISATION => Minus
    }
}
