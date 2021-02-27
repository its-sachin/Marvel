import java.io.*;
import java.util.HashMap;




public class Storylines{
	public static void main(String[] args) {

		Graph marvel = new Graph();
		char dQuote = 34;
		String nodeFile = args[0];
		String edgeFile = args[1];
		String function = args[2];

    	try {

    		FileReader csvNode = new FileReader(nodeFile);
			String line;

    		try {
    			BufferedReader readN = new BufferedReader(csvNode);
    			line = readN.readLine();
    			String vertex;
    			while((line = readN.readLine()) != null) {
    				if (line.charAt(0) == dQuote) {
    					String[] splitted = line.split("\"");

    					if (splitted.length > 3) {
    						vertex = splitted[3];
    					}

    					else {
    						String[] split2 = splitted[2].split(",");
    						vertex = split2[1];
    					}
    				}
    				else {

    					String[] splitted = line.split(",");

    					if (splitted[1].charAt(0) == dQuote) {
    						vertex = splitted[1].split("\"")[1];
    					}
    					else {
    						vertex = splitted[1];
    					}
    				}
    				marvel.addVertex(vertex);
    				
    			}
    		}

    		catch (IOException e) {
	            System.out.println("An error occurred.");
	            e.printStackTrace();
	        }

	        FileReader csvEdge = new FileReader(edgeFile);

    		try {
    			BufferedReader readE = new BufferedReader(csvEdge);
    			line = readE.readLine();
    			String from;
    			String to;
    			int weight;

    			while((line = readE.readLine()) != null) {

    				if (line.charAt(0) == dQuote) {
    					String[] splitted = line.split("\"");
    					from = splitted[1];

    					if (splitted.length > 3) {
    						to = splitted[3];
    						weight = Integer.parseInt(splitted[4].split(",")[1]);
    					}

    					else {
    						String[] split2 = splitted[2].split(",");
    						to = split2[1];
    						weight = Integer.parseInt(split2[2]);
    					}
    				}
    				else {

    					String[] splitted = line.split(",");
    					from = splitted[0];

    					int n = splitted.length - 1;

    					if (splitted[1].charAt(0) == dQuote) {

    						to = splitted[1].split("\"")[1];
    						int k = 2;

    						while (k < n) {
    							to = to + "," + splitted[k].split("\"")[0];
    							k += 1;

    						}
    						weight = Integer.parseInt(splitted[n]);

    					}
    					else {
    						to = splitted[1];
    						weight = Integer.parseInt(splitted[2]);

    					}
    				}

    				// System.out.println(from + " " + to + " " + weight + " ");
    				marvel.addEdge(from ,to , weight);

    			}
    		}

    		catch (IOException e) {
	            System.out.println("An error occurred.");
	            e.printStackTrace();
	        }



    	}

    	catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        if (function.compareTo("average") == 0) {
        	String toStr = String.format("%.2f", marvel.average());
        	System.out.println(toStr);

        }
        else if (function.compareTo("rank") == 0) {
        	marvel.rank();
        }
        
        else if (function.compareTo("independent_storylines_dfs") == 0) {
        	marvel.independent_storylines_dfs();
        }

	}


}


class Graph{
	private int numVertex = 0;

	HashMap<String, HashMap<String, Integer>> adjList = new HashMap<>();
	HashMap<String, int[]> occurCount = new HashMap<>();

	public double average() {
		int total = 0;
		int i = 0;
		for (HashMap.Entry<String, int[]> entry: occurCount.entrySet()){
  
	        // System.out.println(entry.getKey() + " " + entry.getValue()[0] + " " + entry.getValue()[1]);
	        total += entry.getValue()[0];
	        i += 1;
		} 

		return (double)total/i;
	}

	public void addVertex(String name) {
		if (adjList.containsKey(name) == false){
			HashMap<String, Integer> edgeList = new HashMap<>();
			adjList.put(name, edgeList);
			int[] occur = new int[3];
			occurCount.put(name, occur);
			numVertex += 1;
		}
	}

	public void addEdge(String from, String to, int weight) {
		addEdgeOne(from, to, weight);
		addEdgeOne(to, from, weight);
	}

	private void addEdgeOne(String from, String to, int weight) {

		if (adjList.containsKey(from) && adjList.containsKey(to)) {

			HashMap<String, Integer> edgeList = adjList.get(from);

			if (edgeList.containsKey(to)) {

				int temp = edgeList.get(to);
				edgeList.replace(to,temp + weight);
				
			}

			else {

				edgeList.put(to, weight);
				occurCount.get(from)[0] = occurCount.get(from)[0] + 1;
			}
			
			// occurCount.replace(from,newWeight);
			int oldWeight = occurCount.get(from)[1];
			int newWeight = oldWeight + weight;
			occurCount.get(from)[1] = newWeight;
		}
	}

