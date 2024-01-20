package lu.df.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Location {

    private Double lat;

    private Double lon;

    private static Integer CAR_SPEED = 200;
    private static Integer AVG_SPEED = 50;

    public Double distanceTo(Location location, Visit visit){
        if ( visit.getPrev() != null && visit.getPrev().getCoveredSet() != null && visit.getThiefSet() != null &&
                visit.getVisitType() == Visit.VisitType.PHOTO &&
                visit.getPrev().getVisitType() == Visit.VisitType.PROTOCOL &&
                visit.getPrev().getCoveredSet().containsAll(visit.getThiefSet()))
        {
            return 0.0;
        }

        if ( visit.getPrev() != null && visit.getPrev().getCoveredSet() != null && visit.getPrev().getPrev() != null &&
                visit.getVisitType() == Visit.VisitType.PROTOCOL &&
                visit.getPrev().getVisitType() == Visit.VisitType.PHOTO &&
                visit.getPrev().getPrev().getVisitType() == Visit.VisitType.PROTOCOL &&
                visit.getPrev().getPrev().getCoveredSet().containsAll(visit.getPrev().getThiefSet()))
        {
            return 0.0;
        }

        return Math.sqrt(Math.pow(this.lat - location.lat, 2) + Math.pow(this.lon - location.lon, 2));
    }

    public Integer timeTo(Location location, Detective detective, Visit visit){
        Integer speed = detective.getHasCar() ? CAR_SPEED : AVG_SPEED;

        return (int) Math.round((this.distanceTo(location, visit) / speed) * 3600);
    }
}
