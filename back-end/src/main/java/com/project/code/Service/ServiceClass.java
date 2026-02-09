package com.project.code.Service;

import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServiceClass {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public ServiceClass(InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    public boolean validateInventory(Inventory inventory) {
        Inventory result = inventoryRepository.findByProductIdAndStoreId(inventory.getProduct().getId(),inventory.getStore().getId());
        return result == null;
    }

    public boolean validateProduct(Product product) {
        return Optional.ofNullable(productRepository.findByNameIgnoreCase(product.getName().trim())).isEmpty();
    }

    public boolean validateProductId(Long productId) {
        return productRepository.findById(productId).isPresent();
    }

    public Inventory getInventoryId(Inventory inventory) {
        return inventoryRepository.findByProductIdAndStoreId(inventory.getProduct().getId(),inventory.getStore().getId());
    }
}
