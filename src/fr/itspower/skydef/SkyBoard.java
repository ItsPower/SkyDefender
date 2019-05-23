package fr.itspower.skydef;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class SkyBoard {
	
	private static Location drapeau = new Location(Bukkit.getWorld("world"), -429, 252, 107);
	
	private BukkitTask loop;
	private Player p;

	public SkyBoard(Player p) {
		this.p = p;
		startScheduler();
	}

	private void startScheduler() {
		this.loop = new BukkitRunnable() {
			public void run() {
				if(p.isOnline()) {
	            	if(Main.status.equals(Status.WAITING)) {
	            		
	            		final Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
	            		final Objective o = board.registerNewObjective("prysmboard", "dummy");
	            		o.setDisplaySlot(DisplaySlot.SIDEBAR);

	            		o.getScore("§7").setScore(9);
	            		o.getScore("§7En attente de joueurs...").setScore(8);
	            		o.getScore("§a        [ " + Bukkit.getOnlinePlayers().size() + " / 100 ]").setScore(7);
	            		o.getScore("§7 ").setScore(6);
	            		if(Main.skydefPlayers.containsKey(p.getName())) {
	            			SkyDefPlayer skp = Main.skydefPlayers.get(p.getName());
	            			o.setDisplayName(skp.getTeam().getColor()+"§lSkyDefender");
	            			o.getScore("§7Votre team: "+skp.getTeam().getColor()+skp.getTeam().getName()).setScore(5);
	            		} else {
	            			o.getScore("§7Votre team: §8Aucune").setScore(5);
	            			o.setDisplayName("§f§lSkyDefender");
	            		}
	            		o.getScore("§7  ").setScore(4);
	            		
	            		p.setScoreboard(board);
	            		
	            	} else if(Main.status.equals(Status.INGAME) || Main.status.equals(Status.FINISHED)) {
	            		
	            		final Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
	            		final Objective o = board.registerNewObjective("prysmboard", "dummy");
	            		o.setDisplaySlot(DisplaySlot.SIDEBAR);

	            		o.getScore("§7").setScore(9);
	            		o.getScore("§7Timer: §f"+Main.getFormatedTime()).setScore(8);
	            		o.getScore("§7Jour: §f"+Main.jour).setScore(7);
	            		o.getScore("§7 ").setScore(6);
	            		SkyDefPlayer skp = Main.skydefPlayers.get(p.getName());
	            		o.setDisplayName(skp.getTeam().getColor()+"§lSkyDefender");
	            		o.getScore("§7Votre team: "+skp.getTeam().getColor()+skp.getTeam().getName()).setScore(5);
	            		o.getScore("§7  ").setScore(4);
	            		int dist = (int)drapeau.distance(p.getLocation());
	            		o.getScore("§7Drapeau: §f-429 252 107 §7["+(dist>300?"§f":dist>200?"§e":dist>100?"§6":dist>50?"§c":"§4")+dist+"§7]").setScore(3);
	            		o.getScore("§7  ").setScore(2);
	            		
	            		p.setScoreboard(board);
	            		
	            	}
				} else {
					stop();
				}
            }
        }.runTaskTimer(Main.getPlugin(), 0, 20);
	}
	
	public void stop() {
		loop.cancel();
		Main.removeBoard(p.getName());
	}
}
