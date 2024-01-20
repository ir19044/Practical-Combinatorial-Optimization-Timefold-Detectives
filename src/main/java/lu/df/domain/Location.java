package lu.df.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Location {

    private Double lat;

    private Double lon;

    private static Integer CAR_SPEED = 20;
    private static Integer AVG_SPEED = 5;

    public Double distanceTo(Location location, Visit visit){

        if (visit.getVisitType() == Visit.VisitType.PHOTO) {
            if (visit.getDetective().isGivenSetCoveredByAnotherSets(visit)) {
                return 0.0;
            }
        }
        else if (visit.getPrev() != null && visit.getPrev().getVisitType() == Visit.VisitType.PHOTO){
            if(visit.getDetective().isGivenSetCoveredByAnotherSets(visit.getPrev())){
                return 0.0;
            }
        }

        return Math.sqrt(Math.pow(this.lat - location.lat, 2) + Math.pow(this.lon - location.lon, 2));
    }

    public Integer timeTo(Location location, Detective detective, Visit visit){
        Integer speed = detective.getHasCar() ? CAR_SPEED : AVG_SPEED;

        return (int) Math.round((this.distanceTo(location, visit) / speed) * 3600);
    }
}
