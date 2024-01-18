package lu.df.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import ai.timefold.solver.core.api.score.stream.Joiners;
import lu.df.Main;
import lu.df.domain.Detective;
import lu.df.domain.Visit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static ai.timefold.solver.core.api.score.stream.Joiners.equal;

public class StreamCalculator implements ConstraintProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                totalDistance(constraintFactory),
                oneGroupOneProtocol(constraintFactory),
                detectiveThiefGroupLevelMatch(constraintFactory)
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
                .penalize(HardSoftScore.ONE_SOFT, (visit, detective) ->
                        Math.max(visit.getExpMonths()-detective.getExperienceMonths(), 0)*10)
                .asConstraint("detectiveThiefGroupLevelMatch");
    }
}
