package lu.df.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.*;
import lu.df.Main;
import lu.df.domain.Detective;
import lu.df.domain.Visit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;
import static ai.timefold.solver.core.api.score.stream.Joiners.equal;
import static lu.df.domain.Visit.VisitType.PHOTO;


public class StreamCalculator implements ConstraintProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                totalDistance(constraintFactory),
                oneGroupOneProtocol(constraintFactory),
                detectiveThiefGroupLevelMatch(constraintFactory),
                //photoTime(constraintFactory),
                //arrivalTime(constraintFactory),
                worktimeCost(constraintFactory),
                visitOutsideTwFinish(constraintFactory),
                visitOutsideTwStart(constraintFactory),
                worktimeOverFlow(constraintFactory),
                worktimeGroupCountOverflow(constraintFactory),
                workCarDistanceCost(constraintFactory)
        };
    }

    // NB! Distance from current to ALREADY COVERED set == 0
    // MinSetCover functionality
    public Constraint totalDistance(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Detective.class)
                .filter(detective -> detective.getTotalDistance() > 0)
                .penalize(HardSoftScore.ONE_SOFT, detective -> (int) Math.round(detective.getTotalDistance()
                        * detective.getCostDistance() * 100))
                .asConstraint("totalDistance");
    }

    // After each PHOTO => PROTOCOL
    public Constraint oneGroupOneProtocol(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getVisitType() == PHOTO)
                .join(Detective.class, equal(Visit::getDetective, v -> v))
                .penalize(HardSoftScore.ONE_HARD, (visit, detective) ->
                        visit.getNext() == null ||
                                !Objects.equals(visit.getNext().getLocation().getLat(), detective.getWorkOffice().getLat()) ||
                                !Objects.equals(visit.getNext().getLocation().getLon(), detective.getWorkOffice().getLon())
                                ? 1 : 0)
                .asConstraint("oneGroupOneProtocol");
    }

    // LVL(Detective) >= LVL(ThiefGroup)
    public Constraint detectiveThiefGroupLevelMatch(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .join(Detective.class, equal(Visit::getDetective, v -> v))
                .penalize(HardSoftScore.ONE_HARD, (visit, detective) ->
                        Math.max(visit.getExpMonths()-detective.getExperienceMonths(), 0)*10)
                .asConstraint("detectiveThiefGroupLevelMatch");
    }

    public Constraint photoTime(ConstraintFactory constraintFactory){
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getVisitType() == PHOTO && visit.getPhotoTime() != null)
                .penalize(HardSoftScore.ONE_SOFT, Visit::getPhotoTime)
                .asConstraint("photoTime");
    }

    public Constraint arrivalTime(ConstraintFactory constraintFactory){
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getVisitType() == PHOTO && visit.getArrivalTime() != null)
                .penalize(HardSoftScore.ONE_SOFT, Visit::getArrivalTime)
                .asConstraint("arrivalTime");
    }

    // Cost for detective time
    public Constraint worktimeCost(ConstraintFactory constraintFactory){
        return constraintFactory
                .forEach(Detective.class)
                .filter(detective -> !detective.getVisits().isEmpty())
                .join(Visit.class, Joiners.equal(d->d, Visit::getDetective))
                .filter((detective, last) -> last.getNext() == null)
                .penalize(HardSoftScore.ONE_SOFT, (detective, last) ->
                {
                    return (int) Math.round((last.getDepartureTime()) / 3600.0 * detective.getCostWorkTime() * 10);
                })
                .asConstraint("worktimeCost");
    }

    // Cost for detective car
    public Constraint workCarDistanceCost(ConstraintFactory constraintFactory){
        return constraintFactory
                .forEach(Detective.class)
                .filter(detective -> detective.getHasCar() && !detective.getVisits().isEmpty())
                .join(Visit.class, Joiners.equal(d->d, Visit::getDetective))
                .filter((detective, last) -> last.getNext() == null)
                .penalize(HardSoftScore.ONE_SOFT, (d, last) ->
                {
                    return (int) Math.round(last.getDistanceToVisit() * d.getCostDistance());
                })
                .asConstraint("workCarDistanceCost");
    }

    // Try to avoid: TIME END (GROUP CATCH) < TIME END (GROUP)
    public Constraint visitOutsideTwFinish(ConstraintFactory constraintFactory){
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getDepartureTime() != null && visit.getDepartureTime() > visit.getTwFinish())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("visitOutsideTwFinish");
    }

    // Try to avoid: TIME START (GROUP CATCH) < TIME START (GROUP)
    public Constraint visitOutsideTwStart(ConstraintFactory constraintFactory){
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getDepartureTime() != null && visit.getArrivalTime() < visit.getTwStart())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("visitOutsideTwStart");
    }

    // Try to avoid: TIME END (DETECTIVE) < TIME END (GROUP CATCH)
    public Constraint worktimeOverFlow(ConstraintFactory constraintFactory){
        return constraintFactory
                .forEach(Detective.class)
                .filter(detective -> !detective.getVisits().isEmpty())
                .join(Visit.class, Joiners.equal(d->d, Visit::getDetective))
                .filter((detective, last) -> last.getNext() == null)
                .filter(((detective, last) -> last.getDepartureTime() > detective.getTwFinish()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("worktimeOverflow");
    }

    // Try to avoid: MAX (GROUP CATCH) < CAUGHT GROUPS
    public Constraint worktimeGroupCountOverflow(ConstraintFactory constraintFactory){
        return constraintFactory
                .forEach(Detective.class)
                .filter(detective -> !detective.getVisits().isEmpty())
                .join(Visit.class, Joiners.equal(d->d, Visit::getDetective))
                .filter((detective, last) -> last.getNext() == null)
                .filter(((detective, last) -> detective.getMaxGroupCount() < last.getCatchGroupCount()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("worktimeGroupCountOverflow");
    }
}