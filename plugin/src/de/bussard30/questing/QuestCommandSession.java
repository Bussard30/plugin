package de.bussard30.questing;

import java.util.Vector;
import java.util.concurrent.CancellationException;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.jitse.npclib.api.NPC;

public class QuestCommandSession
{

	private Player p;
	private int iteration = 0;

	private int phase = 0;

	private Vector<SubQuest> subQuests;

	private Class<? extends SubQuest> subType;

	private Vector<Object> dataStorage;

	private Object cache0;
	private Object cache1;

	private String name;
	private int reward;

	public QuestCommandSession(Player p)
	{
		this.p = p;
		dataStorage = new Vector<>();
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setReward(int reward)
	{
		this.reward = reward;
	}

	public int getReward()
	{
		return reward;
	}

	public String getName()
	{
		return name;
	}

	/**
	 * 
	 * @return true if quest has been created
	 */
	public boolean onMessage(String msg) throws CancellationException
	{
		if (msg.toLowerCase().startsWith("exit"))
			throw new CancellationException();
		switch (phase)
		{
		case 0:
			name = msg;
			p.sendMessage("Set name: <" + msg + ">");
			phase++;
			break;
		case 1:
			try
			{
				reward = Integer.parseInt(msg);
				p.sendMessage("Set reward: <" + reward + ">");
				phase++;
			} catch (Throwable t)
			{
				return false;
			}
			break;
		case 2:
			// subquest stuff
			if (subType == null)
				if (msg.equals("done"))
					phase++;
				else
					startSubQuestCreation(msg);
			break;
		case 3:
			// TODO confirm
			break;
		}
		return false;
	}

	public Quest getQuest()
	{
		return null;
	}

	public void startSubQuestCreation(String type)
	{
		Class<? extends SubQuest> c = null;
		synchronized (QuestSystem.subQuestsAliases)
		{
			c = QuestSystem.subQuestsAliases.get(type.toLowerCase());
		}
		if (c != null)
		{
			p.sendMessage("Recognized type.");
			subType = c;
		} else
		{
			p.sendMessage("Type not found. Try again!(Type \"exit\" to cancel quest creation)");
		}
	}

	public void continueSubQuestCreation(String msg)
	{
		if (subType.equals(GiveItemToNPCQuest.class))
		{
			switch (iteration)
			{
			case 0:
				// search for npc
				cache0 = QuestSystem.getNPCByName(msg);
				if (cache0 == null)
				{
					p.sendMessage("NPC not found. Try again!(Type \"exit\" to cancel quest creation)");
					return;
				}
			default:
				if (iteration % 3 == 1)
				{
					// item name
					cache1 = Material.getMaterial(msg.replace(" ", "_"));
					if (cache1 == null)
					{
						p.sendMessage("Message not recognized. Try again!(Type \"exit\" to cancel quest creation)");
						return;
					}
					p.sendMessage("Amount: (Integer)");
					iteration++;
				} else if (iteration % 3 == 2)
				{
					try
					{
						dataStorage.add(new ItemStack((Material) cache1, Integer.getInteger(msg)));
						iteration++;
						p.sendMessage("Do want to add more items? yes: \"continue\", no: \"cancel\"");
					} catch (Throwable t)
					{
						p.sendMessage("Message not recognized. Try again!(Type \"exit\" to cancel quest creation)");
					}
				} else
				{
					if (msg.replace(" ", "").toLowerCase().equals("continue"))
					{
						// continue quest creation
					} else if (msg.replace(" ", "").toLowerCase().equals("cancel"))
					{
						// finish quest creation
						ItemStack[] array = new ItemStack[dataStorage.size()];
						int i = 0;
						for (Object o : dataStorage)
						{
							array[i] = (ItemStack) o;
							i++;
						}
						subQuests.add(new GiveItemToNPCQuest(p, (NPC) cache0, array));
						p.sendMessage("Created sub quest.");
						clear();
					} else
					{
						p.sendMessage("Message not recognized. Try again!(Type \"exit\" to cancel quest creation)");
					}
				}

			}
		} else if (subType.equals(GiveSpecialItemToNPCQuest.class))
		{
			switch (iteration)
			{
			case 0:
				// npc name
				cache0 = QuestSystem.getNPCByName(msg);
				if (cache0 == null)
				{
					p.sendMessage("NPC not found. Try again!(Type \"exit\" to cancel quest creation)");

					return;
				}
				break;
			case 1:
				// confirm item in main hand
				break;
			}

		} else if (subType.equals(MobKillSubQuest.class))
		{
			switch (iteration)
			{
			case 0:
				// entity name
				for (EntityType e : EntityType.values())
				{
					if (e.toString().toLowerCase().equals(msg))
					{
						cache0 = e;
						p.sendMessage("Found entity type!");
						p.sendMessage("Enter the amount of entities to be killed:");
					}
				}
				break;
			case 1:
				try
				{
					int amount = Integer.parseInt(msg);
					subQuests.add(new MobKillSubQuest(p, (EntityType) cache0, amount));
					p.sendMessage("Created sub quest.");
				}
				catch(Throwable t)
				{
					p.sendMessage("Could not read integer.");
				}
				break;
			}
		} else if (subType.equals(InteractWithNPCQuest.class))
		{
			// npc name
			cache0 = QuestSystem.getNPCByName(msg);
			if (cache0 == null)
			{
				p.sendMessage("NPC not found. Try again!(Type \"exit\" to cancel quest creation)");
				return;
			}
		}
	}

	private void clear()
	{
		iteration = 0;
		subType = null;
		dataStorage.clear();
		cache0 = null;
		cache1 = null;
	}

}
