package de.bussard30.economy;

public class ShopSession
{
	private int index;

	private int length;

	private ShopIcons category;

	public ShopSession(ShopIcons category, int length)
	{
		this.category = category;
		this.index = 0;
		this.length = length;
	}

	public int getIndex()
	{
		return index;
	}

	public int nextPage()
	{
		return (index == length - 1) ? length - 1 : index++;
	}

	public int prevPage()
	{
		return (index == 0) ? 0 : index--;
	}

	public ShopIcons getCategory()
	{
		return category;
	}
}
