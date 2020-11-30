package de.bussard30.economy;

import org.bukkit.entity.EntityType;

public enum AnimalSpawners
{
	//@formatter:off
	WOLF_SPAWNER(EntityType.WOLF, 175000),
	RABBIT_SPAWNER(EntityType.RABBIT, 200000),
	PIG_SPAWNER(EntityType.PIG, 200000),
	COW_SPAWNER(EntityType.COW, 200000),
	CHICKEN_SPAWNER(EntityType.CHICKEN, 200000),
	SHEEP_SPAWNER(EntityType.SHEEP, 200000),
	CAT_SPAWNER(EntityType.CAT, 200000),
	TURTLE_SPAWNER(EntityType.TURTLE, 200000),
	
	HORSE_SPAWNER(EntityType.HORSE, 250000),
	VILLAGER_SPAWNER(EntityType.VILLAGER, 400000),


	;
	//@formatter:on
	private EntityType e;
	private int price;

	AnimalSpawners(EntityType e, int price)
	{
		this.e = e;
		this.price = price;
	}

	public EntityType getEntityType()
	{
		return e;
	}

	public int getPrice()
	{
		return price;
	}
}
