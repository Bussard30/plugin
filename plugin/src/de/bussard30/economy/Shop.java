package de.bussard30.economy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.bussard30.main.FileManager;
import de.bussard30.main.JedisManager;
import de.bussard30.main.Main;
import de.bussard30.types.InvType;
import de.bussard30.types.Transaction;
import net.md_5.bungee.api.ChatColor;

public class Shop implements CommandExecutor
{
	private static volatile boolean shopReady = false;
	private static HashMap<ShopIcons, ShopItemInfo[]> shopItems;
	private static HashMap<ShopIcons, Inventory[]> shopInventories;
	private static HashMap<Player, ShopSession> shopSessions;
	private static HashMap<Player, Transaction> openTransactions;
	private static HashMap<Player, Integer> money;
	private static HashMap<Material, ShopItemInfo> indexing;
	private static HashMap<Material, ShopIcons> categoryMembers;
	private static ArrayList<ShopItemInfo> shopItemInfos;

	private static ItemStack nextPage;
	private static ItemStack prevPage;

	private static ItemStack buy1;
	private static ItemStack buy10;
	private static ItemStack buy64;

	private static ItemStack sell1;
	private static ItemStack sell10;
	private static ItemStack sell64;

	private static ItemStack incAmount;
	private static ItemStack decAmount;

	private static ItemStack sell;
	private static ItemStack buy;

	private static ItemStack backToShop;
	private static ItemStack backToCategory;

	public static final String infinity_symbol = "\u221E";
	public static final String currency = "\u20BD";

	private static ArrayList<Inventory> shopInventoriesIndex;
	private static ArrayList<Inventory> transactionInventoriesIndex;

