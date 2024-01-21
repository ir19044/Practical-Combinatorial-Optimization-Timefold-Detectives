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
import static java.util.Collections.max;

public class StreamCalculator implements ConstraintProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                totalDistance(constraintFactory),
                oneGroupOneProtocol(constraintFactory),
                detectiveThiefGroupLevelMatch(constraintFactory),
                //groupMinimalCover(constraintFactory)
                photoTime(constraintFactory),
                arrivalTime(constraintFactory),
                worktimeCost(constraintFactory),
                visitOutsideTwFinish(constraintFactory),
                visitOutsideTwStart(constraintFactory),
                worktimeOverFlow(constraintFactory),
                worktimeGroupCountOverflow(constraintFactory)
        };
    }

    public Constraint totalDistance(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Detective.class)
                .filter(detective -> detective.getTotalDistance() > 0)
                .penalize(HardSoftScore.ONE_SOFT, detective -> (int) Math.round(detective.getTotalDistance()
                        * detective.getCostDistance() * 100))
                .asConstraint("totalDistance");
    }

    public Constraint oneGroupOneProtocol(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getVisitType() == Visit.VisitType.PHOTO)
                .join(Detective.class, equal(Visit::getDetective, v -> v))
                .penalize(HardSoftScore.ONE_HARD, (visit, detective) ->
                        visit.getNext() == null ||
                                !Objects.equals(visit.getNext().getLocation().getLat(), detective.getWorkOffice().getLat()) ||
                                !Objects.equals(visit.getNext().getLocation().getLon(), detective.getWorkOffice().getLon())
                                ? 1 : 0)
                .asConstraint("oneGroupOneProtocol");
    }

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
                .filter(visit -> visit.getVisitType() == Visit.VisitType.PHOTO && visit.getPhotoTime() != null)
                .penalize(HardSoftScore.ONE_SOFT, visit -> visit.getPhotoTime())
                .asConstraint("photoTime");
    }

    public Constraint arrivalTime(ConstraintFactory constraintFactory){
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getVisitType() == Visit.VisitType.PHOTO && visit.getArrivalTime() != null)
                .penalize(HardSoftScore.ONE_SOFT, visit -> visit.getArrivalTime())
                .asConstraint("arrivalTime");
    }

    public Constraint worktimeCost(ConstraintFactory constraintFactory){
        return constraintFactory
                .forEach(Detective.class)
                .filter(detective -> !detective.getVisits().isEmpty())
                .join(Visit.class, Joiners.equal(d->d, Visit::getDetective))
                .filter((detective, last) -> last.getNext() == null)
                .penalize(HardSoftScore.ONE_SOFT, (detective, last) ->
                {
                    return (int) Math.round((last.getDepartureTime()) / 3600.0 * detective.getCostWorkTime() * 100);
                })
                .asConstraint("worktimeCost");
    }

    public Constraint visitOutsideTwFinish(ConstraintFactory constraintFactory){
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getDepartureTime() != null && visit.getDepartureTime() > visit.getTwFinish())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("visitOutsideTwFinish");
    }

    // TODO
    public Constraint visitOutsideTwStart(ConstraintFactory constraintFactory){
        return constraintFactory
                .forEach(Visit.class)
                .filter(visit -> visit.getDepartureTime() != null && visit.getArrivalTime() < visit.getTwStart())
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("visitOutsideTwStart");
    }

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
