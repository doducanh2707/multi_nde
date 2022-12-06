package nde_mfea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import problem.IDPCNDU;

public class Population {
	private ArrayList<Individual> population;
	private Individual bestIndividual;
	private IDPCNDU task;

	public Population(IDPCNDU task) {
		this.population = new ArrayList<>();
		this.bestIndividual = null;
		this.task = task;
	}

	public void initPopulation(int nb_inds,ArrayList<ArrayList<Integer>> adjDomain) {
		population.clear();
		while(population.size() < nb_inds) {
			Individual i = new Individual();
			i.randomInit(adjDomain);
			i.updateFitness(task);
			if(i.getFitness() > -Configs.MAX_VALUE) population.add(i);
		}
	}

	public ArrayList<Individual> getPopulation() {
		return population;
	}

	public void setPopulation(ArrayList<Individual> population) {
		this.population = population;
	}
	public Individual getRandomIndividual(){
		int r = Configs.rd.nextInt(population.size());
		return population.get(r);
	}

	public Individual getBestIndividual() {
		return bestIndividual;
	}

	public void setBestIndividual(Individual bestIndividual) {
		this.bestIndividual = bestIndividual;
	}

	public IDPCNDU getTask() {
		return task;
	}

	public void setTask(IDPCNDU task) {
		this.task = task;
	}

	public void evalPopulation() {
		for(Individual ind: this.population) {
			ind.updateFitness(task);
		}
	}

	public void updateBestIndividual() {
		for(Individual i: population) {
			if(bestIndividual == null || bestIndividual.getFitness() < i.getFitness()) 
				this.setBestIndividual(i);;
		}
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

	public void survivalSelection() {
		Collections.sort(this.population, new Comparator<Individual>() {
			@Override
			public int compare(Individual o1, Individual o2) {
				if(o1.getFitness() > o2.getFitness()) return -1;
				else if(o1.getFitness() < o2.getFitness()) return 1;
				return 0;
			}
		});

		while(this.population.size() > Configs.POPULATION_SIZE) {
			this.population.remove(population.size()-1);
		}
		
		this.setBestIndividual(population.get(0));
	}
}