	public Shop()
	{
		shopItems = new HashMap<>();
		shopInventories = new HashMap<>();
		openTransactions = new HashMap<>();
		money = new HashMap<>();
		shopSessions = new HashMap<>();
		indexing = new HashMap<>();
		categoryMembers = new HashMap<>();

		shopInventoriesIndex = new ArrayList<Inventory>();
		transactionInventoriesIndex = new ArrayList<Inventory>();
		shopItemInfos = new ArrayList<>();

		nextPage = new ItemStack(Material.PAPER);
		prevPage = new ItemStack(Material.PAPER);

		ItemMeta nextPage_meta = nextPage.getItemMeta();
		nextPage_meta.setDisplayName("Next page");
		ItemMeta prevPage_meta = nextPage.getItemMeta();
		prevPage_meta.setDisplayName("Previous page");

		nextPage.setItemMeta(nextPage_meta);
		prevPage.setItemMeta(prevPage_meta);

		buy = new ItemStack(Material.EMERALD, 1);
		setName(buy, ChatColor.GREEN + "Buy");
		buy1 = new ItemStack(Material.EMERALD, 1);
		setName(buy1, ChatColor.GREEN + "Buy one");
		buy10 = new ItemStack(Material.EMERALD, 10);
		setName(buy10, ChatColor.GREEN + "Buy ten");
		buy64 = new ItemStack(Material.EMERALD, 64);
		setName(buy64, ChatColor.GREEN + "Buy 64");

		sell = new ItemStack(Material.REDSTONE, 1);
		setName(sell, ChatColor.GOLD + "Sell");
		sell1 = new ItemStack(Material.REDSTONE, 1);
		setName(sell1, ChatColor.GOLD + "Sell one");
		sell10 = new ItemStack(Material.REDSTONE, 10);
		setName(sell10, ChatColor.GOLD + "Sell ten");
		sell64 = new ItemStack(Material.REDSTONE, 64);
		setName(sell64, ChatColor.GOLD + "Sell 64");

		incAmount = new ItemStack(Material.PAPER, 1);
		setName(incAmount, ChatColor.GRAY + "Increase amount");
		decAmount = new ItemStack(Material.PAPER, 1);
		setName(decAmount, ChatColor.GRAY + "Decrease amount");

		nextPage = JedisManager.nbtwrapper.setNBTTag("execute", "next_page", nextPage);
		prevPage = JedisManager.nbtwrapper.setNBTTag("execute", "prev_page", prevPage);

		buy = JedisManager.nbtwrapper.setNBTTag("execute", "buy_item", buy);
		buy1 = JedisManager.nbtwrapper.setNBTTag("execute", "buy_item1", buy1);
		buy10 = JedisManager.nbtwrapper.setNBTTag("execute", "buy_item10", buy10);
		buy64 = JedisManager.nbtwrapper.setNBTTag("execute", "buy_item64", buy64);

		sell = JedisManager.nbtwrapper.setNBTTag("execute", "sell_item", sell);
		sell1 = JedisManager.nbtwrapper.setNBTTag("execute", "sell_item1", sell1);
		sell10 = JedisManager.nbtwrapper.setNBTTag("execute", "sell_item10", sell10);
		sell64 = JedisManager.nbtwrapper.setNBTTag("execute", "sell_item64", sell64);

		incAmount = JedisManager.nbtwrapper.setNBTTag("execute", "stack_custom 1", incAmount);
		decAmount = JedisManager.nbtwrapper.setNBTTag("execute", "stack_custom 0", decAmount);

		backToShop = new ItemStack(Material.PAINTING);
		backToCategory = new ItemStack(Material.PAINTING);
		setName(backToShop, ChatColor.GRAY + "Back to shop");
		setName(backToCategory, ChatColor.GRAY + "Back to category");

		backToShop = JedisManager.nbtwrapper.setNBTTag("execute", "back_to_shop", backToShop);
		backToCategory = JedisManager.nbtwrapper.setNBTTag("execute", "back_to_category", backToCategory);

	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3)
	{
		if (shopReady)
		{
			if (arg0 instanceof Player)
			{
				Player p = (Player) arg0;
				p.closeInventory();
				p.openInventory(getShopMenu());
				Main.hm.put(p.getUniqueId(), InvType.SHOP);
				return true;
			}
		} else
		{
			if (arg0 instanceof Player)
			{
				((Player) arg0).sendMessage("Shop still loading...");
				;
			}
		}
		return false;
	}

	public static Inventory getShopMenu()
	{
		Inventory i = Bukkit.createInventory(null, 45, ChatColor.BLACK + "Shop");
		ItemStack[] items = new ItemStack[45];
		int e = 0;
		for (ShopIcons s : ShopIcons.values())
		{
			items[getIndex(45, e)] = s.getItem();
			e++;
		}
		for (int f = 0; f < 9; f++)
			items[f] = Main.blackGlassPane.clone();
		for (int f = 35; f < 45; f++)
			items[f] = Main.blackGlassPane.clone();
		for (int f = 0; f < 45; f++)
		{
			if (f % 9 == 0 || f % 9 == 8)
			{
				items[f] = Main.blackGlassPane.clone();
			}
		}
		i.setContents(items);
		shopInventoriesIndex.add(i);

		return i;
	}

	public static boolean openCategory(ShopIcons category, Player p)
	{
		if (category == null)
		{
			return false;
		}
		p.closeInventory();
		Inventory[] invs = shopInventories.get(category);
		p.openInventory(invs[0]);
		shopSessions.put(p, new ShopSession(category, invs.length));
		return true;

	}

