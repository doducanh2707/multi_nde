package problem;

import java.io.IOException;

public class ProblemConstructor {
    public static Problem getPairInstances(String x,String y) throws IOException{
        Problem prob = new Problem();
        IDPCNDU task = new IDPCNDU();
        // System.out.println(x);
        task.readData(x);
        prob.addTask(task);
        task = new IDPCNDU();
        task.readData(y);
        prob.addTask(task);

        return prob;
    }
}
