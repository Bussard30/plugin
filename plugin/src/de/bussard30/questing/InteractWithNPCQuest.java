package de.bussard30.questing;

import org.bukkit.entity.Entity;

import net.jitse.npclib.api.NPC;

public class InteractWithNPCQuest extends SubQuest
{

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
		return "InteractWithNPCQuest";
		
	}
	
}
