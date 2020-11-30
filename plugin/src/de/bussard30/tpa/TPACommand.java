package de.bussard30.tpa;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.bussard30.main.Main;
import net.md_5.bungee.api.ChatColor;

public class TPACommand implements CommandExecutor
{

	// sender receiver
	public static HashMap<Player, Player> tpaRequests;

	static
	{
		tpaRequests = new HashMap<>();
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3)
	{
		if (arg0 instanceof Player)

			if (arg3.length == 1)
			{
				Player rec = Bukkit.getPlayer(arg3[0]);
				Player p = (Player) arg0;

				if (tpaRequests.containsKey(p))
				{
					Player p1 = tpaRequests.remove(p);
					p.sendMessage(ChatColor.GRAY + "Canceled tpa request to " + ChatColor.GOLD + p1.getName());
					p1.sendMessage(ChatColor.GRAY + "The tpa request from " + ChatColor.GOLD + p.getName()
							+ "has been canceled");
				}
				tpaRequests.put(rec, p);

				p.sendMessage(ChatColor.GRAY + "Sent tpa request to " + ChatColor.GOLD + rec.getName());
				rec.sendMessage(ChatColor.GRAY + "Received tpa request from " + ChatColor.GOLD + p.getName());
				rec.sendMessage(ChatColor.GRAY + "To accept, type :" + ChatColor.GOLD + " /tpaccept");
				rec.sendMessage(ChatColor.GRAY + "To reject, type :" + ChatColor.GOLD + " /tpadeny");
				rec.sendMessage(ChatColor.GRAY + "The request gets canceled in 60 seconds.");
				Main.getMain().getServer().getScheduler().runTaskLater(Main.getMain(), new Runnable()
				{
					@Override
					public void run()
					{
						Player sen = tpaRequests.remove(p);
						sen.sendMessage(ChatColor.GRAY + "Canceled tpa request to " + ChatColor.GOLD + sen.getName());
						p.sendMessage(ChatColor.GRAY + "The tpa request from " + ChatColor.GOLD + p.getName()
								+ "has been canceled.");
					}
				}, 1200);
			}

		return false;
	}

}
