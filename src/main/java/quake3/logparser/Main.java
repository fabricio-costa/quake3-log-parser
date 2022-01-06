package quake3.logparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	private Pattern KILL_REGEX = Pattern.compile(" *[0-9]*:[0-9]* Kill: ([0-9]*) ([0-9]*) [0-9]*: .* killed .* by (.*)");
	private Pattern CONNECT_REGEX = Pattern.compile(" *[0-9]*:[0-9]* ClientConnect: ([0-9]*).*");
	private Pattern USERCHANGE_REGEX = Pattern.compile(" *[0-9]*:[0-9]* ClientUserinfoChanged: ([0-9]*) n\\\\([^\\\\]*)\\\\.*");
	private Map<Integer, Game> result = new HashMap<>();
	
	public void parseLog(String filename) {
		int line = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String s;
			int gameNumber = 0;
			Game g = null;
			while((s=br.readLine())!=null) {
				line++;
				if (s.contains("InitGame")) {
					if (g != null) {
						g.setIncomplete(true);
						gameNumber = finishGame(g, gameNumber);
					}
					g = new Game();
				} else if (s.contains("ShutdownGame")) {
					gameNumber = finishGame(g, gameNumber);
					g = null;
				} else {
					Matcher killMatcher = null;
					Matcher connectMatcher = null;
					Matcher userChangeMatcher = null;
					
					if ((killMatcher = KILL_REGEX.matcher(s)).matches()) {
						if (g == null) {
							g = new Game();
							g.setIncomplete(true);
						}

						Kill k = new Kill(Integer.parseInt(killMatcher.group(1)), Integer.parseInt(killMatcher.group(2)), killMatcher.group(3));
						g.processKill(k);
					} else if ((connectMatcher = CONNECT_REGEX.matcher(s)).matches()){
						g.connectUser(Integer.parseInt(connectMatcher.group(1)));
					} else if ((userChangeMatcher = USERCHANGE_REGEX.matcher(s)).matches()) {
						g.changeUserName(Integer.parseInt(userChangeMatcher.group(1)), userChangeMatcher.group(2));
					}
				}
			}
			
			if (g != null) {
				g.setIncomplete(true);
				finishGame(g, gameNumber);
			}

			for (Integer gameCounter : result.keySet()) {
				Game game = result.get(gameCounter);
				System.out.println("\"game_" + gameCounter + "\": {");
				System.out.println("  \"total_kills\": " + game.getTotalKills() + ",");
				System.out.print("  \"players\": [");
				
				List<User> users = new ArrayList<>();
				users.addAll(game.getMapPlayersCounter().values());
				for (int i = 0; i < users.size(); i++) {
					System.out.print("\"" + users.get(i).getName() + "\"" + (i<users.size()-1?",":""));
				}
				System.out.println("]");
				
				System.out.println("  \"kills\": {");
				for (int i = 0; i < users.size(); i++) {
					System.out.println("    \"" + users.get(i).getName() + "\": " + users.get(i).getScore() + (i<users.size()-1?",":""));
				}
				System.out.println("  }");
				
				System.out.println("  \"kills_by_means\": {");
				List<String> causasMortisList = new ArrayList<>();
				causasMortisList.addAll(game.getMapCausasMortis().keySet());
				for (int i = 0; i < causasMortisList.size(); i++) {
					System.out.println("    \"" + causasMortisList.get(i) + "\": " + game.getMapCausasMortis().get(causasMortisList.get(i)) + (i<causasMortisList.size()-1?",":""));
				}
				System.out.println("  }");
				System.out.println("}");
				System.out.println("");
			}	
		} catch (Exception ioe) {
			System.err.println("Nao foi possivel ler o arquivo de log");
			System.out.println(line);
			//ioe.printStackTrace();
		}
	}
	
	private int finishGame(Game g, int gameNumber) {
		result.put(++gameNumber, g);
		
		return gameNumber;
	}

	public static void main(String[] args) throws Exception{
		if (args.length == 0) {
			System.out.println("Usage java -jar logparser-1.0.0.jar <log_full_path>");
		} else {
			new Main().parseLog(args[0]);			
		}
	}
}
