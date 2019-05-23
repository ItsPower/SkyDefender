package fr.itspower.skydef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Main
extends JavaPlugin
{
	public static HashMap<String, SkyDefPlayer> skydefPlayers;
	public static BukkitTask task;
	private static Main plugin;
	public static Status status;
	public static boolean gameStarted;
	public static Location A_respawn;
	public static Location D_respawn;
	public static HashMap<String, SkyBoard> boards;
	public static List<Location> drapeau;
	public static Zone drapeauzone;
	public static SkyDefTeam lastBreak;
	public static World w;
	public static int time;
	public static int jour;
	public static final int CENTER_PX = 154;

	public void onEnable() {
		plugin = this;
		time = 0;
		jour = 1;

		getCommand("skydef").setExecutor(new cmd());

		gameStarted = false;
		status = Status.WAITING;
		drapeau = new ArrayList<Location>();
		lastBreak = SkyDefTeam.DARK_GRAY;
		w = Bukkit.getWorld("world");
		A_respawn = new Location(w, -88.0D, 82.0D, 144.0D);
		D_respawn =  new Location(w, -363.5D, 174.0D, 123.5D);
		w.setGameRuleValue("announceAdvancements", "false");
		w.setGameRuleValue("spawnRadius", "0");
		w.setGameRuleValue("keepInventory", "true");
		drapeau.add(new Location(w, 0.0D, 0.0D, 0.0D));
		drapeauzone = new Zone(new Location(w, -425.0D, 249.0D, 103.0D), new Location(w, -430.0D, 254.0D, 108.0D));

		boards = new HashMap<String, SkyBoard>();
		skydefPlayers = new HashMap<String, SkyDefPlayer>();

		getServer().getPluginManager().registerEvents(new Listeners(), this);

		for(Player p : Bukkit.getOnlinePlayers()) {
			Main.boards.put(p.getName(), new SkyBoard(p));
			p.setCollidable(false);

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
					continue;
				}
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nte player " + p.getName() + " prefix &8");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nte player " + p.getName() + " suffix &f");
			}
		}

	}

	public static String getFormatedTime()
	{
		return String.format("%02d:%02d", new Object[] { Integer.valueOf(time / 60), Integer.valueOf(time % 60) });
	}

	public static void startSched() {

		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage("     §c§lDEMARRAGE DE LA PARTIE !");
		Bukkit.broadcastMessage("");
		
		for(SkyDefPlayer sdp : skydefPlayers.values()) {
			if(sdp.getTeam().equals(SkyDefTeam.DEFENDERS)) {
				sdp.getPlayer().teleport(D_respawn);
				sdp.getPlayer().getInventory().setItem(0, new ItemStack(Material.DIAMOND_SWORD, 1, (short)0));
				sdp.getPlayer().getInventory().setItem(1, new ItemStack(Material.DIAMOND_PICKAXE, 1, (short)0));
				sdp.getPlayer().getInventory().setItem(2, new ItemStack(Material.DIAMOND_AXE, 1, (short)0));
				sdp.getPlayer().getInventory().setItem(3, new ItemStack(Material.DIAMOND_SPADE, 1, (short)0));
				sdp.getPlayer().getInventory().setItem(4, new ItemStack(Material.BREAD, 64, (short)0));
				Bukkit.broadcastMessage("");
			} else {
				sdp.getPlayer().teleport(A_respawn);
				sdp.getPlayer().getInventory().setItem(0, new ItemStack(Material.IRON_PICKAXE, 1, (short)0));
				sdp.getPlayer().getInventory().setItem(1, new ItemStack(Material.IRON_AXE, 1, (short)0));
				sdp.getPlayer().getInventory().setItem(2, new ItemStack(Material.IRON_SPADE, 1, (short)0));
				sdp.getPlayer().getInventory().setItem(3, new ItemStack(Material.BREAD, 64, (short)0));
			}
		}
		Bukkit.broadcastMessage("");

		Main.status = Status.INGAME;
		
		task = Bukkit.getScheduler().runTaskTimer(getPlugin(), new Runnable() {
			public void run() {
				if (Main.status.equals(Status.INGAME)) {
					Main.time += 1;
					if (Main.time == 900) {
						Main.jour = 2;
						Bukkit.broadcastMessage(" ");
						Bukkit.broadcastMessage(Main.setCentered("§c§lACTIVATION DU PVP !"));
						Bukkit.broadcastMessage(" ");
					}
					else if (Main.time == 1800) {
						Main.jour = 3;
					}
					else if (Main.time == 2700)
					{
						Main.jour = 4;
					}
					else if (Main.time == 3600)
					{
						Main.jour = 5;
					}
					else if (Main.time == 4500)
					{
						Main.jour = 6;
					}
					else if (Main.time == 5400)
					{
						Main.jour = 7;
						Main.winGame(SkyDefTeam.DEFENDERS);
						Main.stopGame();
					}
					if (!Main.drapeauzone.hasWool())
					{
						Main.winGame(Main.lastBreak);
						Main.stopGame();
					}
				}
			}
		}, 0L, 20L);
	}

	protected static void winGame(SkyDefTeam team)
	{
		status = Status.FINISHED;
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.teleport(D_respawn);
		}
		new Title("§f§lFIN DE LA PARTIE !", "§7Gagnants: " + team.getColor() + team.getName(), 0, 200, 60).sendToAll();
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage(setCentered("§f§lFIN DE LA PARTIE !"));
		Bukkit.broadcastMessage("   §7Equipe gagnante: " + team.getColor() + team.getName());
		Bukkit.broadcastMessage(" ");
		for (SkyDefPlayer loop : skydefPlayers.values()) {
			if (loop.getTeam().equals(team)) {
				Bukkit.broadcastMessage("§7§l " + team.getColor() + loop.getPlayer().getName());
			}
		}
		Bukkit.broadcastMessage(" ");
	}

	public void onDisable()
	{
		saveConfig();
	}

	public static void stopGame()
	{
		task.cancel();
	}

	public SkyDefPlayer getSkyDefPlayer(Player player)
	{
		return (SkyDefPlayer)skydefPlayers.get(player);
	}

	public void setSkyDefPlayer(Player p, SkyDefPlayer p1)
	{
		skydefPlayers.put(p.getName(), p1);
	}

	public static Main getPlugin()
	{
		return plugin;
	}

	public static void removeBoard(String p)
	{
		boards.remove(p);
	}

	public static void resetPlayer(Player p)
	{
		p.getInventory().clear();
		p.setHealth(20.0D);
		p.setFoodLevel(20);
		p.setLevel(0);
		p.setGameMode(GameMode.SURVIVAL);
	}

	public static enum DefaultFontInfo
	{
		A('A', 5),  a('a', 5),  B('B', 5),  b('b', 5),  C('C', 5),  c('c', 5),  D('D', 5),  d('d', 5),  E('E', 5),  e('e', 5),  F('F', 5),  f('f', 4),  G('G', 5),  g('g', 5),  H('H', 5),  h('h', 5),  I('I', 3),  i('i', 1),  J('J', 5),  j('j', 5),  K('K', 5),  k('k', 4),  L('L', 5),  l('l', 1),  M('M', 5),  m('m', 5),  N('N', 5),  n('n', 5),  O('O', 5),  o('o', 5),  P('P', 5),  p('p', 5),  Q('Q', 5),  q('q', 5),  R('R', 5),  r('r', 5),  S('S', 5),  s('s', 5),  T('T', 5),  t('t', 4),  U('U', 5),  u('u', 5),  V('V', 5),  v('v', 5),  W('W', 5),  w('w', 5),  X('X', 5),  x('x', 5),  Y('Y', 5),  y('y', 5),  Z('Z', 5),  z('z', 5),  NUM_1('1', 5),  NUM_2('2', 5),  NUM_3('3', 5),  NUM_4('4', 5),  NUM_5('5', 5),  NUM_6('6', 5),  NUM_7('7', 5),  NUM_8('8', 5),  NUM_9('9', 5),  NUM_0('0', 5),  EXCLAMATION_POINT('!', 1),  AT_SYMBOL('@', 6),  NUM_SIGN('#', 5),  DOLLAR_SIGN('$', 5),  PERCENT('%', 5),  UP_ARROW('^', 5),  AMPERSAND('&', 5),  ASTERISK('*', 5),  LEFT_PARENTHESIS('(', 4),  RIGHT_PERENTHESIS(')', 4),  MINUS('-', 5),  UNDERSCORE('_', 5),  PLUS_SIGN('+', 5),  EQUALS_SIGN('=', 5),  LEFT_CURL_BRACE('{', 4),  RIGHT_CURL_BRACE('}', 4),  LEFT_BRACKET('[', 3),  RIGHT_BRACKET(']', 3),  COLON(':', 1),  SEMI_COLON(';', 1),  DOUBLE_QUOTE('"', 3),  SINGLE_QUOTE('\'', 1),  LEFT_ARROW('<', 4),  RIGHT_ARROW('>', 4),  QUESTION_MARK('?', 5),  SLASH('/', 5),  BACK_SLASH('\\', 5),  LINE('|', 1),  TILDE('~', 5),  TICK('`', 2),  PERIOD('.', 1),  COMMA(',', 1),  SPACE(' ', 3),  DEFAULT('a', 4);

		private char character;
		private int length;

		private DefaultFontInfo(char character, int length)
		{
			this.character = character;
			this.length = length;
		}

		public char getCharacter()
		{
			return this.character;
		}

		public int getLength()
		{
			return this.length;
		}

		public int getBoldLength()
		{
			if (this == SPACE) {
				return getLength();
			}
			return this.length + 1;
		}

		public static DefaultFontInfo getDefaultFontInfo(char c)
		{
			DefaultFontInfo[] arrayOfDefaultFontInfo;
			int i2 = (arrayOfDefaultFontInfo = values()).length;
			for (int i1 = 0; i1 < i2; i1++)
			{
				DefaultFontInfo dFI = arrayOfDefaultFontInfo[i1];
				if (dFI.getCharacter() == c) {
					return dFI;
				}
			}
			return DEFAULT;
		}
	}

	public static String setCentered(String message)
	{
		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;
		char[] arrayOfChar;
		int j = (arrayOfChar = message.toCharArray()).length;
		for (int i = 0; i < j; i++)
		{
			char c = arrayOfChar[i];
			if (c == '&')
			{
				previousCode = true;
			}
			else if (previousCode)
			{
				previousCode = false;
				if ((c == 'l') || (c == 'L')) {
					isBold = true;
				} else {
					isBold = false;
				}
			}
			else
			{
				DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
				messagePxSize += (isBold ? dFI.getBoldLength() : dFI.getLength());
				messagePxSize++;
			}
		}
		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = 154 - halvedMessageSize;
		int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		StringBuilder sb = new StringBuilder();
		while (compensated < toCompensate)
		{
			sb.append(" ");
			compensated += spaceLength;
		}
		return sb.toString() + message;
	}
}
