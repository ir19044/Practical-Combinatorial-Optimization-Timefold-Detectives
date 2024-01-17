package lu.df.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class Visit {

    public enum VisitType {PHOTO, PROTOCOL}

    private String name; // thief group name / office name

    private Integer expMonths; // experience level to catch group

    private VisitType visitType;

    private Location location;

}
