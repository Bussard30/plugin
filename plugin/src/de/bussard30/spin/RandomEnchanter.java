package de.bussard30.spin;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_16_R2.ItemArmor;

public class RandomEnchanter
{
	private static HashMap<Material, Vector<Enchantment>> assignments;
	private static HashMap<Enchantment, Integer> weight;
	private static HashMap<Material, Integer> baseEnchantmentLevel;

	private static Vector<Enchantment> vanillaEnchantments;
	private static Vector<Enchantment> pluginEnchantments;
	private static Vector<Enchantment> allEnchantments;
	private static Random r = new Random();

	public RandomEnchanter()
	{
		weight = new HashMap<Enchantment, Integer>();
		assignments = new HashMap<Material, Vector<Enchantment>>();
		vanillaEnchantments = new Vector<Enchantment>();
		baseEnchantmentLevel = new HashMap<Material, Integer>();
		allEnchantments = new Vector<Enchantment>();
		pluginEnchantments = new Vector<Enchantment>();

		for (Enchantment e : Enchantment.values())
			vanillaEnchantments.add(e);

		// WEIGHT FOR ENCHANTMENTS
		weight.put(Enchantment.PROTECTION_ENVIRONMENTAL, 10);
		weight.put(Enchantment.PROTECTION_FALL, 5);
		weight.put(Enchantment.PROTECTION_FIRE, 5);
		weight.put(Enchantment.PROTECTION_PROJECTILE, 5);
		weight.put(Enchantment.OXYGEN, 2);
		weight.put(Enchantment.PROTECTION_EXPLOSIONS, 2);
		weight.put(Enchantment.WATER_WORKER, 2);
		weight.put(Enchantment.DEPTH_STRIDER, 2);
		weight.put(Enchantment.FROST_WALKER, 2);
		weight.put(Enchantment.THORNS, 1);
		weight.put(Enchantment.BINDING_CURSE, 1);

		weight.put(Enchantment.DAMAGE_ALL, 10);
		weight.put(Enchantment.DAMAGE_ARTHROPODS, 5);
		weight.put(Enchantment.KNOCKBACK, 5);
		weight.put(Enchantment.DAMAGE_UNDEAD, 5);
		weight.put(Enchantment.FIRE_ASPECT, 2);
		weight.put(Enchantment.LOOT_BONUS_MOBS, 2);
		weight.put(Enchantment.SWEEPING_EDGE, 2);

		weight.put(Enchantment.DIG_SPEED, 10);
		weight.put(Enchantment.DURABILITY, 5);
		weight.put(Enchantment.LOOT_BONUS_BLOCKS, 2);
		weight.put(Enchantment.SILK_TOUCH, 1);

		weight.put(Enchantment.LUCK, 2);
		weight.put(Enchantment.LURE, 2);

		weight.put(Enchantment.MENDING, 2);
		weight.put(Enchantment.VANISHING_CURSE, 1);

		// END

		for (Material m : Material.values())
		{
			Vector<Enchantment> v = new Vector<Enchantment>();
			assignments.put(m, v);

			for (Enchantment e : allEnchantments)
			{
				if (e.getItemTarget().includes(m))
					v.add(e);
			}
		}
	}

	public static ItemStack enchantMax(ItemStack i)
	{
		return null;
	}

	public static ItemStack enchant(ItemStack i, boolean enchanted)
	{
		if (!enchanted)
		{
			i.addEnchantment(Enchantment.DURABILITY, 1);
			return i;
		}
		if (i.getType().name().endsWith("_SWORD"))
		{
			i.addEnchantment(Enchantment.DAMAGE_ALL, r.nextInt(Enchantment.DAMAGE_ALL.getMaxLevel()) + 1);
			if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.FIRE_ASPECT, r.nextInt(Enchantment.FIRE_ASPECT.getMaxLevel()) + 1);
			}

