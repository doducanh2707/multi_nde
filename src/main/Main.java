package main;

import java.io.*;
import java.util.*;
// import nde_ga.*;
import nde_mfea.*;
import problem.Problem;
import problem.ProblemConstructor;

public class Main {
    public static double std(int[] rs, double mean) {
		double s = 0;
		for(int i=0; i < Configs.REPEAT; i++) {
			s += Math.pow(rs[i]-mean, 2);
		}
		
		return Math.sqrt(s/(Configs.REPEAT-1));
	}
    public static void main(String[] args) throws IOException{
        File f = new File("data_ndu/Pairing.txt");
        Scanner s = new Scanner(f);
        while(s.hasNextLine()){
            String[] x = s.nextLine().split(" ");
            Problem prob= ProblemConstructor.getPairInstances(x[0],x[1]);
            int taskNum = prob.getTASKS_NUM();
            double[][][] mem_f = new double[Configs.REPEAT][taskNum][Configs.MAX_GENERATIONS];
            double[] mean  = new double[taskNum];
            int[] best  = new int[taskNum];
            int[][] history = new int[taskNum][Configs.REPEAT];
            Arrays.fill(best, Integer.MAX_VALUE);
            System.out.println(x[0].split("/")[2] + "  " +x[1].split("/")[2]);
            System.out.println("Running....");
            for(int seed = 0; seed < Configs.REPEAT;seed++){
                System.out.println("Program running with seed = " + seed);
                Configs.rd = new Random(seed);
                // System.out.println("Program running with seed = " + seed);
                MFEA solver = new MFEA(prob);
                double[] result = solver.run1(mem_f[seed]);
                for (int t = 0; t < taskNum; t++) {
                    mean[t] += result[t] / Configs.REPEAT;
                    best[t] = Math.min(best[t],(int)result[t]);
                    history[t][seed] = (int)result[t];
                }
                // for(int i=0 ;i < prob.getTASKS_NUM();i++){
                //     System.out.print("Task " + i + ":" + result[i] + " ");
                // }
                // System.out.println();
            }
            for(int i=0;i<taskNum;i++){
                String[] name = x[i].split("/");
                String result =String.format("results\\%s.txt", name[1]);
                FileWriter fw = new FileWriter(result,true);
                int BF = best[i];
                double AVG = mean[i];
                double STD = std(history[i], AVG);
                String p = String.format("\t\t%d\t\t%.2f\t\t%.2f", BF, AVG, STD);
                String s_name = name[2].substring(0, name[2].lastIndexOf('.'));
                fw.write(s_name + p +"\n");
                fw.flush();
                fw.close();
            }
            // print results
            String rootFolder = "results/";
            File dir = new File(rootFolder);
            if (!dir.exists()) {
                dir.mkdir();
            }
            for(int i =0;i<taskNum;i++){
                String dirType = rootFolder + x[i].split("/")[1];
                dir = new File(dirType);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                String name = x[i].split("/")[2].split("/")[0];
                String benchmark = dirType + "/" + name.substring(0, name.lastIndexOf('.'));
                dir = new File(benchmark);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                for(int seed = 0 ;seed < Configs.REPEAT;seed++){
                    String fitnessFile = benchmark + "/Seed_" + seed + ".txt";
                    DataOutputStream outFit = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fitnessFile)));
                    for(int r = 0 ; r < Configs.MAX_GENERATIONS;r++){
                        outFit.writeBytes(String.format("Generation %d : %.6f",r+1, mem_f[seed][i][r]) + "\n");
                    }
                    outFit.close();
                }
            }
        }
    }
}
