package main;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputFilter.Config;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import core.MFEA;
import ga.Configs;
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
                String fitnessFile = dirType + "/" + x[i].split("/")[2] + ".txt";
                DataOutputStream outFit = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fitnessFile)));
                for (int r = 0; r < Configs.MAX_GENERATIONS; r++) {
                    outFit.writeBytes("Generation "+(r + 1) + ", ");
                    for (int seed = 0; seed < Configs.REPEAT; seed++) {
                            if (seed < Configs.REPEAT - 1 || i < taskNum - 1) {
                                outFit.writeBytes(String.format("%.6f", mem_f[seed][i][r]) + ", ");
                            } else {
                                outFit.writeBytes("" + String.format("%.6f", mem_f[seed][i][r]) + "\n");
                            }
                    }
                }
                outFit.close();
    
                String dirMean = dirType + "/Mean";
                dir = new File(dirMean);
                if (!dir.exists()) {
                    dir.mkdir();
                }
    
                String meanFile = dirMean + "/MTOSOO_P" + (i + 1) + ".txt";
                DataOutputStream outMean = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(meanFile)));
                for (int task = 0; task < taskNum; task++) {
                    outMean.writeBytes("" + (task + 1) + ", " + String.format("%.6f", mean[task]) + "\n");
                }
                outMean.close();

            }
        }
    }
}
