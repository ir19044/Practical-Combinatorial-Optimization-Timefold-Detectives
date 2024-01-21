package lu.df.domain;

import ai.timefold.solver.jackson.impl.domain.solution.JacksonSolutionFileIO;

public class DetectiveSolutionJsonIO extends JacksonSolutionFileIO<DetectiveSolution> {

    public DetectiveSolutionJsonIO() { super(DetectiveSolution.class); }
}
