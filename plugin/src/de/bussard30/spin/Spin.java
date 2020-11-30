package de.bussard30.spin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.bussard30.economy.Shop;
import de.bussard30.main.FileManager;
import de.bussard30.main.JedisManager;
import de.bussard30.main.Main;
import de.bussard30.types.InvType;
import net.md_5.bungee.api.ChatColor;

public class Spin implements CommandExecutor
{
	public static final double p1 = 0.30d;
	public static final double p2 = 0.40d;
	public static final double p3 = 0.225d;
	public static final double p4 = 0.062d;
	public static final double p5 = 0.013d;

	public static final double p4inc = 0.004d;
	public static final double p5inc = 0.002d;

	public static ItemFetcher[] s1;
	public static ItemFetcher[] s2;
	public static ItemFetcher[] s3;
	public static ItemFetcher[] s4;
	public static FiveStarItemFetcher s5;

	private static Random r;

	private static HashMap<Player, ItemStack[]> spinitems;
	private static HashMap<Player, Integer> spintier;
	private static HashMap<Player, Inventory> spins;
	public static HashMap<Player, Integer> spinqueue;
	private static HashMap<Player, Integer> spinpos;
	private static HashMap<Player, Long> spintime;
	private static HashMap<Player, Boolean> status;

	public static final String star = "\u2606";

	public static int priceForSpin = 40000;

	public Spin()
	{
		initArrays();
		r = new Random();
		spins = new HashMap<>();
		spinqueue = new HashMap<>();
		spinpos = new HashMap<>();
		spintime = new HashMap<>();
		spinitems = new HashMap<>();
		status = new HashMap<>();
		spintier = new HashMap<>();

	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3)
	{
		if (arg0 instanceof Player)
		{
			Player p = (Player) arg0;

			if (Main.hm.get(p.getUniqueId()).equals(InvType.NONE))
			{
				Main.hm.put(p.getUniqueId(), InvType.SPIN);
			} else
			{
				p.closeInventory();
			}
			p.openInventory(getStartInventory(p));
			return true;
		}
		return false;
	}

	private static final int spin_size = 100;

	public static void startSpin(Player p)
	{
		if (FileManager.getFileWriter().getInt(p.getUniqueId().toString() + ".spins") == 0)
		{
			p.closeInventory();
			p.sendMessage("No spin remaining!");
			return;
		}
		Main.getMain().getLogger().info("Starting spin...");
		p.closeInventory();
		Inventory spin_inv = getSpinInventory(p);
		p.openInventory(spin_inv);

		spins.put(p, spin_inv);

		status.put(p, true);

		spinitems.put(p, getRandomItems(p, spin_size));

		ItemStack[] test = spinitems.get(p);
		for (int i = 0; i < test.length; i++)
		{
			if (test[i] == null)
			{
				Main.getMain().getLogger().info("Found null x000001354 at " + i);
			}
		}

		queueSpin(p, spin_size);
	}

	public static void queueSpin(Player p, int destination)
	{
		Main.getMain().getLogger().info("Scheduling spin...");
		spinpos.put(p, 0);
		spintime.put(p, System.currentTimeMillis());
		spinqueue.put(p, Main.getMain().getServer().getScheduler().scheduleSyncRepeatingTask(Main.getMain(),
				createRunnable(p, destination, 20), 0, 2));

	}

