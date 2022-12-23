package main;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
// import nde_ga.*;
import nde_mfea.*;
import problem.Problem;
import problem.ProblemConstructor;

public class test {
    public static double std(int[] rs, double mean) {
		double s = 0;
		for(int i=0; i < Configs.REPEAT; i++) {
			s += Math.pow(rs[i]-mean, 2);
		}
		
		return Math.sqrt(s/(Configs.REPEAT-1));
	}
    public static void main(String[] args) throws IOException{
        File f = new File("data_ndu/Pairing1.txt");
        Scanner s = new Scanner(f);
        while(s.hasNextLine()){
            long t1 = System.currentTimeMillis();
            String[] x = s.nextLine().split(" ");
            Problem prob= ProblemConstructor.getPairInstances(x[0],x[1]);
            int taskNum = prob.getTASKS_NUM();
            System.out.println(x[0].split("/")[2] + "  " +x[1].split("/")[2]);
            System.out.println("Running....");
            Configs.rd = new Random(1);
            MFEA algo = new MFEA(prob);
            algo.test();
            // double[][][] mem_f = new double[Configs.REPEAT][taskNum][Configs.MAX_GENERATIONS];
            // double[] rs = algo.run1(mem_f[0]);
    }
    s.close();
}
}
