package org.example.payload.request;

import jakarta.validation.constraints.NotBlank;

public class ProductRequest {
	@NotBlank
  private String name;

	@NotBlank
	private double price;


	private String status;


	// Getters and Setters


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