			if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.KNOCKBACK, r.nextInt(Enchantment.KNOCKBACK.getMaxLevel()) + 1);
			}

			if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.LOOT_BONUS_MOBS, r.nextInt(Enchantment.LOOT_BONUS_MOBS.getMaxLevel()) + 1);
			}

			if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.SWEEPING_EDGE, r.nextInt(Enchantment.SWEEPING_EDGE.getMaxLevel()) + 1);
			}
		} else if (i.getType().name().endsWith("_PICKAXE"))
		{
			i.addEnchantment(Enchantment.DIG_SPEED, r.nextInt(Enchantment.DIG_SPEED.getMaxLevel()) + 1);
			if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS,
						r.nextInt(Enchantment.LOOT_BONUS_BLOCKS.getMaxLevel()) + 1);
			} else if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.SILK_TOUCH, 1);
			}
		} else if (i.getType().name().endsWith("_AXE"))
		{
			i.addEnchantment(Enchantment.DIG_SPEED, r.nextInt(Enchantment.DIG_SPEED.getMaxLevel()) + 1);
			if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.SILK_TOUCH, 1);
			}
		} else if (i.getType().name().endsWith("_SHOVEL"))
		{
			i.addEnchantment(Enchantment.DIG_SPEED, r.nextInt(Enchantment.DIG_SPEED.getMaxLevel()) + 1);
		} else if (i.getType().equals(Material.BOW))
		{
			i.addEnchantment(Enchantment.ARROW_DAMAGE, r.nextInt(Enchantment.ARROW_DAMAGE.getMaxLevel()) + 1);
			if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.ARROW_FIRE, r.nextInt(Enchantment.ARROW_FIRE.getMaxLevel()) + 1);
			}

			if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.ARROW_KNOCKBACK, r.nextInt(Enchantment.ARROW_KNOCKBACK.getMaxLevel()) + 1);
			}

			if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.ARROW_INFINITE, 1);
			}
		} else if (i.getType().equals(Material.CROSSBOW))
		{
			i.addEnchantment(Enchantment.QUICK_CHARGE, r.nextInt(Enchantment.QUICK_CHARGE.getMaxLevel()) + 1);
			if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.PIERCING, r.nextInt(Enchantment.PIERCING.getMaxLevel()) + 1);
			} else if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.MULTISHOT, r.nextInt(Enchantment.MULTISHOT.getMaxLevel()) + 1);
			}
		} else if (i.getType().equals(Material.TRIDENT))
		{
			i.addEnchantment(Enchantment.IMPALING, r.nextInt(Enchantment.IMPALING.getMaxLevel()) + 1);
			switch (r.nextInt(4))
			{
			case 0:
				i.addEnchantment(Enchantment.CHANNELING, r.nextInt(Enchantment.CHANNELING.getMaxLevel()) + 1);
				break;
			case 1:
				i.addEnchantment(Enchantment.CHANNELING, r.nextInt(Enchantment.CHANNELING.getMaxLevel()) + 1);
				i.addEnchantment(Enchantment.LOYALTY, r.nextInt(Enchantment.LOYALTY.getMaxLevel()) + 1);
				break;
			case 2:
				i.addEnchantment(Enchantment.LOYALTY, r.nextInt(Enchantment.LOYALTY.getMaxLevel()) + 1);
				break;
			case 3:
				i.addEnchantment(Enchantment.RIPTIDE, r.nextInt(Enchantment.RIPTIDE.getMaxLevel()) + 1);
				break;
			default:
				break;
			}
		} else if (isArmor(i))
		{
			i.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,
					r.nextInt(Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel()) + 1);
			if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.THORNS, r.nextInt(Enchantment.THORNS.getMaxLevel()) + 1);
			}
			if (i.getType().name().endsWith("_HELMET"))
			{
				if (r.nextBoolean())
					i.addEnchantment(Enchantment.WATER_WORKER, r.nextInt(Enchantment.WATER_WORKER.getMaxLevel()) + 1);

				if (r.nextBoolean())
					i.addEnchantment(Enchantment.OXYGEN, r.nextInt(Enchantment.OXYGEN.getMaxLevel()) + 1);
			} else if (i.getType().name().endsWith("_BOOTS"))
			{
				if (r.nextBoolean())
				{
					i.addEnchantment(Enchantment.PROTECTION_FALL,
							r.nextInt(Enchantment.PROTECTION_FALL.getMaxLevel()) + 1);
				}
				if (r.nextBoolean())
				{
					i.addEnchantment(Enchantment.DEPTH_STRIDER, r.nextInt(Enchantment.DEPTH_STRIDER.getMaxLevel()) + 1);
				} else if (r.nextBoolean())
				{
					i.addEnchantment(Enchantment.FROST_WALKER, r.nextInt(Enchantment.FROST_WALKER.getMaxLevel()) + 1);
				}
				if (r.nextBoolean())
				{
					i.addEnchantment(Enchantment.SOUL_SPEED, r.nextInt(Enchantment.SOUL_SPEED.getMaxLevel()) + 1);
				}
			}
		}
		if (r.nextInt(8) < 7)
		{
			i.addEnchantment(Enchantment.DURABILITY, r.nextInt(Enchantment.DURABILITY.getMaxLevel()) + 1);
		}
		if (r.nextBoolean())
		{
			i.addEnchantment(Enchantment.MENDING, r.nextInt(Enchantment.MENDING.getMaxLevel()) + 1);
		}
		// https://minecraft.gamepedia.com/Enchanting/Levels
		// https://minecraft.gamepedia.com/Enchanting_mechanics
		return i;

	}

	public static ItemStack enchantLow(ItemStack i, boolean enchanted)
	{
		if (!enchanted)
		{
			i.addEnchantment(Enchantment.DURABILITY, 1);
			return i;
		}
		if (i.getType().name().endsWith("_SWORD"))
		{
			i.addEnchantment(Enchantment.DAMAGE_ALL, a((r.nextInt(Enchantment.DAMAGE_ALL.getMaxLevel()) + 1) / 2));
			if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.FIRE_ASPECT,
						a((r.nextInt(Enchantment.FIRE_ASPECT.getMaxLevel()) + 1) / 2));
			}

			if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.KNOCKBACK, a((r.nextInt(Enchantment.KNOCKBACK.getMaxLevel()) + 1) / 2));
			}

			if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.LOOT_BONUS_MOBS,
						(r.nextInt(Enchantment.LOOT_BONUS_MOBS.getMaxLevel()) + 1) / 2);
			}
		} else if (i.getType().name().endsWith("_PICKAXE"))
		{
			i.addEnchantment(Enchantment.DIG_SPEED, a((r.nextInt(Enchantment.DIG_SPEED.getMaxLevel()) + 1) / 2));
			if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS,
						(r.nextInt(Enchantment.LOOT_BONUS_BLOCKS.getMaxLevel()) + 1) / 2);
			}
		} else if (i.getType().name().endsWith("_AXE"))
		{
			i.addEnchantment(Enchantment.DIG_SPEED, a((r.nextInt(Enchantment.DIG_SPEED.getMaxLevel()) + 1) / 2));
		} else if (i.getType().name().endsWith("_SHOVEL"))
		{
			i.addEnchantment(Enchantment.DIG_SPEED, a((r.nextInt(Enchantment.DIG_SPEED.getMaxLevel()) + 1) / 2));
		} else if (i.getType().equals(Material.BOW))
		{
			i.addEnchantment(Enchantment.ARROW_DAMAGE, a((r.nextInt(Enchantment.ARROW_DAMAGE.getMaxLevel()) + 1) / 2));
			if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.ARROW_FIRE, a((r.nextInt(Enchantment.ARROW_FIRE.getMaxLevel()) + 1) / 2));
			}

			if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.ARROW_KNOCKBACK,
						a((r.nextInt(Enchantment.ARROW_FIRE.getMaxLevel()) + 1) / 2));
			}
		} else if (i.getType().equals(Material.CROSSBOW))
		{
			i.addEnchantment(Enchantment.QUICK_CHARGE, a((r.nextInt(Enchantment.QUICK_CHARGE.getMaxLevel()) + 1) / 2));
			if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.PIERCING, a((r.nextInt(Enchantment.PIERCING.getMaxLevel()) + 1) / 2));
			} else if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.MULTISHOT, a((r.nextInt(Enchantment.PIERCING.getMaxLevel()) + 1) / 2));
			}
		} else if (isArmor(i))
		{
			i.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,
					a((r.nextInt(Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel()) + 1) / 2));
			if (r.nextBoolean())
			{
				i.addEnchantment(Enchantment.THORNS, 1);
			}
			if (i.getType().name().endsWith("_HELMET"))
			{
				if (r.nextBoolean())
					i.addEnchantment(Enchantment.WATER_WORKER,
							a(r.nextInt(Enchantment.WATER_WORKER.getMaxLevel()) + 1));

				if (r.nextBoolean())
					i.addEnchantment(Enchantment.OXYGEN, a((r.nextInt(Enchantment.OXYGEN.getMaxLevel()) + 1) / 2));
			} else if (i.getType().name().endsWith("_BOOTS"))
			{
				if (r.nextBoolean())
				{
					i.addEnchantment(Enchantment.PROTECTION_FALL,
							a((r.nextInt(Enchantment.PROTECTION_FALL.getMaxLevel()) + 1) / 2));
				}
			}
		}
		if (r.nextInt(8) < 7)
		{
			i.addEnchantment(Enchantment.DURABILITY, a((r.nextInt(Enchantment.DURABILITY.getMaxLevel()) + 1) / 2));
		}
		return i;
	}

	public static int a(int i)
	{
		return i == 0 ? 1 : i;
	}

	public static boolean isArmor(ItemStack item)
	{
		return (CraftItemStack.asNMSCopy(item).getItem() instanceof ItemArmor);
	}
}
