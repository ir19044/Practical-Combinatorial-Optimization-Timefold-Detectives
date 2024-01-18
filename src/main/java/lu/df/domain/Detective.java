package lu.df.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.PlanningListVariable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@PlanningEntity
@Getter @Setter @NoArgsConstructor
public class Detective {

    private String empNr;

    private Integer experienceMonths;

    private Location workOffice;

    @PlanningListVariable // CHANGEABLE
    private List<Visit> visits = new ArrayList<>();

    private Double costDistance;


    public Double getTotalDistance(){

        // 1. Step - start working in office

        Double totalDistance = 0.0;
        Location prevLoc = this.getWorkOffice();

        // 2. Step - change office to thief group location / office again

        for (Visit visit: this.getVisits()){
            totalDistance += prevLoc.distanceTo(visit.getLocation());
            prevLoc = visit.getLocation();
        }

        // NB! We do need calculate distance back to office
        // TODO

        //totalDistance += prevLoc.distanceTo(this.getWorkOffice());

        return totalDistance;
    }

    @Override
    public String toString() {
        return this.getEmpNr();
    }
}
