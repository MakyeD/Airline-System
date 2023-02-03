import java.util.*;
import java.io.*;

final public class AirlineSystem implements AirlineInterface {
	
	  private String [] cityNames = null;
	  private Digraph G = null;
	  private static Scanner scan = null;
	  private static final int INFINITY = Integer.MAX_VALUE;

  public boolean loadRoutes(String fileName){
	  
	try {
		
		Scanner input = new Scanner(new FileInputStream(fileName));
		
		int num = Integer.parseInt(input.nextLine());
		
		G = new Digraph(num);
		
		int i = 0;
		
		cityNames = new String[num];
		
		while (i < num) {					//Fill out cityNames array
			
			cityNames[i] = input.nextLine();
			i++;
		}
		
		while (input.hasNext()) {
			
			String[] arr = input.nextLine().split(" ");
			Integer I1 = Integer.valueOf(arr[0]);
			Integer I2 = Integer.valueOf(arr[1]);
			Integer I3 = Integer.valueOf(arr[2]);
			Double I4 = Double.valueOf(arr[3]);
			
			G.addEdge(new WeightedUndirectedEdge(I1-1, I2-1, I3, I4));		//Add edge going both ways
			G.addEdge(new WeightedUndirectedEdge(I2-1, I1-1, I3, I4));
			
		}
		
		input.close();
		return true;
	
	} catch (FileNotFoundException e) {
		
		return false;
	}
	  
  }

  public Set<String> retrieveCityNames() {
    Set <String> citNames = new HashSet<String>();
    
    int i = 0;
    
    while(i < cityNames.length) {
    	
    	citNames.add(cityNames[i]);
    	i++;
    }
    
    return citNames;
  }

  public Set<Route> retrieveDirectRoutesFrom(String city)
    throws CityNotFoundException {
	  
	  Route route;
	  Set<Route> routes = new HashSet<Route>();
	  
	  int from = 0;
	  
	  for (int i = 0; i < cityNames.length; i++) {
		  
		  if(city.equals(cityNames[i])) {		//Find the city and index
			  
			  from = i;

		  }
	  }
	  
	  for(int i = 0; i < G.adj[from].size(); i++) {		//Iterate thru adj[] array and add values to Set
	  
		  route = new Route(city, cityNames[G.adj[from].get(i).to()], G.adj[from].get(i).weight, G.adj[from].get(i).price());
		  routes.add(route);
	  }
	 
    return routes;
  }

  public Set<ArrayList<String>> fewestStopsItinerary(String source,
    String destination) throws CityNotFoundException {
	  
	  try {
	  
		  int from = -1;
		  int to =-1;
	  
		  Set<ArrayList<String>> set = new HashSet<ArrayList<String>>();
	  
		  for(int i = 0; i < cityNames.length; i++) {		//Find source and destination indexes
		  
			  if (cityNames[i].equals(source)){
			  
				  from = i;
			  }
		  
			  if(cityNames[i].equals(destination)) {
			  
				  to = i;
			  }
		  
		  }
	  
		  G.bfs(from); 								//Run bfs on current digraph
	  
		  Stack<Integer> trace = new Stack<>();
	  
		  for(int i = to; i != from; i = G.edgeTo[i]) {  //Push the verticies on to the stack
		  
			  trace.push(i);
		  }
	  
		  trace.push(from);
	  
		  ArrayList<String> holder = new ArrayList<String>();
	  
		  while(!trace.empty()) {
		  
			  int index = trace.pop();
			  holder.add(cityNames[index]);            //Fill out arraylist with city names
		  }
	  
		  set.add(holder);
	  
		  return set;
  }catch (Exception e) {
	  
	  Set<ArrayList<String>> newSet = new HashSet<ArrayList<String>>();
	  return newSet;
  }
  }