	public static Inventory getStartInventory(Player p)
	{
		Main.getMain().getLogger().info("Creating start inventory...");
		// ----------------------------------------------------------------------------------------------------------

		Inventory i = Bukkit.createInventory(null, 9);
		ItemStack[] items = new ItemStack[9];
		ItemStack money = new ItemStack(Material.EMERALD, 1);
		int money_amount = Shop.getMoney(p);

		ItemStack spins = new ItemStack(Material.PAPER, 1);
		int spins_amount = FileManager.getFileWriter().getInt(p.getUniqueId().toString() + ".spins");

		ItemStack buySpins = new ItemStack(money_amount > priceForSpin ? Material.REDSTONE : Material.GUNPOWDER, 1);

		ItemStack spin = new ItemStack(Material.DIAMOND, 1);

		// ----------------------------------------------------------------------------------------------------------

		ItemMeta money_meta = money.getItemMeta();
		money_meta.setDisplayName(ChatColor.GOLD + "Your Money:<" + ChatColor.GREEN + money_amount + ChatColor.GOLD
				+ Shop.currency + ">");

		ItemMeta spins_meta = spins.getItemMeta();
		spins_meta.setDisplayName(
				ChatColor.GOLD + "Spins available:<" + ChatColor.GRAY + spins_amount + ChatColor.GOLD + ">");

		ItemMeta buySpins_meta = buySpins.getItemMeta();
		buySpins_meta.setDisplayName(((money_amount > priceForSpin) ? ChatColor.GOLD : ChatColor.GRAY)
				+ "Buy spin for:<" + ChatColor.GREEN + priceForSpin
				+ ((money_amount > priceForSpin) ? ChatColor.GOLD : ChatColor.GRAY) + Shop.currency + ">");
		if (money_amount < priceForSpin)
		{
			ArrayList<String> l = new ArrayList<>();
			l.add(ChatColor.GRAY + "Not enough money.");
			buySpins_meta.setLore(l);
		}

		ItemMeta spin_meta = spin.getItemMeta();
		spin_meta.setDisplayName(ChatColor.GOLD + "Spin!");

		// ----------------------------------------------------------------------------------------------------------

		money.setItemMeta(money_meta);
		spins.setItemMeta(spins_meta);
		buySpins.setItemMeta(buySpins_meta);
		spin.setItemMeta(spin_meta);

		// ----------------------------------------------------------------------------------------------------------

		items[0] = Main.makeItemStackUnmovable(money);
		items[1] = Main.makeItemStackUnmovable(spins);
		items[2] = Main.makeItemStackUnmovable(JedisManager.nbtwrapper.setNBTTag("execute", "buy_spin", buySpins));
		items[4] = Main.makeItemStackUnmovable(JedisManager.nbtwrapper.setNBTTag("execute", "spin", spin));

		i.setContents(items);
		return i;
	}

	public static Inventory getSpinInventory(Player p)
	{
		Main.getMain().getLogger().info("Creating spin inv...");
		Inventory i = Bukkit.createInventory(null, 54);
		ItemStack[] items = new ItemStack[54];
		for (int f = 0; f < 54; f++)
		{
			if (f % 9 == 3 || f % 9 == 5)
			{
				items[f] = Main.blackGlassPane.clone();
			}
		}
		i.setContents(items);
		return i;
	}

	/**
	 * Method for repeating task
	 * 
	 * @param p
	 */
	public static void spin(Player p, int destination, double v)
	{
		int _spinpos = spinpos.get(p);
		long time = System.currentTimeMillis() - spintime.get(p);
		if (time == 0)
		{
			return;
		}
		double d1 = Math.abs(v - (_spinpos / time));
		double d2 = Math.abs(v - ((_spinpos + 1) / time));
		double d3 = Math.abs(v - ((_spinpos + 2) / time));
		int temp = 0;
		if (d1 < d2)
		{
			// do nothing
		} else if (d2 < d1)
		{
			if (d3 < d2)
			{
				temp = 2;
				_spinpos += 2;
			} else if (d2 < d3)
			{
				temp = 1;
				_spinpos += 1;
			} else
			{
				temp = 2;
				_spinpos += 2;
			}
		} else
		{
			temp = 1;
			_spinpos += 1;
		}
		if (_spinpos >= destination - 5)
		{
			if (_spinpos != destination - 5)
			{
				spin(p, _spinpos, destination - 5 - _spinpos);
			}
			end(p);
		} else
		{
			if (temp != 0)
			{
				spin(p, _spinpos, temp);
				spinpos.put(p, _spinpos);
			}
		}

	}

	static int a1 = 13;
	static int a2 = 22;
	static int a3 = 31;
	static int a4 = 40;
	static int a5 = 49;

	/**
	 * how many it is supposed to spin
	 * 
	 * @param i
	 */
	public static void spin(Player p, int pos, int i)
	{
		if (i == 0)
		{
			return;
		}
		Inventory in = spins.get(p);
		ItemStack[] items = spinitems.get(p);

		ItemStack[] is = in.getContents();
		is[a1] = Main.makeItemStackUnmovable(items[pos + i]);
		is[a2] = Main.makeItemStackUnmovable(items[pos + 1 + i]);
		is[a3] = Main.makeItemStackUnmovable(items[pos + 2 + i]);
		is[a4] = Main.makeItemStackUnmovable(items[pos + 3 + i]);
		is[a5] = Main.makeItemStackUnmovable(items[pos + 4 + i]);

		in.setContents(is);

		// check if necessary
		// in.setContents(is);
	}

