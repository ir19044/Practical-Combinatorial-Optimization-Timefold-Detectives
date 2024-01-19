package lu.df.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Location {

    private Double lat;

    private Double lon;

    public Double distanceTo(Location location, Visit visit){
        if ( visit.getPrev() != null && visit.getPrev().getThiefSetAll() != null && visit.getThiefSet() != null &&
                visit.getVisitType() == Visit.VisitType.PHOTO &&
                visit.getPrev().getVisitType() == Visit.VisitType.PROTOCOL &&
                visit.getPrev().getThiefSetAll().containsAll(visit.getThiefSet()))
        {
            return 0.0;
        }

        if ( visit.getPrev() != null && visit.getPrev().getThiefSetAll() != null && visit.getPrev().getPrev() != null &&
                visit.getVisitType() == Visit.VisitType.PROTOCOL &&
                visit.getPrev().getVisitType() == Visit.VisitType.PHOTO &&
                visit.getPrev().getPrev().getVisitType() == Visit.VisitType.PROTOCOL &&
                visit.getPrev().getPrev().getThiefSetAll().containsAll(visit.getPrev().getThiefSet()))
        {
            return 0.0;
        }

        return Math.sqrt(Math.pow(this.lat - location.lat, 2) + Math.pow(this.lon - location.lon, 2));
    }

    public Double timeTo(Location location){
        return 0.0;
    }
}
