package problem.loc.facility.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.constraint.ConstraintMatch;
import lombok.Getter;
import lombok.Setter;
import problem.loc.facility.domain.Retail;
import problem.loc.facility.domain.Storage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Setter @Getter
public class SimpleIndictmentObject {
    private String indictedObjectID;
    private HardSoftScore score;
    private int matchCount;
    private List<SimpleConstraintMatch> constraintMatches;

    public SimpleIndictmentObject(Object indictedObject, HardSoftScore score, int matchCount, Set<ConstraintMatch<HardSoftScore>> constraintMatches) {
        this.indictedObjectID = indictedObject instanceof Retail ? ((Retail) indictedObject).getName() : ((Storage) indictedObject).getName();
        this.score = score;
        this.matchCount = matchCount;
        this.constraintMatches = constraintMatches.stream().map(SimpleConstraintMatch::new).collect(Collectors.toList());
    }
}