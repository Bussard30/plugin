package de.bussard30.questing;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.jitse.npclib.api.NPC;

public class Quest
{
	private String name;
	private Player owner;
	private SubQuest[] subQuests;
	private int currentQuest = 0;
	private ItemStack[] reward;

	public Quest(String name, Player owner, SubQuest[] subQuests, ItemStack[] reward)
	{

	}

	public void onNPCInteract(NPC n)
	{
		if (subQuests[currentQuest].onNPCInteract(n))
		{
			// next subquest
		}
	}

	public void onMobDeath(Entity e)
	{
		if (subQuests[currentQuest].onEntityDeath(e))
		{
			//next subquest
		}
	}
}
