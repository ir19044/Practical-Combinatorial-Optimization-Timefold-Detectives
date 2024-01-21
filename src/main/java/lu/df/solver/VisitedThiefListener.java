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
            visit.setCoveredSet(new HashSet<>());
            scoreDirector.afterVariableChanged(visit, "coveredSet");

            scoreDirector.beforeVariableChanged(visit, "photoTime");
            visit.setPhotoTime(0);
            scoreDirector.afterVariableChanged(visit, "photoTime");

            scoreDirector.beforeVariableChanged(visit, "arrivalTime");
            visit.setArrivalTime(null);
            scoreDirector.afterVariableChanged(visit, "arrivalTime");

            if(visit.getDetective() != null) {
                scoreDirector.beforeVariableChanged(visit,"catchGroupCount");
                visit.setCatchGroupCount(0);
                scoreDirector.afterVariableChanged(visit,"catchGroupCount");
            }
        } else {
            Set<Thief> coveredSet = visit.getPrev() != null
                    ? new HashSet<>(visit.getPrev().getCoveredSet())
                    : new HashSet<>();

/*
            if(visit.getPrev()!=null &&
                    visit.getDetective().getDetectives().get(2).getVisits().stream().filter(v -> v.getName() == "ThiefGroup-4"
            || v.getName() == "Office2-#1").count() >= 1 && visit.getThiefSet()!=null && visit.getThiefSet().size() == 2 &&
            visit.getPrev().getVisitType() == Visit.VisitType.PROTOCOL && visit.getName() == "ThiefGroup-3"
            && visit.getPrev().getName().equals("Office2-#1")){
                var a = 2;
            }
*/

            Integer arrival = visit.getPrev() != null && visit.getPrev().getArrivalTime() != null
                    ? visit.getPrev().getDepartureTime() +
                            visit.getPrev().getLocation().timeTo(visit.getLocation(), visit.getDetective(), visit)
                    : visit.getDetective().getTwStart() +
                    visit.getDetective().getWorkOffice().timeTo(visit.getLocation(), visit.getDetective(), visit); // start in office

            Integer catchGroupCount = visit.getPrev() != null ? visit.getPrev().getCatchGroupCount() : 0;

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

                if(shadowVisit.getThiefSet() != null && shadowVisit.getThiefSet().size() == 2){
                    var a = 2;
                }


                if(shadowVisit.getVisitType() == Visit.VisitType.PHOTO && shadowVisit.getDetective().isGivenSetCoveredByAnotherSets(shadowVisit)){
                    var a = 2;
                }

                if(shadowVisit.getVisitType() == Visit.VisitType.PROTOCOL ||
                        shadowVisit.getDetective() == null ||
                        shadowVisit.getDetective().isGivenSetCoveredByAnotherSets(shadowVisit))
                {
                    shadowVisit.setPhotoTime(0);
                }
                else
                    shadowVisit.setPhotoTime(185 -5 * shadowVisit.getDetective().getExperienceMonths() + shadowVisit.getExpMonths() / 3);

                scoreDirector.afterVariableChanged(shadowVisit, "photoTime");

                scoreDirector.beforeVariableChanged(shadowVisit, "arrivalTime");
                shadowVisit.setArrivalTime(arrival);
                scoreDirector.afterVariableChanged(shadowVisit, "arrivalTime");


                scoreDirector.beforeVariableChanged(shadowVisit, "catchGroupCount");
                if( shadowVisit.getVisitType() == Visit.VisitType.PHOTO &&
                        !shadowVisit.getDetective().isGivenSetCoveredByAnotherSets(shadowVisit))
                    catchGroupCount += 1;
                shadowVisit.setCatchGroupCount(catchGroupCount);
                scoreDirector.afterVariableChanged(shadowVisit, "catchGroupCount");


                coveredSet = thiefSetAllUpdated;
                shadowVisit = shadowVisit.getNext();

                if (shadowVisit != null) {
                    arrival = shadowVisit.getPrev().getDepartureTime() +
                            shadowVisit.getPrev().getLocation().timeTo(shadowVisit.getLocation(), shadowVisit.getDetective(), shadowVisit);
                }
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
