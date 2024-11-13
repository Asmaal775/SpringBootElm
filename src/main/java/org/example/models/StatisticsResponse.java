package org.example.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StatisticsResponse {

  private ProductStatistics products;
  private DealerStatistics dealers;
  private ClientStatistics clients;

  public void setProducts(ProductStatistics productStats) {
  }


  public static class ProductStatistics {
    private long total;
    private long active;
    private long inactive;
    private BigDecimal totalPrice;
    private ProductDetails lowest;
    private ProductDetails highest;

    public void setTotal(long count) {
    }

    public void setActive(long l) {
    }

    public void setInactive(long l) {
    }

    public void setTotalPrice(BigDecimal bigDecimal) {
    }

    public void setLowest(ProductDetails productDetails) {
    }

    public void setHighest(ProductDetails productDetails) {
    }
  }

  public static class ProductDetails {
    private Long id;
    private String dealerName;
    private String name;
    private BigDecimal price;

    public void setId(Long id) {
    }

    public void setName(String name) {
    }

    public void setPrice(double price) {
    }
  }

  public static class DealerStatistics {
    private long total;
    private long hasProducts;
    private long hasNoProducts;

  }

  public static class ClientStatistics {
    private long total;
    private long active;
    private long inactive;

  }
}