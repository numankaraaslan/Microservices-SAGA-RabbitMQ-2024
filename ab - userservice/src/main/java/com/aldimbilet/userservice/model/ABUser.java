package com.aldimbilet.userservice.model;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ABUSER")
// At some point of the development i have decided to name this class with domainname (aldimbilet.com)
// a and b is the initials of the words "aldim" and "bilet"
// Later than i have realised it became "abuser" :D this has nothing to do with any kind of abuse :D
// In turkish, if you apply the same logic, this could also be Abuzer :)
public class ABUser implements UserDetails
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "USERNAME", nullable = false, unique = true, length = 50)
	private String username;

	@Column(name = "PASSWORD", nullable = false, length = 200)
	private String password;

	private String passwordConfirm;

	@Column(name = "EMAIL", nullable = false, length = 100)
	private String email;

	@Column(name = "NAME", nullable = false, length = 50)
	private String name;

	@Column(name = "SURNAME", nullable = false, length = 50)
	private String surname;

	@Column(name = "REG_DATE", nullable = false)
	private Date regDate = new Date();

	@ManyToMany(fetch = FetchType.EAGER)
	private List<Role> roles;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	@Override
	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	@Override
	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getPasswordConfirm()
	{
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm)
	{
		this.passwordConfirm = passwordConfirm;
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

	public Date getRegDate()
	{
		return regDate;
	}

	public void setRegDate(Date regDate)
	{
		this.regDate = regDate;
	}

	public List<Role> getRoles()
	{
		return roles;
	}

	public void setRoles(List<Role> roles)
	{
		this.roles = roles;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities()
	{
		return roles;
	}

}