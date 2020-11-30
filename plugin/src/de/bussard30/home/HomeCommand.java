package de.bussard30.home;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.bussard30.main.JedisManager;
import de.bussard30.types.Container;

public class HomeCommand implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (args.length == 1)
			{
				if (JedisManager.containsHome(player, args[0].toLowerCase()))
				{
					Container c = JedisManager.getHome(player, args[0].toLowerCase());
					player.teleport(
							new Location(Bukkit.getWorld(c.getUuid()), c.getXyz()[0], c.getXyz()[1], c.getXyz()[2]));
					player.sendMessage(ChatColor.GOLD + "Teleported you to home <" + args[0].toLowerCase() + "> at ["
							+ c.getXyz()[0] + "," + c.getXyz()[1] + "," + c.getXyz()[2] + "].");
					return true;
				} else
				{
					player.sendMessage("Couldn't find home named <" + args[0].toLowerCase() + ">.");
					return true;
				}
			}
		}
		return false;
	}

}
