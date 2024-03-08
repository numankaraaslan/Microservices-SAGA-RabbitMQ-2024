package com.aldimbilet.pojos;

//Just wanted to create pojos specific to some operations to prevent unnecenssary data flow between services
// Jackson will know what to do with it (to carry it between services)
// You may also require some mapper for proper conservions
public class UserRegisterPojo
{
	private String username;

	private String password;

	private String email;

	private String name;

	private String surname;

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getSurname()
	{
		return surname;
	}

	public void setSurname(String surname)
	{
		this.surname = surname;
	}
}
