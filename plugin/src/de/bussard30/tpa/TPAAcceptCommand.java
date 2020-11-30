package de.bussard30.tpa;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.bussard30.main.Main;
import net.md_5.bungee.api.ChatColor;

public class TPAAcceptCommand implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3)
	{
		if (arg0 instanceof Player)
		{
			Player p = (Player) arg0;
			if (TPACommand.tpaRequests.containsKey(p))
			{
				Player sen = TPACommand.tpaRequests.remove(p);
				Location l = p.getLocation();
				sen.sendMessage(
						ChatColor.GRAY + "Teleporting you to " + ChatColor.GOLD + p.getName() + "in 3 seconds!");
				p.sendMessage(ChatColor.GRAY + "Accepted tpa request!");
				Main.getMain().getServer().getScheduler().runTaskLater(Main.getMain(), new Runnable()
				{
					@Override
					public void run()
					{
						sen.teleport(l);
						sen.sendMessage(ChatColor.GRAY + "Teleporting...");
					}
				}, 60);
			} else
			{
				p.sendMessage(ChatColor.GRAY + "There is no tpa request to accept.");
			}
		}
		return false;
	}

}
