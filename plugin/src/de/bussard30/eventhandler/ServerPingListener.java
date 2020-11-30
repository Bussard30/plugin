package de.bussard30.eventhandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import net.md_5.bungee.api.ChatColor;

public class ServerPingListener implements Listener
{
	@EventHandler
	public void onPing(ServerListPingEvent event)
	{
		event.setMotd(ChatColor.GOLD + "PROJEKT 1.0 " + ChatColor.RED + " [1.16.2]" + "\n" + ChatColor.YELLOW + ""
				+ ChatColor.BOLD + "1.2 ECONOMY UPDATE");
	}
}
