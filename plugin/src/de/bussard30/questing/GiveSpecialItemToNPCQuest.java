package de.bussard30.questing;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.jitse.npclib.api.NPC;

public class GiveSpecialItemToNPCQuest extends SubQuest
{
	private NPC target;
	private ItemStack item;
	private Player p;

	public GiveSpecialItemToNPCQuest(Player p, NPC target, ItemStack item)
	{
		this.p = p;
		this.target = target;
		this.item = item;
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
			boolean b = false;
			ItemStack[] inv = p.getInventory().getContents();
			for (int i = 0; i < inv.length; i++)
				if (inv[i].equals(item))
					b = true;
			if (!b)
			{
				p.getInventory().remove(item);
				return true;
			}
		}
		return false;
	}

	@Override
	public void onStart()
	{
		p.sendMessage("Give " + target.getText().get(0) + "at " + target.getLocation().toString() + " "
				+ item.getItemMeta().getDisplayName().replace("_", " ").toLowerCase() + " x" + item.getAmount());
	}
	
	@Override
	public String getName()
	{
		return "GiveSpecialItemToNPCQuest";
		
	}

}
