package de.bussard30.questing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.concurrent.CancellationException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.bussard30.main.FileManager;
import de.bussard30.main.Main;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.events.NPCInteractEvent;
import net.jitse.npclib.api.skin.MineSkinFetcher;

public class QuestSystem implements Listener
{
	// mobs killen
	// items abgeben
	// wither killen für spin + mehr geld am advent
	// npcs bei spawn und random häusern ( mit worldguard protectet )
	public static Vector<NPC> npcs;
	private static HashMap<String, NPC> npcNames;

	private static Vector<Quest> quests;

	private static HashMap<Player, Vector<Quest>> playerQuests;

	/**
	 * sync
	 */
	private static HashMap<Player, QuestCommandSession> questCmdSession;

	/**
	 * sync
	 */
	public static HashMap<String, Class<? extends SubQuest>> subQuestsAliases;

	static
	{
		npcs = new Vector<NPC>();
		quests = new Vector<Quest>();
		playerQuests = new HashMap<Player, Vector<Quest>>();
		questCmdSession = new HashMap<Player, QuestCommandSession>();
		subQuestsAliases = new HashMap<String, Class<? extends SubQuest>>();
		npcNames = new HashMap<String, NPC>();
		initNPCs();
	}

	/**
	 * Fetch npcs from yml file and create them
	 */
	public static void initNPCs()
	{
		for (String key : FileManager.getNpcsWriter().getSection("npcs").getKeys(false))
		{
			Main.getMain().getServer().getLogger().info("NPC key: <" + key + ">");
			;
			String name = FileManager.getNpcsWriter().getString("npcs." + key + ".name");
			int skin_id = FileManager.getNpcsWriter().getInt("npcs." + key + ".skinid");
			Location location = FileManager.getNpcsWriter().getLocation("npcs." + key + ".location");
			boolean visible = FileManager.getNpcsWriter().getBoolean("npcs." + key + ".visible");
			createNPC(name, skin_id, location, visible);
		}
	}

	public static boolean destroyNPC(String name)
	{
		NPC n;
		synchronized (npcNames)
		{
			n = npcNames.remove(name);
		}
		if (n == null)
		{
			return false;
		}
		n.destroy();
		npcs.remove(n);
		FileManager.getNpcsWriter().getYamlConfiguration().set("npcs." + name, null);
		FileManager.getNpcsWriter().save();
		return true;
	}

	public static boolean createNPC(String name, int skin_id, Location loc, boolean visible)
	{
		if (!npcNames.containsKey(name))
		{
			MineSkinFetcher.fetchSkinFromIdAsync(skin_id, skin ->
			{
				Main.logger().info("Fetched skin!");
				ArrayList<String> al = new ArrayList<String>();
				al.add(name);
				NPC n = Main.library.createNPC(al);
				n.setLocation(loc);
				npcs.add(n);
				n.create();

				npcNames.put(name, n);

				for (Player p : Main.getMain().getServer().getOnlinePlayers())
				{
					Main.logger().info("showing npc for " + p.getName());
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getMain(), new Runnable()
					{
						@Override
						public void run()
						{
							n.show(p);

						}
					});
				}

				FileManager.getNpcsWriter().setValue("npcs." + name + ".name", name);
				FileManager.getNpcsWriter().setValue("npcs." + name + ".skinid", skin_id);
				FileManager.getNpcsWriter().setValue("npcs." + name + ".location", loc);
				FileManager.getNpcsWriter().setValue("npcs." + name + ".visible", true);
				FileManager.getNpcsWriter().save();
			});
			return true;
		}
		return false;
	}

	public static NPC getNPCByName(String name) throws NoSuchElementException
	{
		NPC n = null;
		synchronized (npcNames)
		{
			n = npcNames.get(name);
		}
		if (n == null)
		{
			throw new NoSuchElementException("Could not find npc by name <" + name + ">");
		}
		return null;
	}

	public static void registerSubQuest(String name, Class<? extends SubQuest> c)
	{
		subQuestsAliases.put(name, c);
	}

	public static void startCommandSession(Player p)
	{
		questCmdSession.put(p, new QuestCommandSession(p));
	}

	@EventHandler
	public void onNPCInteract(NPCInteractEvent e)
	{
//		Main.logger().info("Registered NPC interact");
//		try
//		{
//			for (Quest q : playerQuests.get(e.getWhoClicked()))
//			{
//				q.onNPCInteract(e.getNPC());
//			}
//		} catch (Throwable t)
//		{
//			Main.logger().info("No quests found for this player.");
//		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e)
	{
//		Main.logger().info("Registered entity death!");
//		Entity killer = e.getEntity().getKiller();
//
//		if (killer instanceof Player)
//		{
//			for (Quest q : playerQuests.get((Player) killer))
//			{
//				q.onMobDeath(e.getEntity());
//			}
//		}
	}

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e)
	{
//		Main.logger().info("Received msg!");
//		QuestCommandSession qcs;
//		synchronized (questCmdSession)
//		{
//			qcs = questCmdSession.get(e.getPlayer());
//		}
//		if (qcs == null)
//		{
//			return;
//		} else
//		{
//			e.setCancelled(true);
//			try
//			{
//				if (qcs.onMessage(e.getMessage()))
//				{
//					// TODO do something with quest???
//					qcs.getQuest();
//					questCmdSession.remove(e.getPlayer());
//				}
//			} catch (CancellationException ce)
//			{
//				questCmdSession.remove(e.getPlayer());
//				e.getPlayer().sendMessage("Stopped quest session.");
//			}
//		}
	}
	
	public static void cancelQuestCreatingSession(Player p)
	{
		questCmdSession.remove(p);
	}
}
