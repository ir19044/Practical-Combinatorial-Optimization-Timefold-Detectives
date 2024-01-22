package lu.df.rest;

import ai.timefold.solver.core.api.score.analysis.ScoreAnalysis;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.constraint.Indictment;
import ai.timefold.solver.core.api.solver.SolutionManager;
import ai.timefold.solver.core.api.solver.SolverManager;
import jakarta.annotation.PostConstruct;
import lu.df.domain.DetectiveSolution;
import lu.df.domain.Router;
import lu.df.solver.SimpleIndictmentObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/routes")
public class DetectiveController {

    @Autowired
    private SolverManager<DetectiveSolution, String> solverManager;

    @Autowired
    private SolutionManager<DetectiveSolution, HardSoftScore> solutionManager;

    private Map<String, DetectiveSolution> solutionMap = new HashMap<>();

    private Router ghRouter = Router.getDefaultRouterInstance();

    @PostMapping("/solve")
    public void solve(@RequestBody DetectiveSolution problem){
        ghRouter.setDistanceTimeMap(problem.getLocationList());
        //solutionIOJSON.write(problem50, new File("data/exampleRiga50.json"));
        solverManager.solveAndListen(problem.getSolutionId(), id -> problem, solution -> {
            solutionMap.put(solution.getSolutionId(), solution);});
    }

    @GetMapping("/solution")
    public DetectiveSolution solution(@RequestParam String id){
        return solutionMap.get(id);
    }

    @GetMapping("/list")
    public List<DetectiveSolution> list(){
        return solutionMap.values().stream().toList();
    }

    @GetMapping("/score")
    public ScoreAnalysis<HardSoftScore> score(@RequestParam String id){
        return solutionManager.analyze(solutionMap.get(id));
    }

    @GetMapping("/indictments")
    public List<SimpleIndictmentObject> indictments(@RequestParam String id) {
        return solutionManager.explain(solutionMap.getOrDefault(id, null)).getIndictmentMap().entrySet().stream()
                .map(entry -> {
                    Indictment<HardSoftScore> indictment = entry.getValue();
                    return
                            new SimpleIndictmentObject(entry.getKey(), // indicted Object
                                    indictment.getScore(),
                                    indictment.getConstraintMatchCount(),
                                    indictment.getConstraintMatchSet());
                }).collect(Collectors.toList());
    }

   // @PostConstruct
    public void init() {
        DetectiveSolution problem50 = DetectiveSolution.generateData(25);
        ghRouter.setDistanceTimeMap(problem50.getLocationList());
        //solutionIOJSON.write(problem50, new File("data/exampleRiga50.json"));
        solverManager.solveAndListen(problem50.getSolutionId(), id -> problem50, solution -> {
            solutionMap.put(solution.getSolutionId(), solution);});
    }
}
