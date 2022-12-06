package piority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import problem.IDPCNDU;

public class Individual {
    private List<Integer> chromosome;
    private int fitness;
    public Individual(){

    }
    public Individual(List<Integer> chromosome){
        this.chromosome = new ArrayList<>(chromosome);
        this.fitness = Integer.MIN_VALUE;
    }
    public Individual(Individual i ){
        this.chromosome = new ArrayList<>(i.chromosome);
        this.fitness = i.fitness;
    }
    public void randomInit(int numberOfDomains){
        List<Integer> piority =  IntStream.range(1, numberOfDomains+1).boxed().collect(Collectors.toList());
        Collections.shuffle(piority);
        this.chromosome = piority;
    }
    public ArrayList<Integer> decode(IDPCNDU task){
        ArrayList<Integer> path = new ArrayList<>();
        int curr = 1;
        path.add(1);
        int target = task.getNumberOfDomains();
        boolean[] visit = new boolean[task.getNumberOfDomains()+1];
        Arrays.fill(visit,false);
        while(curr != target){
            int next = 0;
            int tmp = Integer.MIN_VALUE;
            for(int d : task.adjDomain.get(curr)){
                if(visit[d])
                    continue;
                if(chromosome.get(d-1) > tmp){
                    next = d;  
                    tmp = chromosome.get(d-1);
                } 
            }
            if(next == 0){
                return null;
            }
            path.add(next);
            visit[next] = true;
            curr= next;
        }
        return path;
    }
    private int[][] buildGraph(IDPCNDU task, ArrayList<Integer> path, ArrayList<Integer> listNodes) {
		
		int[][] distance = new int[task.getNumberOfNodes()+1][task.getNumberOfNodes()+1];
		for(int i = 1; i <= task.getNumberOfNodes(); i++) {
			Arrays.fill(distance[i], Configs.MAX_VALUE);
			distance[i][i] = 0;
		}
		for (int i = 0; i < path.size()-1; i++) {
			
			// build node in a domain
			ArrayList<Integer> listBordersThis = task.getListDomain().get(path.get(i));
			// build edge in a domain
			for (int j: listBordersThis) {
				for (int k: listBordersThis) {
					if (task.distance[j][k] != Configs.MAX_VALUE) {
						distance[j][k] = task.distance[j][k];
					}
				}
			}
			
			// Build shortcut path
            ArrayList<Integer> that = task.getListDomain().get(path.get(i+1));
            for (int x: listBordersThis) {
                for (int y: that) {
                    if (task.distance[x][y] != Configs.MAX_VALUE) {
                        distance[x][y] = task.distance[x][y];
                    }
                }
            }
		}		
		return distance;
	}
	private int minDistance(int[] dist, boolean[] visited, ArrayList<Integer> listNodes) {
		int min = Configs.MAX_VALUE, minIndex = -1;
		for(int v: listNodes) {
			if(!visited[v] && dist[v] < min) {
				minIndex = v;
				min = dist[v];
				
			}
		}
		
		return minIndex;
	}
    public int dijkstra(IDPCNDU task, ArrayList<Integer> path, int[][] distance) {
		int[] dist = new int[task.getNumberOfNodes()+1]; // dist[i]: khoang cach tu s -> i
		Arrays.fill(dist,Configs.MAX_VALUE);
		boolean[] visited = new boolean[task.getNumberOfNodes()+1]; // visited[i] = true: da duyet qua i
		Arrays.fill(visited, false);
		
		dist[task.getS()] = 0;
		
		while(true) {
			int u = minDistance(dist, visited, path);
			if(u == -1){
                return Configs.MAX_VALUE;
            }
			visited[u] = true;
			if(u == task.getT()) break;
			
			for(int v: path) {
				if(!visited[v] && u != v && distance[u][v] != Configs.MAX_VALUE && dist[v] > dist[u] + distance[u][v]) { // co them dist[u] != Configs.MAX_VALUE k?
					dist[v] = dist[u] + distance[u][v];
				}
			}
			
		}
		return dist[task.getT()];
	}
	

	public void updateFitness(IDPCNDU task) {
		ArrayList<Integer> path = decode(task);
        if(path  == null){
            this.setFitness(-Configs.MAX_VALUE);
            return;
        }
		ArrayList<Integer> listNodes = new ArrayList<>(); 
		for(int d: path) {
			listNodes.addAll(task.listDomain.get(d)); 
		}

		int[][] distance = buildGraph(task, path, listNodes);
		int cost = dijkstra(task, listNodes, distance);
		this.setFitness(-cost);
	}
    public List<Integer>getChromosome() {
		return chromosome;
	}

	public void setChromosome(List<Integer> chromosome) {
		this.chromosome = chromosome;
	}

	public int getFitness() {
		return fitness;
	}

	public void setFitness(int fitness) {
		this.fitness = fitness;
	}
}
