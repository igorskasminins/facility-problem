package problem.loc.facility.domain;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@PlanningSolution
@Getter @Setter @NoArgsConstructor
public class FacilitySolution {
    private static final Double UPPER_LEFT_COORD_LAT = 56.9947;
    private static final Double UPPER_LEFT_COORD_LON = 24.0309;
    private static final Double LOWER_RIGHT_COORD_LAT = 56.8884;
    private static final Double LOWER_RIGHT_COORD_LON = 24.2520;

    private static final Logger LOGGER = LoggerFactory.getLogger(FacilitySolution.class);

    private String solutionId;
    @PlanningScore
    private HardSoftScore score;

    @PlanningEntityCollectionProperty
    private List<Storage> storageList = new ArrayList<>();

    @ProblemFactCollectionProperty
    @ValueRangeProvider
    @JsonIdentityReference(alwaysAsId = true)
    private List<Retail> RetailList = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<Location> locationList = new ArrayList<>();

    public void print() {
        this.getStorageList().forEach(storage -> {
            LOGGER.info(storage.getName() + "("+ storage.getCapacity() +")");
            storage.getRetails().forEach(retail -> {
                LOGGER.info("     " + retail.getName() + " "
                        + " (" + retail.getCapacity() + ")  " + retail.getStorage().getName()
                );
            });
        });
    }

    private static int problemId = 0;
    private static Integer getProblemId() { problemId++; return problemId;}

    public static FacilitySolution generateData(int scale, boolean useMap) {
        FacilitySolution problem = new FacilitySolution();
        problem.setSolutionId(FacilitySolution.getProblemId().toString());

        Random random = new Random();

        // vehicles: scale / 20 + 1
        for (int i = 1; i <= scale / 10 + 1; i++) {
            Storage v1 = new Storage();
            v1.setName("Storage #" + i);
            v1.setCapacity(300 + random.nextInt(100) - 50);
            int size = random.nextInt(3);
            v1.setSize(size);
            v1.setMaxServingUnits(size * 4000);
            v1.setInitialOpeningCost(size * 300);
            Location storageLocation;
            if (useMap) {
                storageLocation = new Location(LOWER_RIGHT_COORD_LAT + (UPPER_LEFT_COORD_LAT - LOWER_RIGHT_COORD_LAT) * random.nextDouble(),
                        UPPER_LEFT_COORD_LON + (LOWER_RIGHT_COORD_LON - UPPER_LEFT_COORD_LON) * random.nextDouble());
            } else {
                storageLocation = new Location(random.nextDouble(100), random.nextDouble(100));
            }

            v1.setLocation(storageLocation);

            problem.getStorageList().add(v1);
            problem.getLocationList().add(storageLocation);
        }

        for (int i = 1; i <= scale * 2; i++) {
            Retail retail = new Retail();
            retail.setName("Retail store #" + i);
            retail.setTimeToServe(25 + random.nextInt(50));
            Location retailLocation;
            if (useMap) {
                retailLocation = new Location(LOWER_RIGHT_COORD_LAT + (UPPER_LEFT_COORD_LAT - LOWER_RIGHT_COORD_LAT) * random.nextDouble(),
                        UPPER_LEFT_COORD_LON + (LOWER_RIGHT_COORD_LON - UPPER_LEFT_COORD_LON) * random.nextDouble());
            } else {
                retailLocation = new Location(random.nextDouble(100), random.nextDouble(100));
            }

            retail.setLocation(retailLocation);

            problem.getRetailList().add(retail);
            problem.getLocationList().add(retailLocation);

            retail.setCapacity(1);
        }

        return problem;
    }
}