	public static void end(Player p)
	{
		/**
		 * spins = new HashMap<>();<br>
		 * spinqueue = new HashMap<>();<br>
		 * spinpos = new HashMap<>();<br>
		 * spintime = new HashMap<>();<br>
		 * spinitems = new HashMap<>();<br>
		 * status = new HashMap<>();<br>
		 * spintier = new HashMap<>();
		 */
		Main.getMain().getLogger().info("SpinEnd");
		if (status.get(p) == false || status.get(p) == null)
		{
			return;
		} else
		{
			status.put(p, false);
		}
		spins.remove(p);
		ItemStack[] items = spinitems.remove(p);
		ItemStack i = items[items.length - 3];
		spinpos.remove(p);
		spintime.remove(p);
		spinitems.remove(p);
		int tier = spintier.remove(p);

		Main.getMain().getServer().getScheduler().cancelTask(spinqueue.remove(p));
		// end animation
		Main.getMain().getServer().getScheduler().scheduleSyncDelayedTask(Main.getMain(), new Runnable()
		{
			@Override
			public void run()
			{
				// give player item
				ItemStack ii = JedisManager.nbtwrapper.removeNBTTag("unmovable", i);
				HashMap<Integer, ItemStack> excess = p.getInventory()
						.addItem(JedisManager.nbtwrapper.removeNBTTag("execute", ii));
				for (Map.Entry<Integer, ItemStack> me : excess.entrySet())
				{
					p.getWorld().dropItem(p.getLocation(), me.getValue());
				}

				switch (tier)
				{
				case 1:
					p.sendMessage(ChatColor.GRAY + "You got a " + star + i.getType().name() + ChatColor.GRAY + "!");
					break;
				case 2:
					p.sendMessage(ChatColor.GRAY + "You got a " + ChatColor.AQUA + star + star + i.getType().name()
							+ ChatColor.GRAY + "!");
					break;
				case 3:
					p.sendMessage(ChatColor.GRAY + "You got a " + ChatColor.BLUE + star + star + star
							+ i.getType().name() + ChatColor.GRAY + "!");
					break;
				case 4:
					p.sendMessage(ChatColor.GRAY + "You got a " + ChatColor.LIGHT_PURPLE + star + star + star + star
							+ i.getType().name() + ChatColor.GRAY + "!");
					Main.getMain().getServer()
							.broadcastMessage(p.getName() + ChatColor.DARK_PURPLE + " got a " + ChatColor.LIGHT_PURPLE
									+ star + star + star + star + i.getType().name() + ChatColor.GRAY + "!");
					break;
				case 5:
					p.sendMessage(ChatColor.GRAY + "You got a " + ChatColor.GOLD + star + star + star + star + star
							+ i.getType().name() + ChatColor.GRAY + "!");
					Main.getMain().getServer()
							.broadcastMessage(p.getName() + ChatColor.DARK_PURPLE + " got a " + ChatColor.GOLD + star
									+ star + star + star + star + i.getType().name() + ChatColor.GRAY + "!");
					break;
				}

				p.closeInventory();
				status.remove(p);
				Main.hm.put(p.getUniqueId(), InvType.NONE);
				int spins = FileManager.getFileWriter().getInt(p.getUniqueId().toString() + ".spins");
				FileManager.getFileWriter().setValue(p.getUniqueId().toString() + ".spins", spins - 1);
				FileManager.getFileWriter().save();
			}

		}, 30);
	}
	
	public static void buySpin(Player p)
	{
		int money = Shop.getMoney(p);
		if(money >= priceForSpin)
		{
			p.sendMessage("Bought spin!");
			Shop.setMoney(p, money - priceForSpin);
			int spins = FileManager.getFileWriter().getInt(p.getUniqueId().toString() + ".spins");
			FileManager.getFileWriter().setValue(p.getUniqueId().toString() + ".spins", spins + 1);
			FileManager.getFileWriter().save();
		}
		else
		{
			p.sendMessage("Not enough money for spin!");
		}
	}

	public static Runnable createRunnable(Player p, int destination, double v)
	{
		Main.getMain().getLogger().info("Creating runnable for spin...");
		return new Runnable()
		{
			@Override
			public void run()
			{
				spin(p, destination, v);
			}

		};
	}

