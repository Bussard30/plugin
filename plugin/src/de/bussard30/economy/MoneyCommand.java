package de.bussard30.economy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.bussard30.main.Main;
import net.md_5.bungee.api.ChatColor;

public class MoneyCommand implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3)
	{
		if (arg0 instanceof Player)
		{
			if (arg3.length == 0)
			{
				Player p = (Player) arg0;
				p.sendMessage(ChatColor.GRAY + "Your current money:" + ChatColor.GREEN + "<" + Shop.getMoney(p)
						+ Shop.currency + ">");
				return true;
			} else if (arg3.length == 3)
				if (arg3[0].toLowerCase().equals("give"))
					try
					{
						Player p = (Player) arg0;
						Player rec = Main.getPlayer(arg3[1]);
						int amount = Integer.parseInt(arg3[2]);

						int money = Shop.getMoney(p);
						if(money - amount >= 0)
						{
							Shop.setMoney(p, money - amount);
							Shop.setMoney(rec, Shop.getMoney(rec) + amount);
							p.sendMessage(ChatColor.GRAY + "Sent " + ChatColor.GREEN + +amount + Shop.currency
									+ ChatColor.GRAY + " to " + rec.getName());
							rec.sendMessage(ChatColor.GRAY + "You received " + ChatColor.GREEN + +amount + Shop.currency
									+ ChatColor.GRAY + " from " + p.getName());
							return true;
						}
						else
						{
							p.sendMessage(ChatColor.GRAY + "You don't have enough money.");
							return true;
						}

					} catch (NumberFormatException e)
					{
						((Player) arg0).sendMessage( ChatColor.RED + 
								"Could not recognize money amount. Number cannot be bigger than (2^32)/ 2");
						return true;
					} catch (Throwable t)
					{
						((Player) arg0).sendMessage(ChatColor.RED + 
								"Some error occured.");
						return false;
					}
				else
				{
					((Player) arg0).sendMessage(ChatColor.RED + 
							"Some other error occured.");
				}
		}
		return false;
	}

}
