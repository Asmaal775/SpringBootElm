package org.example.controllers;

import org.example.models.Product;
import org.example.models.StatisticsResponse;
import org.example.payload.request.ProductRequest;
import org.example.payload.request.SignupRequest;
import org.example.payload.response.ProductResponse;
import org.example.repository.ProductRepository;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private ProductRepository productRepository;

    // Method to get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public long getTotalProductCount() {
        return productRepository.count();
    }

    public Product addProduct(String name, double price, String status) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setStatus(status);

        return productRepository.save(product);
    }

    public Product changeProductStatus(Long productId, String newStatus) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Update the status
        product.setStatus(newStatus);
        return productRepository.save(product);
    }

    public Page<Product> getAllProductsAdmin(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts() {
        List<Product> products = getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/total")
    public ResponseEntity<Long> getTotalProduct() {
        long count = getTotalProductCount();
        return ResponseEntity.ok(count);
    }

    @PostMapping("/products/add")
    public ResponseEntity<Product> addsingleProduct(@RequestBody ProductRequest productRequest) {
        try {
            Product newProduct = addProduct(productRequest.getName(), productRequest.getPrice(),
                    productRequest.getStatus());
            return ResponseEntity.ok(newProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/products/{id}/change-status")
    public ResponseEntity<Product> changeProdStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Product updatedProduct = changeProductStatus(id, status);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/products/admin")
    public ResponseEntity<?> getAllProductsForAdmin(@PageableDefault(size = 10)
                                                    Pageable pageable) {
        Page<Product> productPage = getAllProductsAdmin(pageable);

        return ResponseEntity.ok(new PaginatedResponse(productPage));
    }

    public static class PaginatedResponse {
        private long total;
        private Page<Product> items;

        public PaginatedResponse(Page<Product> items) {
            this.total = items.getTotalElements();
            this.items = items;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public Page<Product> getItems() {
            return items;
        }

        public void setItems(Page<Product> items) {
            this.items = items;
        }
    }

    @GetMapping("/products/statistics")
    public StatisticsResponse getStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String to) {

        from = Optional.ofNullable(from).orElse("1970-01-01");
        to = Optional.ofNullable(to).orElse("9999-12-31");

        StatisticsResponse response = new StatisticsResponse();

        // Product statistics
        StatisticsResponse.ProductStatistics productStats = new StatisticsResponse.ProductStatistics();
        productStats.setTotal(productRepository.count());
        productStats.setActive(productRepository.countActiveProducts(from, to));
        productStats.setInactive(productRepository.countInactiveProducts(from, to));
        productStats.setTotalPrice(productRepository.sumActiveProductPrices(from, to));
        productStats.setLowest(toProductDetails(productRepository.findLowestActiveProduct(from, to)));
        productStats.setHighest(toProductDetails(productRepository.findHighestActiveProduct(from, to)));
        response.setProducts(productStats);

//      // Dealer statistics
//      StatisticsResponse.DealerStatistics dealerStats = new StatisticsResponse.DealerStatistics();
//      dealerStats.setTotal(dealerRepository.count());
//      dealerStats.setHasProducts(dealerRepository.countByProductsIsNotEmpty());
//      dealerStats.setHasNoProducts(dealerRepository.countByProductsIsEmpty());
//      response.setDealers(dealerStats);
//
//      // Client statistics
//      StatisticsResponse.ClientStatistics clientStats = new StatisticsResponse.ClientStatistics();
//      clientStats.setTotal(clientRepository.count());
//      clientStats.setActive(clientRepository.countByActiveTrue());
//      clientStats.setInactive(clientRepository.countByActiveFalse());
//      response.setClients(clientStats);

        return response;
    }

    private StatisticsResponse.ProductDetails toProductDetails(Product product) {
        if (product == null) {
            return null;
        }

        StatisticsResponse.ProductDetails details = new StatisticsResponse.ProductDetails();
        details.setId(product.getId());
        details.setName(product.getName());
        details.setPrice(product.getPrice());
        return details;
    }

    @GetMapping("/products/user")
    public ProductResponse getActiveProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        var pageOfProducts = productRepository.findAllActiveProducts(pageable);

        ProductResponse response = new ProductResponse();
        response.setTotal(pageOfProducts.getTotalElements());
        response.setItems(pageOfProducts.getContent().stream()
                .map(this::convertToDTO)  // Convert Product to ProductDTO
                .toList());

        return response;
    }

    private ProductResponse.ProductDTO convertToDTO(Product product) {
        ProductResponse.ProductDTO dto = new ProductResponse.ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        return dto;
    }


}