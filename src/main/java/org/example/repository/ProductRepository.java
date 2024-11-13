package org.example.repository;

import org.example.models.Product;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
//  Optional<Product> findByProductname(String name);
//
//  Boolean existsByProduct(String exists);
@Query("SELECT p FROM Product p WHERE p.status = 'Active'")
Page<Product> findAllActiveProducts(Pageable pageable);
@Query("SELECT COUNT(p) FROM Product p WHERE p.status = 'Active' AND p.createdAt BETWEEN :fromDate AND :toDate")
long countActiveProducts(@Param("fromDate") String fromDate, @Param("toDate") String toDate);

 @Query("SELECT COUNT(p) FROM Product p WHERE p.status = 'InActive' AND p.createdAt BETWEEN :fromDate AND :toDate")
 long countInactiveProducts(@Param("fromDate") String fromDate, @Param("toDate") String toDate);

 @Query("SELECT SUM(p.price) FROM Product p WHERE p.status = 'Active' AND p.createdAt BETWEEN :fromDate AND :toDate")
 BigDecimal sumActiveProductPrices(@Param("fromDate") String fromDate, @Param("toDate") String toDate);

 @Query("SELECT p FROM Product p WHERE p.status = 'Active' AND p.createdAt BETWEEN :fromDate AND :toDate ORDER BY p.price ASC, p.id DESC")
 Product findLowestActiveProduct(@Param("fromDate") String fromDate, @Param("toDate") String toDate);

 @Query("SELECT p FROM Product p WHERE p.status = 'Active' AND p.createdAt BETWEEN :fromDate AND :toDate ORDER BY p.price DESC, p.id DESC")
 Product findHighestActiveProduct(@Param("fromDate") String fromDate, @Param("toDate") String toDate);

 }
