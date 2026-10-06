package domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlID;

@Entity
public abstract class User {

	private static final long serialVersionUID = 1L;
	@XmlID
	@Id
	private String email;
	private String name;
	private String pass;

	protected User() {
		super();
	}

	public User(String email, String name, String pass) {
		this.email = email;
		this.name = name;
		this.pass = pass;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (email != other.email)
			return false;
		return true;
	}
}