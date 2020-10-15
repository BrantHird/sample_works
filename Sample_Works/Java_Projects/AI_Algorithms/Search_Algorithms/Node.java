import java.util.ArrayList;
import java.util.List;

public class Node {

    int number_1 ;
    int number_2 ;
    int number_3 ;
    int changed ;
    Node parent;
    int depth ;
    int heuristic;
    boolean alternative;

    List<Node> nodeList ;


    public Node( int n1, int n2, int n3, int change, Node parent){

        this.number_1 = n1 ;
        this.number_2 = n2 ;
        this.number_3 = n3 ;
        this.changed = change;
        this.parent = parent ;
        nodeList = new ArrayList<>();
        this.alternative = false ;

    }

    public List<Node> generateChildren(List<Node> forbiddenNodes, List<Node> visitedNodes){

        List<Node> children = new ArrayList<>();

        List<Node> potentials = new ArrayList<>();

        potentials.add(new Node(this.number_1 - 1 , number_2, number_3, 1, this));
        potentials.add(new Node(this.number_1 + 1 , number_2, number_3, 1, this));
        potentials.add(new Node(this.number_1 , number_2 - 1, number_3, 2, this));
        potentials.add(new Node(this.number_1 , number_2 + 1, number_3, 2, this));
        potentials.add(new Node(this.number_1 , number_2, number_3 - 1, 3, this));
        potentials.add(new Node(this.number_1 , number_2, number_3 + 1, 3, this));

        for(Node n : potentials){

            boolean valid = true ;

            for(Node f : forbiddenNodes) {
                if (n.compareTo(f)) {
                    valid = false;
                }
            }

            for(Node v : visitedNodes){
                if(n.compareTo(v) && n.getChanged() == v.getChanged()){
                    valid = false;
                }
            }

            if(n.getNumber_1() > 9 || n.getNumber_1() < 0) {
                valid = false;
            }
            if(n.getNumber_2() > 9 || n.getNumber_2() < 0) {
                valid = false;
            }
            if(n.getNumber_3() > 9 || n.getNumber_3() < 0) {
                valid = false;
            }

            if(n.getParent().getChanged() == n.getChanged()){
                valid = false ;
            }

            if(valid){
                children.add(n);
            }

        }


        return children;

    }

    public int getNumber_1(){
        return number_1 ;
    }

    public int getNumber_2(){
        return number_2 ;
    }

    public int getNumber_3(){
        return number_3 ;
    }

    public int getChanged(){
        return changed ;
    }

    public void setDepth(int d){
        this.depth = d ;
    }

    public int getDepth(){
        return this.depth;
    }

    public void setHeuristic(int h){
        this.heuristic = h ;
    }

    public int getHeuristic(){
        return heuristic;
    }

    public boolean compareTo(Node n){

        if(n.getNumber_1() == this.number_1 &&
            n.getNumber_2() == this.number_2 &&
            n.getNumber_3() == this.number_3){
            return  true ;
        }

        return false;

    }

    public Node getParent(){
        return this.parent;
    }

    public void setAlternative(boolean truth){
        this.alternative = truth ;
    }

    public boolean getAlternative(){
        return alternative;
    }




}
