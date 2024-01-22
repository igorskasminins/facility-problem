package problem.loc.facility.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import problem.loc.facility.domain.FacilitySolution;
import problem.loc.facility.domain.Storage;

public class StreamCalculator implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                totalServingTime(constraintFactory),
                isCapacityOverCapped(constraintFactory),
                isWorkingTimeOverCapped(constraintFactory),
                openingCost(constraintFactory),
                areDistancesTooFar(constraintFactory)
        };
    }

    public Constraint totalServingTime(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Storage.class)
                .filter(storage -> storage.getTotalServingUnits() > 0)
                .penalize(HardSoftScore.ONE_SOFT, storage -> (int) Math.round(storage.getTotalServingUnits()))
                .asConstraint("totalServingTime");
    }

    public Constraint isCapacityOverCapped(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Storage.class)
                .filter(Storage::isCapacityOverCapped)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("isCapacityOverCapped");
    }

    public Constraint isWorkingTimeOverCapped(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Storage.class)
                .filter(Storage::isWorkingTimeOverCapped)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("isWorkingTimeOverCapped");
    }

    public Constraint areDistancesTooFar(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Storage.class)
                .filter(Storage::areDistancesTooFar)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("areDistancesTooFar");
    }

    public Constraint openingCost(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Storage.class)
                .filter(Storage::isActive)
                .penalize(HardSoftScore.ONE_SOFT, Storage::getInitialOpeningCost)
                .asConstraint("openingCost");
    }
}
