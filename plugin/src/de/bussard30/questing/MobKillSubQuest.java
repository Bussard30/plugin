package de.bussard30.questing;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.jitse.npclib.api.NPC;

public class MobKillSubQuest extends SubQuest
{

	private Player p;
	private EntityType target;
	private int amount;
	private int cur_amount;

	public MobKillSubQuest(Player p, EntityType target, int amount)
	{
		this.p = p;
		this.target = target;
		this.amount = amount;
		cur_amount = 0;
	}

	@Override
	public boolean onEntityDeath(Entity e)
	{
		return false;
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onNPCInteract(NPC n)
	{
		return false;
		// TODO Auto-generated method stub

	}

	@Override
	public void onStart()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getName()
	{
		return "MobKillQuest";

	}

}