	private static void generateInventories()
	{
		for (ShopIcons s : ShopIcons.values())
		{
			ShopItemInfo[] al = shopItems.get(s);
			Inventory[] invs = new Inventory[al.length != 0 ? (int) Math.ceil(((double) al.length) / 45d) : 1];

			Main.logger().info("al.l:" + al.length + "Creating invs:"
					+ (al.length != 0 ? (int) Math.ceil(((double) al.length) / 45d) : 1) + " " + s.getName());

			for (int i = 0; i < invs.length; i++)
			{
				invs[i] = Bukkit.createInventory(null, 54, ChatColor.BLACK + s.getName());
				ItemStack[] items = invs[i].getContents();
				for (int f = 0; f < 45 && ((i * 45) + f) < al.length; f++)
				{
					Main.logger().info("[" + (i * 45 + f) + "]" + al[i * 45 + f].getMaterial().name() + " à "
							+ al[i * 45 + f].getSellAmount64());
					ItemStack is = new ItemStack(al[i * 45 + f].getMaterial(), al[i * 45 + f].getSellAmount64());
					ItemMeta im = is.getItemMeta();
					im.setDisplayName(ChatColor.GOLD + al[i * 45 + f].getMaterial().name().replace("_", " ")
							+ ChatColor.GREEN + " B:" + al[i * 45 + f].getBuyPrice() + ChatColor.GOLD + "|"
							+ ChatColor.RED + "S:" + al[i * 45 + f].getSellPrice());
					is.setItemMeta(im);
					items[f] = JedisManager.nbtwrapper.setNBTTag("execute", "open_transaction " + is.getType().name(),
							is);
					indexing.put(al[i * 45 + f].getMaterial(), al[i * 45 + f]);
					categoryMembers.put(al[i * 45 + f].getMaterial(), s);
					if (al[i * 45 + f].getBuyPrice() < al[i * 45 + f].getSellPrice())
					{
						Main.logger().warning(
								"Buy price lower than sell price for material: " + al[i * 45 + f].getMaterial().name());
					}
				}
				for (int g = 46; g < 53; g++)
				{
					items[g] = Main.blackGlassPane.clone();
				}
				
				items[45] = i == 0 ? Main.blackGlassPane.clone() : prevPage.clone();
				items[53] = i == al.length ? Main.blackGlassPane.clone() : nextPage.clone();
				items[49] = backToShop.clone();

				invs[i].setContents(items);
				shopInventoriesIndex.add(invs[i]);
			}
			shopInventories.put(s, invs);
		}
		setShopReady();
	}

	@SuppressWarnings("unused")
	public Inventory getSpawnerInventory()
	{
		Inventory i = Bukkit.createInventory(null, 54, ChatColor.BLACK + "Spawner");
        
        
		ItemStack[] items = i.getContents();
		for (int g = 46; g < 53; g++)
		{
			items[g] = Main.blackGlassPane.clone();
		}
		//TODO
		items[45] = true ? Main.blackGlassPane.clone() : prevPage.clone();
		items[53] = true ? Main.blackGlassPane.clone() : nextPage.clone();
		items[49] = backToShop.clone();

		i.setContents(items);
		shopInventoriesIndex.add(i);
		return i;
	}

	public Inventory getPotionsInventory()
	{
		Inventory i = Bukkit.createInventory(null, 54, ChatColor.BLACK + "Spawner");
		ItemStack[] items = i.getContents();
		for (int g = 46; g < 53; g++)
		{
			items[g] = Main.blackGlassPane.clone();
		}
		items[45] = true ? Main.blackGlassPane.clone() : prevPage.clone();
		items[53] = true ? Main.blackGlassPane.clone() : nextPage.clone();
		items[49] = backToShop.clone();

		i.setContents(items);
		shopInventoriesIndex.add(i);
		return i;
	}

	public static void openTransactionInventory(Player p, ShopItemInfo sii)
	{
		if (sii != null)
		{
			p.closeInventory();
			Inventory transaction = getTransactionInventory(p, sii);
			openTransactions.put(p, new Transaction(p, sii, transaction));
			p.openInventory(transaction);
			transactionInventoriesIndex.add(transaction);
			Main.hm.put(p.getUniqueId(), InvType.TRANSACTION);
		} else
		{
			Main.logger().warning("Empty transaction request! Canceled.");
		}

	}

