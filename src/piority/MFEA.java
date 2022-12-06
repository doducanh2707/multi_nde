package piority;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import problem.Problem;

public class MFEA {
    private ArrayList<Population> pop;
    private Problem prob;
    public MFEA(Problem prob){
        this.prob = prob;
        this.pop = new ArrayList<>();
        for(int i =0;i<prob.getTASKS_NUM(); i++){
            Population  p  = new Population(prob.getTask(i));
            this.pop.add(p);
        }
    }
    public double[] run1(double[][] mem_f){
		double[] result = new double[prob.getTASKS_NUM()];
        for(int i =0;i<prob.getTASKS_NUM();i++){
            pop.get(i).initPopulation(Configs.POPULATION_SIZE,prob.getAdjDomain());
            pop.get(i).updateBestIndividual();
        }
        int gen = 0;
        int len_task  = prob.getTASKS_NUM();
        while(gen < Configs.MAX_GENERATIONS){	
            ArrayList<ArrayList<Individual>> offspring = new ArrayList<>();
            for(int i = 0 ;i < prob.getTASKS_NUM();i++){
                offspring.add(new ArrayList<>());
            }
            int size = 0;
            while(size < Configs.POPULATION_SIZE *len_task){
                int  t = Configs.rd.nextInt(len_task);
                int  k = Configs.rd.nextInt(len_task);
                Individual p1,p2;
                if(t == k || Configs.rd.nextDouble() < Configs.rmp){
                    p1 = pop.get(t).getRandomIndividual();
                    p2 = pop.get(k).getRandomIndividual();
                    ArrayList<Individual> child = crossover(p1, p2);
                    for(Individual c : child){
                        c= mutation(c);
                        if(Configs.rd.nextDouble() < 0.5){
                            c.updateFitness(prob.getTask(t));
                            offspring.get(t).add(c);
                        }
                        else{
                            c.updateFitness(prob.getTask(k));
                            offspring.get(k).add(c);
                        }
                    }
                }
                else{
                    p1 = pop.get(t).getRandomIndividual();
                    p2 = pop.get(t).getRandomIndividual();
                    ArrayList<Individual> child = crossover(p1, p2);
                    Individual c = child.get(0);
                    c = mutation(c);
                    c.updateFitness(prob.getTask(t));
                    offspring.get(t).add(c);

                    p1 = pop.get(k).getRandomIndividual();
                    p2 = pop.get(k).getRandomIndividual();
                    child = crossover(p1, p2);
                    c = child.get(0);
                    c = mutation(c);
                    c.updateFitness(prob.getTask(k));
                    offspring.get(k).add(c);
                }
                size +=2;
            }
            for(int i =0;i<prob.getTASKS_NUM();i++){
                ArrayList<Individual> imiPop = new ArrayList<>();
                imiPop.addAll(offspring.get(i));
                imiPop.addAll(pop.get(i).getPopulation());
                pop.get(i).setPopulation(imiPop);
                pop.get(i).survivalSelection();
            }
			// System.out.print(String.format("Generation %d:\t",gen+1));
            for(int i=0 ;i < prob.getTASKS_NUM();i++){
                // System.out.print("Task " + i + ":" + -pop.get(i).getBestIndividual().getFitness() + " ");
				result[i] = -pop.get(i).getBestIndividual().getFitness();
				mem_f[i][gen] = -pop.get(i).getBestIndividual().getFitness();
            }
			// System.out.println();
            gen++;
        }
		return result;
    }
	
	public static Individual mutation(Individual p){
		List<Integer> c = new ArrayList<>(p.getChromosome());
		int i = Configs.rd.nextInt(c.size());
		int k = Configs.rd.nextInt(c.size());
		int tmp = c.get(i);
		c.set(i,c.get(k));
		c.set(k,tmp);
		return new Individual(c);
	}
	public static ArrayList<Individual> crossover(Individual p1, Individual p2){
		ArrayList<Individual> child = new ArrayList<>();
		List<Integer> parent1 = new ArrayList<>(p1.getChromosome());
		List<Integer> parent2 = new ArrayList<>(p2.getChromosome());
		int start = Configs.rd.nextInt(p1.getChromosome().size()-2);
		int end = Configs.rd.nextInt(p1.getChromosome().size()-start +1) + start;
		List<Integer> child1 = new ArrayList<>(parent1.subList(start, end));
		List<Integer> child2 = new ArrayList<>(parent2.subList(start, end));
		for(int i =0;i<parent1.size();i++){
			int r1 = parent1.get(i);
			int r2 = parent2.get(i);
			if(!child1.contains(r2))
				child1.add(r2);
			if(!child2.contains(r1))
				child2.add(r1);		
		}
		Collections.rotate(child1, start);
	    Collections.rotate(child2, start);
		child.add(new Individual(child1));
		child.add(new Individual(child2));
		return child;
	}

}


