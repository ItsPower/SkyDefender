package fr.itspower.skydef;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class Listeners
implements Listener
{
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		e.setJoinMessage("");

		p.setCollidable(false);

		Main.boards.put(p.getName(), new SkyBoard(p));
		
		if (Main.status.equals(Status.WAITING)) {
			Main.resetPlayer(p);
			p.getInventory().setItem(4, new ItemBuilder(Material.NAME_TAG, 1, (short)0).name("Choix de l'équipe").build());
			Bukkit.broadcastMessage("§7[§a+§7] §f" + p.getName() + " §7rejoint le serveur.");
			p.teleport(Main.A_respawn);
			if (p.isOp()) {
				SkyDefPlayer skp = new SkyDefPlayer(p);
				SkyDefTeam team = SkyDefTeam.getById(16);
				skp.setTeam(team);
				team.add1player();
				Main.skydefPlayers.put(p.getName(), skp);
				return;
			}
			if(!Main.skydefPlayers.containsKey(p.getName())) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nte player " + p.getName() + " prefix &8");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nte player " + p.getName() + " suffix &f");
			}
		} else if ((Main.status.equals(Status.INGAME)) && (Main.skydefPlayers.containsKey(p.getName()))) {
			Bukkit.broadcastMessage("§7[§a+§7] " + Main.skydefPlayers.get(p.getName()).getTeam().getColor() + p.getName() + " §7est de retour sur le serveur.");
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		Player p = e.getPlayer();
		Main.removeBoard(p.getName());
		e.setQuitMessage("");
		if (Main.status.equals(Status.WAITING)) {
			Main.skydefPlayers.remove(p.getName());
			Bukkit.broadcastMessage("§7[§c-§7] §f" + p.getName() + " §7quitte le serveur.");
		}
		else if ((Main.status.equals(Status.INGAME)) && 
				(Main.skydefPlayers.containsKey(p.getName())))
		{
			SkyDefPlayer pd = (SkyDefPlayer)Main.skydefPlayers.get(p.getName());
			Bukkit.broadcastMessage("§7[§c-§7] " + pd.getTeam().getColor() + "(" + pd.getTeam().getName() + ") " + p.getName() + " §7quitte la partie, il peut rejoindre à tout moment.");
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e)
	{
		if (((Main.status.equals(Status.WAITING)) || (Main.status.equals(Status.FINISHED))) && (!e.getPlayer().isOp())) {
			e.setCancelled(true);
		}
		Location loc = e.getBlock().getLocation();
		if (Main.drapeauzone.isInArea(loc))
		{
			SkyDefPlayer sdp = Main.skydefPlayers.get(e.getPlayer().getName());
			if (sdp.getTeam().equals(SkyDefTeam.DEFENDERS))
			{
				e.setCancelled(true);
				new Title("", "§7Vous devez défendre votre drapeau!", 0, 40, 20).send(e.getPlayer());
			}
			else
			{
				Block b = e.getBlock();
				if (b.getType() == Material.WOOL)
				{
					if (!Main.lastBreak.equals(sdp.getTeam())) {
						Bukkit.broadcastMessage("§7[§c§l!§7] L'équipe " + sdp.getTeam().getColor() + sdp.getTeam().getName() + " §7commence à détruire le drapeau !");
					}
					Main.lastBreak = sdp.getTeam();
					b.setType(Material.AIR);
				}
			}
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e)
	{
		if (((Main.status.equals(Status.WAITING)) || (Main.status.equals(Status.FINISHED))) && (!e.getPlayer().isOp())) {
			e.setCancelled(true);
		}
		Location loc = e.getBlock().getLocation();
		if ((Main.drapeauzone.isInArea(loc)) && (!e.getPlayer().isOp()))
		{
			e.setCancelled(true);
			new Title("", "§7Cette zone est protégée...", 0, 40, 20).send(e.getPlayer());
		}
	}

	@EventHandler
	public void playerChat(AsyncPlayerChatEvent e)
	{
		e.setCancelled(true);

		Player p = e.getPlayer();
		String msg = e.getMessage();
		if (p.isOp())
		{
			Bukkit.broadcastMessage("§c[§lAdmin§c] "+p.getName()+" » §e" + msg);
			return;
		}
		if (Main.skydefPlayers.containsKey(p.getName()))
		{
			SkyDefPlayer skp = (SkyDefPlayer)Main.skydefPlayers.get(p.getName());
			SkyDefTeam team = skp.getTeam();
			if (Main.status.equals(Status.WAITING)) {
				Bukkit.broadcastMessage(team.getColor() + p.getName() + "§7 » §f" + msg);
			} else if (Main.status.equals(Status.INGAME))
			{
				if (msg.startsWith("!")) {
					Bukkit.broadcastMessage("§7[§c§l!§7] " + team.getColor() + p.getName() + "§7 » §f" + msg.substring(1));
				} else {
					for (SkyDefPlayer loop : Main.skydefPlayers.values()) {
						if (loop.getTeam().equals(team)) {
							loop.getPlayer().sendMessage(team.getColor() + p.getName() + "§7 » §f" + msg);
						}
					}
				}
			}
			else {
				Bukkit.broadcastMessage("§7" + p.getName() + " §7» §f" + msg);
			}
		}
		else
		{
			Bukkit.broadcastMessage("§8" + p.getName() + " §7» §f" + msg);
		}
	}

	@EventHandler
	public void onFood(FoodLevelChangeEvent e)
	{
		if (Main.status.equals(Status.WAITING)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e)
	{
		Entity damager = e.getDamager();
		Entity damaged = e.getEntity();
		if ((Main.status.equals(Status.WAITING)) || (Main.status.equals(Status.FINISHED)))
		{
			e.setCancelled(true);
			return;
		}
		if (((damager instanceof Player)) && ((damaged instanceof Player)))
		{
			if (Main.jour < 2)
			{
				new Title("", "§7Le PVP est actif au jour §f2§7.", 0, 40, 20).send((Player)damager);
				e.setCancelled(true);
				return;
			}
			SkyDefPlayer kdamager = (SkyDefPlayer)Main.skydefPlayers.get(damager.getName());
			SkyDefPlayer kdamaged = (SkyDefPlayer)Main.skydefPlayers.get(damaged.getName());
			if (kdamager.getTeam().equals(kdamaged.getTeam())) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e)
	{
		if ((e.getEntity() instanceof Player))
		{
			if ((Main.status.equals(Status.WAITING)) || (Main.status.equals(Status.FINISHED)))
			{
				e.setCancelled(true);
				return;
			}
			final Player p = (Player)e.getEntity();
			if (p.getHealth() - e.getDamage() <= 0.0D)
			{
				e.setCancelled(true);
				p.setGameMode(GameMode.SPECTATOR);
				p.getInventory().clear();
				Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable()
				{
					public void run()
					{
						if (!p.isOnline()) {
							return;
						}
						new Title("", "§7Réapparition dans §a5§7s", 0, 30, 10).send(p);
						Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable()
						{
							public void run()
							{
								if (!p.isOnline()) {
									return;
								}
								new Title("", "§7Réapparition dans §a4§7s", 0, 30, 10).send(p);
								Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable()
								{
									public void run()
									{
										if (!p.isOnline()) {
											return;
										}
										new Title("", "§7Réapparition dans §e3§7s", 0, 30, 10).send(p);
										Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable()
										{
											public void run()
											{
												if (!p.isOnline()) {
													return;
												}
												new Title("", "§7Réapparition dans §62§7s", 0, 30, 10).send(p);
												Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable()
												{
													public void run()
													{
														if (!p.isOnline()) {
															return;
														}
														new Title("", "§7Réapparition dans §c1§7s", 0, 30, 10).send(p);
														Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable()
														{
															public void run()
															{
																if (!p.isOnline()) {
																	return;
																}
																new Title("", "§7Réapparition!", 0, 20, 10).send(p);
																SkyDefPlayer sdp = (SkyDefPlayer)Main.skydefPlayers.get(p.getName());
																Main.resetPlayer(p);
																if (sdp.getTeam().equals(SkyDefTeam.DEFENDERS)) {
																	p.teleport(Main.D_respawn);
																	sdp.getPlayer().getInventory().addItem(new ItemBuilder(Material.DIAMOND_PICKAXE, 1, (short)0).enchantment(Enchantment.DURABILITY, 10).enchantment(Enchantment.DIG_SPEED, 3).build());
																	sdp.getPlayer().getInventory().addItem(new ItemBuilder(Material.COOKED_BEEF, 16, (short)0).build());
																} else {
																	p.teleport(Main.w.getSpawnLocation());
																	p.getInventory().addItem(new ItemBuilder(Material.DIAMOND_PICKAXE, 1, (short)0).enchantment(Enchantment.DURABILITY, 10).build());
																}
															}
														}, 20L);
													}
												}, 20L);
											}
										}, 20L);
									}
								}, 20L);
							}
						}, 20L);
					}
				}, 1L);
			}
		}
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent e)
	{
		if (((Main.status.equals(Status.INGAME)) || (Main.status.equals(Status.FINISHED))) && 
				(!Main.skydefPlayers.containsKey(e.getPlayer().getName())) && (!e.getPlayer().getName().equals("Its_Power")) && (!e.getPlayer().getName().equals("Aiguemarin")))
		{
			e.setKickMessage("Vous n'êtes pas dans cette partie.");
			e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		String menuName = e.getWhoClicked().getOpenInventory().getTopInventory().getName();
		Player p = (Player) e.getWhoClicked();
		int slot = e.getRawSlot();
		if (slot == -999) return;
		if(menuName.equals("§fChoix de l'équipe.")) {
			e.setCancelled(true);
			if(slot == 4) {
				SkyDefPlayer skp = Main.skydefPlayers.get(p.getName());
				SkyDefTeam team = SkyDefTeam.getById(0);
				if(skp == null) {
					skp = new SkyDefPlayer(p);
					Main.skydefPlayers.put(p.getName(), skp);
				} else {
					if(!skp.getTeam().equals(team)) {
						skp.getTeam().remove1player();
					}
				}
				skp.setTeam(team);
				team.add1player();
				return;
			} else if(slot == 19) {
				SkyDefPlayer skp = Main.skydefPlayers.get(p.getName());
				SkyDefTeam team = SkyDefTeam.getById(1);
				if(skp == null) {
					skp = new SkyDefPlayer(p);
					Main.skydefPlayers.put(p.getName(), skp);
				} else {
					if(!skp.getTeam().equals(team)) {
						skp.getTeam().remove1player();
					}
				}
				skp.setTeam(team);
				team.add1player();
				return;
			} else if(slot == 20) {
				SkyDefPlayer skp = Main.skydefPlayers.get(p.getName());
				SkyDefTeam team = SkyDefTeam.getById(2);
				if(skp == null) {
					skp = new SkyDefPlayer(p);
					Main.skydefPlayers.put(p.getName(), skp);
				} else {
					if(!skp.getTeam().equals(team)) {
						skp.getTeam().remove1player();
					}
				}
				skp.setTeam(team);
				team.add1player();
			} else if(slot == 21) {
				SkyDefPlayer skp = Main.skydefPlayers.get(p.getName());
				SkyDefTeam team = SkyDefTeam.getById(3);
				if(skp == null) {
					skp = new SkyDefPlayer(p);
					Main.skydefPlayers.put(p.getName(), skp);
				} else {
					if(!skp.getTeam().equals(team)) {
						skp.getTeam().remove1player();
					}
				}
				skp.setTeam(team);
				team.add1player();
			} else if(slot == 22) {
				SkyDefPlayer skp = Main.skydefPlayers.get(p.getName());
				SkyDefTeam team = SkyDefTeam.getById(4);
				if(skp == null) {
					skp = new SkyDefPlayer(p);
				} else {
					if(!skp.getTeam().equals(team)) {
						skp.getTeam().remove1player();
					}
				}
				skp.setTeam(team);
				team.add1player();
			} else if(slot == 23) {
				SkyDefPlayer skp = Main.skydefPlayers.get(p.getName());
				SkyDefTeam team = SkyDefTeam.getById(5);
				if(skp == null) {
					skp = new SkyDefPlayer(p);
				} else {
					if(!skp.getTeam().equals(team)) {
						skp.getTeam().remove1player();
					}
				}
				skp.setTeam(team);
				team.add1player();
			} else if(slot == 24) {
				SkyDefPlayer skp = Main.skydefPlayers.get(p.getName());
				SkyDefTeam team = SkyDefTeam.getById(6);
				if(skp == null) {
					skp = new SkyDefPlayer(p);
					Main.skydefPlayers.put(p.getName(), skp);
				} else {
					if(!skp.getTeam().equals(team)) {
						skp.getTeam().remove1player();
					}
				}
				skp.setTeam(team);
				team.add1player();
			} else if(slot == 25) {
				SkyDefPlayer skp = Main.skydefPlayers.get(p.getName());
				SkyDefTeam team = SkyDefTeam.getById(7);
				if(skp == null) {
					skp = new SkyDefPlayer(p);
					Main.skydefPlayers.put(p.getName(), skp);
				} else {
					if(!skp.getTeam().equals(team)) {
						skp.getTeam().remove1player();
					}
				}
				skp.setTeam(team);
				team.add1player();
			} else if(slot == 28) {
				SkyDefPlayer skp = Main.skydefPlayers.get(p.getName());
				SkyDefTeam team = SkyDefTeam.getById(8);
				if(skp == null) {
					skp = new SkyDefPlayer(p);
					Main.skydefPlayers.put(p.getName(), skp);
				} else {
					if(!skp.getTeam().equals(team)) {
						skp.getTeam().remove1player();
					}
				}
				skp.setTeam(team);
				team.add1player();
			} else if(slot == 29) {
				SkyDefPlayer skp = Main.skydefPlayers.get(p.getName());
				SkyDefTeam team = SkyDefTeam.getById(9);
				if(skp == null) {
					skp = new SkyDefPlayer(p);
					Main.skydefPlayers.put(p.getName(), skp);
				} else {
					if(!skp.getTeam().equals(team)) {
						skp.getTeam().remove1player();
					}
				}
				skp.setTeam(team);
				team.add1player();
			} else if(slot == 30) {
				SkyDefPlayer skp = Main.skydefPlayers.get(p.getName());
				SkyDefTeam team = SkyDefTeam.getById(10);
				if(skp == null) {
					skp = new SkyDefPlayer(p);
					Main.skydefPlayers.put(p.getName(), skp);
				} else {
					if(!skp.getTeam().equals(team)) {
						skp.getTeam().remove1player();
					}
				}
				skp.setTeam(team);
				team.add1player();
			} else if(slot == 31) {
				SkyDefPlayer skp = Main.skydefPlayers.get(p.getName());
				SkyDefTeam team = SkyDefTeam.getById(11);
				if(skp == null) {
					skp = new SkyDefPlayer(p);
					Main.skydefPlayers.put(p.getName(), skp);
				} else {
					if(!skp.getTeam().equals(team)) {
						skp.getTeam().remove1player();
					}
				}
				skp.setTeam(team);
				team.add1player();
			} else if(slot == 32) {
				SkyDefPlayer skp = Main.skydefPlayers.get(p.getName());
				SkyDefTeam team = SkyDefTeam.getById(12);
				if(skp == null) {
					skp = new SkyDefPlayer(p);
					Main.skydefPlayers.put(p.getName(), skp);
				} else {
					if(!skp.getTeam().equals(team)) {
						skp.getTeam().remove1player();
					}
				}
				skp.setTeam(team);
				team.add1player();
			} else if(slot == 33) {
				SkyDefPlayer skp = Main.skydefPlayers.get(p.getName());
				SkyDefTeam team = SkyDefTeam.getById(13);
				if(skp == null) {
					skp = new SkyDefPlayer(p);
					Main.skydefPlayers.put(p.getName(), skp);
				} else {
					if(!skp.getTeam().equals(team)) {
						skp.getTeam().remove1player();
					}
				}
				skp.setTeam(team);
				team.add1player();
			} else if(slot == 34) {
				SkyDefPlayer skp = Main.skydefPlayers.get(p.getName());
				SkyDefTeam team = SkyDefTeam.getById(14);
				if(skp == null) {
					skp = new SkyDefPlayer(p);
					Main.skydefPlayers.put(p.getName(), skp);
				} else {
					if(!skp.getTeam().equals(team)) {
						skp.getTeam().remove1player();
					}
				}
				skp.setTeam(team);
				team.add1player();
			} else if(slot == 40) {
				SkyDefPlayer skp = Main.skydefPlayers.get(p.getName());
				SkyDefTeam team = SkyDefTeam.getById(15);
				if(skp == null) {
					skp = new SkyDefPlayer(p);
					Main.skydefPlayers.put(p.getName(), skp);
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
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(e.getAction().equals(Action.PHYSICAL)) return;
		if(Main.status.equals(Status.WAITING)) {
			ItemStack item = p.getInventory().getItemInMainHand();
			if(item.getType().equals(Material.NAME_TAG)) {
				cmd.openChooseInv(p);
			}
		}
	}
}
