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

            scoreDirector.beforeVariableChanged(visit, "coveredSet");
            if (visit.getVisitType() == Visit.VisitType.PHOTO) visit.setCoveredSet(visit.getThiefSet());
            else visit.setCoveredSet(new HashSet<>());
            scoreDirector.afterVariableChanged(visit, "coveredSet");

            scoreDirector.beforeVariableChanged(visit, "photoTime");
            visit.setPhotoTime(0);
            scoreDirector.afterVariableChanged(visit, "photoTime");

            scoreDirector.beforeVariableChanged(visit, "arrivalTime");
            visit.setArrivalTime(null);
            scoreDirector.afterVariableChanged(visit, "arrivalTime");

            if(visit.getDetective() != null) {
                scoreDirector.beforeVariableChanged(visit.getDetective().getClass(), "catchGroupCount");
                visit.getDetective().setCatchGroupCount(0);
                scoreDirector.afterVariableChanged(visit.getDetective().getClass(), "catchGroupCount");
            }
        } else {
            Set<Thief> coveredSet = visit.getPrev() != null
                    ? new HashSet<>(visit.getPrev().getCoveredSet())
                    : new HashSet<>();

            Integer arrival = visit.getPrev() != null && visit.getPrev().getArrivalTime() != null
                    ? visit.getPrev().getDepartureTime() +
                            visit.getPrev().getLocation().timeTo(visit.getLocation(), visit.getDetective(), visit)
                    : 0;

            Visit shadowVisit = visit;
            while (shadowVisit != null) {
                Set<Thief> thiefSetAllUpdated = new HashSet<>(coveredSet);

                if(shadowVisit.getThiefSet() != null){ // info about BEFORE this found!
                        thiefSetAllUpdated.addAll(shadowVisit.getThiefSet()); // start set
                }

                scoreDirector.beforeVariableChanged(shadowVisit, "coveredSet");
                shadowVisit.setCoveredSet(thiefSetAllUpdated);
                scoreDirector.afterVariableChanged(shadowVisit, "coveredSet");



                scoreDirector.beforeVariableChanged(shadowVisit, "photoTime");
              //  if(shadowVisit.getVisitType() == Visit.VisitType.PROTOCOL &&
               //         shadowVisit.getPrev() != null && shadowVisit.getPrev().getVisitType() == Visit.VisitType.PHOTO

                if(shadowVisit.getThiefSet() != null && shadowVisit.getThiefSet().size() == 2){
                    var a = 2;
                }


                if(shadowVisit.getVisitType() == Visit.VisitType.PHOTO && shadowVisit.getDetective().isGivenSetAlreadyCoveredByAnotherSets(shadowVisit)){
                    var a = 2;
                }

                // TODO:  (shadowVisit.getPrev()!=null && shadowVisit.getPrev().getCoveredSet().containsAll(shadowVisit.getThiefSet())
                if(shadowVisit.getVisitType() == Visit.VisitType.PROTOCOL ||
                        shadowVisit.getDetective() == null ||
                        shadowVisit.getDetective().isGivenSetAlreadyCoveredByAnotherSets(shadowVisit))
                {
                    shadowVisit.setPhotoTime(0);
                }
                else
                    shadowVisit.setPhotoTime(185 -5 * shadowVisit.getDetective().getExperienceMonths() + shadowVisit.getExpMonths() / 3);

                scoreDirector.afterVariableChanged(shadowVisit, "photoTime");


                coveredSet = thiefSetAllUpdated;
                shadowVisit = shadowVisit.getNext();
            }

            var a = 2;


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
