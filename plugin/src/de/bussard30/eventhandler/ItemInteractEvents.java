package de.bussard30.eventhandler;

import java.util.UUID;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import de.bussard30.economy.Shop;
import de.bussard30.economy.ShopIcons;
import de.bussard30.economy.SpawnerFactory;
import de.bussard30.main.FileManager;
import de.bussard30.main.JedisManager;
import de.bussard30.main.Main;
import de.bussard30.questing.QuestSystem;
import de.bussard30.spin.Spin;
import de.bussard30.types.InvType;
import net.jitse.npclib.api.NPC;
import net.md_5.bungee.api.ChatColor;

public class ItemInteractEvents implements Listener
{
	private static ItemStack elytra;

	public ItemInteractEvents()
	{
		elytra = new ItemStack(Material.ELYTRA);
		ItemMeta im = elytra.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "Elytra" + ChatColor.GRAY + "(Destroys on click)");
		elytra.setItemMeta(im);
		elytra = JedisManager.nbtwrapper.setNBTTag("execute", "delete_elytra", elytra);

	}

	@EventHandler
	public void onCraft(CraftItemEvent e)
	{
		Player p = (Player) e.getWhoClicked();
		Bukkit.getLogger().info("CraftItemEvent triggered.");
		if (e.getRecipe() instanceof ShapedRecipe)
		{
			if (e.getInventory().getResult().isSimilar(Main.BACKPACK))
			{
				Bukkit.getLogger().info("Result: Backpack");
				ItemStack is = e.getInventory().getResult().clone();
				UUID uid;
				while (JedisManager.containsBackpack((uid = UUID.randomUUID())))
					;
				Bukkit.getLogger().info("Attached uuid:<" + uid.toString() + ">. or <"
						+ new String(JedisManager.getBytesFromUUID(uid)) + ">");
				ItemStack newItemStack0 = JedisManager.nbtwrapper.setNBTTag("uuid",
						new String(JedisManager.getBytesFromUUID(uid)), is);
				ItemStack newItemStack = JedisManager.nbtwrapper.setNBTTag("backpack_type", "true", newItemStack0);
				Bukkit.getLogger().info("This uuid has been added: <"
						+ new String(JedisManager.nbtwrapper.getNBTTag("uuid", newItemStack)) + ">");
				e.getInventory().setResult(newItemStack);
				JedisManager.createNewBackpack(newItemStack);
			} else
			{
				Bukkit.getLogger().info("Something else called: " + e.getInventory().getResult().toString());
			}
		} else
		{
			Bukkit.getLogger().info("Something else called: " + e.getInventory().getResult().toString());
		}
	}

	@EventHandler
	public synchronized void onPlayerInteraction(PlayerInteractEvent e)
	{
		if (e.getItem() != null)
		{
			Bukkit.getLogger().info("int");
			String tag = null;
			if (e.getItem() != null && (tag = JedisManager.nbtwrapper.getNBTTag("backpack_type", e.getItem())) != null)
				if (tag.equals("true"))
				{
					Bukkit.getLogger().info("int#");
					e.setCancelled(true);
					Inventory i = JedisManager.getBackpackContent(e.getItem());
					e.getPlayer().openInventory(i);
					Main.hm.put(e.getPlayer().getUniqueId(), InvType.BACKPACK);
				}
		}
	}

	@EventHandler
	public synchronized void onPlayerBlockPlace(BlockPlaceEvent e)
	{
		if (e.getItemInHand() != null)
		{
			try
			{
				if (JedisManager.nbtwrapper.getNBTTag("backpack_type", e.getItemInHand()).equals("true"))
				{
					e.setCancelled(true);
					Inventory i = JedisManager.getBackpackContent(e.getItemInHand());
					e.getPlayer().openInventory(i);
					Main.hm.put(e.getPlayer().getUniqueId(), InvType.BACKPACK);
				} else if (JedisManager.nbtwrapper.getNBTTag("onPlace", e.getItemInHand()).startsWith("spawner"))
				{
					if (SpawnerFactory.getSpawnerBlock(e.getItemInHand(), e.getBlockPlaced().getLocation(),
							e.getPlayer()))
					{
						e.getPlayer().sendMessage(ChatColor.GRAY + "Spawner placed!");
					} else
					{
						e.getPlayer().sendMessage(ChatColor.RED + "Could not place spawner.");
					}

				}
			} catch (NullPointerException npe)
			{

			}
		}

	}

	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent e)
	{
		if (JedisManager.nbtwrapper.getNBTTag("unmovable", e.getItem()) == "true")
		{
			e.setCancelled(true);
		}
		if (e.getInitiator().getHolder() != null || e.getDestination().getHolder() != null)
		{
			if (e.getInitiator().getHolder() instanceof Player)
			{
				Player p = (Player) e.getInitiator().getHolder();
				if (Main.hm.get(p.getUniqueId()).equals(InvType.SHOP)
						|| Main.hm.get(p.getUniqueId()).equals(InvType.SPIN))
				{
					e.setCancelled(true);
				}
			} else if (e.getDestination().getHolder() instanceof Player)
			{
				Player p = (Player) e.getDestination().getHolder();
				if (Main.hm.get(p.getUniqueId()).equals(InvType.SHOP)
						|| Main.hm.get(p.getUniqueId()).equals(InvType.SPIN))
				{
					e.setCancelled(true);
				}
			}
		}
		if (e.getInitiator().getViewers().size() == 1)
		{
			ItemStack backpack = e.getInitiator().getViewers().get(0).getInventory().getItemInMainHand();
			if (JedisManager.nbtwrapper.getNBTTag("backpack_type", backpack).equals("true"))
			{
				if (e.getSource().getType().equals(InventoryType.PLAYER))
				{
					JedisManager.saveBackpackContent(backpack, e.getDestination());
				} else
				{
					JedisManager.saveBackpackContent(backpack, e.getSource());
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e)
	{
		if (e.getPlayer() instanceof Player)
		{
			try
			{
				InvType i = Main.hm.get(((Player) e.getPlayer()).getUniqueId());
				if (i.equals(InvType.SPIN))
				{
					Integer in = Spin.spinqueue.get((Player) e.getPlayer());
					if (in != null)
						Spin.end((Player) e.getPlayer());
					Main.hm.put(((Player) e.getPlayer()).getUniqueId(), InvType.NONE);
				} else if (i.equals(InvType.TRANSACTION))
				{
					if (!Shop.checkTransInvs(e.getPlayer().getOpenInventory().getTopInventory()))
					{
						Main.hm.put(((Player) e.getPlayer()).getUniqueId(), InvType.NONE);
					}
					Shop.closeTransaction((Player) e.getPlayer());
				} else if (Main.hm.get(e.getPlayer().getUniqueId()).equals(InvType.SHOP))
				{
					if (!Shop.checkShopInvs(e.getPlayer().getOpenInventory().getTopInventory()))
					{
						Main.hm.put(((Player) e.getPlayer()).getUniqueId(), InvType.NONE);
					}
					Shop.stopSession((Player) e.getPlayer());
				}

			} catch (NullPointerException npe)
			{

			}

		}

		if (e.getViewers().size() == 1)
		{
			ItemStack backpack = e.getViewers().get(0).getInventory().getItemInMainHand();
			if (JedisManager.nbtwrapper.getNBTTag("backpack_type", backpack) == "true")
			{
				JedisManager.saveBackpackContent(backpack, e.getInventory());
				Main.hm.put(e.getPlayer().getUniqueId(), InvType.NONE);
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		String tag = null;
		if (e.getWhoClicked() instanceof Player)
		{
			if ((tag = JedisManager.nbtwrapper.getNBTTag("execute", e.getCurrentItem())) != null)
			{
				if (tag.startsWith("buy_spin"))
				{
					Main.logger().info("BUYING SPIN!");
					Spin.buySpin((Player) e.getWhoClicked());
					e.setResult(Result.DENY);
				} else if (tag.startsWith("next_page"))
				{
					e.setResult(Result.DENY);
					Shop.nextPage((Player) e.getWhoClicked());
				} else if (tag.startsWith("prev_page"))
				{
					e.setResult(Result.DENY);
					Shop.prevPage((Player) e.getWhoClicked());
				} else if (tag.startsWith("spin"))

				{
					e.setResult(Result.DENY);
					Spin.startSpin((Player) e.getWhoClicked());

				} else if (tag.startsWith("show_shop"))
				{
					e.setResult(Result.DENY);
					if (tag.split(" ").length == 2)
					{
						Shop.openCategory(ShopIcons.getByName(tag.split(" ")[1]), (Player) e.getWhoClicked());
					}

				} else if (tag.startsWith("open_transaction"))
				{
					e.setResult(Result.DENY);
					if (tag.split(" ").length == 2)
					{
						Shop.openTransactionInventory((Player) e.getWhoClicked(), Shop.getSIIByName(tag.split(" ")[1]));
					}

					// example: execute buy_item1
				} else if (tag.startsWith("buy_item"))
				{
					e.setResult(Result.DENY);
					switch (tag)
					{
					case "buy_item":
						Shop.buy((Player) e.getWhoClicked());
						break;
					case "buy_item1":
						Shop.buy((Player) e.getWhoClicked(), 1);
						break;
					case "buy_item10":
						Shop.buy((Player) e.getWhoClicked(), 10);
						break;
					case "buy_item64":
						Shop.buy((Player) e.getWhoClicked(), 64);
						break;
					}
				} else if (tag.startsWith("sell_item"))
				{
					e.setResult(Result.DENY);
					switch (tag)
					{
					case "sell_item":
						Shop.sell((Player) e.getWhoClicked());
						break;
					case "sell_item1":
						Shop.sell((Player) e.getWhoClicked(), 1);
						break;
					case "sell_item10":
						Shop.sell((Player) e.getWhoClicked(), 10);
						break;
					case "sell_item64":
						Shop.sell((Player) e.getWhoClicked(), 64);
						break;
					}
					// example: execute confirm_item_buy 32
				}
				// in shop transaction you can click on paper to increase amount
				// and custom buy
				else if (tag.startsWith("stack_custom"))
				{
					e.setResult(Result.DENY);
					if (tag.split(" ").length == 2)
					{
						if (tag.split(" ")[1].equals("0"))
						{
							// decrease amount
							Shop.decreaseCustomAmount((Player) e.getWhoClicked());
						} else if (tag.split(" ")[1].equals("1"))
						{
							// increase amount
							Shop.increaseCustomAmount((Player) e.getWhoClicked());
						}
					}
				} else if (tag.startsWith("delete_elytra"))
				{
					e.setResult(Result.DENY);
					Player p = (Player) e.getWhoClicked();
					p.getInventory().setChestplate(null);
				} else if (tag.startsWith("back_to_shop"))
				{
					e.setResult(Result.DENY);
					Shop.toShop((Player) e.getWhoClicked());
				} else if (tag.startsWith("back_to_category"))
				{
					e.setResult(Result.DENY);
					Shop.toCategory((Player) e.getWhoClicked());
				}

			}
			if (Main.hm.get(((Player) e.getWhoClicked()).getUniqueId()).equals(InvType.SHOP))
			{
				if (Shop.checkShopInvs(e.getInventory()) || Shop.checkShopInvs(e.getClickedInventory()))
				{
					e.setResult(Result.DENY);
				}
			} else if (Main.hm.get(((Player) e.getWhoClicked()).getUniqueId()).equals(InvType.SPIN))
			{
				e.setResult(Result.DENY);
			} else if (Main.hm.get(((Player) e.getWhoClicked()).getUniqueId()).equals(InvType.TRANSACTION))
			{
				if (Shop.checkTransInvs(e.getInventory()) || Shop.checkTransInvs(e.getClickedInventory()))
				{
					e.setResult(Result.DENY);
				}
			} else
			{
				if (Shop.checkTransInvs(e.getInventory()) || Shop.checkTransInvs(e.getClickedInventory()))
				{
					e.setResult(Result.DENY);
				}
				if (Shop.checkShopInvs(e.getInventory()) || Shop.checkShopInvs(e.getClickedInventory()))
				{
					e.setResult(Result.DENY);
				}
			}
		}

		if (JedisManager.nbtwrapper.getNBTTag("unmovable", e.getCurrentItem()) == "true")
		{
			e.setResult(Result.DENY);
		}
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Main.hm.put(e.getPlayer().getUniqueId(), InvType.NONE);
		for (NPC n : (Vector<NPC>) QuestSystem.npcs.clone())
		{
			n.show(e.getPlayer());
		}
		Main.defaultValues(e.getPlayer());
		Shop.initMoney(e.getPlayer());
		if (FileManager.getFileWriter().getBoolean(e.getPlayer().getUniqueId().toString() + ".free_elytra"))
		{
			FileManager.getFileWriter().setValue(e.getPlayer().getUniqueId().toString() + ".free_elytra", false);
			FileManager.getFileWriter().save();

			e.getPlayer().getInventory().setChestplate(elytra.clone());
			e.getPlayer().sendMessage("You received an elytra for joining the first time!");
		}
	}

	public void onPlayerLeave(PlayerQuitEvent e)
	{
		Main.hm.remove(e.getPlayer().getUniqueId());
		QuestSystem.cancelQuestCreatingSession(e.getPlayer());
	}
}
