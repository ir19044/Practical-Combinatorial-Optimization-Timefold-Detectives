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
import java.util.*;
import static lu.df.domain.Visit.VisitType.PHOTO;
import static lu.df.domain.Visit.VisitType.PROTOCOL;


@PlanningSolution
@Getter @Setter @NoArgsConstructor
public class DetectiveSolution {
    private static final Logger LOGGER = LoggerFactory.getLogger(DetectiveSolution.class);
    private static final Integer MINUTE = 60;
    private static final Integer HOUR = 3600;
    private static final Integer DAY = HOUR * 24;
    private static final Integer TIME0AM = 0;

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
            LOGGER.info(detective.getEmpNr() + "(lvl: " + detective.getExperienceMonths() + ")");
            detective.getVisits().forEach(visit -> {

                StringBuilder thieveList = new StringBuilder();
                if(visit.getVisitType() == PHOTO) {
                    List<Thief> thievesList = new ArrayList<>(visit.getThiefSet());
                    thievesList.sort(Comparator.comparingInt(Thief::getId));

                    for (Thief thief : thievesList) {
                        thieveList.append(thief.getId()).append(", ");
                    }
                    thieveList.delete(thieveList.length() - 2, thieveList.length());
                }

                LOGGER.info("     " + visit.getName() + " "
                        + visit.getVisitType() + "(" + visit.getExpMonths() +")" + "  dist:"+ visit.getDistanceToVisit() +
                        "  photoTime:"+visit.getPhotoTime() +
                        "  arrTime: "+formatTime(visit.getArrivalTime()) +
                        "  depTime: "+formatTime(visit.getDepartureTime()) +
                        "    GroupTimeWindow:  "+formatTime(visit.getTwStart())+"-->"+formatTime(visit.getTwFinish()) +
                        "    DetTimeWindow:  "+formatTime(visit.getDetective().getTwStart())+"-->"+formatTime(visit.getDetective().getTwFinish())+
                        "    Caught:  "+visit.getCatchGroupCount()+
                        "    Set:  {"+thieveList+"}");
            });
        });
    }


    private static int problemId = 0;
    private static Integer getProblemId() { problemId++; return problemId;}

    public static DetectiveSolution generateData(int scale){ // scale number of thieve groups

        // 1. Step - Define problem

        DetectiveSolution problem = new DetectiveSolution();
        problem.setSolutionId(DetectiveSolution.getProblemId().toString());

        Random random = new Random(scale);

        // 2. Step - Offices

        List<Location> officeLocations = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            Location ofcLoc = new Location(random.nextDouble(100), random.nextDouble(100));

            for (int j = 1; j <=2 + 4 * scale / 7 ; j++) {
                Visit ofc = new Visit();

                ofc.setName("Office"+i+"-#"+j);
                ofc.setExpMonths(0); // redundant, default value
                ofc.setVisitType(PROTOCOL);

                ofc.setTwStart(TIME0AM);
                ofc.setTwFinish(DAY);

                ofc.setLocation(ofcLoc);
                officeLocations.add(ofcLoc);

                problem.getLocationList().add(ofcLoc);
                problem.getVisitList().add(ofc);
            }
        }

        // 3. Step - Detectives

        List<Detective> detectives = new ArrayList<>();

        for (int i = 1; i <= scale / 5 + 1; i++){
            Detective d = new Detective();
            detectives.add(d);

            d.setEmpNr("Detective-"+i);
            d.setExperienceMonths(10+random.nextInt(20));

            int startHour = random.nextInt(12);
            int endHour = 12 + random.nextInt(9);

            d.setTwStart(startHour*HOUR+random.nextInt(59)*MINUTE);
            d.setTwFinish(endHour*HOUR+random.nextInt(59)*MINUTE);
            d.setMaxGroupCount(1+random.nextInt(8));

            d.setHasCar(random.nextBoolean());

            int randomIndex = random.nextInt(officeLocations.size());
            Location ofc = officeLocations.get(randomIndex);
            Location detLoc = new Location(ofc.getLat(), ofc.getLon());
            d.setWorkOffice(detLoc);
            d.setCostDistance(random.nextDouble(10));
            d.setCostWorkTime(random.nextDouble(12));

            problem.getDetectiveList().add(d);
            problem.getLocationList().add(detLoc);
        }

        for (int i = 0; i < detectives.size(); i++){
            Detective d = detectives.get(i);
            if (i != 0){
                d.setPrev(detectives.get(i-1));
            }
            if( i != detectives.size()-1){
                d.setNext(detectives.get(i+1));
            }
        }

        // 4. Step - Thieve groups

        for( int i = 1; i <= scale; i++){
            Visit t = new Visit();
            t.setName("ThiefGroup-"+i);
            t.setExpMonths(random.nextInt(16));

            Set<Thief> thieves = new HashSet<>();
            int thieveCount = 2+(random.nextInt(scale))/10;
            for (int j = 1; j <= thieveCount; j++) {
                var th = random.nextInt(scale);
                thieves.add(new Thief(th, "Thief" + th));
            }
            t.setThiefSet(thieves);

            t.setVisitType(PHOTO); // default
            int startHour = 5 + random.nextInt(7);
            int endHour = 5 + startHour + random.nextInt(5);

            t.setTwStart(startHour*HOUR+random.nextInt(59)*MINUTE);
            t.setTwFinish(endHour*HOUR+random.nextInt(59)*MINUTE);

            Location tLoc = new Location(random.nextDouble(100), random.nextDouble(100));
            t.setLocation(tLoc);

            problem.getLocationList().add(tLoc);
            problem.getVisitList().add(t);
        }

        return problem;
    }

    public static DetectiveSolution generateData(){
        // 1. Step - Define problem
        DetectiveSolution problem = new DetectiveSolution();
        problem.setSolutionId("P1");

        // 2. Step - Define data

        // Office - 1
        for (int i = 1; i <= 3; i++) {
            Visit ofc1 = new Visit();
            ofc1.setName("Office1-#"+i);
            ofc1.setExpMonths(0); // redundant, default value
            ofc1.setVisitType(Visit.VisitType.PROTOCOL);

            ofc1.setTwStart(TIME0AM);
            ofc1.setTwFinish(DAY);

            Location ofc1Loc = new Location(0.0, 0.0);
            ofc1.setLocation(ofc1Loc);
            problem.getLocationList().add(ofc1Loc);
            problem.getVisitList().add(ofc1);
        }

        // Office - 2
        for (int i = 1; i <= 3; i++) {
            Visit ofc2 = new Visit();
            ofc2.setName("Office2-#"+i);
            ofc2.setExpMonths(0); // redundant, default value
            ofc2.setVisitType(Visit.VisitType.PROTOCOL);

            ofc2.setTwStart(TIME0AM);
            ofc2.setTwFinish(DAY);

            Location ofc2Loc = new Location(6.0, 6.0);
            ofc2.setLocation(ofc2Loc);
            problem.getLocationList().add(ofc2Loc);
            problem.getVisitList().add(ofc2);
        }

        // Detective 1
        Detective d1 = new Detective();
        d1.setEmpNr("Detective-1");
        d1.setExperienceMonths(25);

        d1.setTwStart(8 * HOUR); // Time to start work (foreach work day)
        d1.setTwFinish(12 * HOUR + 18 * MINUTE); // Time to finish work (foreach work day)
        d1.setMaxGroupCount(1);

        d1.setHasCar(true);

        Location detLoc1 = new Location(0.0, 0.0);
        d1.setWorkOffice(detLoc1);
        d1.setCostDistance(8.9);
        d1.setCostWorkTime(11.0);

        // Detective 2
        Detective d2 = new Detective();
        d2.setEmpNr("Detective-2");
        d2.setExperienceMonths(21);
        d2.setWorkOffice(detLoc1);
        d2.setCostDistance(4.0);

        d2.setTwStart(8 * HOUR); // Time to start work (foreach work day)
        d2.setTwFinish(23 * HOUR); // Time to finish work (foreach work day)
        d2.setMaxGroupCount(2);

        d2.setHasCar(true);
        d2.setCostWorkTime(10.0);


        // Detective 3
        Detective d3 = new Detective();
        d3.setEmpNr("Detective-3");
        d3.setExperienceMonths(21);

        d3.setTwStart(18 * HOUR); // Time to start work (foreach work day)
        d3.setTwFinish(23 * HOUR); // Time to finish work (foreach work day)
        d3.setMaxGroupCount(2);

        Location detLoc2 = new Location(6.0, 6.0);
        d3.setWorkOffice(detLoc2);
        d3.setCostDistance(5.0);

        d3.setHasCar(true);
        d3.setCostWorkTime(10.0);

        // ThiefGroup 1
        Visit t1 = new Visit();
        t1.setName("ThiefGroup-1");
        t1.setExpMonths(20);
        t1.setThiefSet(new HashSet<>() {{
            add(new Thief(1, "Thief1"));
            add(new Thief(2, "Thief2"));
            add(new Thief(3, "Thief3"));
        }});

        t1.setVisitType(Visit.VisitType.PHOTO);
        t1.setTwStart(14 * HOUR);
        t1.setTwFinish(16 * HOUR);

        Location t1Loc = new Location(0.0, 2.0);
        t1.setLocation(t1Loc);

        // ThiefGroup - 2
        Visit t2 = new Visit();
        t2.setName("ThiefGroup-2");
        t2.setExpMonths(25);
        t2.setThiefSet(new HashSet<>() {{
            add(new Thief(4, "Thief4"));
            add(new Thief(5, "Thief5"));
        }});

        t2.setVisitType(Visit.VisitType.PHOTO);
        t2.setTwStart(8 * HOUR);
        t2.setTwFinish(9 * HOUR);

        Location t2Loc = new Location(1.0, 5.1);
        t2.setLocation(t2Loc);

        // ThiefGroup - 3
        Visit t3 = new Visit();
        t3.setName("ThiefGroup-3");
        t3.setExpMonths(15);
        t3.setThiefSet(new HashSet<>() {{
            add(new Thief(1, "Thief1"));
            add(new Thief(6, "Thief6"));
        }});

        t3.setTwStart(21 * HOUR);
        t3.setTwFinish(22 * HOUR);
        t3.setVisitType(Visit.VisitType.PHOTO);

        Location t3Loc = new Location(5.0, 5.0);
        t3.setLocation(t3Loc);

        // ThiefGroup - 4
        Visit t4 = new Visit();
        t4.setName("ThiefGroup-4");
        t4.setExpMonths(21);
        t4.setThiefSet(new HashSet<>() {{
            add(new Thief(1, "Thief1"));
            add(new Thief(2, "Thief2"));
            add(new Thief(3, "Thief3"));
            add(new Thief(4, "Thief4"));
        }});

        t4.setVisitType(Visit.VisitType.PHOTO);
        t4.setTwStart(15 * HOUR);
        t4.setTwFinish(16 * HOUR);

        Location t4Loc = new Location(6.0, 2.0);
        t4.setLocation(t4Loc);

        // Fill detective, office and visits lists

        problem.getDetectiveList().addAll(List.of(d1, d2, d3));
        problem.getLocationList().addAll(List.of(t1Loc, t2Loc, t3Loc, t4Loc, detLoc1, detLoc2));
        problem.getVisitList().addAll(List.of(t1, t2, t3, t4));

        return problem;
    }

    public static DetectiveSolution generateSimpleData(){
        // 1. Step - Define problem
        DetectiveSolution problem = new DetectiveSolution();
        problem.setSolutionId("P1");

        // 2. Step - Define data

        // Office - 1
        for (int i = 1; i <= 4; i++) {
            Visit ofc1 = new Visit();
            ofc1.setName("Office1-#"+i);
            ofc1.setExpMonths(0); // redundant, default value
            ofc1.setVisitType(Visit.VisitType.PROTOCOL);

            ofc1.setTwStart(TIME0AM);
            ofc1.setTwFinish(DAY);

            Location ofc1Loc = new Location(0.0, 0.0);
            ofc1.setLocation(ofc1Loc);
            problem.getLocationList().add(ofc1Loc);
            problem.getVisitList().add(ofc1);
        }

        // Detective 1
        Detective d1 = new Detective();
        d1.setEmpNr("Detective-1");
        d1.setExperienceMonths(15);

        d1.setTwStart(8 * HOUR); // Time to start work (foreach work day)
        d1.setTwFinish(9 * HOUR); // Time to finish work (foreach work day)
        d1.setMaxGroupCount(2);

        d1.setHasCar(true);

        Location detLoc1 = new Location(0.0, 0.0);
        d1.setWorkOffice(detLoc1);

        d1.setCostDistance(0.1);
        d1.setCostWorkTime(8.0);

        // ThiefGroup 1
        Visit t1 = new Visit();
        t1.setName("ThiefGroup-1");
        t1.setExpMonths(20);
        t1.setThiefSet(new HashSet<>() {{
            add(new Thief(1, "Thief1"));
            add(new Thief(2, "Thief2"));
            add(new Thief(3, "Thief3"));
        }});

        t1.setVisitType(Visit.VisitType.PHOTO);
        t1.setTwStart(8 * HOUR);
        t1.setTwFinish(16 * HOUR);

        Location t1Loc = new Location(0.0, 2.0);
        t1.setLocation(t1Loc);

        // ThiefGroup - 2
        Visit t2 = new Visit();
        t2.setName("ThiefGroup-2");
        t2.setExpMonths(1);
        t2.setThiefSet(new HashSet<>() {{
            add(new Thief(2, "Thief2"));
            add(new Thief(3, "Thief3"));
        }});

        t2.setVisitType(Visit.VisitType.PHOTO);
        t2.setTwStart(10 * HOUR);
        t2.setTwFinish(13 * HOUR);

        Location t2Loc = new Location(0.0, 3.0);
        t2.setLocation(t2Loc);

        // ThiefGroup - 3
        Visit t3 = new Visit();
        t3.setName("ThiefGroup-3");
        t3.setExpMonths(15);
        t3.setThiefSet(new HashSet<>() {{
            add(new Thief(2, "Thief2"));
            add(new Thief(4, "Thief4"));
        }});

        t3.setVisitType(Visit.VisitType.PHOTO);
        t3.setTwStart(6 * HOUR);
        t3.setTwFinish(11 * HOUR);

        Location t3Loc = new Location(0.0, 5.0);
        t3.setLocation(t3Loc);

        // Fill detective, office and visits lists

        problem.getDetectiveList().addAll(List.of(d1));
        problem.getLocationList().addAll(List.of(t1Loc, t2Loc, t3Loc, detLoc1));
        problem.getVisitList().addAll(List.of(t1, t2, t3));

        return problem;
    }

    public static String formatTime(Integer timeInSeconds) {
        if (timeInSeconds != null) {
            long HH = timeInSeconds / 3600;
            long MM = (timeInSeconds % 3600) / 60;
            long SS = timeInSeconds % 60;
            return String.format("%02d:%02d:%02d", HH, MM, SS);
        } else return "null";

    }
}