	private static Inventory getTransactionInventory(Player p, ShopItemInfo sii)
	{
		Inventory i = Bukkit.createInventory(null, 18,
				ChatColor.BLACK
						+ (sii.getMaterial().name().replace("_", " ").toLowerCase().substring(0, 1).toUpperCase()
								+ sii.getMaterial().name().replace("_", " ").toLowerCase().substring(1)));
		ItemStack[] items = i.getContents();

		// buy material 0
		ItemStack m0 = new ItemStack(sii.getMaterial(), sii.getSellAmount64());
		ItemMeta im_m0 = m0.getItemMeta();
		im_m0.setDisplayName(ChatColor.GOLD + sii.getMaterial().name().replace("_", " ") + ChatColor.GREEN + " B:"
				+ sii.getBuyPrice() + ChatColor.GRAY + "R:" + infinity_symbol);
		m0.setItemMeta(im_m0);

		// sell material 1
		ItemStack m1 = new ItemStack(sii.getMaterial(), sii.getSellAmount64());
		ItemMeta im_m1 = m1.getItemMeta();
		im_m1.setDisplayName(ChatColor.GOLD + sii.getMaterial().name().replace("_", " ") + ChatColor.GREEN + " S:"
				+ sii.getSellPrice() + ChatColor.GRAY + "R:" + sii.getSellAmount());
		m1.setItemMeta(im_m1);

		// money display
		ItemStack money = new ItemStack(Material.OAK_SIGN, 1);
		ItemMeta money_meta = money.getItemMeta();
		money_meta.setDisplayName("" + ChatColor.GREEN + Shop.money.get(p) + currency);
		money.setItemMeta(money_meta);

		// material money | buy buy buy | custom buy

		items[0] = Main.makeItemStackUnmovable(m0);
		items[1] = Main.makeItemStackUnmovable(money);

		items[2] = Main.blackGlassPane.clone();
		if (sii.getBuyPrice() > 0)
		{
			items[3] = buy1.clone();
			items[4] = buy10.clone();
			items[5] = buy64.clone();
			items[8] = buy.clone();
		}
		items[7] = incAmount.clone();
		items[6] = Main.blackGlassPane.clone();
		items[9] = Main.makeItemStackUnmovable(m1);
		items[10] = backToCategory.clone();
		items[11] = Main.blackGlassPane.clone();
		if (sii.getSellPrice() > 0)
		{
			items[12] = sell1.clone();
			items[13] = sell10.clone();
			items[14] = sell64.clone();
			items[17] = sell.clone();
		}
		items[15] = Main.blackGlassPane.clone();
		items[16] = decAmount.clone();

		i.setContents(items);
		return i;
	}

	public static void updateTransactionInventory(Inventory i, Player p, ShopItemInfo sii)
	{
		ItemStack[] items = i.getContents();

		// buy material 0
		ItemStack m0 = new ItemStack(sii.getMaterial(), sii.getSellAmount64());
		ItemMeta im_m0 = m0.getItemMeta();
		im_m0.setDisplayName(ChatColor.GOLD + sii.getMaterial().name().replace("_", " ") + ChatColor.GREEN + " B:"
				+ sii.getBuyPrice() + ChatColor.GRAY + "R:" + infinity_symbol);
		m0.setItemMeta(im_m0);

		// sell material 1
		ItemStack m1 = new ItemStack(sii.getMaterial(), sii.getSellAmount64());
		ItemMeta im_m1 = m1.getItemMeta();
		im_m1.setDisplayName(ChatColor.GOLD + sii.getMaterial().name().replace("_", " ") + ChatColor.GREEN + " S:"
				+ sii.getSellPrice() + ChatColor.GRAY + "R:" + sii.getSellAmount());
		m1.setItemMeta(im_m1);

		// money display
		ItemStack money = new ItemStack(Material.OAK_SIGN, 1);
		ItemMeta money_meta = money.getItemMeta();
		money_meta.setDisplayName("" + ChatColor.GREEN + Shop.money.get(p) + currency);
		money.setItemMeta(money_meta);

		// material money | buy buy buy | custom buy

		items[0] = Main.makeItemStackUnmovable(m0);
		items[1] = Main.makeItemStackUnmovable(money);

		items[2] = Main.blackGlassPane.clone();
		if (sii.getBuyPrice() > 0)
		{
			items[3] = buy1.clone();
			items[4] = buy10.clone();
			items[5] = buy64.clone();
			items[8] = buy.clone();
		}
		items[9] = Main.makeItemStackUnmovable(m1);
		if (sii.getSellPrice() > 0)
		{
			items[12] = sell1.clone();
			items[13] = sell10.clone();
			items[14] = sell64.clone();
			items[17] = sell.clone();
		}
		i.setContents(items);
	}

