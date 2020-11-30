package de.bussard30.main;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3)
	{
		if(arg0 instanceof Player)
		{
			Player p = (Player)arg0;
			Location l = Main.getMain().getServer().getWorld("world").getSpawnLocation().clone();
			l.setDirection(p.getLocation().getDirection());
			p.teleport(l);
			p.sendMessage("Teleported you to spawn!");
			return true;
		}
		return false;
	}

}