	/**
	 * All items unless size - 2 random but not actually enchanted, just
	 * enchanted picture item[size - 2] is fully enchanted(calculation takes a
	 * while)
	 * 
	 * @return
	 */
	public static ItemStack[] getRandomItems(Player p, int size)
	{
		Main.getMain().getLogger().info("Getting random items...");
		double p1 = Spin.p1;
		double p2 = Spin.p2;
		double p3 = Spin.p3;
		double p4 = Spin.p4;
		double p5 = Spin.p5;

		p1 = FileManager.getFileWriter().getDouble(p.getUniqueId().toString() + ".p1");
		p2 = FileManager.getFileWriter().getDouble(p.getUniqueId().toString() + ".p2");
		p3 = FileManager.getFileWriter().getDouble(p.getUniqueId().toString() + ".p3");
		p4 = FileManager.getFileWriter().getDouble(p.getUniqueId().toString() + ".p4");
		p5 = FileManager.getFileWriter().getDouble(p.getUniqueId().toString() + ".p5");

		double delta = (p4 - Spin.p4) + (p5 - Spin.p5);
		p1 -= 0.35 * delta;
		p2 -= 0.35 * delta;
		p3 -= 0.3 * delta;

		Main.getMain().getLogger().info("p1 " + p1);
		Main.getMain().getLogger().info("p2 " + p2);
		Main.getMain().getLogger().info("p3 " + p3);
		Main.getMain().getLogger().info("p4 " + p4);
		Main.getMain().getLogger().info("p5 " + p5);

		ItemStack[] is = new ItemStack[size];
		for (int i = 0; i < size - 3; i++)
		{
			double d = r.nextDouble();
			if (d < p1)
			{
				// drop 1 star
				is[i] = s1[r.nextInt(s1.length)].getItem(false);
			} else if (d < p1 + p2)
			{
				// drop 2 star
				is[i] = s2[r.nextInt(s2.length)].getItem(false);
			} else if (d < p1 + p2 + p3)
			{
				// drop 3 star
				is[i] = s3[r.nextInt(s3.length)].getItem(false);
			} else if (d < p1 + p2 + p3 + p4)
			{
				// drop 4 star
				is[i] = s4[r.nextInt(s4.length)].getItem(false);
			} else if (d < p1 + p2 + p3 + p4 + p5)
			{
				// drop 5 star
				is[i] = s5.getItem(false);
			}
		}
		double d = r.nextDouble();
		if (d < p1)
		{
			Main.getMain().getLogger().info("1 star drop!");
			spintier.put(p, 1);
			// drop 1 star
			is[size - 3] = s1[r.nextInt(s1.length)].getItem(true);
			pity4Star(p, p4);
			pity5Star(p, p5);
		} else if (d < p1 + p2)
		{
			Main.getMain().getLogger().info("2 star drop!");
			spintier.put(p, 2);
			// drop 2 star
			is[size - 3] = s2[r.nextInt(s2.length)].getItem(true);
			pity4Star(p, p4);
			pity5Star(p, p5);
		} else if (d < p1 + p2 + p3)
		{
			Main.getMain().getLogger().info("3 star drop!");
			spintier.put(p, 3);
			// drop 3 star
			is[size - 3] = s3[r.nextInt(s3.length)].getItem(true);
			pity4Star(p, p4);
			pity5Star(p, p5);
		} else if (d < p1 + p2 + p3 + p4)
		{
			Main.getMain().getLogger().info("4 star drop!");
			spintier.put(p, 4);
			// drop 4 star
			is[size - 3] = s4[r.nextInt(s4.length)].getItem(true);
			reset4Star(p);
			pity5Star(p, p5);
		} else if (d < p1 + p2 + p3 + p4 + p5)
		{
			Main.getMain().getLogger().info("5 star drop!");
			spintier.put(p, 5);
			// drop 5 star
			is[size - 3] = s5.getItem(true);
			pity4Star(p, p4);
			reset5Star(p);
		}

		d = r.nextDouble();
		if (d < p1)
		{
			// drop 1 star
			is[size - 2] = s1[r.nextInt(s1.length)].getItem(false);
		} else if (d < p1 + p2)
		{
			// drop 2 star
			is[size - 2] = s2[r.nextInt(s2.length)].getItem(false);
		} else if (d < p1 + p2 + p3)
		{
			// drop 3 star
			is[size - 2] = s3[r.nextInt(s3.length)].getItem(false);
		} else if (d < p1 + p2 + p3 + p4)
		{
			// drop 4 star
			is[size - 2] = s4[r.nextInt(s4.length)].getItem(false);
		} else if (d < p1 + p2 + p3 + p4 + p5)
		{
			// drop 5 star
			is[size - 2] = s5.getItem(false);
		}

		d = r.nextDouble();
		if (d < p1)
		{
			// drop 1 star
			is[size - 1] = s1[r.nextInt(s1.length)].getItem(false);
		} else if (d < p1 + p2)
		{
			// drop 2 star
			is[size - 1] = s2[r.nextInt(s2.length)].getItem(false);
		} else if (d < p1 + p2 + p3)
		{
			// drop 3 star
			is[size - 1] = s3[r.nextInt(s3.length)].getItem(false);
		} else if (d < p1 + p2 + p3 + p4)
		{
			// drop 4 star
			is[size - 1] = s4[r.nextInt(s4.length)].getItem(false);
		} else if (d < p1 + p2 + p3 + p4 + p5)
		{
			// drop 5 star
			is[size - 1] = s5.getItem(false);
		}
		return is;
	}

