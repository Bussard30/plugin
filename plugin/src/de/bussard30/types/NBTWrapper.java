package de.bussard30.types;

import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_16_R2.NBTTagCompound;

public class NBTWrapper
{
	public ItemStack setNBTTag(String tagName, String value, ItemStack itemStack)
	{
		net.minecraft.server.v1_16_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound tagCompound = nmsStack.getOrCreateTag();
		tagCompound.setString(tagName, value);
		nmsStack.setTag(tagCompound);
		itemStack = CraftItemStack.asBukkitCopy(nmsStack);
		return itemStack;
	}

	public String getNBTTag(String tagName, ItemStack itemStack)
	{
		net.minecraft.server.v1_16_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound tagCompound = nmsStack.getTag();
		if (tagCompound == null)
			return null;
		return tagCompound.getString(tagName);
	}

	public ItemStack removeNBTTag(String tagName, ItemStack itemStack)
	{
		net.minecraft.server.v1_16_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		nmsStack.removeTag("tagName");
		itemStack = CraftItemStack.asBukkitCopy(nmsStack);
		return itemStack;
	}
}
