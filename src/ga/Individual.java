package ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import problem.IDPCNDU;

public class Individual {
	private ArrayList<NodeDepth> chromosome;
	private int fitness;
	private int skill_factor;
	public Individual() {

	}
	
	public Individual(ArrayList<NodeDepth> chromosome) {
		ArrayList<NodeDepth> copyChromosome = new ArrayList<>();
		for(NodeDepth n: chromosome) {
			copyChromosome.add(new NodeDepth(n));
		}
		
		this.chromosome = copyChromosome;
		this.fitness = Integer.MIN_VALUE;
	}
	
	public Individual(Individual i) {
		ArrayList<NodeDepth> copyChromosome = new ArrayList<>();
		for(NodeDepth n: i.chromosome) {
			copyChromosome.add(new NodeDepth(n));
		}
		
		this.chromosome = copyChromosome;
		this.fitness = i.fitness;
	}


	public void randomInit(ArrayList<ArrayList<Integer>> adjDomain) {
		ArrayList<ArrayList<Integer>> st = primRST(adjDomain);
		this.chromosome = encode(st);
	}

	public ArrayList<NodeDepth> getChromosome() {
		return chromosome;
	}

	public void setChromosome(ArrayList<NodeDepth> chromosome) {
		this.chromosome = chromosome;
	}

	public int getFitness() {
		return fitness;
	}

	public void setFitness(int fitness) {
		this.fitness = fitness;
	}
	public int getskillfactor() {
		return skill_factor;
	}

	public void setskillfactor(int skill_factor) {
		this.skill_factor = skill_factor;
	}
	private void DFSUtil(int v, boolean[] visited, int[] depth, int depthOfV, ArrayList<NodeDepth> rs, ArrayList<ArrayList<Integer>> T) {
		visited[v] = true;
		depth[v] = depthOfV;
		rs.add(new NodeDepth(v, depth[v]));
		
		ArrayList<Integer> adj = T.get(v);
		for(int u: adj) {
			if(!visited[u]) {
				DFSUtil(u, visited, depth, depthOfV+1, rs, T);
			}
		}
	}
	

	public ArrayList<NodeDepth> DFS(ArrayList<ArrayList<Integer>> T) {
		boolean[] visited = new boolean[T.size()];
		int[] depth = new int[T.size()];
		ArrayList<NodeDepth> rs = new ArrayList<>(); // thu tu tham cac mien duyet theo DFS
		
		depth[1] = 0;
		DFSUtil(1, visited, depth, depth[1], rs, T);
		
		return rs;
	}
	
	// chuyen tu cay khung lien mien -> ma hoa node-depth
	public ArrayList<NodeDepth> encode(ArrayList<ArrayList<Integer>> T) {
		ArrayList<NodeDepth> result = DFS(T);

		return result;
	}

	// chuyen tu do thi lien mien -> cay khung lien mien
	public ArrayList<ArrayList<Integer>> primRST(ArrayList<ArrayList<Integer>> adjDomain){
		ArrayList<ArrayList<Integer>> T = new ArrayList<ArrayList<Integer>>();
		for(int d = 1; d <= adjDomain.size(); d++) {
			T.add(new ArrayList<Integer>());
		}

		ArrayList<Integer> C = new ArrayList<>(); // chua cac node di qua
		ArrayList<Edge> A = new ArrayList<>(); // chua cac canh co the phat trien

		// init
		C.add(1);
		for(int v: adjDomain.get(1)) {
			A.add(new Edge(1,v));
		}

		while(C.size() != (adjDomain.size()-1)) {
			Edge e = A.get(Configs.rd.nextInt(A.size()));
			int u = e.getNode1();
			int v = e.getNode2();
			A.remove(e);
			if(!C.contains(v)) {
				T.get(u).add(v);
				C.add(v);
				for(int w: adjDomain.get(v)) {
					if(!C.contains(w)) A.add(new Edge(v,w));
				}
			}
		}

		return T;
	}
	
	// tra ve duong di lien mien
	
	public ArrayList<Integer> decode(IDPCNDU task) {
		ArrayList<Integer> path = new ArrayList<>();
		ArrayList<NodeDepth> u = new ArrayList<>();
		for(NodeDepth x : chromosome)
			u.add(new NodeDepth(x));

		ArrayList<NodeDepth> tree = new ArrayList<>();
		int[] visited = new int[chromosome.size()];
		Arrays.fill(visited, 0);
		tree.add(u.get(0));
		visited[0] = 1;
		while(Arrays.stream(visited).sum() != u.size()){
			// for(int idx = 0;idx<chromosome.size();idx++){
			// 	System.out.print(visited[idx]+ " ");
			// }
			// System.out.println();
			for(int j =1;j<u.size();j++){
				if(visited[j] == 1)
					continue;
				NodeDepth x = u.get(j);
				if(x.getNode() > task.getNumberOfDomains()){
					visited[j] =1;
					continue;
				}
				for(int k = tree.size()-1;k>=0;k--){
					NodeDepth y = tree.get(k);
					if(y.getDepth() < x.getDepth() && task.adjDomain.get(y.getNode()).contains(x.getNode())){
						tree.add(k+1,new NodeDepth(x.getNode(),y.getDepth()+1));
						visited[j] = 1;
						break;
					}
				}
				if(visited[j] == 0)
					u.get(j).setDepth(u.get(j).getDepth()+1);

			}
		}
		// if(task.getNumberOfDomains() == 10){
		// 	System.out.println(tree);
		// }
		// find the destination domain
		// tree = chromosome;
		NodeDepth p = new NodeDepth(0, 0);
		int i;
		for(i = tree.size()-1; i >= 0; i--) {
			if(tree.get(i).getNode() == task.getNumberOfDomains()) {
				path.add(task.getNumberOfDomains());
				p = tree.get(i);
				break;
			}
		}
		for(int j = i-1; j >= 0; j--) {
			if(tree.get(j).getDepth() < p.getDepth()) {
				p = tree.get(j);
				path.add(p.getNode());
			}
		}
		// reverse: source domain -> ... -> destination domain
		Collections.reverse(path);

		return path;
	}
	
	// tim node co khoang cach nho nhat de tham
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
	
	public int dijkstra(IDPCNDU task, ArrayList<Integer> path) {
		int[] dist = new int[task.getNumberOfNodes()+1]; // dist[i]: khoang cach tu s -> i
		Arrays.fill(dist, Configs.MAX_VALUE); 
		boolean[] visited = new boolean[task.getNumberOfNodes()+1]; // visited[i] = true: da duyet qua i
		Arrays.fill(visited, false);
		
		dist[task.getS()] = 0;
		
		int[][] distance = task.distance;
		
		// VD voi thu tu mien 1 -> 2 -> 3 -> 4, neu co canh noi truc tiep tu mien 1 den mien 4 -> van thoa man
		while(true) {
			int u = minDistance(dist, visited, path);
			if(u == -1) return Configs.MAX_VALUE;
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
//		int N = task.getNumberOfNodes(); 
//		int[][] virtualDistance = new int[N+1][N+1];
//		for(int i = 1; i <= N; i++) {
//			Arrays.fill(virtualDistance[i], Configs.MAX_VALUE);
//		}
		
		ArrayList<Integer> listNodes = new ArrayList<>(); // list nodes in the virtual graph: bao gom tat ca cac node bien trong mien di qua
		for(int d: path) {
//			listNodes.addAll(task.borderNode.get(d));
			listNodes.addAll(task.listDomain.get(d));
		}
		
		int cost = dijkstra(task, listNodes);
		
		this.setFitness(-cost);
	}
	
}
