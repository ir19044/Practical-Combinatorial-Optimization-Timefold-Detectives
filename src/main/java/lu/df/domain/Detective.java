package lu.df.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.NextElementShadowVariable;
import ai.timefold.solver.core.api.domain.variable.PlanningListVariable;
import ai.timefold.solver.core.api.domain.variable.PreviousElementShadowVariable;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@PlanningEntity
@Getter @Setter @NoArgsConstructor
@JsonIdentityInfo(scope = Detective.class,
        property = "empNr",
        generator = ObjectIdGenerators.PropertyGenerator.class)
public class Detective {

    private String empNr; // Employee name

    private Integer experienceMonths; // Months of exp. More exp. => Less time to take a photo

    private Location workOffice; // Office location

    @JsonIgnore
    private Detective next;

    @JsonIgnore
    private Detective prev;

    @PlanningListVariable // CHANGEABLE
    @JsonIdentityReference
    private List<Visit> visits = new ArrayList<>();

    private Integer twStart; // Time to start work

    private Integer twFinish; // Time to finish work

    private Integer maxGroupCount; // Max group count to catch per week

    private Boolean hasCar; // movement type - by car or another. The first one => Less time to visit group

    private Double costDistance; // car cost

    private Double costWorkTime; // detective cost

    @JsonIgnore
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

    @JsonIgnore
    // NB! Return true <=> union of all sets for all detectives contains visit.getThiefSet()
    public final boolean isGivenSetCoveredByAnotherSets(Visit visit){

        Set<Thief> thiefSet = visit.getThiefSet();
        Set<Thief> coveredByAll = new HashSet<>();

        Detective d = this;
        while (d != null){
            Set<Thief> coveredByDetective = this.getCovered(d, visit);
            coveredByAll.addAll(coveredByDetective);
            d = d.getNext();
        }

        d = this;
        while (d.getPrev() != null){
            d = d.getPrev();
            Set<Thief> coveredByDetective = this.getCovered(d, visit);
            coveredByAll.addAll(coveredByDetective);
        }

        return coveredByAll.containsAll(thiefSet);
    }

    @JsonIgnore
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
