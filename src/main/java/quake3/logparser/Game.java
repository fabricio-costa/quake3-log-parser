package quake3.logparser;

import java.util.HashMap;
import java.util.Map;

public class Game {
	private static int WORLD_ID = 1022;
	
	private int disconnectedKey = 0;
	private int totalKills = 0;
	private Map<Integer, User> mapPlayersCounter = new HashMap<>();
	private Map<String, Integer> mapCausasMortis = new HashMap<>();
	private boolean incomplete = false;
	
	public Game() {
		
	}
	
	public int getTotalKills() {
		return totalKills;
	}
	
	public void setTotalKills(int totalKills) {
		this.totalKills = totalKills;
	}
		
	public Boolean isIncomplete() {
		return incomplete;
	}
	
	public void setIncomplete(boolean incomplete) {
		this.incomplete = incomplete;
	}
	
	public Map<Integer, User> getMapPlayersCounter(){
		return mapPlayersCounter;
	}
	
	public Map<String, Integer> getMapCausasMortis(){
		return mapCausasMortis;
	}
	
	public void connectUser(int id) {
		User user = new User();
		user.setName("");
		user.setScore(0);
		
		if (mapPlayersCounter.containsKey(id)) {
			User disconnected = mapPlayersCounter.remove(id);
			mapPlayersCounter.put(--disconnectedKey, disconnected);
		}
		
		mapPlayersCounter.put(id, user);
	}
	
	public void changeUserName(int id, String name) {
		if (mapPlayersCounter.containsKey(id)) {
			User user = mapPlayersCounter.get(id);
			user.setName(name);
		}
	}
	
	public void processKill(Kill kill) {
		this.totalKills++;
		
		if (!mapCausasMortis.containsKey(kill.getCausaMortis())) {
			mapCausasMortis.put(kill.getCausaMortis(), 0);
		}
		mapCausasMortis.put(kill.getCausaMortis(), mapCausasMortis.get(kill.getCausaMortis())+1);
		
		User user = null;
		if (WORLD_ID == kill.getKillerId()) {
			user = mapPlayersCounter.get(kill.getKilledId());
			if (user!=null) {
				user.setScore(user.getScore()-1);
			}
		} else {
			user = mapPlayersCounter.get(kill.getKillerId());
			if (user!=null) {
				user.setScore(user.getScore()+1);				
			}
		}
	}
}
