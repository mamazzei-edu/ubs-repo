package br.sp.gov.fatec.ubs.backend.controllers;

public class LoginResponse {
    private String token;

    private long expiresIn;

	private long userId;

	private long createdAt = System.currentTimeMillis();

	private String[] roles;

    public String[] getRoles() {
		return roles;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	public String getToken() {
        return token;
    }

	// Getters and setters...
	public void setToken(String token) {
		this.token = token;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	
	
}