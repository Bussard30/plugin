package de.bussard30.home;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.bussard30.main.JedisManager;
import de.bussard30.types.Container;

public class HomesCommand implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (args.length == 0)
			{
				player.sendMessage(ChatColor.GOLD + "Your current homes: ");
				player.sendMessage(ChatColor.GOLD + "------------------------------------------");
				for (Map.Entry<String, Container> entry : JedisManager.getHomesWithP(player).entrySet())
				{
					player.sendMessage(ChatColor.GOLD + "<" + entry.getKey() + "> : " + ChatColor.GRAY + "["
							+ entry.getValue().getXyz()[0] + "," + entry.getValue().getXyz()[1] + ","
							+ entry.getValue().getXyz()[2] + "]");
				}
				player.sendMessage(ChatColor.GOLD + "------------------------------------------");
				return true;
			} else
			{
				player.sendMessage("Wrong usage of parameters.");
				return true;
			}
		}
		return false;
	}

}
