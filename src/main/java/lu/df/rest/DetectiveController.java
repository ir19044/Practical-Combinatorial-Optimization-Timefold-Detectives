package lu.df.rest;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.solver.SolutionManager;
import ai.timefold.solver.core.api.solver.SolverManager;
import lu.df.domain.DetectiveSolution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/routes")
public class DetectiveController {

    @Autowired
    private SolverManager<DetectiveSolution, String> solverManager;

    @Autowired
    private SolutionManager<DetectiveSolution, HardSoftScore> solutionManager;

    private Map<String, DetectiveSolution> solutionMap = new HashMap<>();

    @PostMapping("/solve")
    public void solve(@RequestBody DetectiveSolution problem){
        solverManager.solveAndListen(problem.getSolutionId(), id -> problem,
                solution -> solutionMap.put(solution.getSolutionId(), solution));
    }

    @GetMapping("/solution")
    public DetectiveSolution solution(@RequestParam String id){
        return solutionMap.get(id);
    }

}
