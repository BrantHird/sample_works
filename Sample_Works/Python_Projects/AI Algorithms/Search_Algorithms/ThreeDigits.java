
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;



public class ThreeDigits {



    public static int heuristicCalculator(Node n1 , Node n2){

        int h1 = Math.abs(n1.getNumber_1() - n2.getNumber_1());
        int h2 = Math.abs(n1.getNumber_2() - n2.getNumber_2());
        int h3 = Math.abs(n1.getNumber_3() - n2.getNumber_3());

        int heuristic = h1 + h2 + h3 ;

        return heuristic;


    }


    public static int costSoFar(Node n){

        n = n.getParent();

        int cost = 0 ;


        while(n!=null){

            cost ++ ;

            n = n.getParent();

        }

        return cost;
    }


    public static List<Integer> breakDown(String s){

        char start1 = s.charAt(0);
        char start2 = s.charAt(1);
        char start3 = s.charAt(2);
        int s1 = start1 - '0';
        int s2 = start2 - '0';
        int s3 = start3 - '0';

        List<Integer> returnList = new ArrayList<>();
        returnList.add(s1);
        returnList.add(s2);
        returnList.add(s3);

        return returnList ;
    }

    public static List<Node> generatePath(List<Node> visited){
        List<Node> path = new ArrayList<>();
        List<Node> reversePath = new ArrayList<>();

        Node thisNode = visited.get(visited.size() - 1);

        while(thisNode != null){
            reversePath.add(thisNode);
            thisNode = thisNode.getParent();
        }

        for(int i = reversePath.size() - 1 ; i > -1 ; i --){

            path.add(reversePath.get(i));

        }

        return  path ;
    }




    public static List<Node> hillSearch(Node start, Node end, List<Node> forbiddenList, List<Boolean> changedList ){

        List<Node> visited = new ArrayList<>();

        Node n = start ;

        n.setHeuristic(heuristicCalculator(n,end));

        List<Node> potentials = new ArrayList<>();

        boolean changed  ;

        while(true){

            changed = false;

            visited.add(n);

            if(n.compareTo(end)){
                changedList.add(true);
                break ;
            }

            potentials.addAll(n.generateChildren(forbiddenList, visited));

            if(potentials.size() == 0){
                break;
            }

            for(Node child : potentials){

                child.setHeuristic(heuristicCalculator(child, end));

                boolean valid = true;

                if(child.getHeuristic() < n.getHeuristic()){

                    for(Node v : visited){
                        if(child.compareTo(v) && child.getChanged() == v.getChanged()){
                            valid = false;
                        }
                    }

                    if(valid){
                        n = child ;
                        changed = true ;
                    }

                }

                if(child.getHeuristic() == n.getHeuristic()){
                    if(!child.getAlternative()) {
                        for (Node v : visited) {
                            if (child.compareTo(v) && child.getChanged() == v.getChanged()) {
                                valid = false;
                            }
                        }

                        if (valid) {
                            n.setAlternative(true);
                            n = child;
                            changed = true;
                        }

                        }

                }

            }

            if(!changed){
                changedList.add(false);
                break ;
            }

            potentials.remove(n);

        }

        changedList.add(true);

        return visited;

    }


    public static List<Node> greedy(Node start, Node end, List<Node> forbiddenList){

        List<Node> visited = new ArrayList<>();

        Node n = start ;

        List<Node> potentials = new ArrayList<>();

        while(true){

            visited.add(n);

            if(n.compareTo(end)){
                break ;
            }

            potentials.addAll(n.generateChildren(forbiddenList, visited));

            if(potentials.size() == 0){
                break;
            }

            int i = 0;

            Node chosen ;

            while(true){

                chosen = potentials.get(i);

                boolean valid = true;

                for(Node v : visited){

                    if(v.compareTo(chosen) && chosen.getChanged() == v.getChanged()){
                        valid = false;
                        i++ ;
                        break ;
                    }
                }

                if(i > potentials.size() || valid){
                    break ;
                }

            }

            if(chosen == null){
                break ;
            }

            chosen.setHeuristic(heuristicCalculator(chosen,end));

            for(Node child : potentials){

                child.setHeuristic(heuristicCalculator(child, end));

                if(child.getHeuristic() <=  chosen.getHeuristic()){
                    chosen = child ;
                }

            }

            potentials.remove(chosen);

            n = chosen ;

        }

        return visited;

    }



