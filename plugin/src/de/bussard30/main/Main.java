package de.bussard30.main;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import de.bussard30.economy.MoneyCommand;
import de.bussard30.economy.Shop;
import de.bussard30.eventhandler.ItemInteractEvents;
import de.bussard30.eventhandler.ServerPingListener;
import de.bussard30.home.DelhomeCommand;
import de.bussard30.home.HomeCommand;
import de.bussard30.home.HomesCommand;
import de.bussard30.home.SethomeCommand;
import de.bussard30.questing.CreateNPCCommand;
import de.bussard30.questing.CreateQuestCommand;
import de.bussard30.questing.GiveItemToNPCQuest;
import de.bussard30.questing.InteractWithNPCQuest;
import de.bussard30.questing.MobKillSubQuest;
import de.bussard30.questing.QuestSystem;
import de.bussard30.spin.Spin;
import de.bussard30.tpa.TPAAcceptCommand;
import de.bussard30.tpa.TPACommand;
import de.bussard30.tpa.TPADenyCommand;
import de.bussard30.types.InvType;
import net.jitse.npclib.NPCLib;
import redis.clients.jedis.JedisPool;

public class Main extends JavaPlugin
{
	public static JedisPool jedisPool;

	public static ItemStack BACKPACK;

	private static Main main;

	public static HashMap<UUID, InvType> hm;

	public static ItemStack blackGlassPane;

	public static NPCLib library;

	public static String currentDate;

	public Main()
	{

	}

	@Override
	public void onEnable()
	{
		main = this;
		currentDate = FileManager.getCurrentDate();
		
		library = new NPCLib(this);
		hm = new HashMap<UUID, InvType>();
		Main.logger().info("Current DATE:" + currentDate);

		try
		{
			jedisPool = new JedisPool("localhost");
		} catch (Throwable t)
		{
			Bukkit.getLogger().log(Level.SEVERE, "Couldn't connect to redis.", t);
		}
		Main.getMain().getServer().getLogger().info("data path: " + Main.getMain().getDataFolder().getPath());
		this.getCommand("home").setExecutor(new HomeCommand());
		this.getCommand("homes").setExecutor(new HomesCommand());
		this.getCommand("sethome").setExecutor(new SethomeCommand());
		this.getCommand("delhome").setExecutor(new DelhomeCommand());
		this.getCommand("shop").setExecutor(new Shop());
		this.getCommand("spin").setExecutor(new Spin());
		this.getCommand("money").setExecutor(new MoneyCommand());

		this.getCommand("npc").setExecutor(new CreateNPCCommand());
		this.getCommand("quest").setExecutor(new CreateQuestCommand());
		this.getCommand("spawn").setExecutor(new SpawnCommand());

		this.getCommand("tpa").setExecutor(new TPACommand());
		this.getCommand("tpaccept").setExecutor(new TPAAcceptCommand());
		this.getCommand("tpadeny").setExecutor(new TPADenyCommand());

		this.getServer().getPluginManager().registerEvents(new ItemInteractEvents(), this);
		this.getServer().getPluginManager().registerEvents(new QuestSystem(), this);
		this.getServer().getPluginManager().registerEvents(new ServerPingListener(), this);

		QuestSystem.registerSubQuest("mobkillsubquest", MobKillSubQuest.class);
		QuestSystem.registerSubQuest("mobkillquest", MobKillSubQuest.class);
		QuestSystem.registerSubQuest("killmobs", MobKillSubQuest.class);

		QuestSystem.registerSubQuest("giveitemtonpcquest", GiveItemToNPCQuest.class);
		QuestSystem.registerSubQuest("giveitemtonpc", GiveItemToNPCQuest.class);
		QuestSystem.registerSubQuest("itemtonpcquest", GiveItemToNPCQuest.class);
		QuestSystem.registerSubQuest("giveitem", GiveItemToNPCQuest.class);

		QuestSystem.registerSubQuest("givespecialitemtonpcquest", GiveItemToNPCQuest.class);
		QuestSystem.registerSubQuest("givespecialitemtonpc", GiveItemToNPCQuest.class);
		QuestSystem.registerSubQuest("specialitemtonpcquest", GiveItemToNPCQuest.class);
		QuestSystem.registerSubQuest("givespecialitem", GiveItemToNPCQuest.class);

		QuestSystem.registerSubQuest("interactwithnpcquest", InteractWithNPCQuest.class);
		QuestSystem.registerSubQuest("interactwithnpc", InteractWithNPCQuest.class);
		QuestSystem.registerSubQuest("interactnpc", InteractWithNPCQuest.class);

		initRecipes();
		blackGlassPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);

		ItemMeta itemMeta = blackGlassPane.getItemMeta();
		itemMeta.setDisplayName(" ");
		blackGlassPane.setItemMeta(itemMeta);

		blackGlassPane = makeItemStackUnmovable(blackGlassPane);
		FileManager.loadFile();
	}

	public static ItemStack makeItemStackUnmovable(ItemStack i)
	{
		return JedisManager.nbtwrapper.setNBTTag("unmovable", "true", i);
	}

	@Override
	public void onDisable()
	{

	}

	public static Main getMain()
	{
		return main;
	}

	public void initDataFile()
	{
		FileManager.loadFile();
	}

	public void initRecipes()
	{
		ItemStack item = new ItemStack(Material.CHEST);

		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GOLD + "Backpack");

		item.setItemMeta(meta);

		BACKPACK = item;

		NamespacedKey key = new NamespacedKey(this, "backpack");
		ShapedRecipe recipe = new ShapedRecipe(key, item);

		recipe.shape("GIG", "ICI", "GDG");

		recipe.setIngredient('G', Material.GOLD_INGOT);
		recipe.setIngredient('I', Material.IRON_INGOT);
		recipe.setIngredient('C', Material.CHEST);
		recipe.setIngredient('D', Material.DIAMOND);

		Bukkit.addRecipe(recipe);
	}

	public static Player getPlayer(String s)
	{
		return Bukkit.getPlayer(s);
	}

	public static Player getPlayer(UUID uuid)
	{
		return Bukkit.getPlayer(uuid);
	}

	public static Logger logger()
	{
		return Main.getMain().getLogger();
	}

	public static void defaultValues(Player p)
	{
		FileManager.getFileWriter().setDefaultValue(p.getUniqueId().toString() + ".spins", 1);
		FileManager.getFileWriter().setDefaultValue(p.getUniqueId().toString() + ".money", 1000);
		FileManager.getFileWriter().setDefaultValue(p.getUniqueId().toString() + ".p1", Spin.p1);
		FileManager.getFileWriter().setDefaultValue(p.getUniqueId().toString() + ".p2", Spin.p2);
		FileManager.getFileWriter().setDefaultValue(p.getUniqueId().toString() + ".p3", Spin.p3);
		FileManager.getFileWriter().setDefaultValue(p.getUniqueId().toString() + ".p4", Spin.p4);
		FileManager.getFileWriter().setDefaultValue(p.getUniqueId().toString() + ".p5", Spin.p5);
		FileManager.getFileWriter().setDefaultValue(p.getUniqueId().toString() + ".free_elytra", true);
		FileManager.getFileWriter().save();
	}

	public static void startDateCycle()
	{
		main.getServer().getScheduler().scheduleSyncRepeatingTask(main, new Runnable()
		{

			@Override
			public void run()
			{
				if (FileManager.getCurrentDate() != currentDate)
				{
					currentDate = FileManager.getCurrentDate();
					Shop.resetSellAmount();
				}

			}

		}, 0, 18000);
	}

}
