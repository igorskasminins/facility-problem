package problem.loc.facility.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.InverseRelationShadowVariable;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@PlanningEntity
@Getter @Setter @NoArgsConstructor
public class Retail {
    private String name;
    private Integer capacity;
    private Location location;
    private Integer timeToServe;

    @InverseRelationShadowVariable(sourceVariableName = "retails")
    @JsonIdentityReference(alwaysAsId = true)
    private Storage storage;

    @Override
    public String toString() {
        return this.getName();
    }
}
