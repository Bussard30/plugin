package de.bussard30.economy;

import java.util.NoSuchElementException;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.bussard30.main.JedisManager;
import de.bussard30.main.Main;

public enum ShopIcons
{
	Stone(Material.STONE), Wood(Material.OAK_WOOD), Natural(Material.VINE), Nether(Material.NETHERRACK), Redstone(
			Material.REDSTONE), Mining(Material.IRON_PICKAXE), Minerals(Material.DIAMOND), Combat(
					Material.IRON_SWORD), Tools(Material.IRON_SHOVEL), Brewing(Material.BREWING_STAND), MagicItems(
							Material.ENCHANTED_BOOK), MobDrops(Material.ROTTEN_FLESH), Food(
									Material.APPLE), Color(Material.PURPLE_DYE), End(Material.END_STONE);
	private ItemStack item;

	ShopIcons(ItemStack item)
	{
		this.item = item;
	}

	ShopIcons(Material m)
	{
		item = new ItemStack(m);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(getName());
		item.setItemMeta(itemMeta);
		item = Main.makeItemStackUnmovable(item);
		item = JedisManager.nbtwrapper.setNBTTag("execute", "show_shop " + getName(), item);
	}

	public String getName()
	{
		return this.toString().replace("_", " ");
	}

	public ItemStack getItem()
	{
		return item.clone();
	}

	/**
	 * Can return null.
	 * 
	 * @param s
	 * @return
	 * @throws NoSuchElementException
	 */
	@Nullable
	public static ShopIcons getByName(String s) throws NoSuchElementException
	{
		if (s == null)
		{
			return null;
		}
		if (s.equals("None"))
		{
			return null;
		}
		for (ShopIcons si : ShopIcons.values())
		{
			if (si.getName().equals(s))
				return si;
		}
		throw new NoSuchElementException();
	}

}
