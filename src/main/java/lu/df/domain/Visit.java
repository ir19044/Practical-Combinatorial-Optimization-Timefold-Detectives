package lu.df.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.*;
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
public class Visit {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public enum VisitType {PHOTO, PROTOCOL}

    @Getter @Setter
    public static class Thief { private int id; private String name;
        public Thief(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Thief thief = (Thief) o;
            return id == thief.id && Objects.equals(name, thief.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    private String name; // thief group name / office name

    private Integer expMonths; // experience level to catch group

    private VisitType visitType;

    private Location location;

    private Set<Thief> thiefSet;

    private Set<Thief> maximalThiefSet;

    @InverseRelationShadowVariable(sourceVariableName = "visits")
    private Detective detective;

    @NextElementShadowVariable(sourceVariableName = "visits")
    private Visit next;

    @PreviousElementShadowVariable(sourceVariableName = "visits")
    private Visit prev;

    // If at least one changed, then recalculate thiefList
    @ShadowVariable(variableListenerClass = VisitedThiefListener.class, sourceVariableName = "detective")
    @ShadowVariable(variableListenerClass = VisitedThiefListener.class, sourceVariableName = "prev")
    //@ShadowVariable(variableListenerClass = VisitedThiefListener.class, sourceVariableName = "next")
    private Set<Thief> coveredSet = new HashSet<>();

    private Integer twStart;  // Time window START to visit this item

    private Integer twFinish; // Time window FINISH to visit this item

    @PiggybackShadowVariable(shadowVariableName = "coveredSet") // depends on detective lvl and thief group lvl
    private Integer photoTime; // Time to take a photo

    @PiggybackShadowVariable(shadowVariableName = "coveredSet") // depends on planning
    private Integer arrivalTime = null;

    @PiggybackShadowVariable(shadowVariableName = "coveredSet")
    private Integer catchGroupCount;

    @PiggybackShadowVariable(shadowVariableName = "coveredSet")
    private Integer distanceToVisit;

    public Integer getDepartureTime() {
        if(this.getArrivalTime() == null)
            return null;
        else {
            int timeWithPhoto = this.getArrivalTime() + this.getPhotoTime();

            if(this.getTwStart() > timeWithPhoto){
                var a =2;
            }

            return this.getNext() != null
                    ? Math.max(this.getNext().getTwStart()- this.getLocation().timeTo(this.getNext()), timeWithPhoto)
                    : timeWithPhoto;
        }
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
