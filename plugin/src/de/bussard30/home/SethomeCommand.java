package de.bussard30.home;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.bussard30.main.JedisManager;

public class SethomeCommand implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (args.length == 1)
			{
				JedisManager.addHome(player, args[0].toLowerCase());
				player.sendMessage(ChatColor.GOLD + "Created home <" + args[0].toLowerCase() + ">.");
				return true;
			}
		}
		return false;
	}

}
