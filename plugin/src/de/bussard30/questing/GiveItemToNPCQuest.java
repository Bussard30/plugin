package de.bussard30.questing;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.jitse.npclib.api.NPC;

public class GiveItemToNPCQuest extends SubQuest
{
	private NPC target;
	private ItemStack[] items;
	private Player p;

	public GiveItemToNPCQuest(Player p, NPC target, ItemStack[] items)
	{
		this.p = p;
		this.target = target;
		this.items = items;
	}


	@Override
	public boolean onEntityDeath(Entity e)
	{
		return false;
	}

	@Override
	public boolean onNPCInteract(NPC n)
	{
		if (n == target)
		{
			boolean b = true;
			for (int i = 0; i < items.length; i++)
				if (!p.getInventory().containsAtLeast(items[i], items[i].getAmount()))
					b = false;
			if (b)
			{
				for (int i = 0; i < items.length; i++)
					p.getInventory().remove(items[i]);
				return true;
			}
		}
		return false;
	}

	@Override
	public void onStart()
	{
		if (items.length == 1)
		{
			p.sendMessage("Give " + target.getText().get(0) + "at " + target.getLocation().toString() + " "
					+ items[0].getType().name().replace("_", " ").toLowerCase() + " x" + items[0].getAmount());
		} else if (items.length == 2)
		{
			p.sendMessage("Give " + target.getText().get(0) + "at " + target.getLocation().toString() + " "
					+ items[0].getType().name().replace("_", " ").toLowerCase() + " x" + items[0].getAmount() + "and"
					+ items[1].getType().name().replace("_", " ").toLowerCase() + " x" + items[1].getAmount());
		} else
		{
			String s = "Give " + target.getText().get(0) + "at " + target.getLocation().toString() + " ";
			for (int i = 0; i < items.length - 1; i++)
				s += items[i].getType().name().replace("_", " ").toLowerCase() + " x" + items[i].getAmount() + ",";
			s += "and" + items[1].getType().name().replace("_", " ").toLowerCase() + " x" + items[1].getAmount();
			p.sendMessage(s);
		}

	}
	
	@Override
	public String getName()
	{
		return "GiveItemToNPC";
		
	}

}