    public static List<Node> aStar(Node start, Node end, List<Node> forbiddenList){

        List<Node> visited = new ArrayList<>();

        Node n = start ;


        List<Node> potentials = new ArrayList<>();

        while(true){

            visited.add(n);

            if(n.compareTo(end)){
                break ;
            }

            potentials.addAll(n.generateChildren(forbiddenList, visited));


            if(potentials.size() == 0){
                break ;
            }


            Node chosen ;

            int j = 0 ;

            while(true){

                chosen = potentials.get(j);

                boolean valid = true;

                for(Node v : visited){

                    if(v.compareTo(chosen) && chosen.getChanged() == v.getChanged()){
                        valid = false;
                        j++ ;
                        break ;
                    }
                }

                if(j > potentials.size() || valid){
                    break ;
                }

            }

            if(chosen == null){
                break ;
            }

            chosen.setHeuristic(heuristicCalculator(chosen,end));

            for(Node child : potentials){

                child.setHeuristic(heuristicCalculator(child, end));

                if(child.getHeuristic() + costSoFar(child) <=  chosen.getHeuristic() + costSoFar(chosen)){
                    chosen = child ;
                }

            }

            potentials.remove(chosen);

            n = chosen ;

        }

        return visited;

    }

    public static List<Node> DFS(Node start, Node end, List<Node> forbiddenList, int depthLimit){

        List<Node> visited = new ArrayList<>();

        Stack<Node> stack = new Stack<>();

        stack.add(start);

        start.setDepth(0);

        while(!stack.empty()){

            Node current = stack.pop();

            boolean reTry = false;

            for(Node k : visited){
                if(current.compareTo(k) && current.getChanged() == k.getChanged()){
                    reTry = true;
                }
            }

            if(reTry){
                continue;
            }

            visited.add(current);


            if(current.compareTo(end)){
                return visited;
            }

            if(current.getDepth() == depthLimit){
                continue;
            }

            else{

                List<Node> children = current.generateChildren(forbiddenList, visited);

                List<Node> orderedChildren = new ArrayList<>();

                for(int i = children.size() - 1 ; i > -1 ; i --){
                    orderedChildren.add(children.get(i));
                }


                for(Node n : orderedChildren){
                    n.setDepth(current.getDepth() + 1);
                    stack.push(n);
                }

            }

            if(visited.size() >= 1000){
                break ;
            }

        }

        return visited ;


    }


    public static List<Node> IDS(Node start, Node end, List<Node> forbiddenList){

        List<Node> visited = new ArrayList<>();

        int depth = 0;

        boolean finished = false;

        while(!finished){

            visited.addAll(DFS(start,end,forbiddenList,depth));

            for(Node n : visited){
                if(n.compareTo(end)){
                    finished = true ;
                }
            }

            depth ++ ;

            if(visited.size() >= 1000){
                break ;
            }

        }


        return visited;

    }


    public static void printResult(List<Node> visited){

        List<Node> path = generatePath(visited);

        int limit = visited.size();

        if(visited.size() >= 1000){
            System.out.println("No solution found.");
            limit = 1000 ;
        }

        else {
            for (int i = 0; i < path.size(); i++) {

                if (i == path.size() - 1) {

                    System.out.println("" + path.get(i).getNumber_1() + path.get(i).getNumber_2() + path.get(i).getNumber_3());
                } else {
                    System.out.print("" + path.get(i).getNumber_1() + path.get(i).getNumber_2() + path.get(i).getNumber_3() + ",");
                }

            }
        }


        for(int q = 0 ; q < limit ; q ++){

            if(q == limit - 1){

                System.out.println("" + visited.get(q).getNumber_1() + visited.get(q).getNumber_2() + visited.get(q).getNumber_3());
            }

            else {
                System.out.print("" + visited.get(q).getNumber_1() + visited.get(q).getNumber_2() + visited.get(q).getNumber_3() + ",");
            }
        }

    }