	public static void setShopReady()
	{
		shopReady = true;
		Main.logger().info("Loaded shop!");
	}

	public static void setHashMap(HashMap<ShopIcons, ArrayList<ShopItemInfo>> hm,
			HashMap<Material, Integer> sellAmounts)
	{
		if (sellAmounts == null)
			for (Map.Entry<ShopIcons, ArrayList<ShopItemInfo>> me : hm.entrySet())
			{
				Main.logger().info("sethashmap.size:" + me.getValue().size());
				ShopItemInfo[] sii = me.getValue().toArray(new ShopItemInfo[0]);
				shopItems.put(me.getKey(), sii);
			}
		else
			for (Map.Entry<ShopIcons, ArrayList<ShopItemInfo>> me : hm.entrySet())
			{
				Main.logger().info("sethashmap.size:" + me.getValue().size() + "[WITH VALUE]");
				ShopItemInfo[] sii = me.getValue().toArray(new ShopItemInfo[0]);
				shopItems.put(me.getKey(), sii);
				for (ShopItemInfo si : me.getValue())
				{
					if (sellAmounts.containsKey(si.getMaterial()))
					{
						Main.logger().info("found mapping...");
						si.setSellAmount(sellAmounts.get(si.getMaterial()));
					}
				}
			}
		generateInventories();
	}

	public static void nextPage(Player p)
	{
		ShopSession s = shopSessions.get(p);
		int prev = s.getIndex();
		int next = s.nextPage();

		if (prev != next)
		{
			p.closeInventory();
			p.openInventory(shopInventories.get(s.getCategory())[next]);
		}

	}

	public static void prevPage(Player p)
	{
		ShopSession s = shopSessions.get(p);
		int prev = s.getIndex();
		int next = s.prevPage();

		if (prev != next)
		{
			p.closeInventory();
			p.openInventory(shopInventories.get(s.getCategory())[next]);
		}
	}

	public static void buy(Player p, int i)
	{
		if (openTransactions.containsKey(p))
		{
			Transaction t = openTransactions.get(p);
			if (t.buy(i))
				updateTransactionInventory(t.getTransactionInventory(), p, t.getShopItemInfo());
		} else
		{
			Main.hm.put(p.getUniqueId(), InvType.NONE);
		}
	}

	public static void buy(Player p)
	{
		if (openTransactions.containsKey(p))
		{
			Transaction t = openTransactions.get(p);
			if (t.buy())
				updateTransactionInventory(t.getTransactionInventory(), p, t.getShopItemInfo());
		} else
		{
			Main.hm.put(p.getUniqueId(), InvType.NONE);
		}
	}

	public static void sell(Player p, int i)
	{
		if (openTransactions.containsKey(p))
		{
			Transaction t = openTransactions.get(p);
			if (t.sell(i))
				updateTransactionInventory(t.getTransactionInventory(), p, t.getShopItemInfo());
		} else
		{
			Main.hm.put(p.getUniqueId(), InvType.NONE);
		}
	}

