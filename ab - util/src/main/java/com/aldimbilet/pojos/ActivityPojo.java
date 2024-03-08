package com.aldimbilet.pojos;

// Instead of caryying a domain object from activity microservice, we have a configureable and decoratable pojo to move around
public class ActivityPojo
{
	private Long id;

	private String name;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
