package de.bussard30.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import de.bussard30.types.Container;
import de.bussard30.types.NBTWrapper;
import redis.clients.jedis.Jedis;

public class JedisManager
{
	public static NBTWrapper nbtwrapper = new NBTWrapper();

	public static void storeValue(byte[] location, byte[] value)
	{
		Jedis jedis = Main.jedisPool.getResource();

		jedis.set(location, value);
		jedis.close();
	}

	public static void storeValue(String location, String value)
	{
		Jedis jedis = Main.jedisPool.getResource();
		jedis.set(location, value);
		jedis.close();
	}
	
	public static void deleteHome(Player p, String name)
	{
		Jedis jedis = Main.jedisPool.getResource();
		jedis.hdel(getBytesFromUUID(p.getUniqueId()), name.getBytes());
		jedis.close();
	}

	public static String getValue(String location)
	{
		Jedis jedis = Main.jedisPool.getResource();
		String s = jedis.rpop(location);
		jedis.close();
		return s;
	}

	public static boolean contains(String key)
	{
		Jedis jedis = Main.jedisPool.getResource();
		boolean b = jedis.exists(key);
		jedis.close();
		return b;
	}

	public static boolean contains(byte[] key)
	{
		Jedis jedis = Main.jedisPool.getResource();
		boolean b = jedis.exists(key);
		jedis.close();
		return b;
	}

	public static boolean containsBackpack(UUID key)
	{
		Jedis jedis = Main.jedisPool.getResource();
		boolean b = jedis.exists("backpack/" + new String(getBytesFromUUID(key)));
		jedis.close();
		return b;
	}

	public static byte[] getValue(byte[] location)
	{
		Jedis jedis = Main.jedisPool.getResource();
		byte[] b = jedis.rpop(location);
		jedis.close();
		return b;

	}

	public static void addHome(Player p, String homeName)
	{
		Jedis jedis = Main.jedisPool.getResource();
		jedis.hset(getBytesFromUUID(p.getUniqueId()), homeName.getBytes(), containerToBytes(new Container(new double[]
		{ p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ() }, p.getWorld().getUID())));
		jedis.close();
	}

	public static ArrayList<String> getHomes(Player p)
	{
		Jedis jedis = Main.jedisPool.getResource();
		Map<String, String> fields = jedis.hgetAll(new String(getBytesFromUUID(p.getUniqueId())));
		ArrayList<String> al = new ArrayList<>();
		for (Map.Entry<String, String> entry : fields.entrySet())
		{
			al.add(entry.getValue());
		}
		jedis.close();
		return al;
	}

	public static Map<String, Container> getHomesWithP(Player p)
	{
		Jedis jedis = Main.jedisPool.getResource();
		Map<byte[], byte[]> map = jedis.hgetAll(getBytesFromUUID(p.getUniqueId()));
		HashMap<String, Container> map1 = new HashMap<String, Container>();
		for (Map.Entry<byte[], byte[]> entry : map.entrySet())
		{
			map1.put(new String(entry.getKey()), bytesToContainer(entry.getValue()));
		}
		jedis.close();
		return map1;
	}

	public static boolean containsHome(Player p, String home)
	{
		Jedis jedis = Main.jedisPool.getResource();
		boolean b = jedis.hexists(getBytesFromUUID(p.getUniqueId()), home.getBytes());
		jedis.close();
		return b;
	}

	public static Container getHome(Player p, String home)
	{
		Jedis jedis = Main.jedisPool.getResource();
		Container c = bytesToContainer(jedis.hget(getBytesFromUUID(p.getUniqueId()), home.getBytes()));
		jedis.close();
		return c;
	}

	/**
	 * This method creates a new entry for an backpack
	 */
	public static void createNewBackpack(ItemStack i)
	{
		Inventory backpackInventory = Bukkit.createInventory(null, 27, "Backpack");
		Jedis jedis = Main.jedisPool.getResource();
		jedis.set("backpack/" + nbtwrapper.getNBTTag("uuid", i), inventoryToString(backpackInventory));
		jedis.close();
	}

	/**
	 * Fetches inventory from certain backpack
	 * 
	 * @param p
	 * @param i
	 * @return
	 */
	public static Inventory getBackpackContent(ItemStack i)
	{
		Jedis jedis = Main.jedisPool.getResource();
		String s = jedis.get("backpack/" + nbtwrapper.getNBTTag("uuid", i));
		jedis.close();
		return stringToInventory(s);
	}

	public static void saveBackpackContent(ItemStack is, Inventory i)
	{
		Jedis jedis = Main.jedisPool.getResource();
		jedis.set("backpack/" + nbtwrapper.getNBTTag("uuid", is), inventoryToString(i));
		jedis.close();
	}