	private static void reset4Star(Player p)
	{
		FileManager.getFileWriter().setValue(p.getUniqueId().toString() + ".p4", p4);
		FileManager.getFileWriter().save();
	}

	private static void reset5Star(Player p)
	{
		FileManager.getFileWriter().setValue(p.getUniqueId().toString() + ".p5", p5);
		FileManager.getFileWriter().save();
	}

	private static void pity4Star(Player p, double p4)
	{
		Main.getMain().getLogger().info("Pity 4.");
		FileManager.getFileWriter().setValue(p.getUniqueId().toString() + ".p4", p4 + p4inc);
		FileManager.getFileWriter().save();
	}

	private static void pity5Star(Player p, double p5)
	{
		Main.getMain().getLogger().info("Pity 5.");
		FileManager.getFileWriter().setValue(p.getUniqueId().toString() + ".p5", p5 + p5inc);
		FileManager.getFileWriter().save();
	}

	private void initArrays()
	{
		s1 = new ItemFetcher[]
		{ new ItemFetcher()
		{
			@Override
			protected ItemStack getItem(boolean enchanted)
			{
				// tools
				switch (r.nextInt(3))
				{
				case 0:
					return RandomEnchanter.enchant(new ItemStack(Material.GOLDEN_SHOVEL), enchanted);
				case 1:
					return RandomEnchanter.enchant(new ItemStack(Material.GOLDEN_PICKAXE), enchanted);
				case 2:
					return RandomEnchanter.enchant(new ItemStack(Material.GOLDEN_AXE), enchanted);
				default:
					break;
				}
				return null;
			}
		}, new ItemFetcher()
		{
			@Override
			protected ItemStack getItem(boolean enchanted)
			{
				// sword
				return RandomEnchanter.enchant(new ItemStack(Material.GOLDEN_SWORD), enchanted);
			}
		}, new ItemFetcher()
		{
			@Override
			protected ItemStack getItem(boolean enchanted)
			{
				// armor piece
				switch (r.nextInt(4))
				{
				case 0:
					return RandomEnchanter.enchant(new ItemStack(Material.GOLDEN_HELMET), enchanted);
				case 1:
					return RandomEnchanter.enchant(new ItemStack(Material.GOLDEN_CHESTPLATE), enchanted);
				case 2:
					return RandomEnchanter.enchant(new ItemStack(Material.GOLDEN_LEGGINGS), enchanted);
				case 3:
					return RandomEnchanter.enchant(new ItemStack(Material.GOLDEN_BOOTS), enchanted);
				default:
					break;

				}
				return null;
			}
		}, new ItemFetcher()
		{
			@Override
			protected ItemStack getItem(boolean enchanted)
			{
				return new ItemStack(Material.COAL, 32);
			}
		}, new ItemFetcher()
		{
			@Override
			protected ItemStack getItem(boolean enchanted)
			{
				// cookies
				return new ItemStack(Material.COOKIE, 10);
			}
		} };

		s2 = new ItemFetcher[]
		{ new ItemFetcher()
		{

			@Override
			protected ItemStack getItem(boolean enchanted)
			{
				// tools
				switch (r.nextInt(3))
				{
				case 0:
					return RandomEnchanter.enchant(new ItemStack(Material.IRON_SHOVEL), enchanted);
				case 1:
					return RandomEnchanter.enchant(new ItemStack(Material.IRON_PICKAXE), enchanted);
				case 2:
					return RandomEnchanter.enchant(new ItemStack(Material.IRON_AXE), enchanted);
				default:
					break;
				}
				return null;
			}
		}, new ItemFetcher()
		{
			@Override
			protected ItemStack getItem(boolean enchanted)
			{
				// sword
				return RandomEnchanter.enchant(new ItemStack(Material.IRON_SWORD), enchanted);
			}
		}, new ItemFetcher()
		{
			@Override
			protected ItemStack getItem(boolean enchanted)
			{
				// armor piece
				switch (r.nextInt(4))
				{
				case 0:
					return RandomEnchanter.enchant(new ItemStack(Material.IRON_HELMET), enchanted);
				case 1:
					return RandomEnchanter.enchant(new ItemStack(Material.IRON_CHESTPLATE), enchanted);
				case 2:
					return RandomEnchanter.enchant(new ItemStack(Material.IRON_LEGGINGS), enchanted);
				case 3:
					return RandomEnchanter.enchant(new ItemStack(Material.IRON_BOOTS), enchanted);
				default:
					break;

				}
				return null;
			}
		}, new ItemFetcher()
		{
			@Override
			protected ItemStack getItem(boolean enchanted)
			{
				return new ItemStack(Material.IRON_INGOT, 32);
			}
		}, new ItemFetcher()
		{
			@Override
			protected ItemStack getItem(boolean enchanted)
			{
				// cookies
				return new ItemStack(Material.BREAD, 16);
			}
		} };

		s3 = new ItemFetcher[]
		{ new ItemFetcher()
		{

			@Override
			protected ItemStack getItem(boolean enchanted)
			{
				// tools
				switch (r.nextInt(3))
				{
				case 0:
					return RandomEnchanter.enchantLow(new ItemStack(Material.DIAMOND_SHOVEL), enchanted);
				case 1:
					return RandomEnchanter.enchantLow(new ItemStack(Material.DIAMOND_PICKAXE), enchanted);
				case 2:
					return RandomEnchanter.enchantLow(new ItemStack(Material.DIAMOND_AXE), enchanted);
				default:
					break;
				}
				return null;
			}
		}, new ItemFetcher()
		{
			@Override
			protected ItemStack getItem(boolean enchanted)
			{
				// sword
				switch (r.nextInt(4))
				{
				case 0:
					return RandomEnchanter.enchantLow(new ItemStack(Material.DIAMOND_SWORD), enchanted);
				case 1:
					return RandomEnchanter.enchantLow(new ItemStack(Material.TRIDENT), enchanted);
				case 2:
					return RandomEnchanter.enchantLow(new ItemStack(Material.BOW), enchanted);
				case 3:
					return RandomEnchanter.enchantLow(new ItemStack(Material.CROSSBOW), enchanted);
				default:
					break;
				}
				return null;
			}
		}, new ItemFetcher()
		{
			@Override
			protected ItemStack getItem(boolean enchanted)
			{
				// armor piece
				switch (r.nextInt(4))
				{
				case 0:
					return RandomEnchanter.enchantLow(new ItemStack(Material.DIAMOND_HELMET), enchanted);
				case 1:
					return RandomEnchanter.enchantLow(new ItemStack(Material.DIAMOND_CHESTPLATE), enchanted);
				case 2:
					return RandomEnchanter.enchantLow(new ItemStack(Material.DIAMOND_LEGGINGS), enchanted);
				case 3:
					return RandomEnchanter.enchantLow(new ItemStack(Material.DIAMOND_BOOTS), enchanted);
				default:
					break;

				}
				return null;
			}
		}, new ItemFetcher()
		{
			@Override
			protected ItemStack getItem(boolean enchanted)
			{
				return new ItemStack(Material.GOLD_INGOT, 32);
			}
		}, new ItemFetcher()
		{
			@Override
			protected ItemStack getItem(boolean enchanted)
			{
				// cookies
				return new ItemStack(Material.COOKED_BEEF, 16);
			}
		} };

		s4 = new ItemFetcher[]
		{ new ItemFetcher()
		{

			@Override
			protected ItemStack getItem(boolean enchanted)
			{
				// tools
				switch (r.nextInt(3))
				{
				case 0:
					return RandomEnchanter.enchant(new ItemStack(Material.DIAMOND_SHOVEL), enchanted);
				case 1:
					return RandomEnchanter.enchant(new ItemStack(Material.DIAMOND_PICKAXE), enchanted);
				case 2:
					return RandomEnchanter.enchant(new ItemStack(Material.DIAMOND_AXE), enchanted);
				default:
					break;
				}
				return null;
			}
		}, new ItemFetcher()
		{
			@Override
			protected ItemStack getItem(boolean enchanted)
			{
				// sword
				switch (r.nextInt(4))
				{
				case 0:
					return RandomEnchanter.enchant(new ItemStack(Material.DIAMOND_SWORD), enchanted);
				case 1:
					return RandomEnchanter.enchant(new ItemStack(Material.TRIDENT), enchanted);
				case 2:
					return RandomEnchanter.enchant(new ItemStack(Material.BOW), enchanted);
				case 3:
					return RandomEnchanter.enchant(new ItemStack(Material.CROSSBOW), enchanted);
				default:
					break;
				}
				return null;

			}
		}, new ItemFetcher()
		{
			@Override
			protected ItemStack getItem(boolean enchanted)
			{
				// armor piece
				switch (r.nextInt(4))
				{
				case 0:
					return RandomEnchanter.enchant(new ItemStack(Material.DIAMOND_HELMET), enchanted);
				case 1:
					return RandomEnchanter.enchant(new ItemStack(Material.DIAMOND_CHESTPLATE), enchanted);
				case 2:
					return RandomEnchanter.enchant(new ItemStack(Material.DIAMOND_LEGGINGS), enchanted);
				case 3:
					return RandomEnchanter.enchant(new ItemStack(Material.DIAMOND_BOOTS), enchanted);
				default:
					break;

				}
				return null;
			}
		}, new ItemFetcher()
		{
			@Override
			protected ItemStack getItem(boolean enchanted)
			{
				return new ItemStack(Material.DIAMOND, 16);
			}
		}, new ItemFetcher()
		{
			@Override
			protected ItemStack getItem(boolean enchanted)
			{
				// cookies
				return new ItemStack(Material.CAKE, 16);
			}
		} };

		s5 = new FiveStarItemFetcher();

	}