  public Set<ArrayList<Route>> shortestDistanceItinerary(String source,
    String destination) throws CityNotFoundException {
	  
	  try {
	  
		  int from = -1;
		  int to =-1;
	  
		  Set<ArrayList<Route>> set = new HashSet<ArrayList<Route>>();
		  ArrayList<Route> arr = new ArrayList<Route>();
	  
		  for(int i = 0; i < cityNames.length; i++) {		//Find source and destination indexes
		  
		 	if (cityNames[i].equals(source)){
			  
		 		from = i;
		 	}
		  
		 	if(cityNames[i].equals(destination)) {
			  
		 		to = i;
		 	}
		 	
		  }
		  
		  
		  if(from == -1) {
			  if(to == -1) {
				  
				  return set;
			  }
		  }

		  G.dijkstras(from, to);			//Run dijkstras
	  
		  Stack<Integer> trace = new Stack<>();
	  
		  for(int i = to; i != from; i = G.edgeTo[i]) {
		  
			  trace.push(i);
		  }
	  
	  
		  int prevVertex = from;
		  while(!trace.empty()) {
		  
			  int index = trace.pop();
			  arr.add(new Route(cityNames[prevVertex], cityNames[index],
				G.distTo[index] - G.distTo[prevVertex], 
				G.priceTo[index] - G.priceTo[prevVertex]));  //Create new route
			  prevVertex = index;
		  }
	  
		  set.add(arr);
	  
		  return set;
  }catch (Exception e) {
	
	  Set<ArrayList<Route>> newSet = new HashSet<ArrayList<Route>>();
	  return newSet;
  }
  }

  public Set<ArrayList<Route>> shortestDistanceItinerary(String source,
    String transit, String destination) throws CityNotFoundException {
	
	try {
	  
		int from = -1;
		int to = -1; 
	
		Set<ArrayList<Route>> set = new HashSet<ArrayList<Route>>();
		ArrayList<Route> arr = new ArrayList<Route>();
		ArrayList<Route> arr2 = new ArrayList<Route>();
	
		for(int i = 0; i <cityNames.length; i++) { //Find source and transit indexes
		
			if(cityNames[i].equals(source)) {
			
				from = i;
			}
		
			if(cityNames[i].equals(transit)) {
			
				to = i;
			}
		}
		
			G.dijkstras(from, to);  //Run dijkstras for shortest distance
		
			Stack<Integer> trace = new Stack<>();
		
			for(int k = from; k != from; k = G.edgeTo[k]) {
			
				trace.push(k);
			}
		
			int prevVertex = from;
		
			while(!trace.empty()) {
			
				int v = trace.pop();
				arr.add(new Route(cityNames[prevVertex], cityNames[v], 
						G.distTo[v]-G.distTo[prevVertex], 
						G.priceTo[v] + G.priceTo[prevVertex]));			
				prevVertex = v;
			}
		
			set.add(arr);   
		
			for(int i = 0; i <cityNames.length; i++) {
			
				if(cityNames[i].equals(transit)) {
				
					from = i;
				}
			
				if(cityNames[i].equals(destination)) {
				
					to = i;
				}
		
			}
		
			prevVertex = from;
		
			while(!trace.empty()) {     
			
				int v = trace.pop();
				arr.add(new Route(cityNames[prevVertex], 
						cityNames[v], G.distTo[v]-G.distTo[prevVertex], 
						G.priceTo[v] + G.priceTo[prevVertex]));
				prevVertex = v;
			}
		
			set.add(arr);
		
			G.dijkstras(from, to);  //Find shortest dist from transit to destination
		

			for(int k = from; k != from; k = G.edgeTo[k]) {
			
				trace.push(k);
		}
			
			 prevVertex = from;
				
				while(!trace.empty()) {
				
					int v = trace.pop();
					arr2.add(new Route(cityNames[prevVertex], 
							cityNames[v], G.distTo[v]-G.distTo[prevVertex], 
							G.priceTo[v] + G.priceTo[prevVertex]));
					prevVertex = v;
				}
				
				set.add(arr2);		//Add second route to set
	  
    return set;  
    
	}catch(Exception e) {
		
		Set<ArrayList<Route>> newSet = new HashSet<ArrayList<Route>>();
		 return newSet;
	}
  }

  public boolean addCity(String city){
	  
	 try {
	  
	  for(int i = 0; i < cityNames.length; i++) {
		 
		 if(cityNames[i].equals(city)) {
			 
			 return false;
		 }
	 }
	  
	  
	 String[] cityHolder = new String[cityNames.length + 1]; //Increment array size
	  
	 for(int i = 0; i < cityNames.length; i ++) {
		
		cityHolder[i] = cityNames[i];
	}
	  
	  cityHolder[cityHolder.length - 1] = city;			//Create new city at end of array
	  
	  cityNames = cityHolder;
	  
	  Digraph temp = new Digraph(cityNames.length); //Create and fill out new digraph with original values
	  
	  for(int i = 0; i < cityNames.length - 1;  i++) {
		  
		  Iterable<WeightedUndirectedEdge> it = G.adj(i); 
	  
	  for (WeightedUndirectedEdge e : it) {
	    	
		  temp.addEdge(new WeightedUndirectedEdge(e.v, e.w, e.weight, e.price)); 
		  
		  temp.addEdge(new WeightedUndirectedEdge(e.w, e.v,  e.weight, e.price));
	  	}
	  }
	  
	  G = temp;
	  
	  return true;
	  
	 }catch(Exception e) {
		 
		 return false;
	 }
	  
  }

