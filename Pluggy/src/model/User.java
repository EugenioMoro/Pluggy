package model;

public class User {

	private int id;
	private String username;
	private String chatId;
	private Boolean isAdmin;
	private Boolean isAuth;
	private Boolean isSub;
	private int hours;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getChatId() {
		return chatId;
	}
	public void setChatId(String chatId) {
		this.chatId = chatId;
	}
	public Boolean getIsAdmin() {
		return isAdmin;
	}
	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	public Boolean getIsAuth() {
		return isAuth;
	}
	public void setIsAuth(Boolean isAuth) {
		this.isAuth = isAuth;
	}
	public Boolean getIsSub() {
		return isSub;
	}
	public void setIsSub(Boolean isSub) {
		this.isSub = isSub;
	}
	public int getHours() {
		return hours;
	}
	public void setHours(int hours) {
		this.hours = hours;
	}
	
	
}