	protected abstract class ItemFetcher
	{
		protected ItemFetcher()
		{

		}

		protected abstract ItemStack getItem(boolean enchanted);
	}

	protected class FiveStarItemFetcher extends ItemFetcher
	{
		private HashMap<Material, ItemStack> items = new HashMap<>();
		private HashMap<ItemStack, Integer> remainingItemAmount = new HashMap<>();

		ArrayList<ItemStack> s5Items = new ArrayList<>();
		ArrayList<ItemStack> remainingItems = new ArrayList<>();

		public FiveStarItemFetcher()
		{

			// ----------------------------------------------------------------------------------------------------
			ItemStack netherite_pickaxe = new ItemStack(Material.NETHERITE_PICKAXE, 1);
			netherite_pickaxe.addEnchantment(Enchantment.DIG_SPEED, 5);
			netherite_pickaxe.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 2);
			netherite_pickaxe.addEnchantment(Enchantment.DURABILITY, 3);
			netherite_pickaxe.addEnchantment(Enchantment.MENDING, 1);

			ItemMeta im0 = netherite_pickaxe.getItemMeta();
			im0.setDisplayName(ChatColor.GOLD + "Netherite Pickaxe");
			netherite_pickaxe.setItemMeta(im0);

			ItemStack netherite_sword = new ItemStack(Material.NETHERITE_SWORD, 1);
			netherite_sword.addEnchantment(Enchantment.DAMAGE_ALL, 4);
			netherite_sword.addEnchantment(Enchantment.FIRE_ASPECT, 2);
			netherite_sword.addEnchantment(Enchantment.KNOCKBACK, 1);
			netherite_sword.addEnchantment(Enchantment.LOOT_BONUS_MOBS, 3);
			netherite_sword.addEnchantment(Enchantment.SWEEPING_EDGE, 3);
			netherite_sword.addEnchantment(Enchantment.DURABILITY, 3);
			netherite_sword.addEnchantment(Enchantment.MENDING, 1);

