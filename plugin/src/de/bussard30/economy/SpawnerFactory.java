package de.bussard30.economy;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.bussard30.main.JedisManager;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R2.BlockPosition;
import net.minecraft.server.v1_16_R2.NBTTagCompound;
import net.minecraft.server.v1_16_R2.NBTTagList;
import net.minecraft.server.v1_16_R2.TileEntityMobSpawner;

public class SpawnerFactory
{

	public static ItemStack getSpawner(EntityType e)
	{
		ItemStack i = new ItemStack(Material.SPAWNER, 1);
		Shop.setName(i, ChatColor.GOLD + e.toString().toUpperCase().replace("_", " ").substring(0, 1) + ChatColor.GOLD
				+ e.toString().toLowerCase().replace("_", " ").substring(1) + ChatColor.GRAY + " Spawner");
		return JedisManager.nbtwrapper.setNBTTag("onPlace", "spawner " + e.toString(), i);
	}

	public static boolean getSpawnerBlock(ItemStack i, Location loc, Player p)
	{
		BlockPosition blockPos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getZ());
		//this might not work REMINDER
		TileEntityMobSpawner spawner = (TileEntityMobSpawner) ((CraftWorld) p.getWorld()).getHandle()
				.getTileEntity(blockPos);
		EntityType e = null;
		try
		{
			e = EntityType.valueOf(
					JedisManager.nbtwrapper.getNBTTag("onPlace", p.getInventory().getItemInMainHand()).split(" ")[1]);
		} catch (Throwable t)
		{
			return false;
		}
		NBTTagCompound spawnerTag = spawner.b();
		spawnerTag.setShort("SpawnRange", (short) 20);
		spawnerTag.setShort("MaxNearbyEntities", (short) 100);
		NBTTagCompound spawnData = new NBTTagCompound();
		spawnData.setString("id", e.toString().toLowerCase()); // sets the
																// spawner to a
																// zombie
		spawnerTag.set("SpawnData", spawnData);
		spawner.save(spawnerTag);
		return true;
	}

	public static void getSpecialSpawnerBlock(ItemStack i, Location loc, Player p)
	{
		BlockPosition blockPos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getZ());
		TileEntityMobSpawner spawner = (TileEntityMobSpawner) ((CraftWorld) p.getWorld()).getHandle()
				.getTileEntity(blockPos);
		NBTTagCompound spawnerTag = spawner.b();
		spawnerTag.setShort("SpawnRange", (short) 20);
		spawnerTag.setShort("MaxNearbyEntities", (short) 100);
		NBTTagList handList = new NBTTagList();
		NBTTagList armorList = new NBTTagList();
		NBTTagCompound mainHand = new NBTTagCompound();
		NBTTagCompound offHand = new NBTTagCompound();
		mainHand.setString("id", "minecraft:diamond_sword");
		mainHand.setShort("Count", (short) 1);
		NBTTagList enchantments = new NBTTagList();
		NBTTagCompound sharpness3 = new NBTTagCompound();
		sharpness3.setShort("id", (short) 16);
		sharpness3.setShort("lvl", (short) 3);
		enchantments.add(sharpness3);
		NBTTagCompound ench = new NBTTagCompound();
		ench.set("ench", enchantments);
		mainHand.set("tag", ench);
		handList.add(mainHand);
		handList.add(offHand);
		NBTTagCompound helmet = new NBTTagCompound();
		NBTTagCompound chestplate = new NBTTagCompound();
		NBTTagCompound leggings = new NBTTagCompound();
		NBTTagCompound boots = new NBTTagCompound();
		helmet.setString("id", "minecraft:leather_helmet");
		helmet.setShort("Count", (short) 1);
		chestplate.setString("id", "minecraft:golden_chestplate");
		chestplate.setShort("Count", (short) 1);
		// we're leaving the leg slot empty
		boots.setString("id", "minecraft:iron_boots");
		boots.setShort("Count", (short) 1);
		armorList.add(boots);
		armorList.add(leggings);
		armorList.add(chestplate);
		armorList.add(helmet);
		NBTTagCompound spawnData = new NBTTagCompound();
		spawnData.setString("id", "zombie"); // sets the spawner to a zombie
		spawnData.set("HandItems", handList);
		spawnData.set("ArmorItems", armorList);
		spawnerTag.set("SpawnData", spawnData);
		spawner.save(spawnerTag);
	}
}
