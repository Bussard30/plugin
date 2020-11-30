package de.bussard30.questing;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.bussard30.main.Main;

public class CreateNPCCommand implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3)
	{
//		if (arg0 instanceof Player)
//		{
//			Player p = (Player) arg0;
//			if (arg3[0].toLowerCase().equals("create") || arg3[0].toLowerCase().equals("add"))
//			{
//				if (arg3.length == 3)
//				{
//
//					Main.logger().info("name:" + arg3[1]);
//					Main.logger().info("integer:" + Integer.parseInt(arg3[2]));
//					Main.logger()
//							.info("location:" + new Location(p.getWorld(), Math.floor(p.getLocation().getX()) + 0.5d,
//									Math.floor(p.getLocation().getY()), Math.floor(p.getLocation().getZ()) + 0.5d)
//											.toString());
//					QuestSystem.createNPC(arg3[1], Integer.parseInt(arg3[2]),
//							new Location(p.getWorld(), Math.floor(p.getLocation().getX()) + 0.5d,
//									Math.floor(p.getLocation().getY()), Math.floor(p.getLocation().getZ()) + 0.5d),
//							true);
//					p.sendMessage("Created npc with skin id!");
//					return true;
//				} else
//				{
//					p.sendMessage("Not correct usage of : /quest <create|add> <name> <skin_id>");
//					return true;
//				}
//			} else if (arg3[0].toLowerCase().equals("destroy") || arg3[0].toLowerCase().equals("remove"))
//			{
//				if (arg3.length == 2)
//				{
//					boolean b = QuestSystem.destroyNPC(arg3[1]);
//					p.sendMessage(b ? "Removed NPC \"" + arg3[1] + "\"" : "Could not find NPC \"" + arg3[1] + "\"");
//					return true;
//				}
//				else
//				{
//					p.sendMessage("Not correct usage of : /quest <destroy|remove> <name>");
//					return true;
//				}
//
//			}
//			else
//			{
//				p.sendMessage("Not correct usage of : /quest <destroy|remove;add|create>");
//			}
//		}
		return true;
	}

}
