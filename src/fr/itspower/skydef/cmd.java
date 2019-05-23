package fr.itspower.skydef;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class cmd implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("skydef")) {
        	
        	if (!(s instanceof Player)) {
                s.sendMessage("Plugin non utilisable sur la console.");
                return true;
            }
        	
        	if (!s.isOp()) {
        		s.sendMessage("Vous n'avez pas les permissions requises.");
        		return true;
        	}
        	
            Player p = (Player) s;
            
        	if (args.length == 0) {
        		s.sendMessage("/sd start");
        		s.sendMessage("/sd changeteam");
        		s.sendMessage("/sd forceteam <joueur> <id>");
        		return true;
        		
        	} else if (args.length == 1) { // forceteam <j> <id>
        		if(args[0].equalsIgnoreCase("changeteam")) {
        			openChooseInv(p);
        		}
        		if(args[0].equalsIgnoreCase("start")) {

        			for(Player ploop : Bukkit.getOnlinePlayers()) {
        				Main.resetPlayer(ploop);
        				SkyDefPlayer skp = Main.skydefPlayers.get(ploop.getName());
        				if(skp == null) {
        					skp = new SkyDefPlayer(p);
        					Main.skydefPlayers.put(ploop.getName(), skp);
        				} else {
        					continue;
        				}
        				int min = 0;
            			SkyDefTeam minTeam = SkyDefTeam.getById(new Random().nextInt(16));
            			for(SkyDefTeam teamloop : SkyDefTeam.values()) {
            				if(teamloop.getPlayers() >= min) {
            					min = teamloop.getPlayers();
            					minTeam = teamloop;
            				}
            			}
        				skp.setTeam(minTeam);
        				minTeam.add1player();
        			}
        			
        			Main.startSched();
        		}
        	} else if (args.length == 3) {
        		if(args[0].equalsIgnoreCase("forceteam")) {
        			Player target = Bukkit.getPlayer(args[1]);
        			SkyDefTeam team = SkyDefTeam.getById(Integer.parseInt(args[2]));
        			SkyDefPlayer skp = Main.skydefPlayers.get(target.getName());
    				if(skp == null) {
    					skp = new SkyDefPlayer(p);
    					Main.skydefPlayers.put(target.getName(), skp);
    				} else {
    					if(!skp.getTeam().equals(team)) {
    						skp.getTeam().remove1player();
    					}
    				}
    				skp.setTeam(team);
    				team.add1player();
        		}
        	}
		}
		return true;
	}

	static void openChooseInv(Player p) {
		p.closeInventory();
		
		Inventory inv = Bukkit.createInventory(null, 45, "§fChoix de l'équipe.");
        ItemStack DEFENDERS = new ItemBuilder(Material.WOOL,1,(short)SkyDefTeam.DEFENDERS.getWoolId()).name(SkyDefTeam.DEFENDERS.getColor()+SkyDefTeam.DEFENDERS.getName()).build();
        ItemStack ORANGE = new ItemBuilder(Material.WOOL,1,(short)SkyDefTeam.ORANGE.getWoolId()).name(SkyDefTeam.ORANGE.getColor()+SkyDefTeam.ORANGE.getName()).build();
        ItemStack PURPLE = new ItemBuilder(Material.WOOL,1,(short)SkyDefTeam.PURPLE.getWoolId()).name(SkyDefTeam.PURPLE.getColor()+SkyDefTeam.PURPLE.getName()).build();
        ItemStack BLUE = new ItemBuilder(Material.WOOL,1,(short)SkyDefTeam.BLUE.getWoolId()).name(SkyDefTeam.BLUE.getColor()+SkyDefTeam.BLUE.getName()).build();
        ItemStack YELLOW = new ItemBuilder(Material.WOOL,1,(short)SkyDefTeam.YELLOW.getWoolId()).name(SkyDefTeam.YELLOW.getColor()+SkyDefTeam.YELLOW.getName()).build();
        ItemStack GREEN = new ItemBuilder(Material.WOOL,1,(short)SkyDefTeam.GREEN.getWoolId()).name(SkyDefTeam.GREEN.getColor()+SkyDefTeam.GREEN.getName()).build();
        ItemStack ROSE = new ItemBuilder(Material.WOOL,1,(short)SkyDefTeam.ROSE.getWoolId()).name(SkyDefTeam.ROSE.getColor()+SkyDefTeam.ROSE.getName()).build();
        ItemStack DARK_GRAY = new ItemBuilder(Material.WOOL,1,(short)SkyDefTeam.DARK_GRAY.getWoolId()).name(SkyDefTeam.DARK_GRAY.getColor()+SkyDefTeam.DARK_GRAY.getName()).build();
        ItemStack GRAY = new ItemBuilder(Material.WOOL,1,(short)SkyDefTeam.GRAY.getWoolId()).name(SkyDefTeam.GRAY.getColor()+SkyDefTeam.GRAY.getName()).build();
        ItemStack AQUA = new ItemBuilder(Material.WOOL,1,(short)SkyDefTeam.AQUA.getWoolId()).name(SkyDefTeam.AQUA.getColor()+SkyDefTeam.AQUA.getName()).build();
        ItemStack GOLD = new ItemBuilder(Material.WOOL,1,(short)SkyDefTeam.GOLD.getWoolId()).name(SkyDefTeam.GOLD.getColor()+SkyDefTeam.GOLD.getName()).build();
        ItemStack DARK_BLUE = new ItemBuilder(Material.WOOL,1,(short)SkyDefTeam.DARK_BLUE.getWoolId()).name(SkyDefTeam.DARK_BLUE.getColor()+SkyDefTeam.DARK_BLUE.getName()).build();
        ItemStack DARK_AQUA = new ItemBuilder(Material.WOOL,1,(short)SkyDefTeam.DARK_AQUA.getWoolId()).name(SkyDefTeam.DARK_AQUA.getColor()+SkyDefTeam.DARK_AQUA.getName()).build();
        ItemStack DARK_GREEN = new ItemBuilder(Material.WOOL,1,(short)SkyDefTeam.DARK_GREEN.getWoolId()).name(SkyDefTeam.DARK_GREEN.getColor()+SkyDefTeam.DARK_GREEN.getName()).build();
        ItemStack RED = new ItemBuilder(Material.WOOL,1,(short)SkyDefTeam.RED.getWoolId()).name(SkyDefTeam.RED.getColor()+SkyDefTeam.RED.getName()).build();
        ItemStack BLACK = new ItemBuilder(Material.WOOL,1,(short)SkyDefTeam.BLACK.getWoolId()).name("§8"+SkyDefTeam.BLACK.getName()).build();
        //ItemStack ADMIN = new ItemBuilder(Material.WOOL,1,(short)SkyDefTeam.ADMIN.getWoolId()).name(SkyDefTeam.ADMIN.getName()).build();

        inv.setItem(4, DEFENDERS);
        
        inv.setItem(19, ORANGE);
        inv.setItem(20, PURPLE);
        inv.setItem(21, BLUE);
        inv.setItem(22, YELLOW);
        inv.setItem(23, GREEN);
        inv.setItem(24, ROSE);
        inv.setItem(25, DARK_GRAY);
        inv.setItem(28, GRAY);
        inv.setItem(29, AQUA);
        inv.setItem(30, GOLD);
        inv.setItem(31, DARK_BLUE);
        inv.setItem(32, DARK_AQUA);
        inv.setItem(33, DARK_GREEN);
        inv.setItem(34, RED);
        inv.setItem(40, BLACK);
         
        p.openInventory(inv);
	}

}