  public boolean addRoute(String source, String destination, int distance,
    double price) throws CityNotFoundException {
	  
	 try {
		  
		  
		 int from = -1;
		 int to = -1;
		 int stem = -1;
		 
		 for(int i = 0; i < cityNames.length; i++) {
			 
			 if(cityNames[i].equals(source)) {
				 
				 from = i;
			 }
			 
			 if(cityNames[i].equals(destination)) {
				 
				 to = i;
			 }
			 
			 
		 }
		 
		 for(int i = 0; i < cityNames.length; i ++) {
			 
			 if(cityNames[i].equals(source)) {
			
				 
				 stem = i;
			 }
		 }
		 
		 for(int i = 0; i < G.adj[stem].size(); i++) {
			 
			 
			Route tempRoute = new Route(source, 
					cityNames[G.adj[stem].get(i).to()],
					G.adj[stem].get(i).weight(), 
					G.adj[stem].get(i).price());    
			
			if(tempRoute.source.toString().equals(source) && 
			tempRoute.destination.toString().equals(destination)) {  //Check if route exists and return false if true
				
				return false;
			}
			 
		 }
		 
		 G.addEdge(new WeightedUndirectedEdge(from, to, distance, price));  //Add edge going both ways
		 G.addEdge(new WeightedUndirectedEdge(to, from, distance, price));
		  
	  }catch(Exception e) {
		  
		 return false; 
	  }
	  
	  
    return true;
  }

  public boolean updateRoute(String source, String destination, int distance,
    double price) throws CityNotFoundException {
	  
	 try {
		  
		  
		  boolean check = false;
		  int from = -1;
		  int to = -1;
		  
		  for(int i = 0; i <cityNames.length; i++) {
			  
			  if(cityNames[i].equals(source)) {
				  
				from = i;  
			  }
			  
			  if(cityNames[i].equals(destination)) {
				  
				  to = i;
			  }
		  }
		  
		 Route temp;
		 
		 Iterable<WeightedUndirectedEdge> fromInd = G.adj(from);
		 Iterable<WeightedUndirectedEdge> toInd = G.adj(to);
	
		 
		 for (WeightedUndirectedEdge e : fromInd) {  //Iterate thru until destination is found
			 
			 temp = new Route(source, cityNames[e.to()], e.weight(), e.price); 
			 if(temp.destination.equals(destination)) {
				 
				 check = true;
				 G.removeEdge(e);  //Delete edge (route)
				 break;
			 }
		 }
		 
		 if(check == false) {
			 
			 return false;
		 }
		 
		 check = false;
		 
		 for(WeightedUndirectedEdge e : toInd) { //Delete edge going the opposite way
			 
			 temp = new Route(source, cityNames[e.to()], e.weight(), e.price());
			 if(temp.destination.equals(source)) {
				 
				 check = true;
				 G.removeEdge(e); 
				 break;
			 }
		 }
		 
		 if(check == false) {
			 
			 return false;
		 }
		 
		 
		 G.addEdge(new WeightedUndirectedEdge(from, to, distance, price)); //Add edges going both ways
		 G.addEdge(new WeightedUndirectedEdge(to, from, distance, price));
		 
		 return true;
		  
	  }catch(Exception e) {
		  
		  return false;
		  
	  }
	  
  }
  
  /**
  *  The <tt>Digraph</tt> class represents an directed graph of vertices
  *  named 0 through v-1. It supports the following operations: add an edge to
  *  the graph, iterate over all of edges leaving a vertex.Self-loops are
  *  permitted.
  */
  private class Digraph {
    private final int v;
    private int e;
    private LinkedList<WeightedUndirectedEdge>[] adj;
    private boolean[] marked;  // marked[v] = is there an s-v path
    private int[] edgeTo;      // edgeTo[v] = previous edge on shortest s-v path
    private int[] distTo;      // distTo[v] = number of edges shortest s-v path
    private double[] priceTo;  //flight price

