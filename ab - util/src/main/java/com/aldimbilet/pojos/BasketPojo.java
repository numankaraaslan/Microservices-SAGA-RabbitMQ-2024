package com.aldimbilet.pojos;

// This pojo can be the base pojo for all basket based operations
// For example, the basket can have its own name or id for later use
// You can extend this like "SpecialBasket extends Basket" and make it a db entry
// That would also mean you need a basket service alltogether
public class BasketPojo
{
	private Long userId;

	private Long actId;

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	public Long getActId()
	{
		return actId;
	}

	public void setActId(Long actId)
	{
		this.actId = actId;
	}
}