	/*
	 * -------------------------------------------------------------------------
	 * ----------- CONVERTION METHODS
	 * -------------------------------------------------------------------------
	 * -----------
	 */

	public static byte[] doublesToBytes(double[] doubles)
	{
		ByteBuffer bb = ByteBuffer.allocate(doubles.length * 8);
		for (double d : doubles)
		{
			bb.putDouble(d);
		}
		return bb.array();
	}

	public static double[] bytesToDoubles(byte[] b)
	{
		ByteBuffer bb = ByteBuffer.wrap(b);
		double[] doubles = new double[b.length / 8];
		for (int i = 0; i < doubles.length; i++)
		{
			doubles[i] = bb.getDouble();
		}
		return doubles;
	}

	public static byte[] getBytesFromUUID(UUID uuid)
	{
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());

		return bb.array();
	}

	public static UUID getUUIDFromBytes(byte[] bytes)
	{
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		Long high = byteBuffer.getLong();
		Long low = byteBuffer.getLong();
		return new UUID(high, low);
	}

	public static Container bytesToContainer(byte[] b)
	{
		byte[] xyz = new byte[24];
		for (int i = 0; i < 24; i++)
		{
			xyz[i] = b[i];
		}
		byte[] uuid = new byte[b.length - 24];
		for (int i = 24; i < b.length; i++)
		{
			uuid[i - 24] = b[i];
		}
		return new Container(bytesToDoubles(xyz), getUUIDFromBytes(uuid));
	}

	public static byte[] containerToBytes(Container c)
	{
		byte[] b0 = doublesToBytes(new double[]
		{ c.getXyz()[0], c.getXyz()[1], c.getXyz()[2] });
		byte[] b1 = getBytesFromUUID(c.getUuid());
		byte[] b2 = new byte[b0.length + b1.length];
		for (int i = 0; i < b0.length + b1.length; i++)
		{
			if (i < b0.length)
			{
				b2[i] = b0[i];
			} else
			{
				b2[i] = b1[i - b0.length];
			}
		}
		return b2;
	}

	public static Inventory stringToInventory(String s)
	{
		try
		{
			return fromBase64(s);
		} catch (IOException e)
		{
			Bukkit.getLogger().log(Level.SEVERE, "Error while converting string to inventory.", e);
			return null;
		}
	}

	public static String inventoryToString(Inventory i) throws IllegalStateException
	{
		return inventoryToBase64(i);
	}

	public static String inventoryToBase64(Inventory inventory)
	{
		return toBase64(inventory);

	}

	/**
	 * Converts the player inventory to a String array of Base64 strings. First
	 * string is the content and second string is the armor.
	 * 
	 * @param playerInventory
	 *            to turn into an array of strings.
	 * @return Array of strings: [ main content, armor content ]
	 * @throws IllegalStateException
	 */
	public static String playerInventoryToBase64(PlayerInventory playerInventory) throws IllegalStateException
	{
		// get the main content part, this doesn't return the armor
		return toBase64(playerInventory);
	}

	/**
	 * A method to serialize an inventory to Base64 string.
	 * 
	 * <p />
	 * 
	 * Special thanks to Comphenix in the Bukkit forums or also known as aadnk
	 * on GitHub.
	 * 
	 * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
	 * 
	 * @param inventory
	 *            to serialize
	 * @return Base64 string of the provided inventory
	 * @throws IllegalStateException
	 */
	public static String toBase64(Inventory inventory) throws IllegalStateException
	{
		try
		{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

			// Write the size of the inventory
			dataOutput.writeObject(inventory.getType());

			// Save every element in the list
			for (int i = 0; i < inventory.getSize(); i++)
			{
				dataOutput.writeObject(inventory.getItem(i));
			}

			// Serialize that array
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e)
		{
			throw new IllegalStateException("Unable to save item stacks.", e);
		}
	}

	/**
	 * 
	 * A method to get an {@link Inventory} from an encoded, Base64, string.
	 * 
	 * <p />
	 * 
	 * Special thanks to Comphenix in the Bukkit forums or also known as aadnk
	 * on GitHub.
	 * 
	 * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
	 * 
	 * @param data
	 *            Base64 string of data containing an inventory.
	 * @return Inventory created from the Base64 string.
	 * @throws IOException
	 */
	public static Inventory fromBase64(String data) throws IOException
	{
		try
		{
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

			InventoryType type = (InventoryType) dataInput.readObject();
			Inventory inventory = Bukkit.getServer().createInventory(null, type);

			// Read the serialized inventory
			for (int i = 0; i < inventory.getSize(); i++)
			{
				inventory.setItem(i, (ItemStack) dataInput.readObject());
			}

			dataInput.close();
			return inventory;
		} catch (ClassNotFoundException e)
		{
			throw new IOException("Unable to decode class type.", e);
		}
	}

}
