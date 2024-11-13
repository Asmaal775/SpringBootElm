package org.example.payload.response;

import java.math.BigDecimal;
import java.util.List;

public class ProductResponse {

    private double total;           // Total number of active products
    private List<ProductDTO> items;  // List of active products

    // Getters and Setters
    public double getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<ProductDTO> getItems() {
        return items;
    }

    public void setItems(List<ProductDTO> items) {
        this.items = items;
    }

    public static class ProductDTO {
        private Long id;
        private String name;
        private double price;

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
    }
}
