package de.bussard30.questing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateQuestCommand implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3)
	{
//		if (arg0 instanceof Player)
//		{
//			Player p = (Player) arg0;
//			if (arg3[0].toLowerCase().equals("create"))
//			{
//				p.sendMessage("Staring quest creation session...");
//				QuestSystem.startCommandSession(p);
//				return true;
//			}
//			p.sendMessage("Wrong command syntax: /quest <create|remove>");
//		}
		return true;
	}

}
