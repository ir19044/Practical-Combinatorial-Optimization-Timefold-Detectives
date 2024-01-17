package lu.df;

import lu.df.domain.DetectiveSolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        LOGGER.info("Hello world from Logger!");
        LOGGER.debug("Hello from debug!");

        DetectiveSolution problem = DetectiveSolution.generateData();
        problem.print();
    }
}