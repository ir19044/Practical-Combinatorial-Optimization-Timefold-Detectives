package lu.df.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.PiggybackShadowVariable;
import ai.timefold.solver.core.api.domain.variable.PlanningListVariable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lu.df.domain.DetectiveSolution.WeekDay;
import lu.df.domain.Visit.Thief;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@PlanningEntity
@Getter @Setter @NoArgsConstructor
public class Detective {

    private String empNr;

    private Integer experienceMonths;

    private Location workOffice;

    private List<Detective> detectives = new ArrayList<>();

    @PlanningListVariable // CHANGEABLE
    private List<Visit> visits = new ArrayList<>();

    private Integer twStart; // Time to start work (foreach work day)

    private Integer twFinish; // Time to finish work (foreach work day)

    private List<WeekDay> workDays;

    private Integer maxGroupCount; // Max group count to catch per week

    private Boolean hasCar; // movement type - by car or another

    @PiggybackShadowVariable(shadowEntityClass = Visit.class, shadowVariableName = "coveredSet")
    private Integer catchGroupCount;

    private Double costDistance;

    private Double costWorkTime;


    public Double getTotalDistance(){

        // 1. Step - start working in office

        Double totalDistance = 0.0;
        Location prevLoc = this.getWorkOffice();

        // 2. Step - change office to thief group location / office again

        for (Visit visit: this.getVisits()){
            totalDistance += prevLoc.distanceTo(visit.getLocation(), visit);
            prevLoc = visit.getLocation();
        }

        return totalDistance;
    }

    public final boolean isGivenSetCoveredByAnotherSets(Visit visit){

        Set<Thief> thiefSet = visit.getThiefSet();
        Set<Thief> coveredByAll = new HashSet<>();

        for(Detective detective: this.getDetectives()){
            Set<Thief> coveredByDetective = this.getCovered(detective, visit);
            coveredByAll.addAll(coveredByDetective);
        }

        return coveredByAll.containsAll(thiefSet);
    }

    private Set<Thief> getCovered(Detective detective, Visit visit){
        Set<Thief> foundCovered = new HashSet<>();

        for(Visit v: detective.getVisits()){
             if (v.getPhotoTime() != null && v.getPhotoTime() > 0){
                 if(detective == this && v.getThiefSet() == visit.getThiefSet()) { }
                 else
                    foundCovered.addAll(v.getThiefSet());
             }
         }
        return foundCovered;
    }

    @Override
    public String toString() {
        return this.getEmpNr();
    }
}
