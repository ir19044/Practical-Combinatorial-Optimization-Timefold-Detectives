package lu.df.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Location {

    private Double lat;

    private Double lon;

    public Double distanceTo(Location location){
        return 0.0;
    }

    public Double timeTo(Location location){
        return 0.0;
    }
}
