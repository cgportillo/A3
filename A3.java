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
		String inputFile = "tmdb_5000_credits.csv";
		start.readFile(inputFile);
	}

	public void readFile(String inputFile) {

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

				// go through the JSONArray which has the names from cast column, make them
				// JSONObjects
				// initialize name sets for keys
				HashSet<String> newnames = null;
				HashSet<String> existingnames = null;

				for (int i = 0; i < jsonarr.size(); i++) {
					JSONObject name = new JSONObject();
					name = (JSONObject) jsonarr.get(i);

					String vactor = (String) name.get("name");
					vactor = vactor.toLowerCase().trim();

					// if there is no key for the actor, create key and new set
					if (graph.containsKey(vactor) == false) {
						newnames = new HashSet<String>();
						graph.put(vactor, newnames);

						for (int x = 0; x < jsonarr.size(); x++) {
							name = new JSONObject();
							name = (JSONObject) jsonarr.get(x);
							String coactor = (String) name.get("name");
							coactor = coactor.toLowerCase().trim();
							newnames.add(coactor);
						}
						graph.put(vactor, newnames);
					}

//    	        	else, get their existing names in their key sets, add names to existing values
					else if (graph.containsKey(vactor)) {
						existingnames = graph.get(vactor);
						for (int x = 0; x < jsonarr.size(); x++) {
							name = new JSONObject();
							name = (JSONObject) jsonarr.get(x);
							String coactor = (String) name.get("name");
							coactor = coactor.toLowerCase().trim();
							existingnames.add(coactor);
						}
						graph.put(vactor, existingnames);
					}
				}
			}
			
			// prompt for names of actors to find path
			System.out.println("Choose actor 1: ");
			String actor1 = scans.nextLine();
			actor1 = actor1.toLowerCase();
			System.out.println(graph.get(actor1));
			
			System.out.println("Choose actor 2: ");
			String actor2 = scans.nextLine();
			actor2 = actor2.toLowerCase();
			System.out.println(graph.get(actor2));

			reader.close();
			scans.close();
			csvParser.close();

			if (graph.containsKey(actor1) == false) {
				System.out.println(actor1 + " does not exist in this file.");
			}	
			else if(graph.containsKey(actor2) == false) {
				System.out.println(actor2 + " does not exist in this file.");
			} else {
				System.out.println("1: " + graph.get(actor1));
				System.out.println("2: " + graph.get(actor2));
				bfs(graph, actor1, actor2);
			}
		}

		catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	public void bfs(HashMap<String, HashSet<String>> graph, String actor1, String actor2) {

		Hashtable<String, Boolean> visited = new Hashtable<String, Boolean>();
		Hashtable<String, String> path = new Hashtable<String, String>();
		Queue<String> queue = new LinkedList<String>();
		Hashtable<String, String> finalpath = new Hashtable<String, String>();

		queue.add(actor1);

		for (String name : graph.keySet()) {
			visited.put(name, false);
		}

		// while queue is not empty
		while (!queue.isEmpty()) {
			// actor = dequeue from queue
			String actor = queue.remove();
			// iterate through actor's neighbors
			for (String coactor : graph.get(actor)) {

				// if not visit:
				// put into queue, update path and visit table
				if (!visited.get(coactor)) {
					queue.add(coactor);
					path.put(actor1, coactor);
					visited.put(actor1, true);

					// if equals des/to/actor2:
					// print path table, generate path from path table
					// used a loop in order to store the people in a new container called final path and then printed that path out
					if (coactor.equals(actor2)) {
						path.put(actor2, actor);
						String s = path.get(actor1);
						while (!s.equals(actor1)) {
							System.out.println(s);
							s = path.get(s);
						}
						return;
					}
				}
			}
		}
		return;
	}

	public Set<String> neighbors(String neighbs, HashMap<String, HashSet<String>> graph) {
		return graph.get(neighbs);
	}
}