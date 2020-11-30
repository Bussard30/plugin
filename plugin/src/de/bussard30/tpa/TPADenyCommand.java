package de.bussard30.tpa;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class TPADenyCommand implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3)
	{
		if (arg0 instanceof Player)

			if (TPACommand.tpaRequests.containsKey((Player) arg0))
			{
				TPACommand.tpaRequests.remove((Player) arg0)
						.sendMessage(ChatColor.GRAY + "Your tpa request has been rejected.");
				((Player) arg0).sendMessage(ChatColor.GRAY + "Rejected tpa request.");
				return true;
			}
			else
			{
				((Player) arg0).sendMessage(ChatColor.GRAY + "There is no tpa request to reject.");
			}

		return true;
	}

}