	public static void sell(Player p)
	{
		if (openTransactions.containsKey(p))
		{
			Transaction t = openTransactions.get(p);
			if (t.sell())
				updateTransactionInventory(t.getTransactionInventory(), p, t.getShopItemInfo());
		} else
		{
			Main.hm.put(p.getUniqueId(), InvType.NONE);
		}
	}

	public static void increaseCustomAmount(Player p)
	{
		if (openTransactions.containsKey(p))
		{
			openTransactions.get(p).amountp();
			;
		}
	}

	public static void decreaseCustomAmount(Player p)
	{
		if (openTransactions.containsKey(p))
		{
			openTransactions.get(p).amounts();
			;
		}
	}

	public static ShopItemInfo getSIIByName(String s)
	{
		return indexing.get(Material.getMaterial(s));

	}

	public static int getIndex(int size, int i)
	{
		int f = 0;
		for (int e = 9; e < 54 && e < size - 9; e++)
		{
			if (e % 9 == 0 || e % 9 == 8)
			{
				continue;
			} else
			{
				if (f == i)
				{
					return e;
				} else
				{
					f++;
				}
			}
		}
		throw new RuntimeException("Out of index range.");
	}

	public static boolean checkShopInvs(Inventory i)
	{
		return shopInventoriesIndex.contains(i);
	}

	public static boolean checkTransInvs(Inventory i)
	{
		return transactionInventoriesIndex.contains(i);
	}

	public static void closeTransaction(Player p)
	{
		Transaction t = openTransactions.remove(p);
		transactionInventoriesIndex.remove(t.getTransactionInventory());
	}

	public static void resetSellAmount()
	{
		Log.warn("CLEARING ALL SELL AMOUNT VALUES");
		FileManager.getSellsWriter().clear();
		for (ShopItemInfo sii : shopItemInfos)
		{
			sii.resetSellAmount();
		}
	}

	public static void initMoney(Player player)
	{
		money.put(player, FileManager.getFileWriter().getInt(player.getUniqueId().toString() + ".money"));
	}

	public static int getMoney(Player p)
	{
		return money.get(p);
	}

	public static void setMoney(Player p, int money)
	{
		FileManager.getFileWriter().setValue(p.getUniqueId().toString() + ".money", money);
		FileManager.getFileWriter().save();
		Shop.money.put(p, money);

	}

	public static void removeItems(Inventory inventory, Material type, int amount)
	{
		if (amount <= 0)
			return;
		int size = inventory.getSize();
		for (int slot = 0; slot < size; slot++)
		{
			ItemStack is = inventory.getItem(slot);
			if (is == null)
				continue;
			if (type == is.getType())
			{
				int newAmount = is.getAmount() - amount;
				if (newAmount > 0)
				{
					is.setAmount(newAmount);
					break;
				} else
				{
					inventory.clear(slot);
					amount = -newAmount;
					if (amount == 0)
						break;
				}
			}
		}
	}

	/**
	 * @param p
	 */
	public static void toShop(Player p)
	{
		stopSession(p);
		p.closeInventory();
		p.openInventory(getShopMenu());
		Main.hm.put(p.getUniqueId(), InvType.SHOP);
	}

	/**
	 * has to be in transaction
	 * 
	 * @param p
	 */
	public static void toCategory(Player p)
	{
		Transaction t = openTransactions.remove(p);
		transactionInventoriesIndex.remove(t.getTransactionInventory());
		openCategory(getShopIconByMaterial(t.getMaterial()), p);
	}

	public static ShopIcons getShopIconByMaterial(Material m)
	{
		return categoryMembers.get(m);
	}

	public static void setName(ItemStack i, String s)
	{
		ItemMeta im = i.getItemMeta();
		im.setDisplayName(s);
		i.setItemMeta(im);
	}

	public static void stopSession(Player p)
	{
		shopSessions.remove(p);
	}

}