    /**
    * Create an empty digraph with v vertices.
    */
    public Digraph(int v) {
      if (v < 0) throw new RuntimeException("Number of vertices must be nonnegative");
      this.v = v;
      this.e = 0;
      @SuppressWarnings("unchecked")
      LinkedList<WeightedUndirectedEdge>[] temp =
      (LinkedList<WeightedUndirectedEdge>[]) new LinkedList[v];
      adj = temp;
      for (int i = 0; i < v; i++)
        adj[i] = new LinkedList<WeightedUndirectedEdge>();
    }

    /**
    * Add the edge e to this digraph.
    */
    public void addEdge(WeightedUndirectedEdge edge) {
      int from = edge.from();
      adj[from].add(edge);
      e++;
    }
    
    public void removeEdge(WeightedUndirectedEdge edge) {
    	
       int from = edge.from();
       adj[from].remove(edge);
       e--;
    }


    /**
    * Return the edges leaving vertex v as an Iterable.
    * To iterate over the edges leaving vertex v, use foreach notation:
    * <tt>for (WeightedDirectedEdge e : graph.adj(v))</tt>.
    */
    public Iterable<WeightedUndirectedEdge> adj(int v) {
      return adj[v];
    }

  
  public void bfs(int source) {
      marked = new boolean[this.v];
      distTo = new int[this.e];
      edgeTo = new int[this.v];

      Queue<Integer> q = new LinkedList<Integer>();
      for (int i = 0; i < v; i++){
        distTo[i] = INFINITY;
        marked[i] = false;
      }
      distTo[source] = 0;
      marked[source] = true;
      q.add(source);

      while (!q.isEmpty()) {
        int v = q.remove();
        for (WeightedUndirectedEdge w : adj(v)) {
          if (!marked[w.to()]) {
            edgeTo[w.to()] = v;
            distTo[w.to()] = distTo[v] + 1;
            marked[w.to()] = true;
            q.add(w.to());
          }
        }
      }
    }

    public void dijkstras(int source, int destination) {
      marked = new boolean[this.v];
      distTo = new int[this.v];
      edgeTo = new int[this.v];
      priceTo = new double[this.v];


      for (int i = 0; i < v; i++){
        distTo[i] = INFINITY;
        marked[i] = false;
        priceTo[i] = INFINITY;
        
      }
      distTo[source] = 0;
      marked[source] = true;
      priceTo[source] = 0;
      int nMarked = 1;

      int current = source;
      while (nMarked < this.v) {
        for (WeightedUndirectedEdge w : adj(current)) {
          if (distTo[current]+w.weight() < priceTo[w.to()]) {
        	
        	edgeTo[w.to()] = current;
  	      	distTo[w.to()] = distTo[current] + w.weight();
  	      	priceTo[w.to()] = priceTo[current] + w.price();
          }
        }
        //Find the vertex with minimim path distance
        //This can be done more effiently using a priority queue!
        int min = INFINITY;
        current = -1;
  
        for(int i=0; i<distTo.length; i++){
          if(marked[i])
            continue;
          if(distTo[i] < min){
            min = distTo[i];
            current = i;
          }
        }
        
        if(current != -1 && distTo[current] != INFINITY) {
      	  
      	  marked[current] = true;
      	  nMarked++;
        
        }else {
      	  
      	  break;
        }
      }
    }

  }
  

  /**
  *  The <tt>WeightedUndirectedEdge</tt> class represents a weighted edge in an directed graph.
  */
    
    public class WeightedUndirectedEdge {
        private final int v;
        private final int w;
        private int weight;
        private double price;
        /**
        * Create a directed edge from v to w with given weight.
        */
        public WeightedUndirectedEdge(int v, int w, int weight, double price) {
          this.v = v;
          this.w = w;
          this.weight = weight;
          this.price = price;
        }

        public int from(){
          return v;
        }

        public int to(){
          return w;
        }

        public int weight(){
          return weight;
        }
        
        public double price(){
        	return price; 
        }
        
        public void setWeight(int weight) {
        
        	this.weight = weight;
        }
        
        public void setPrice(double price) {
        	
        	this.price = price;
        }
      }
  

}
