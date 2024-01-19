package lu.df.solver;

import ai.timefold.solver.core.api.domain.variable.VariableListener;
import ai.timefold.solver.core.api.score.director.ScoreDirector;
import lu.df.domain.DetectiveSolution;
import lu.df.domain.Visit;
import lu.df.domain.Visit.Thief;

import java.util.HashSet;
import java.util.Set;

public class VisitedThiefListener implements VariableListener<DetectiveSolution, Visit> {

    @Override
    public void beforeVariableChanged(ScoreDirector<DetectiveSolution> scoreDirector, Visit visit) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector<DetectiveSolution> scoreDirector, Visit visit) {
        if (visit.getDetective() == null) {
            scoreDirector.beforeVariableChanged(visit, "thiefSetAll");
            if (visit.getVisitType() == Visit.VisitType.PHOTO){
                visit.setThiefSetAll(visit.getThiefSet());
            }
            else{
                visit.setThiefSetAll(new HashSet<>());
            }

            scoreDirector.afterVariableChanged(visit, "thiefSetAll");
        } else {

            Set<Thief> thiefSetAll = visit.getPrev() != null
                    ? new HashSet<>(visit.getPrev().getThiefSetAll())
                    : new HashSet<>();

            Visit shadowVisit = visit;
            while (shadowVisit != null) {
                Set<Thief> thiefSetAllUpdated = new HashSet<>(thiefSetAll);

                if(shadowVisit.getThiefSet() != null){ // info about BEFORE this found!
                        thiefSetAllUpdated.addAll(shadowVisit.getThiefSet()); // start set
                }

                scoreDirector.beforeVariableChanged(shadowVisit, "thiefSetAll");
                shadowVisit.setThiefSetAll(thiefSetAllUpdated);
                scoreDirector.afterVariableChanged(shadowVisit, "thiefSetAll");

                thiefSetAll = thiefSetAllUpdated;
                shadowVisit = shadowVisit.getNext();
            }
        }
    }

    @Override
    public void beforeEntityAdded(ScoreDirector<DetectiveSolution> scoreDirector, Visit visit) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector<DetectiveSolution> scoreDirector, Visit visit) {

    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<DetectiveSolution> scoreDirector, Visit visit) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector<DetectiveSolution> scoreDirector, Visit visit) {

    }
}
