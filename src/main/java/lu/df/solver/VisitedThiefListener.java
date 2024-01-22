package lu.df.solver;

import ai.timefold.solver.core.api.domain.variable.VariableListener;
import ai.timefold.solver.core.api.score.director.ScoreDirector;
import lu.df.domain.DetectiveSolution;
import lu.df.domain.Visit;
import lu.df.domain.Thief;
import java.util.HashSet;
import java.util.Set;
import static lu.df.domain.Visit.VisitType.PHOTO;
import static lu.df.domain.Visit.VisitType.PROTOCOL;


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

            scoreDirector.beforeVariableChanged(visit,"catchGroupCount");
            visit.setCatchGroupCount(0);
            scoreDirector.afterVariableChanged(visit,"catchGroupCount");

            scoreDirector.beforeVariableChanged(visit,"distanceToVisit");
            visit.setDistanceToVisit(null);
            scoreDirector.afterVariableChanged(visit,"distanceToVisit");

        } else {

            // 1. Step - Init default variable values

            Set<Thief> coveredSet = visit.getPrev() != null
                    ? new HashSet<>(visit.getPrev().getCoveredSet())
                    : new HashSet<>();

            int detectiveStartTime = visit.getDetective().getTwStart();
            int timeToDrive = visit.getDetective().getWorkOffice().timeTo(visit);
            int catchGroupCount = visit.getPrev() != null ? visit.getPrev().getCatchGroupCount() : 0;
            int distanceToVisit = visit.getPrev() != null ? visit.getPrev().getDistanceToVisit() : 0;


            int arrival;

            if(visit.getPrev() != null && visit.getPrev().getArrivalTime() != null){
                if(visit.getVisitType() == PHOTO && !visit.getDetective().isGivenSetCoveredByAnotherSets(visit)) {
                    arrival = visit.getPrev().getDepartureTime() + visit.getPrev().getLocation().timeTo(visit);
                }
                else{
                    arrival = visit.getPrev() != null ? visit.getPrev().getDepartureTime() : visit.getDetective().getTwStart();
                }
            }
            else if (visit.getVisitType() == PHOTO && !visit.getDetective().isGivenSetCoveredByAnotherSets(visit)){
                arrival = Math.max(visit.getTwStart()- timeToDrive, detectiveStartTime+timeToDrive); // start in office
            }
            else{
                arrival = visit.getDetective().getTwStart();
            }

            // 2. Step - Consequences. Update all data after variable change.

            Visit shadowVisit = visit;
            while (shadowVisit != null) {

                boolean isCovered = shadowVisit.getDetective() != null
                        && shadowVisit.getThiefSet() != null
                        && shadowVisit.getDetective().isGivenSetCoveredByAnotherSets(shadowVisit);

                // 2.1. Variable: coveredSet

                Set<Thief> thiefSetAllUpdated = new HashSet<>(coveredSet);

                if(shadowVisit.getThiefSet() != null){ // info about BEFORE this found!
                        thiefSetAllUpdated.addAll(shadowVisit.getThiefSet());
                }

                scoreDirector.beforeVariableChanged(shadowVisit, "coveredSet");
                shadowVisit.setCoveredSet(thiefSetAllUpdated);
                scoreDirector.afterVariableChanged(shadowVisit, "coveredSet");

                // 2.2. Variable: photoTime

                scoreDirector.beforeVariableChanged(shadowVisit, "photoTime");
                if(shadowVisit.getVisitType() == PROTOCOL || shadowVisit.getDetective() == null || isCovered) {
                    shadowVisit.setPhotoTime(0);
                }
                else
                    shadowVisit.setPhotoTime(185 -5 * shadowVisit.getDetective().getExperienceMonths() + shadowVisit.getExpMonths() / 3);
                scoreDirector.afterVariableChanged(shadowVisit, "photoTime");

                // 2.3. Variable: arrivalTime

                scoreDirector.beforeVariableChanged(shadowVisit, "arrivalTime");
                shadowVisit.setArrivalTime(arrival);
                scoreDirector.afterVariableChanged(shadowVisit, "arrivalTime");

                // 2.4. Variable: catchGroupCount

                scoreDirector.beforeVariableChanged(shadowVisit, "catchGroupCount");
                if( shadowVisit.getVisitType() == PHOTO && !isCovered) catchGroupCount += 1;
                shadowVisit.setCatchGroupCount(catchGroupCount);
                scoreDirector.afterVariableChanged(shadowVisit, "catchGroupCount");

                // 2.5. Variable: distanceToVisit

                scoreDirector.beforeVariableChanged(shadowVisit, "distanceToVisit");

                if(shadowVisit.getPrev() != null) {
                    if(shadowVisit.getPrev().getVisitType() == PHOTO && !shadowVisit.getDetective().isGivenSetCoveredByAnotherSets(shadowVisit.getPrev())) {
                        distanceToVisit += (int) Math.round(shadowVisit.getPrev().getLocation().distanceTo(shadowVisit.getLocation(), shadowVisit));
                    }
                    else{
                        distanceToVisit += (int) Math.round(shadowVisit.getDetective().getWorkOffice().distanceTo(shadowVisit.getLocation(), shadowVisit));
                    }
                }
                else if ( shadowVisit.getVisitType() == PHOTO) {
                    distanceToVisit += (int) Math.round(shadowVisit.getLocation().distanceTo(shadowVisit.getDetective().getWorkOffice(), shadowVisit));
                }

                shadowVisit.setDistanceToVisit(distanceToVisit);
                scoreDirector.afterVariableChanged(shadowVisit, "distanceToVisit");

                // 2.6. Go to next!

                coveredSet = thiefSetAllUpdated;
                shadowVisit = shadowVisit.getNext();

                if (shadowVisit != null) {
                    arrival = shadowVisit.getPrev().getDepartureTime() +
                            shadowVisit.getPrev().getLocation().timeTo(shadowVisit);
                }
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
