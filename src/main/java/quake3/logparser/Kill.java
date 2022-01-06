package quake3.logparser;

public class Kill {
	private int killerId;
	private int killedId;
	private String causaMortis;
	
	public Kill(int killerId, int killedId, String causaMortis) {
		this.killerId = killerId;
		this.killedId = killedId;
		this.causaMortis = causaMortis;
	}
	
	public int getKillerId() {
		return killerId;
	}
	
	public void setKillerId(int killerId) {
		this.killerId = killerId;
	}
	
	public int getKilledId() {
		return killedId;
	}
	
	public void setKilledId(int killedId) {
		this.killedId = killedId;
	}
	
	public String getCausaMortis() {
		return causaMortis;
	}
	
	public void setCausaMortis(String causaMortis) {
		this.causaMortis = causaMortis;
	}
}
