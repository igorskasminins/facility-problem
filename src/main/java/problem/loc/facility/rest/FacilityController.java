package problem.loc.facility.rest;

import ai.timefold.solver.core.api.score.analysis.ScoreAnalysis;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.constraint.Indictment;
import ai.timefold.solver.core.api.solver.SolutionManager;
import ai.timefold.solver.core.api.solver.SolverManager;
import problem.loc.facility.domain.FacilitySolution;
import problem.loc.facility.solver.SimpleIndictmentObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;
import problem.loc.facility.domain.Router;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/routes")
public class FacilityController {
    @Autowired
    private SolverManager<FacilitySolution, String> solverManager;
    @Autowired
    private SolutionManager<FacilitySolution, HardSoftScore> solutionManager;

    private final Map<String, FacilitySolution> solutionMap = new HashMap<>();

    private final Router ghRouter = Router.getDefaultRouterInstance();

    @PostMapping("/solve")
    public void solve(@RequestBody FacilitySolution problem) {
        ghRouter.setDistanceTimeMap(problem.getLocationList());
        solverManager.solveAndListen(problem.getSolutionId(), id -> problem,
                solution -> solutionMap.put(solution.getSolutionId(), solution));
    }

    @GetMapping("/solution")
    public FacilitySolution solution(@RequestParam String id) {
        return solutionMap.get(id);
    }

    @GetMapping("/list")
    public List<FacilitySolution> list() {
        return solutionMap.values().stream().toList();
    }

    @GetMapping("/score")
    public ScoreAnalysis<HardSoftScore> score(@RequestParam String id) {
        return solutionManager.analyze(solutionMap.get(id));
    }

    @GetMapping("/indictments")
    public List<SimpleIndictmentObject> indictments(@RequestParam String id) {
        return solutionManager.explain(solutionMap.getOrDefault(id, null)).getIndictmentMap().entrySet().stream()
                .map(entry -> {
                    Indictment<HardSoftScore> indictment = entry.getValue();
                    return
                            new SimpleIndictmentObject(entry.getKey(),
                                    indictment.getScore(),
                                    indictment.getConstraintMatchCount(),
                                    indictment.getConstraintMatchSet());
                }).collect(Collectors.toList());
    }

    @PostConstruct
    public void init() {
        FacilitySolution problem50 = FacilitySolution.generateData(50, true);
        ghRouter.setDistanceTimeMap(problem50.getLocationList());
        solverManager.solveAndListen(problem50.getSolutionId(), id -> problem50, solution -> {
            solutionMap.put(solution.getSolutionId(), solution);});
    }
}