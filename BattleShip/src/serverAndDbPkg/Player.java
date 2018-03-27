package serverAndDbPkg;

import java.io.Serializable;

public class Player implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String email;
	private String password;
	private String nameIngame;
	private String rank;
	private String ipAddress;
	private int port;
	
	
	
	public Player(String email, String password, String nameIngame, String rank, String ipAddress, int port) {
		super();
		this.email = email;
		this.password = password;
		this.nameIngame = nameIngame;
		this.rank = rank;
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}



	public int getPort() {
		return port;
	}



	public void setPort(int port) {
		this.port = port;
	}



	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNameIngame() {
		return nameIngame;
	}

	public void setNameIngame(String nameIngame) {
		this.nameIngame = nameIngame;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

}