			ItemMeta netherite_sword_meta = netherite_sword.getItemMeta();
			netherite_sword_meta.setDisplayName(ChatColor.GOLD + "Netherite Sword");
			netherite_sword.setItemMeta(netherite_sword_meta);

			ItemStack bow = new ItemStack(Material.BOW, 1);
			bow.addEnchantment(Enchantment.ARROW_DAMAGE, 5);
			bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
			bow.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
			bow.addEnchantment(Enchantment.ARROW_FIRE, 1);
			bow.addEnchantment(Enchantment.DURABILITY, 3);
			bow.addEnchantment(Enchantment.MENDING, 1);

			ItemMeta bow_meta = bow.getItemMeta();
			bow_meta.setDisplayName(ChatColor.GOLD + "Bow");
			bow.setItemMeta(bow_meta);

			ItemStack netherite_helmet = new ItemStack(Material.NETHERITE_HELMET, 1);
			netherite_helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
			netherite_helmet.addEnchantment(Enchantment.WATER_WORKER, 1);
			netherite_helmet.addEnchantment(Enchantment.OXYGEN, 3);
			netherite_helmet.addEnchantment(Enchantment.THORNS, 3);
			netherite_helmet.addEnchantment(Enchantment.DURABILITY, 3);
			netherite_helmet.addEnchantment(Enchantment.MENDING, 1);

			ItemMeta netherite_helmet_meta = netherite_helmet.getItemMeta();
			netherite_helmet_meta.setDisplayName(ChatColor.GOLD + "Netherite Helmet");
			netherite_helmet.setItemMeta(netherite_helmet_meta);

