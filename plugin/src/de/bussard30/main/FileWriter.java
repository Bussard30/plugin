package de.bussard30.main;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileWriter
{

	private File f;
	private YamlConfiguration c;

	public FileWriter(String FilePath, String FileName)
	{
		this.f = new File(FilePath, FileName);
		if (!f.exists())
		{
			try
			{
				f.createNewFile();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		YamlConfiguration yml = new YamlConfiguration();
		this.c = YamlConfiguration.loadConfiguration(this.f);

	}
	
	public void clear()
	{
		f.delete();
		try
		{
			f.createNewFile();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public boolean exist()
	{
		return this.f.exists();
	}

	public FileWriter setValue(String ValuePath, Object Value)
	{
		this.c.set(ValuePath, Value);
		return this;
	}

	public FileWriter setDefaultValue(String valuePath, Object value)
	{
		if (!valueExist(valuePath))
		{
			this.c.set(valuePath, value);
			save();
		}
		return this;
	}

	public Object getObject(String ValuePath)
	{
		return this.c.get(ValuePath);
	}

	public boolean valueExist(String value)
	{
		return this.getObject(value) != null;
	}

	public FileWriter save()
	{
		try
		{
			this.c.save(this.f);
		} catch (IOException var2)
		{
			var2.printStackTrace();
		}

		return this;
	}

	public boolean getBoolean(String ValuePath)
	{
		return this.c.getBoolean(ValuePath);
	}

	public String getString(String ValuePath)
	{
		return this.c.getString(ValuePath);
	}

	public Integer getInt(String ValuePath)
	{
		return this.c.getInt(ValuePath);
	}

	public List<String> getStringList(String ValuePath)
	{
		return this.c.getStringList(ValuePath);
	}

	public List<Integer> getIntList(String ValuePath)
	{
		return this.c.getIntegerList(ValuePath);
	}

	public Long getLong(String ValuePath)
	{
		return this.c.getLong(ValuePath);
	}

	public Float getFloat(String ValuePath)
	{
		return (float) this.c.getLong(ValuePath);
	}

	public Double getDouble(String ValuePath)
	{
		return this.c.getDouble(ValuePath);
	}

	public Set<String> getKeys(boolean deep)
	{
		return this.c.getKeys(deep);
	}

	public Map<String, Object> getValueMap(boolean deep)
	{
		return c.getValues(deep);
	}

	public ConfigurationSection getSection(String s)
	{
		return c.getConfigurationSection(s);
	}

	public Location getLocation(String string)
	{
		return c.getLocation(string);
	}

	public YamlConfiguration getYamlConfiguration()
	{
		return c;
	}

}
