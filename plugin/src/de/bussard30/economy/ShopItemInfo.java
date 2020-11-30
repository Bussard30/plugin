package de.bussard30.economy;

import org.bukkit.Material;

import de.bussard30.main.FileManager;
import de.bussard30.main.Main;

public class ShopItemInfo
{
	public static double declPer64 = 0.10d;

	private Material m;
	private int buyPrice;
	private int sellPrice;
	private ShopIcons category;
	private int sellAmount;
	private int originalSellAmount;

	public ShopItemInfo(Material material, int buyPrice, int sellPrice, ShopIcons category, int sellAmount)
	{
		this.m = material;
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.category = category;
		this.sellAmount = sellAmount;
		this.originalSellAmount = sellAmount;
	}

	public int getBuyPrice()
	{
		return buyPrice;
	}

	public int getSellPrice()
	{
		return sellAmount < 0
				? (int) Math.ceil((double) sellPrice * (1 - Math.pow(declPer64, Math.ceil(Math.abs(sellAmount / 64)))))
				: sellPrice;
	}

	/**
	 * @return sellamount, can be everything
	 */
	public int getSellAmount()
	{
		return sellAmount;
	}

	/**
	 * @return sellamount, if bigger than 64 it returns 64, if smaller than 1 it
	 *         returns 1
	 */
	public int getSellAmount64()
	{
		return sellAmount > 64 ? 64 : sellAmount <= 0 ? 1 : sellAmount;
	}

	public void setSellAmount(int sellAmount)
	{
		if (this.sellAmount != sellAmount)
		{
			Main.logger().info("Set new sellamount value!");
			this.sellAmount = sellAmount;
			FileManager.getSellsWriter().setValue(Main.currentDate + "." + getMaterial().name() + ".sellamount", sellAmount);
			FileManager.getSellsWriter().save();
		}
	}

	public ShopIcons getCategory()
	{
		return category;
	}

	public Material getMaterial()
	{
		return m;
	}

	public void resetSellAmount()
	{
		sellAmount = originalSellAmount;
	}

}
