package problem.loc.facility.domain;

import ai.timefold.solver.jackson.impl.domain.solution.JacksonSolutionFileIO;

public class FacilitySolutionJsonIO extends JacksonSolutionFileIO<FacilitySolution> {
    public FacilitySolutionJsonIO() {
        super(FacilitySolution.class);
    }
}