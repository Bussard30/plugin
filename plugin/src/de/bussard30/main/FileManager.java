package de.bussard30.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import de.bussard30.economy.Shop;
import de.bussard30.economy.ShopIcons;
import de.bussard30.economy.ShopItemInfo;

public class FileManager
{
	private static FileWriter fileWriter = new FileWriter(Main.getMain().getDataFolder().getPath(), "data.yml");
	private static FileWriter shopWriter = new FileWriter(Main.getMain().getDataFolder().getPath(), "shop_items.yml");
	private static FileWriter npcsWriter = new FileWriter(Main.getMain().getDataFolder().getPath(), "npcs.yml");
	private static FileWriter questsWriter = new FileWriter(Main.getMain().getDataFolder().getPath(), "quests.yml");
	private static FileWriter sellWriter = new FileWriter(Main.getMain().getDataFolder().getPath(), "sells.yml");

	public static void loadFile()
	{
		setDefaultValue("message.prefix", "Plugin");

		HashMap<ShopIcons, ArrayList<ShopItemInfo>> hm = new HashMap<>();
		for (ShopIcons shopIcon : ShopIcons.values())
		{
			hm.put(shopIcon, new ArrayList<ShopItemInfo>());
		}
		ConfigurationSection items = shopWriter.getSection("items");
		Set<String> keys = items.getKeys(false);
		for (String s : keys)
		{
			Material m;
			try
			{
				m = Material.getMaterial(s);
			} catch (Throwable t)
			{
				Main.logger().info("Could not find material for entry in shopItems.yml called: <" + s);
				continue;
			}
			try
			{
				ShopIcons si = ShopIcons.getByName(items.getString(s + ".category"));
				if (si == null)
					continue;
				hm.get(si).add(new ShopItemInfo(m, items.getInt(s + ".buyprice"), items.getInt(s + ".sellprice"), si,
						items.getInt(s + ".sellamount")));
				Main.logger().info("adding" + m.name());
			} catch (NoSuchElementException e)
			{

			}

		}

		// --------------------------------------------------------------------------------------------------
		HashMap<Material, Integer> sellAmounts = new HashMap<>();
		String date = Main.currentDate;
		ConfigurationSection sellSec = sellWriter.getSection(date);
		if (sellSec == null)
		{
			Main.logger().warning("SellSec null!");
			Shop.setHashMap(hm, null);
			return;
		}
		Set<String> sellKeys = sellSec.getKeys(false);
		if (sellKeys == null)
		{
			Main.logger().warning("SellKeys null!");
			Shop.setHashMap(hm, null);
			return;
		}
		if (sellKeys.isEmpty())
		{
			Main.logger().warning("SellSec empty!");
			Shop.setHashMap(hm, null);
			return;
		}
		for (String s : sellKeys)
		{
			sellAmounts.put(Material.getMaterial(s), sellSec.getInt(s + ".sellamount"));
			Main.logger().info("Found sellamount value! for" + s);
		}
		Shop.setHashMap(hm, sellAmounts);

	}

	public static void fetchShop()
	{

	}

	private static void setValue(final String valuePath, final String value)
	{
		fileWriter.setValue(valuePath, value);
		fileWriter.save();
	}

	private static void setDefaultValue(final String valuePath, final String value)
	{
		fileWriter.setDefaultValue(valuePath, value);
	}

	public static Object getObject(final String valuePath)
	{
		return fileWriter.getObject(valuePath);
	}

	public static FileWriter getFileWriter()
	{
		return fileWriter;
	}

	public static FileWriter getNpcsWriter()
	{
		return npcsWriter;
	}

	public static FileWriter getQuestsWriter()
	{
		return questsWriter;
	}

	public static FileWriter getSellsWriter()
	{
		return sellWriter;

	}

	public static String getCurrentDate()
	{
		Date d = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy");
		Main.logger().info("Date:" + formatter.format(d));
		return formatter.format(d);
	}
}
