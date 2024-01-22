package problem.loc.facility.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.PlanningListVariable;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@PlanningEntity
@Getter @Setter @NoArgsConstructor
@JsonIdentityInfo(scope = Storage.class,
        property = "name",
        generator = ObjectIdGenerators.PropertyGenerator.class)
public class Storage {
    private String name;
    private Integer capacity;
    private Integer size;
    private Integer maxServingUnits;
    private Integer initialOpeningCost;

    @PlanningListVariable
    @JsonIdentityReference
    private List<Retail> retails = new ArrayList<>();

    private Location location;

    @JsonIgnore
    public double getTotalServingUnits() {
        double totalServingUnits = 0.0;

        for (Retail visit: this.getRetails()) {
            totalServingUnits += this.location.distanceTo(visit.getLocation()) * 2;
            totalServingUnits += visit.getTimeToServe();
        }

        return totalServingUnits;
    }

    @JsonIgnore
    public Boolean isCapacityOverCapped() {
        double totalCapacity = 0.0;
        for (Retail retail: this.getRetails()) {
            totalCapacity += retail.getCapacity();
        }

        return totalCapacity > this.getCapacity();
    }

    @JsonIgnore
    public Boolean isWorkingTimeOverCapped() {
        return this.getTotalServingUnits() > this.getMaxServingUnits();
    }

    @JsonIgnore
    public Boolean areDistancesTooFar() {
        for (Retail retail: this.getRetails()) {
            if (this.getLocation().distanceTo(retail.getLocation()) + retail.getTimeToServe() > (double) this.getMaxServingUnits() / this.getSize()) {
                return true;
            }
        }

        return false;
    }

    @JsonIgnore
    public Boolean isActive() {
        return this.getRetails() != null;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