			ItemStack netherite_chestplate = new ItemStack(Material.NETHERITE_CHESTPLATE, 1);
			netherite_chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
			netherite_chestplate.addEnchantment(Enchantment.THORNS, 3);
			netherite_chestplate.addEnchantment(Enchantment.DURABILITY, 3);
			netherite_chestplate.addEnchantment(Enchantment.MENDING, 1);

			ItemMeta netherite_chestplate_meta = netherite_chestplate.getItemMeta();
			netherite_chestplate_meta.setDisplayName(ChatColor.GOLD + "Netherite Chestplate");
			netherite_chestplate.setItemMeta(netherite_chestplate_meta);

			ItemStack netherite_leggings = new ItemStack(Material.NETHERITE_LEGGINGS, 1);
			netherite_leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
			netherite_leggings.addEnchantment(Enchantment.THORNS, 3);
			netherite_leggings.addEnchantment(Enchantment.DURABILITY, 3);
			netherite_leggings.addEnchantment(Enchantment.MENDING, 1);

			ItemMeta netherite_leggings_meta = netherite_leggings.getItemMeta();
			netherite_leggings_meta.setDisplayName(ChatColor.GOLD + "Netherite Leggings");
			netherite_leggings.setItemMeta(netherite_leggings_meta);

			ItemStack netherite_boots = new ItemStack(Material.NETHERITE_BOOTS, 1);
			netherite_boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
			netherite_boots.addEnchantment(Enchantment.PROTECTION_FALL, 3);
			netherite_boots.addEnchantment(Enchantment.FROST_WALKER, 2);
			netherite_boots.addEnchantment(Enchantment.SOUL_SPEED, 3);
			netherite_boots.addEnchantment(Enchantment.THORNS, 3);
			netherite_boots.addEnchantment(Enchantment.DURABILITY, 3);
			netherite_boots.addEnchantment(Enchantment.MENDING, 1);

			ItemMeta netherite_boots_meta = netherite_boots.getItemMeta();
			netherite_boots_meta.setDisplayName(ChatColor.GOLD + "Netherite Boots");
			netherite_boots.setItemMeta(netherite_boots_meta);

			ItemStack cookie = new ItemStack(Material.COOKIE, 5);
			ItemMeta cookie_meta = cookie.getItemMeta();
			cookie_meta.setDisplayName(ChatColor.GOLD + "Emil's Weinachtscookie");
			cookie.setItemMeta(cookie_meta);

			ItemStack netherite_ingot = new ItemStack(Material.NETHERITE_INGOT, 4);

			// ----------------------------------------------------------------------------------------------------

			s5Items.add(netherite_boots);
			s5Items.add(netherite_chestplate);
			s5Items.add(netherite_helmet);
			s5Items.add(netherite_ingot);
			s5Items.add(netherite_leggings);
			s5Items.add(netherite_pickaxe);
			s5Items.add(netherite_sword);
			s5Items.add(cookie);
			s5Items.add(bow);

			// ----------------------------------------------------------------------------------------------------

			for (ItemStack i : s5Items)
			{
				items.put(i.getType(), i);
			}

			// ----------------------------------------------------------------------------------------------------

			if (FileManager.getFileWriter().getYamlConfiguration().contains("five-star-drops"))
			{
				for (String s : FileManager.getFileWriter().getSection("five-star-drops").getKeys(false))
				{
					remainingItemAmount.put(items.get(Material.getMaterial(s)),
							FileManager.getFileWriter().getInt("five-star-drops." + s));
				}

			} else
			{
				for (ItemStack i : s5Items)
				{
					FileManager.getFileWriter().setValue("five-star-drops." + i.getType().name(), 1);
					FileManager.getFileWriter().save();
					remainingItemAmount.put(i, 2);
				}
			}

			for (Map.Entry<ItemStack, Integer> me : remainingItemAmount.entrySet())
			{
				if (me.getValue() > 0)
				{
					remainingItems.add(me.getKey());
				}
			}
		}

		@Override
		protected ItemStack getItem(boolean enchanted)
		{
			ItemStack[] is = remainingItems.toArray(new ItemStack[0]);
			ItemStack result = is[r.nextInt(is.length)];
			int remaining = remainingItemAmount.get(result) - 1;
			if (enchanted)
			{
				FileManager.getFileWriter().setValue("five-star-drops." + result.getType().name(), remaining);
				FileManager.getFileWriter().save();
				remainingItemAmount.put(result, remainingItemAmount.get(result) - 1);
				if (remaining <= 0)
				{
					remainingItems.remove(result);
				}
			}
			return result.clone();
		}
	}

}
