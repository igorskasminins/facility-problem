package problem.loc.facility;

import ai.timefold.solver.test.api.score.stream.ConstraintVerifier;
import problem.loc.facility.domain.Location;
import problem.loc.facility.domain.FacilitySolution;
import problem.loc.facility.domain.Storage;
import problem.loc.facility.domain.Retail;
import problem.loc.facility.solver.StreamCalculator;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ConstraintTest {
    Storage STORAGE = new Storage();
    Retail RETAIL1 = new Retail();
    Retail RETAIL2 = new Retail();
    Location STORAGE_LOCATION = new Location(0.0,0.0);
    Location RETAIL_LOCATION_1 = new Location(5.0, 0.0);
    Location RETAIL_LOCATION_2 = new Location(0.0, 10.0);
    public ConstraintTest() {
        STORAGE.setLocation(STORAGE_LOCATION);
        STORAGE.setCapacity(90);
        STORAGE.setMaxServingUnits(1000);
        STORAGE.setInitialOpeningCost(300);
        STORAGE.setSize(1);

        RETAIL1.setStorage(STORAGE);
        RETAIL1.setLocation(RETAIL_LOCATION_1);
        RETAIL1.setTimeToServe(10);
        RETAIL1.setCapacity(30);

        RETAIL2.setStorage(STORAGE);
        RETAIL2.setLocation(RETAIL_LOCATION_2);
        RETAIL2.setTimeToServe(20);
        RETAIL2.setCapacity(60);

        STORAGE.getRetails().addAll(List.of(RETAIL1, RETAIL2));
        STORAGE.setLocation(STORAGE_LOCATION);
    }
    ConstraintVerifier<StreamCalculator, FacilitySolution> constraintVerifier = ConstraintVerifier.build(
            new StreamCalculator(), FacilitySolution.class, Storage.class, Retail.class);
    @Test
    void areDistancesTooFarTest() {
        constraintVerifier.verifyThat(StreamCalculator::areDistancesTooFar)
                .given(STORAGE, RETAIL1, RETAIL2)
                .penalizesBy(0);
    }

    @Test
    void isCapacityOverCappedTest() {
        constraintVerifier.verifyThat(StreamCalculator::isCapacityOverCapped)
                .given(STORAGE, RETAIL1, RETAIL2)
                .penalizesBy(0);
    }

    @Test
    void isWorkingTimeOverCappedTest() {
        constraintVerifier.verifyThat(StreamCalculator::isWorkingTimeOverCapped)
                .given(STORAGE, RETAIL1, RETAIL2)
                .penalizesBy(0);
    }

    @Test
    void totalServingTimeTest() {
        // 10 for R1, 20 for R2. 10 to travel for R1 and 20 to travel for R2 = 60 time units
        constraintVerifier.verifyThat(StreamCalculator::totalServingTime)
                .given(STORAGE, RETAIL1, RETAIL2)
                .penalizesBy(60);
    }

    @Test
    void openingCostTest() {
        constraintVerifier.verifyThat(StreamCalculator::openingCost)
                .given(STORAGE, RETAIL1, RETAIL2)
                .penalizesBy(300);
    }
}