<?xml version="1.0" encoding="UTF-8"?>
<plannerBenchmark xmlns="https://timefold.ai/xsd/benchmark" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                  xsi:schemaLocation="https://timefold.ai/xsd/benchmark https://timefold.ai/xsd/benchmark/benchmark.xsd">
    <benchmarkDirectory>local/benchmarkReport</benchmarkDirectory>

    <inheritedSolverBenchmark>
        <solver>
            <environmentMode>REPRODUCIBLE</environmentMode>
            <!-- Define the model -->
            <solutionClass>lu.df.domain.DetectiveSolution</solutionClass>
            <entityClass>lu.df.domain.Detective</entityClass>
            <entityClass>lu.df.domain.Visit</entityClass>

            <!-- Define the score function -->
            <scoreDirectorFactory>
                <constraintProviderClass>lu.df.solver.StreamCalculator</constraintProviderClass>
            </scoreDirectorFactory>

            <!-- Configure the optimization algorithms (optional) -->
            <termination>
                <secondsSpentLimit>30</secondsSpentLimit>
            </termination>

            <constructionHeuristic />
        </solver>
        <problemBenchmarks>
            <solutionFileIOClass>lu.df.domain.DetectiveSolutionJsonIO</solutionFileIOClass>
            <writeOutputSolutionEnabled>true</writeOutputSolutionEnabled>
            <inputSolutionFile>data/classExample5.json</inputSolutionFile>
            <inputSolutionFile>data/classExample10.json</inputSolutionFile>
            <inputSolutionFile>data/classExample20.json</inputSolutionFile>
            <inputSolutionFile>data/classExample35.json</inputSolutionFile>
            <inputSolutionFile>data/classExample40.json</inputSolutionFile>
        </problemBenchmarks>
        <!--<subSingleCount>5</subSingleCount>-->
    </inheritedSolverBenchmark>

    <#list [1, 2, 5, 10] as entityTabuSize>
    <#list [200, 500, 1000] as accepteedCountLimit>
    <solverBenchmark>
        <name>Tabu size ${entityTabuSize} limit ${accepteedCountLimit}</name>
        <solver>
            <localSearch>
                <unionMoveSelector>
                    <subListChangeMoveSelector />
                    <listChangeMoveSelector />
                    <listSwapMoveSelector />
                </unionMoveSelector>
                <acceptor>
                    <valueTabuSize>${entityTabuSize}</valueTabuSize>
                </acceptor>
                <forager>
                    <acceptedCountLimit>${accepteedCountLimit}</acceptedCountLimit>
                </forager>
            </localSearch>
        </solver>
    </solverBenchmark>
    </#list>
    </#list>

    <#list [1, 5] as lateAcceptanceSize>
    <#list [200, 1000] as accepteedCountLimit>
    <solverBenchmark>
        <name>LAHC size ${lateAcceptanceSize} limit ${accepteedCountLimit}</name>
        <solver>
            <localSearch>
                <unionMoveSelector>
                    <subListChangeMoveSelector />
                    <listChangeMoveSelector />
                    <listSwapMoveSelector />
                </unionMoveSelector>
                <acceptor>
                    <lateAcceptanceSize>${lateAcceptanceSize}</lateAcceptanceSize>
                </acceptor>
                <forager>
                    <acceptedCountLimit>${accepteedCountLimit}</acceptedCountLimit>
                </forager>
            </localSearch>
        </solver>
    </solverBenchmark>
    </#list>
    </#list>
</plannerBenchmark>