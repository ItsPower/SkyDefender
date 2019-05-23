package fr.itspower.skydef;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SkyDefPlayer {
	private Player player;
	
	private SkyDefTeam team;
	
	private boolean isLastDamagePlayer;
	

	public SkyDefPlayer(Player player) {
		this.player = player;
		this.team = SkyDefTeam.DARK_GRAY;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public void setIsLastDamagePlayer(boolean isLastDamagePlayer){
		this.isLastDamagePlayer = isLastDamagePlayer;
	}
	
	public boolean isLastDamagePlayer(){
		return isLastDamagePlayer;
	}
	
	public SkyDefTeam getTeam() {
		return this.team;
	}
	
	public void setTeam(SkyDefTeam team) {
		this.team = team;
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nte player "+player.getName()+" prefix "+team.getColor().toString()+"█ "+team.getColor().toString());
		new Title("","§7Team: "+team.getColor().toString()+team.getName(), 0, 60, 20).send(player);
		
		/*if(team.isEnabled()){
			this.team = team;
		}
		else {
			throw new IllegalArgumentException("Tried to add player " + player.getName() + " to a disabled team !");
		}*/
	}
}
