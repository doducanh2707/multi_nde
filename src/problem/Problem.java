package problem;
import java.util.ArrayList;

public class Problem {
    private ArrayList<IDPCNDU> tasks;
    private int TASKS_NUM;
    private int numberOfDomains;
    private ArrayList<ArrayList<Integer>> adjDomain;
    public ArrayList<ArrayList<Integer> > parentDomain; 
    public Problem(){
        tasks = new ArrayList<>();
        numberOfDomains = 0;
        adjDomain = new ArrayList<>();
        parentDomain = new ArrayList<>();
    }
    public IDPCNDU getTask(int task_id){
        return tasks.get(task_id);
    }
    public void addTask(IDPCNDU task){
        tasks.add(task);
        TASKS_NUM +=1;
        if(numberOfDomains < task.getNumberOfDomains()){
            numberOfDomains = task.getNumberOfDomains();
            for(int i =0;i<adjDomain.size();i++){
                ArrayList<Integer> adj = adjDomain.get(i);
                ArrayList<Integer> temp = new ArrayList<>(task.adjDomain.get(i));
                temp.removeAll(adj);
                adj.addAll(temp);
                adj = parentDomain.get(i);
                temp = new ArrayList<>(task.parentDomain.get(i));
                temp.removeAll(adj);
                adj.addAll(temp);
            }
            for(int i = adjDomain.size();i <= numberOfDomains;i++){
                adjDomain.add(new ArrayList<>(task.adjDomain.get(i)));
                parentDomain.add(new ArrayList<>(task.parentDomain.get(i)));
            }
        }
        else{
            for(int i =0;i<task.getNumberOfDomains();i++){
                ArrayList<Integer> adj = adjDomain.get(i);
                ArrayList<Integer> temp = new ArrayList<>(task.adjDomain.get(i));
                temp.removeAll(adj);
                adj.addAll(temp);
                adj = parentDomain.get(i);
                temp = new ArrayList<>(task.parentDomain.get(i));
                temp.removeAll(adj);
                adj.addAll(temp);
            }
        }
        assert adjDomain.size() != numberOfDomains;
    }

    public ArrayList<IDPCNDU> getTasks() {
        return this.tasks;
    }

    public void setTasks(ArrayList<IDPCNDU> tasks) {
        this.tasks = tasks;
    }

    public int getTASKS_NUM() {
        return this.TASKS_NUM;
    }

    public void setTASKS_NUM(int TASKS_NUM) {
        this.TASKS_NUM = TASKS_NUM;
    }

    public int getNumberOfDomains() {
        return this.numberOfDomains;
    }

    public void setNumberOfDomains(int numberOfDomains) {
        this.numberOfDomains = numberOfDomains;
    }

    public ArrayList<ArrayList<Integer>> getAdjDomain() {
        return this.adjDomain;
    }

    public void setAdjDomain(ArrayList<ArrayList<Integer>> adjDomain) {
        this.adjDomain = adjDomain;
    }

    public ArrayList<ArrayList<Integer>> getParentDomain() {
        return this.parentDomain;
    }

    public void setParentDomain(ArrayList<ArrayList<Integer>> parentDomain) {
        this.parentDomain = parentDomain;
    }

}
