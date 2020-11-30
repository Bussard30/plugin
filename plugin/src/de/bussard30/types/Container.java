package de.bussard30.types;

import java.util.UUID;

public class Container
{
	private double[] xyz;
	private UUID uuid;

	public Container(double[] xyz, UUID uuid)
	{
		this.xyz = xyz;
		this.uuid = uuid;
	}

	public double[] getXyz()
	{
		return xyz;
	}

	public void setXyz(double[] xyz)
	{
		this.xyz = xyz;
	}

	public UUID getUuid()
	{
		return uuid;
	}

	public void setUuid(UUID uuid)
	{
		this.uuid = uuid;
	}
}
