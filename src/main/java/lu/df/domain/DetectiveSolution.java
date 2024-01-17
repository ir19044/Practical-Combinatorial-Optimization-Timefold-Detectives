package lu.df.domain;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


@PlanningSolution
@Getter @Setter @NoArgsConstructor
public class DetectiveSolution {
    private static final Logger LOGGER = LoggerFactory.getLogger(DetectiveSolution.class);

    private String solutionId;

    @PlanningScore
    private HardSoftScore score;

    @PlanningEntityCollectionProperty
    private List<Detective> detectiveList = new ArrayList<>();

    @ProblemFactCollectionProperty // Not changeable
    @ValueRangeProvider
    private List<Visit> visitList = new ArrayList<>();

    @ProblemFactCollectionProperty // Not changeable
    private List<Location> locationList = new ArrayList<>();

    public void print(){
        this.getDetectiveList().forEach(detective -> {
            LOGGER.info(detective.getEmpNr() + "(" + detective.getExperienceMonths() + ")");
            detective.getVisits().forEach(visit -> {
                LOGGER.info("     " + visit.getName() + " "
                        + visit.getVisitType() + "(" + visit.getExpMonths() +")");
            });
        });
    }

    public static DetectiveSolution generateData(){
        // 1. Step - Define problem
        DetectiveSolution problem = new DetectiveSolution();
        problem.setSolutionId("P1");

        // 2. Step - Define detectives

        // Detective 1
        Detective d1 = new Detective();
        d1.setEmpNr("Detective-1");
        d1.setExperienceMonths(15);

        Location detectiveLoc1 = new Location(0.0, 0.0);
        d1.setWorkOffice(detectiveLoc1);

        // Detective 2
        Detective d2 = new Detective();
        d2.setEmpNr("Detective-2");
        d2.setExperienceMonths(20);
        d2.setWorkOffice(detectiveLoc1);

        // Detective 3
        Detective d3 = new Detective();
        d3.setEmpNr("Detective-3");
        d3.setExperienceMonths(15);

        Location detectiveLoc2 = new Location(6.0, 6.0);
        d3.setWorkOffice(detectiveLoc2);

        // ThiefGroup 1
        Visit t1 = new Visit();
        t1.setName("ThiefGroup-1");
        t1.setExpMonths(20);
        t1.setVisitType(Visit.VisitType.PHOTO);

        Location t1Loc = new Location(0.0, 2.0);
        t1.setLocation(t1Loc);

        // ThiefGroup - 2
        Visit t2 = new Visit();
        t2.setName("ThiefGroup-2");
        t2.setExpMonths(1);
        t2.setVisitType(Visit.VisitType.PHOTO);

        Location t2Loc = new Location(0.0, 2.0);
        t2.setLocation(t2Loc);

        // ThiefGroup - 3
        Visit t3 = new Visit();
        t3.setName("ThiefGroup-3");
        t3.setExpMonths(15);
        t3.setVisitType(Visit.VisitType.PHOTO);

        Location t3Loc = new Location(5.0, 5.0);
        t3.setLocation(t3Loc);

        // ThiefGroup - 4
        Visit t4 = new Visit();
        t4.setName("ThiefGroup-4");
        t4.setExpMonths(10);
        t4.setVisitType(Visit.VisitType.PHOTO);

        Location t4Loc = new Location(6.0, 2.0);
        t4.setLocation(t4Loc);

        // Office - 1
        Visit ofc1 = new Visit();
        ofc1.setName("Office-1");
        ofc1.setExpMonths(0); // redundant, default value
        ofc1.setVisitType(Visit.VisitType.PROTOCOL);

        Location ofc1Loc = new Location(0.0, 0.0);
        ofc1.setLocation(ofc1Loc);

        // Office - 2
        Visit ofc2 = new Visit();
        ofc2.setName("Office-2");
        ofc2.setExpMonths(0); // redundant, default value
        ofc2.setVisitType(Visit.VisitType.PROTOCOL);

        Location ofc2Loc = new Location(6.0, 6.0);
        ofc2.setLocation(ofc2Loc);

        // Fill detective, office and visits lists

        problem.getDetectiveList().addAll(List.of(d1, d2, d3));
        problem.getLocationList().addAll(List.of(
                detectiveLoc1, detectiveLoc2, t1Loc, t2Loc, t3Loc, t4Loc, ofc1Loc, ofc2Loc));
        problem.getVisitList().addAll(List.of(t1, t2, t3, t4, ofc1, ofc2));

        return problem;
    }
}
