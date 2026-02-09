package com.project.code.Repo;


import com.project.code.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    public List<Product> findByCategory(String category);
    public List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    public Product findBySku(String sku);
    public Product findByNameIgnoreCase(String name);

    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId and LOWER(i.product.name) LIKE LOWER(CONCAT('%', :pname, '%'))")
    public List<Product> findByNameLike(@Param("storeId") Long storeId, @Param("pname")String pname);

    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId AND LOWER(i.product.name) LIKE LOWER(CONCAT('%', :pname, '%')) AND i.product.category = :category")
    public List<Product> findByNameAndCategory(@Param("storeId")Long storeId, @Param("pname") String pname, @Param("category") String category);

    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId AND i.product.category = :category")
    public List<Product> findByCategoryAndStore(@Param("storeId")Long storeId, @Param("category") String category);

    @Query("SELECT i FROM Product i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :pname, '%'))")
    public List<Product> findBySubName(@Param("pname")String pname);

    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId")
    public List<Product> findProductsByStoreId(@Param("storeId")Long storeId);

    @Query("SELECT i FROM Product i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :pname, '%')) AND i.category = :category")
    public List<Product> findBySubNameAndCategory(@Param("pname")String pname, @Param("category") String category);
}