    public static List<Node> BFS(Node start, Node end, List<Node> forbiddenList){

        List<Node> visited = new ArrayList<>();
        LinkedList<Node> queue = new LinkedList<>();
        List<Node> expanded = new ArrayList<>();

        queue.add(start) ;

        expanded.add(start);

        while(!queue.isEmpty()){

            boolean valid = true;

            Node n = queue.pop();

            for(Node v : visited){
                if(n.compareTo(v) && n.getChanged() == v.getChanged()){
                    valid = false;
                }
            }

            if(!valid){
                continue;
            }

            visited.add(n);

            if(n.compareTo(end)){
                break ;
            }

            List<Node> nChildren = n.generateChildren(forbiddenList, visited);

            expanded.addAll(nChildren);

            queue.addAll(nChildren);

            if(visited.size() >= 1000){
                break ;
            }

        }

        return visited ;

    }


    public static void main(String args[]){

        File file = new File(args[1]);

        Scanner sc ;

        try{
            sc = new Scanner(file);
        }

        catch(FileNotFoundException E){
            System.out.println("file not found");
            return ;
        }

        String start = sc.nextLine();
        List<Integer> startInts = breakDown(start);

        String end = sc.nextLine();
        List<Integer> endInts = breakDown(end);

        List<List<Integer>> forbiddenList = new ArrayList<>();


        if(sc.hasNextLine()) {
            String forbidden = sc.nextLine();
            String[] forbids = forbidden.split(",");


            for (String s : forbids) {
                List<Integer> f = new ArrayList<>();
                f = breakDown(s);
                forbiddenList.add(f);
            }

        }

        Node startNode = new Node(startInts.get(0), startInts.get(1), startInts.get(2), 0, null);

        Node endNode = new Node(endInts.get(0), endInts.get(1), endInts.get(2), 0, null);

        List<Node> forbiddenNodes = new ArrayList<>();

        for(List<Integer> forbiddenNumbers : forbiddenList){
            Node toAdd = new Node(forbiddenNumbers.get(0), forbiddenNumbers.get(1), forbiddenNumbers.get(2), 0, null);
            forbiddenNodes.add(toAdd);
        }




        if(args[0].equals( "B")){

            List<Node> visited = BFS(startNode, endNode, forbiddenNodes);

            printResult(visited);



        }


        else if(args[0].equals( "D")){

            List<Node> visited = DFS(startNode, endNode, forbiddenNodes, 1000);

            printResult(visited);

        }

        else if(args[0].equals( "I")){

            List<Node> visited = IDS(startNode, endNode, forbiddenNodes);

            printResult(visited);

        }

        else if(args[0].equals( "G")){

            List<Node> visited = greedy(startNode, endNode, forbiddenNodes);

            printResult(visited);

        }

        else if(args[0].equals( "A")){

            List<Node> visited = aStar(startNode, endNode, forbiddenNodes);

            printResult(visited);

        }

        else if(args[0].equals("H")){

            List<Boolean> changed = new ArrayList<>();

            List<Node> visited = hillSearch(startNode,endNode,forbiddenNodes, changed);

            List<Node> path = generatePath(visited);

            if(visited.size() >= 1000 || !changed.get(0)){
                System.out.println("No solution found.");
            }

            else {
                for (int i = 0; i < path.size(); i++) {

                    if (i == path.size() - 1) {

                        System.out.println("" + path.get(i).getNumber_1() + path.get(i).getNumber_2() + path.get(i).getNumber_3());
                    } else {
                        System.out.print("" + path.get(i).getNumber_1() + path.get(i).getNumber_2() + path.get(i).getNumber_3() + ",");
                    }

                }
            }


            for(int q = 0 ; q < visited.size() ; q ++){

                if(q == visited.size() - 1){

                    System.out.println("" + visited.get(q).getNumber_1() + visited.get(q).getNumber_2() + visited.get(q).getNumber_3());
                }

                else {
                    System.out.print("" + visited.get(q).getNumber_1() + visited.get(q).getNumber_2() + visited.get(q).getNumber_3() + ",");
                }
            }

        }




    }
}
