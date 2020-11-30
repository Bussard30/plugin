package de.bussard30.home;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.bussard30.main.JedisManager;
import net.md_5.bungee.api.ChatColor;

public class DelhomeCommand implements CommandExecutor
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
					JedisManager.deleteHome(player, args[0].toLowerCase());
					player.sendMessage(ChatColor.GRAY + "Deleted home named <" + args[0].toLowerCase() + ">.");
					return true;
				} else
				{
					player.sendMessage(ChatColor.GRAY + "Couldn't find home named <" + args[0].toLowerCase() + ">.");
					return true;
				}
			}
			else
			{
				player.sendMessage(ChatColor.GRAY + "Wrong amount of parameters.");
				return false;
			}
		}
		return false;
	}
}
