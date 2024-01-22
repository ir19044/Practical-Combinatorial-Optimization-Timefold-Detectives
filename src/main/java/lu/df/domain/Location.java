package lu.df.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import static lu.df.domain.Visit.VisitType.PHOTO;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Location {

    private Double lat;

    private Double lon;

    public Location(Double lat, Double lon){
        this.lat = lat;
        this.lon = lon;
    }

    private static Integer CAR_SPEED = 50;
    private static Integer AVG_SPEED = 20;

    @JsonIgnore
    private Map<Location, Double> distanceCarMap = new HashMap<>();

    @JsonIgnore
    private Map<Location, Integer> timeCarMap = new HashMap<>();

    public Double distanceTo(Location location, Visit visit){

        // 1. Case - All thieves are caught in the past! (UNION of other sets)

        if (visit.getVisitType() == PHOTO) {
            if (visit.getDetective().isGivenSetCoveredByAnotherSets(visit)) {
                return 0.0;
            }
        }
        else if (visit.getPrev() != null && visit.getPrev().getVisitType() == PHOTO){
            if(visit.getDetective().isGivenSetCoveredByAnotherSets(visit.getPrev())){
                return 0.0;
            }
        }

        // 2. Case - It is the new set! => We should cover it!

        return this.distanceCarMap.get(location);
        //return Math.sqrt(Math.pow(this.lat - location.lat, 2) + Math.pow(this.lon - location.lon, 2));
    }

    public Integer timeTo(Visit visit){
        Location location = visit.getLocation();
        Detective detective = visit.getDetective();

        Integer speed = detective.getHasCar() ? CAR_SPEED : AVG_SPEED;

        return this.timeCarMap.get(location);
        //return (int) Math.round((this.distanceTo(location, visit) / speed) * 3600);
    }
}