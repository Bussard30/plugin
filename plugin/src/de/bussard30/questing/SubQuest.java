package de.bussard30.questing;

import org.bukkit.entity.Entity;

import net.jitse.npclib.api.NPC;

public abstract class SubQuest
{
	public abstract boolean onEntityDeath(Entity e);
	public abstract boolean onNPCInteract(NPC n);
	public abstract void onStart();
	public abstract String getName();
}
