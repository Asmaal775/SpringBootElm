package org.example.payload.request;

import jakarta.validation.constraints.NotBlank;
import org.example.models.Role;

import java.util.Set;

public class LoginRequest {
	@NotBlank
  private String username;

	@NotBlank
	private String password;

	private String email;

	private Set<Role> role;
	private boolean active = true; // Default to active

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	public Set<Role> getRole() {
		return role;
	}

	public void setRole(Set<Role> role) {
		this.role = role;
	}
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
