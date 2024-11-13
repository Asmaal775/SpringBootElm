package org.example.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product",
    uniqueConstraints = { 
      @UniqueConstraint(columnNames = "id"),
      @UniqueConstraint(columnNames = "name"),
      @UniqueConstraint(columnNames = "price") ,
      @UniqueConstraint(columnNames = "status")
    })
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private double price;

  private String status;

  private LocalDateTime createdAt;

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public LocalDateTime getcreatedAt() {
    return createdAt;
  }

  public void setcreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public void setTotal(long totalElements) {
  }

  public void setItems(List<Object> list) {
  }
}
