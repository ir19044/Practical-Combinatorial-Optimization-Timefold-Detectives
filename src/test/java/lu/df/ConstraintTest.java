package lu.df;

import ai.timefold.solver.test.api.score.stream.ConstraintVerifier;
import lu.df.domain.Detective;
import lu.df.domain.DetectiveSolution;
import lu.df.domain.Location;
import lu.df.domain.Visit;
import lu.df.solver.StreamCalculator;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ConstraintTest {
    Detective DETECTIVE = new Detective();
    Visit VISIT1 = new Visit();
    Visit VISIT2 = new Visit();
    Visit VISIT3 = new Visit();
    Location OFFICE = new Location(0.0,0.0);
    Location locVisit1 = new Location(0.0, 3.0);
    Location locVisit2 = new Location(3.0, 7.0);
    public ConstraintTest() {

    }

    ConstraintVerifier<StreamCalculator, DetectiveSolution> constraintVerifier = ConstraintVerifier.build(
            new StreamCalculator(), DetectiveSolution.class, Detective.class, Visit.class);


    // Distance calculation for: OFFICE -> VISIT1 -> VISIT2
    //          TotalDistance: (0,0)=>(0,3)=>(3,7) i.e.  3+5=8
    //          Cost of distance: 8 * 0,1 * 100 = 80
    @Test
    void totalDistanceTest_OfficeVisit1Visit2_80(){
        // Arrange

        VISIT1.setDetective(DETECTIVE);
        VISIT1.setLocation(locVisit1);
        VISIT1.setVisitType(Visit.VisitType.PHOTO);
        VISIT2.setDetective(DETECTIVE);
        VISIT2.setLocation(locVisit2);
        VISIT2.setVisitType(Visit.VisitType.PHOTO);

        VISIT1.setNext(VISIT2);
        VISIT2.setPrev(VISIT1);

        DETECTIVE.getVisits().addAll(List.of(VISIT1, VISIT2));
        DETECTIVE.setWorkOffice(OFFICE);
        DETECTIVE.setCostDistance(0.1);

        // Act

        constraintVerifier.verifyThat(StreamCalculator::totalDistance)
                .given(DETECTIVE, VISIT1, VISIT2)
                .penalizesBy(80); // Assert
    }

    // After each PHOTO detective should return to OFFICE
    // Path: OFFICE->PHOTO->OFFICE->PHOTO
    @Test
    void  oneGroupOneProtocolTest_ValidPath_0(){
        // Arrange

        VISIT1.setDetective(DETECTIVE);
        VISIT1.setVisitType(Visit.VisitType.PHOTO);
        VISIT1.setLocation(locVisit1);

        VISIT2.setDetective(DETECTIVE);
        VISIT2.setVisitType(Visit.VisitType.PROTOCOL);
        VISIT2.setLocation(OFFICE);

        VISIT3.setDetective(DETECTIVE);
        VISIT3.setVisitType(Visit.VisitType.PHOTO);
        VISIT3.setLocation(locVisit2);

        VISIT1.setNext(VISIT2);
        VISIT2.setNext(VISIT3);

        VISIT3.setPrev(VISIT2);
        VISIT2.setPrev(VISIT1);

        DETECTIVE.getVisits().addAll(List.of(VISIT1, VISIT2, VISIT3));
        DETECTIVE.setWorkOffice(OFFICE);

        // Act

        constraintVerifier.verifyThat(StreamCalculator::oneGroupOneProtocol)
                .given(DETECTIVE, VISIT1, VISIT2, VISIT3)
                .penalizesBy(0); // Assert
    }

    // After each PHOTO detective should return to OFFICE
    // Path: OFFICE->PHOTO->PHOTO
    @Test
    void  oneGroupOneProtocolTest_InvalidPath_1(){
        // Arrange

        VISIT1.setDetective(DETECTIVE);
        VISIT1.setVisitType(Visit.VisitType.PHOTO);

        VISIT2.setDetective(DETECTIVE);
        VISIT2.setVisitType(Visit.VisitType.PHOTO);


        VISIT1.setNext(VISIT2);
        VISIT2.setPrev(VISIT1);

        DETECTIVE.getVisits().addAll(List.of(VISIT1, VISIT2));
        DETECTIVE.setWorkOffice(OFFICE);

        // Act

        constraintVerifier.verifyThat(StreamCalculator::oneGroupOneProtocol)
                .given(DETECTIVE, VISIT1, VISIT2)
                .penalizesBy(1); // Assert
    }
}
