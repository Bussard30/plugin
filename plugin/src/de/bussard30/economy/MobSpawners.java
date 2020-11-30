package de.bussard30.economy;

import org.bukkit.entity.EntityType;

public enum MobSpawners
{
	//@formatter:off
	ZOMBIE_SPAWNER(EntityType.ZOMBIE, 500000),
	SKELETON_SPAWNER(EntityType.SKELETON, 500000),
	CREEPER_SPAWNER(EntityType.CREEPER, 600000),
	SPIDER_SPAWNER(EntityType.SPIDER, 500000),

	BLAZE_SPAWNER(EntityType.BLAZE, 750000),
	MAGMA_SPAWNER(EntityType.MAGMA_CUBE, 750000),
	SLIME_SPAWNER(EntityType.SLIME, 1000000),
	WITCH_SPAWNER(EntityType.WITCH, 1000000),
	ENDERMAN_SPAWNER(EntityType.ENDERMAN, 1000000),
	WITHER_SKELETON_SPAWNER(EntityType.WITHER_SKELETON, 1250000),

	;
	//@formatter:on
	private EntityType e;
	private int price;

	MobSpawners(EntityType e, int price)
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
