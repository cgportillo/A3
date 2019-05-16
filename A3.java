import java.util.*;
import java.io.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class A3 {

	public static void main(String[] args) {
		A3 start = new A3();
		start.readnAdd(args[0]);
	}

	public void readnAdd(String inputFile) {

		try {

			Scanner scans = new Scanner(System.in);
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
					.withHeader("movie_id", "title", "cast", "crew").withFirstRecordAsHeader().withTrim());
			JSONParser jsonParser = new JSONParser();
			HashMap<String, HashSet<String>> graph = new HashMap<String, HashSet<String>>();

			// for every movie line, create JSONArray of "cast" column
			for (CSVRecord record : csvParser) {

				// make JSONArray of "cast" column
				JSONArray jsonarr = (JSONArray) jsonParser.parse(record.get("cast"));

				// go through JSONArray which has the names from cast column, make them
				// JSONObjects
				// initialize name sets for keys
				HashSet<String> newnames = null;
				HashSet<String> existingnames = null;

				for (int i = 0; i < jsonarr.size(); i++) {

					// make each movie actor name an object
					JSONObject name = new JSONObject();
					name = (JSONObject) jsonarr.get(i);

					// get names of movie actors, make them strings
					String vactor = (String) name.get("name");
					vactor = vactor.toLowerCase().trim();

					// if there is no key for the actor, create key and new set, add to graph
					if (graph.containsKey(vactor) == false) {
						newnames = new HashSet<String>();
						graph.put(vactor, newnames);

						// add all actors in the film to their coactor set
						for (int x = 0; x < jsonarr.size(); x++) {
							name = new JSONObject();
							name = (JSONObject) jsonarr.get(x);
							String coactor = (String) name.get("name");
							coactor = coactor.toLowerCase().trim();
							newnames.add(coactor);
						}

						// add to graph
						graph.put(vactor, newnames);
					}

//    	        	else, get their existing names in their key sets, add names to existing values, add to graph
					else if (graph.containsKey(vactor)) {

						// retrieve name set of each actor that had an existing set already
						existingnames = graph.get(vactor);

						// add all actors in the film to their coactor set
						for (int y = 0; y < jsonarr.size(); y++) {
							name = new JSONObject();
							name = (JSONObject) jsonarr.get(y);
							String coactor = (String) name.get("name");
							coactor = coactor.toLowerCase().trim();
							existingnames.add(coactor);
						}

						// add to graph
						graph.put(vactor, existingnames);
					}
				}
			}

			// prompt for names of actors to find path
			System.out.println("Choose actor 1: ");
			String actor1 = scans.nextLine();
			actor1 = actor1.toLowerCase();
			System.out.println("Choose actor 2: ");
			String actor2 = scans.nextLine();
			actor2 = actor2.toLowerCase();

			// close Buffered Reader, Scanner, and CSVParser
			reader.close();
			scans.close();
			csvParser.close();

			// checks for if the graph contains the names the user is searching for

			if (graph.containsKey(actor1) == false && graph.containsKey(actor2) == false) {
				System.out.println("Neither of these actors exist in the file.");
			} else if (graph.containsKey(actor1) == false) {
				System.out.println(actor1 + " does not exist in this file.");
			} else if (graph.containsKey(actor2) == false) {
				System.out.println(actor2 + " does not exist in this file.");
			} else {
				// execute breadth-first search to see if the actors some how have a connection
				// through the people they've worked with
				bfs(graph, actor1, actor2);
			}
		}

		catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	public String bfs(HashMap<String, HashSet<String>> graph, String actor1, String actor2) {

		// initialize visited hashtable which entails all the visited nodes explored
		// initialize path hashtable which entails all the paths
		// initialize queue linkedlist which entails a list of the people we are going
		// to be going through as we move along
		
		Hashtable<String, Boolean> visited = new Hashtable<String, Boolean>();
		Hashtable<String, String> path = new Hashtable<String, String>();
		Queue<String> queue = new LinkedList<String>();

		// initialize all visited names in the graph to false
		for (String name : graph.keySet()) {
			visited.put(name, false);
		}

		// add actor1 to queue, set it to true in visited, add to final path
		queue.add(actor1);
		visited.put(actor1, true);
		path.put(actor1, "");

		// while queue is not empty, we want to explore people in the queue
		while (!queue.isEmpty()) {

			// actor = dequeue from queue
			String actor = queue.remove();

			// iterate through actor's neighbors
			for (String coactor : neighbors(actor, graph)) {

				// if not visited:
				// put into queue, update path and visit table
				if (!visited.contains(coactor)) {
					queue.add(coactor);
					visited.put(coactor, true);
					path.put(coactor, actor);

					// if equals des/to/actor2:
					// print path table, generate path from path table
					if (coactor.equals(actor2)) {
						path.put(actor2, coactor);
						System.out.println("Shortest path between " + actor1 + " to " + actor2 + ": "); 
						System.out.println(path.get(actor2));
						return path.get(actor2);
					}
					// need to try and print back. right now, it prints the last person it added.
				}
			}
		}
		System.out.println("There was no path between " + actor1 + " to " + actor2);
		return null;
	}

	// gets the neighbors of the actor we dequeue to further explore their
	// connections to find if
	// they are connected to the person who we are searching for
	public Set<String> neighbors(String neighbs, HashMap<String, HashSet<String>> graph) {
		return graph.get(neighbs);
	}
}