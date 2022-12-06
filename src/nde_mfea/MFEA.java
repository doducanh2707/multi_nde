package nde_mfea;
import java.util.ArrayList;

import problem.IDPCNDU;
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
    // Edge Permutation Operation
	public void EPO(int va, int vb, ArrayList<NodeDepth> tmp, ArrayList<NodeDepth> chromosome) {
		NodeDepth nodeVB = new NodeDepth();
		for(NodeDepth nd: chromosome) {
			if(nd.getNode() == vb) {
				nodeVB = new NodeDepth(nd);
				break;
			}
		}

		// update depth
		int dp = tmp.get(0).getDepth(); // do sau goc cua cay con
		for(NodeDepth nd: tmp) {
			nd.setDepth(nd.getDepth()-dp+nodeVB.getDepth()+1);
		}

		int i;
		for(i = 0; i < chromosome.size(); i++) {
			if(chromosome.get(i).getNode() == vb) {
				break;
			}
		}
		
		// noi tmp vao vi tri sau cua node vb
		chromosome.addAll(i+1, tmp);
	}
    private Individual mutation(Individual p){
        Individual offspring = new Individual(p);
        ArrayList<NodeDepth> chromosome = offspring.getChromosome();
        int va = Configs.rd.nextInt(prob.getNumberOfDomains()-2)+2;
        ArrayList<NodeDepth> tmp = new ArrayList<>(); // pruned subtree

		// prune subtree
		int i;
		// tim vi tri cua mien VA trong ma hoa node-depth
		for(i = 0; i < chromosome.size(); i++) {
			if(va == chromosome.get(i).getNode()) {
				break;
			}
		}
		// tim cac node con lai trong subtree
		tmp.add(new NodeDepth(chromosome.get(i)));
		for(int j = i+1; j < chromosome.size(); j++) {
			if(chromosome.get(j).getDepth() <= chromosome.get(i).getDepth()) {
				break;
			}
			tmp.add(new NodeDepth(chromosome.get(j)));
		}

		// xoa cac phan tu cua tmp trong chromosome
		for(NodeDepth xx: tmp) {
			for(int j = 0; j < chromosome.size(); j++) {
				if(chromosome.get(j).getNode() == xx.getNode()) {
					chromosome.remove(j);
					break;
				}
			}
		}

		// xoa cac node trong tmp trong parentDomain.get(va)
		ArrayList<Integer> parent = new ArrayList<>();
		for(int d: prob.parentDomain.get(va)) {
			parent.add(d);
		}
		for(NodeDepth nd: tmp) {
			int node = nd.getNode();
			if(parent.contains(node)) {
				parent.remove(parent.indexOf(node));
			}
		}

		// chon ngau nhien vb de lam cha moi cua pruned subtree co goc va
		int vb = parent.get(Configs.rd.nextInt(parent.size()));
		
		EPO(va,vb,tmp,chromosome);

		return offspring;
    }
    // vpb: cha cua vri tren cay B
	// kiem tra xem vpb co nam trong cay con cua vri tren cay A ko (A = p1)
	public boolean check(int vri, int vpb, Individual p1) {
		ArrayList<NodeDepth> chromosome = p1.getChromosome();

		int i;
		// tim vi tri cua vri trong ma hoa
		for(i = 0; i < chromosome.size(); i++) {
			if(chromosome.get(i).getNode() == vri) {
				break;
			}
		}

		int depth = chromosome.get(i).getDepth();
		
		// neu vpb nam trong cay con goc vri thi return false
		for(int j = i+1; j < chromosome.size(); j++) {
			if(chromosome.get(j).getDepth() == depth) break;
			if(depth < chromosome.get(j).getDepth() && chromosome.get(j).getNode() == vpb) {
				return false;
			}
		}


		return true;
	}

	// tim node cha cua vri tren cay p
	public int findParent(int vri, Individual p) {
		int rs = 0;
		int depth = 0; // do sau cua vri
		boolean found = false;
		for(int i = p.getChromosome().size()-1; i >= 0; i--) {
			if(p.getChromosome().get(i).getNode() == vri) {
				found = true;
				depth = p.getChromosome().get(i).getDepth();
			}
			if(found && p.getChromosome().get(i).getDepth() < depth) {
				rs = p.getChromosome().get(i).getNode();
				break;
			}
		}

		return rs;
	}
    public Individual eco(Individual p1, Individual p2) {
		Individual offspring = new Individual(p1);
		int n = prob.getNumberOfDomains(); 
		int i = (int) (Configs.rd.nextDouble()*n/2 + n/4); // so luong node dc doi vi tri
		ArrayList<Integer> vr = new ArrayList<>();

		while(vr.size() < i) {
			int tmp = Configs.rd.nextInt(n)+1;
			if(tmp > 1 && !vr.contains(tmp)) {
				vr.add(tmp);
			}
		}

		for(int j: vr) {
			int p = findParent(j, p2); // p la cha cua j trong cay p2

			if(check(j, p, offspring)) { // kiem tra xem p co nam trong cay con dc cat ra ko
				ArrayList<NodeDepth> tmp = new ArrayList<>();
				ArrayList<NodeDepth> chromosome = offspring.getChromosome();

				// prune subtree
				int ii;
				// tim vi tri cua mien VA trong ma hoa node-depth
				for(ii = 0; ii < chromosome.size(); ii++) {
					if(j == chromosome.get(ii).getNode()) {
						break;
					}
				}

				// tim cac node con lai trong subtree
				tmp.add(new NodeDepth(chromosome.get(ii)));

				for(int jj = ii+1; jj < chromosome.size(); jj++) {
					if(chromosome.get(jj).getDepth() <= chromosome.get(ii).getDepth()) {
						break;
					}
					tmp.add(new NodeDepth(chromosome.get(jj)));
				}

				// xoa cac phan tu cua tmp trong chromosome
				for(NodeDepth xx: tmp) {
					for(int jj = 0; jj < chromosome.size(); jj++) {
						if(chromosome.get(jj).getNode() == xx.getNode()) {
							chromosome.remove(jj);
							break;
						}
					}
				}

				EPO(j, p, tmp, chromosome);
			}
		}


		return offspring;
	}
    private ArrayList<Individual> crossover(Individual p1 ,Individual p2){  
        ArrayList<Individual> child = new ArrayList<>();
        child.add(eco(p1,p2));
        child.add(eco(p1,p2));
        return child;
    }

}


