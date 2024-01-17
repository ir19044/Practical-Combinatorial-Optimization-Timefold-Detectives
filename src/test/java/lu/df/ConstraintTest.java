package lu.df;

import ai.timefold.solver.test.api.score.stream.ConstraintVerifier;
import lu.df.domain.Detective;
import lu.df.domain.DetectiveSolution;
import lu.df.domain.Location;
import lu.df.domain.Visit;
import lu.df.solver.ScoreCalculator;

import java.util.List;

public class ConstraintTest {
    Detective DETECTIVE = new Detective();
    Visit VISIT1 = new Visit();
    Visit VISIT2 = new Visit();
    Location OFFICE = new Location(0.0,0.0);
    Location locVisit1 = new Location(4.0, 0.0);
    Location locVisit2 = new Location(4.0, 4.0);
    public ConstraintTest() {
        VISIT1.setDetective(DETECTIVE);
        VISIT1.setLocation(locVisit1);
        VISIT2.setDetective(DETECTIVE);
        VISIT2.setLocation(locVisit2);

        VISIT1.setNext(VISIT2);
        VISIT2.setPrev(VISIT1);
        DETECTIVE.getVisits().addAll(List.of(VISIT1, VISIT2));
        DETECTIVE.setWorkOffice(OFFICE);
    }
}
