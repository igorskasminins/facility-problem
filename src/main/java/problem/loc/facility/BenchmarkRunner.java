package problem.loc.facility;

import ai.timefold.solver.benchmark.api.PlannerBenchmark;
import ai.timefold.solver.benchmark.api.PlannerBenchmarkFactory;
import problem.loc.facility.domain.FacilitySolution;
import problem.loc.facility.domain.FacilitySolutionJsonIO;

import java.io.File;

public class BenchmarkRunner {
    public static void main(String[] args) {
        // Generate examples
        FacilitySolutionJsonIO FacilitySolutionJsonIO = new FacilitySolutionJsonIO();
        FacilitySolutionJsonIO.write(FacilitySolution.generateData(5, false),
                new File("data/classExample5.json"));
        FacilitySolutionJsonIO.write(FacilitySolution.generateData(100, false),
                new File("data/classExample50.json"));
        FacilitySolutionJsonIO.write(FacilitySolution.generateData(100, false),
                new File("data/classExample100.json"));
        FacilitySolutionJsonIO.write(FacilitySolution.generateData(150, false),
                new File("data/classExample150.json"));

        // Run the benchmark on the generated inputs
        PlannerBenchmarkFactory benchmarkFactoryFromXML = PlannerBenchmarkFactory
                .createFromXmlResource("BenchmarkConfig.xml");

        PlannerBenchmark benchmark = benchmarkFactoryFromXML.buildPlannerBenchmark();
        benchmark.benchmarkAndShowReportInBrowser();
    }
}
