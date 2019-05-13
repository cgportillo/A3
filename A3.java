import java.util.*;
import java.io.*;

public class A3 {
		
    public static void main(String[] args) {
    	
        A3 start = new A3();
        String inputFile = "tmdb_5000_credits.csv";
        start.readFile(inputFile);
    }
        
    public void readFile(String inputFile) {
    	
    	HashMap<String, HashSet<String>> graph = new HashMap<String, HashSet<String>>();
    	
    	try {
    		
    		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
    		Scanner user = new Scanner(System.in);
            String line;
            line = reader.readLine();
            String cast;
            String name = "";
            String startindex = "";
            HashSet<String> valuenameset = null;
            String keyname = "";
            
            // read in each "movie line" at a time
            while ((line = reader.readLine()) != null) {
            	
            	// check to see if whatever's inside the square brackets inside file is null
                if (line.charAt(line.indexOf("[") + 1) == ']') {
                    continue;
                }
                
                // if not, get inside the brackets and remove double quotes
                else {
                    cast = line.substring(line.indexOf(",\"[") + 2, line.indexOf("]\",") + 1);
                    cast = cast.replaceAll("\"\"", "\"");                
                }
                
                // split lines up in string array, convert to arraylist, initialize hashset
                String[] linenames = cast.split("},");
                List<String> movielinenames = new ArrayList<String>();
                movielinenames = Arrays.asList(linenames);
                
                // for however many names there are in the movie line
                for (int j = 0; j < movielinenames.size(); j++) {
                	
                	// get specific names
                    startindex = movielinenames.get(j);
                    int namesindex = (startindex.indexOf("name")) + 8;
                    int endindex = (startindex.indexOf(",", namesindex)) - 1;
                    
                    // if there are true indices for start and end of a name
                    if (namesindex > -1 && endindex > -1) {
                    	
                    	// create name
                    	name = startindex.substring(namesindex, endindex);
                    	keyname = startindex.substring(namesindex, endindex);
                    	
                    	if (graph.containsKey(keyname) == false) {
                    		
                    		// create new set for new name
                    		valuenameset = new HashSet<String>();
                    		
                    		// get all names in that movie and add them to person's set
                    		for (int x = 0; x < movielinenames.size(); x++) {
                    		
                    			// grab xth term in the line which is already split, computes the beginning and last indices of the name
                    			startindex = movielinenames.get(x);
                                namesindex = (startindex.indexOf("name")) + 8;
                                endindex = (startindex.indexOf(",", namesindex)) - 1;
                                
                                // if there are true indices for start and end of a name
                                if (namesindex > -1 && endindex > -1) {
                                	name = startindex.substring(namesindex, endindex);
	                    			valuenameset.add(name);
	                    			graph.put(keyname, valuenameset);
                                }
                    		}
                    	}
                    	
                    	else {
                    		// retrieve existing set of names of the specific actor in the line we are reading in
                    		HashSet<String> existingvalues = graph.get(keyname);
                    		
                    		// if name existed already, get every single person in that new movie, add to their own set they've worked with
                    		// starting from the beginning of the line where which we read in
                    		for (int l = 0; l < movielinenames.size(); l++) {
                    			
                    			startindex = movielinenames.get(l);
                                namesindex = (startindex.indexOf("name")) + 8;
                                endindex = (startindex.indexOf(",", namesindex)) - 1;
                               
                                if (namesindex > -1 && endindex > -1) {
	                            	name = startindex.substring(namesindex, endindex);
	                    			existingvalues.add(name);
	    	                        graph.put(keyname, existingvalues);
                                }
                    		}
                    	}
                    }
                }
            }
          
            // prompt for names of actors to find path
            System.out.println("Actor 1: ");
            String actor1 = user.nextLine();
            System.out.println("Actor 2: ");
            String actor2 = user.nextLine();
            
            if(graph.containsKey(actor1) == false || graph.containsKey(actor2) == false) {
            	System.out.println("One or both of the actors you want to find do not exist in this file.");
            }
            else {
            	bfs(graph, actor1, actor2);
            }
    	}
    	
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void bfs(HashMap<String, HashSet<String>> graph, String actor1, String actor2) {

    	ArrayList<String> visited = new ArrayList<String>();
    	Queue<String> queue = new LinkedList<String>();
    	Hashtable<String, String> path = new Hashtable<String, String>();
    	
    	path.put(actor1, "");
    	queue.add(actor1);
    	visited.add(actor1);
    	String current = "";
    	
    	while(!queue.isEmpty()) {
    		
    		String next = queue.remove();
    		System.out.println(next);
    		
    		Set<String> list = neighbors(next, graph);
    		System.out.println("Neighbors of " + next + " : " + list);
			Iterator<String> listit = list.iterator();
			
			if (visited.contains(actor2)) {
//				visited.add(next);
				path.put(actor1, next);
				break;
			}
			
			else {
				while(listit.hasNext()) {
					current = listit.next();
//					System.out.println(current);
					
					if (!visited.contains(actor2)) {
	    				visited.add(current);
	    				queue.add(current);
	    				path.put(actor1, current);
	    			}
				}
			}
    	}
    	System.out.println("Path between " + actor1 + " and " + actor2 + " : " + actor1 + path.get(actor1) + actor2);
    }
    
    public Set<String> neighbors(String neighbs, HashMap<String, HashSet<String>> graph) {
    	return graph.get(neighbs);
	}
}