	public int numVertex() {
		return numVertex;
	}
	

	public void rank() {
		Node[] array = new Node[numVertex];
		int i = 0;
		for (HashMap.Entry<String, int[]> entry: occurCount.entrySet()){ 
	        array[i] = new Node(entry.getKey(), entry.getValue()[1]);
	        i += 1;
		}
		mSort(array);

		for (i = 0; i < numVertex-1; i++) {
			System.out.print(array[i].name+  ",");
		}
		System.out.print(array[i].name + "\n");
	}

	//  main function that merge sort the given array
	private void mSort(Node[] array) {
		mSortRec(array, 0, array.length - 1);
	}

	// recursively calls merge sort
	private void mSortRec(Node[] array, int left, int right) {
		if (left < right) {
			int mid = (left + right)/2;
			mSortRec(array, left, mid);
			mSortRec(array, mid+1, right);
			merge(array, left, mid, right);
		}
	}

	// merge function that takes an array and three indices (given that a[left....mid] and a[mid+1...right] are sorted) and sort a[left...right]
	private void merge(Node[] array, int left, int mid, int right) {
		int i = left;
		int j = mid + 1;
		Node[] leftArray = new Node[mid - left + 1];
		Node[] rightArray = new Node[right - mid];

		while (i <= mid) {
			leftArray[i-left] = array[i];
			i += 1;
		}

		while (j <= right) {
			rightArray[j-(mid+1)] = array[j];
			j += 1;
		}

		i = 0;
		j = 0;
		int k = left;
		while (i < leftArray.length && j < rightArray.length) {
			// System.out.println(leftArray[i].name + " " + rightArray[j]);
			if (leftArray[i].weight > rightArray[j].weight || (leftArray[i].weight == rightArray[j].weight && leftArray[i].name.compareTo(rightArray[j].name) > 0)) {
				array[k] = leftArray[i];
				i += 1;
			}
			else {
				array[k] = rightArray[j];
				j += 1;
			}
			k += 1;
		}

		while (i < leftArray.length) {
			array[k] = leftArray[i];
			i += 1;
			k += 1;
		}

		while (j < rightArray.length) {
			array[k] = rightArray[j];
			j += 1;
			k += 1;
		}
	}

	public void independent_storylines_dfs() {

		HashMap<String, HashMap<String, Integer>> connectedComp = new HashMap<>();

		for (HashMap.Entry<String, int[]> entry: occurCount.entrySet()){ 
	        entry.getValue()[2] = 0;
		}

		for (HashMap.Entry<String, int[]> entry: occurCount.entrySet()){
			if (entry.getValue()[2] == 0) {
				HashMap<String, Integer> currComp = new HashMap<>();
				String source = entry.getKey();
				currComp.put(source, 0);
				String lowest = dfs(source, currComp);
				connectedComp.put(lowest, currComp);
			}
		}

		int n = connectedComp.size();
		int k = 0;

		Node[] interArr = new Node[n];
		for (HashMap.Entry<String, HashMap<String, Integer>> entry: connectedComp.entrySet()){ 
	        interArr[k] = new Node(entry.getKey(), entry.getValue().size());
	        k+= 1;
		}
		mSort(interArr);

		for (int i = 0; i < n; i++) {
			// System.out.println(interArr[i].name + " ----CHECK------");
			HashMap<String, Integer> currComp = connectedComp.get(interArr[i].name);
			int n1 = currComp.size();

			Node[] intraArr = new Node[n1];
			k = 0;
			for (String entry: currComp.keySet()){ 
		        intraArr[k] = new Node(entry, 0);
		        k += 1;
			}
			mSort(intraArr);

			for (int j = 0; j < n1-1; j++) {
				System.out.print(intraArr[j].name + ",");
			}
			System.out.print(intraArr[n1-1].name + "\n");
		}


	}
	// consider case when no element is there then also dfs is called.

	private String dfs(String curr, HashMap<String, Integer> currComp) {
		HashMap<String, Integer> edgeList = adjList.get(curr);

		String lowest = curr;

		for (String now: edgeList.keySet()){ 
			if (occurCount.get(now)[2] == 0) {
				currComp.put(now, 0);
				if (lowest.compareTo(now) < 0) {
					lowest = now;
				}
				occurCount.get(now)[2] = 1;
				String l2 = dfs(now, currComp);
				if (lowest.compareTo(l2) < 0) {
					lowest = l2;
				}
			}
		}

		return lowest;

	}
}

class Node{
	int weight;
	String name;
	public Node(String name, int n) {
		this.name = name;
		this.weight = n;
	}
}