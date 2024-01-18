package lu.df.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.InverseRelationShadowVariable;
import ai.timefold.solver.core.api.domain.variable.NextElementShadowVariable;
import ai.timefold.solver.core.api.domain.variable.PreviousElementShadowVariable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@PlanningEntity
@Getter @Setter @NoArgsConstructor
public class Visit {

    public enum VisitType {PHOTO, PROTOCOL}

    private String name; // thief group name / office name

    private Integer expMonths; // experience level to catch group

    private VisitType visitType;

    private Location location;

    @InverseRelationShadowVariable(sourceVariableName = "visits")
    private Detective detective;

    @NextElementShadowVariable(sourceVariableName = "visits")
    private Visit next;

    @PreviousElementShadowVariable(sourceVariableName = "visits")
    private Visit prev;

    @Override
    public String toString() {
        return this.getName();
    }
}
