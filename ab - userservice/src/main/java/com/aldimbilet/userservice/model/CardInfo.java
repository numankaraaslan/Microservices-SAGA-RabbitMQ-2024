package com.aldimbilet.userservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "CARDINFO")
public class CardInfo
{
	@Id
	@Column(name = "USER_ID", nullable = false)
	private Long userId;

	@Column(name = "CARDNUMBER", nullable = false, unique = true, length = 50)
	private String cardNumber;

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	public String getCardNumber()
	{
		return cardNumber;
	}

	public void setCardNumber(String cardNumber)
	{
		this.cardNumber = cardNumber;
	}
}
