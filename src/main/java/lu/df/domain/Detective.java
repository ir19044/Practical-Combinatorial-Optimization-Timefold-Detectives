package lu.df.domain;

import ai.timefold.solver.core.api.domain.variable.PlanningListVariable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor
public class Detective {

    private String empNr;

    private Integer experienceMonths;

    private Location workOffice;

    @PlanningListVariable // CHANGEABLE
    private List<Visit> visits = new ArrayList<>();

}
