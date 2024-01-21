package lu.df.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lu.df.Main;
import lu.df.solver.VisitedThiefListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@PlanningEntity
@Getter @Setter @NoArgsConstructor
@JsonIdentityInfo(scope = Visit.class,
        property = "name",
        generator = ObjectIdGenerators.PropertyGenerator.class)
public class Visit {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public enum VisitType {PHOTO, PROTOCOL}

    private String name; // thief group name / office name

    private Integer expMonths; // experience level to catch group

    private VisitType visitType; // PROTOCOL or PHOTO

    private Location location;

    private Set<Thief> thiefSet;

    @InverseRelationShadowVariable(sourceVariableName = "visits")
    @JsonIdentityReference
    private Detective detective;

    @NextElementShadowVariable(sourceVariableName = "visits")
    @JsonIdentityReference
    private Visit next;

    @PreviousElementShadowVariable(sourceVariableName = "visits")
    @JsonIdentityReference
    private Visit prev;

    // If at least one changed, then recalculate thiefList
    @ShadowVariable(variableListenerClass = VisitedThiefListener.class, sourceVariableName = "detective")
    @ShadowVariable(variableListenerClass = VisitedThiefListener.class, sourceVariableName = "prev")
    private Set<Thief> coveredSet = new HashSet<>();

    private Integer twStart;  // Time window START to visit this item

    private Integer twFinish; // Time window FINISH to visit this item

    @PiggybackShadowVariable(shadowVariableName = "coveredSet") // depends on detective lvl and thief group lvl
    private Integer photoTime;

    @PiggybackShadowVariable(shadowVariableName = "coveredSet") // depends on planning
    private Integer arrivalTime = null;

    @PiggybackShadowVariable(shadowVariableName = "coveredSet")
    private Integer catchGroupCount;

    @PiggybackShadowVariable(shadowVariableName = "coveredSet")
    private Integer distanceToVisit;

    @JsonIgnore
    public Integer getDepartureTime() {
        if(this.getArrivalTime() == null)
            return null;
        else {
            int timeWithPhoto = this.getArrivalTime() + this.getPhotoTime();
            return this.getNext() != null && this.getNext().getVisitType() == VisitType.PHOTO
                    && !this.getNext().getDetective().isGivenSetCoveredByAnotherSets(this.getNext())
                    ? Math.max(this.getNext().getTwStart()- this.getLocation().timeTo(this.getNext()), timeWithPhoto)
                    : timeWithPhoto;
        }
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
