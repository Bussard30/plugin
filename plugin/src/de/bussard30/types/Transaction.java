package de.bussard30.types;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.bussard30.economy.Shop;
import de.bussard30.economy.ShopItemInfo;

public class Transaction
{
	private Player p;
	private ShopItemInfo sii;
	private int amount;
	private Inventory transactionInventory;

	public Transaction(Player p, ShopItemInfo sii, Inventory transactionInventory)
	{
		this.p = p;
		this.sii = sii;
		this.transactionInventory = transactionInventory;
		amount = 1;
	}

	public Player getPlayer()
	{
		return p;
	}

	public Material getMaterial()
	{
		return sii.getMaterial();
	}

	public ShopItemInfo getShopItemInfo()
	{
		return sii;
	}

	public void setCustomAmount(int customAmount)
	{
		this.amount = customAmount;
	}

	public int getCustomAmount()
	{
		return amount;
	}

	public void amountp()
	{
		amount++;
	}

	public void amounts()
	{
		amount--;
	}

	// updates money, gives player item
	public boolean buy(int amount)
	{
		int money = Shop.getMoney(p);
		if (money >= amount * sii.getBuyPrice())
		{
			Shop.setMoney(p, money - (amount * sii.getBuyPrice()));
			HashMap<Integer, ItemStack> excess = p.getInventory().addItem(new ItemStack(sii.getMaterial(), amount));
			sii.setSellAmount(sii.getSellAmount() + amount);
			for (Map.Entry<Integer, ItemStack> me : excess.entrySet())
			{
				p.getWorld().dropItem(p.getLocation(), me.getValue());
			}
			return true;
		} else
		{
			p.sendMessage("Not enough money to buy this amount of items!");
			return false;
		}

	}

	public boolean buy()
	{
		return buy(amount);
	}

	// updates sell amount and money, removes item from player
	public boolean sell(int amount)
	{
		if (p.getInventory().containsAtLeast(new ItemStack(sii.getMaterial()), amount))
		{
			Shop.removeItems(p.getInventory(), sii.getMaterial(), amount);
			Shop.setMoney(p, Shop.getMoney(p) + amount * sii.getSellPrice());
			sii.setSellAmount(sii.getSellAmount() - amount);
			return true;
		} else
		{
			p.sendMessage("Not enough items to sell!");
			return false;
		}

	}

	public boolean sell()
	{
		return sell(amount);
	}

	public Inventory getTransactionInventory()
	{
		return transactionInventory;
	}

}
