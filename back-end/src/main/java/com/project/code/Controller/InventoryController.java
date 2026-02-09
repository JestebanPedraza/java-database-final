package com.project.code.Controller;


import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ServiceClass serviceClass;

    @PutMapping
    public Map<String, String> updateInventory(@RequestBody CombinedRequest request) {

        Product product = request.getProduct();
        Inventory inventory = request.getInventory();

        Map<String, String> response = new HashMap<>();

        if (!serviceClass.validateProductId(product.getId())) {
            response.put("message", "Id " + product.getId() + " no presente en la base de datos");
            return response;
        }
        productRepository.save(product);
        response.put("message", "Producto actualizado con éxito con id: " + product.getId());
        if (inventory != null) {
            try {
                Inventory result = serviceClass.getInventoryId(inventory);
                if (result != null) {
                    inventory.setId(result.getId());
                    inventoryRepository.save(inventory);
                } else {
                    response.put("message", "No hay datos disponibles para este producto o id de tienda");
                    return response;
                }
            } catch (DataIntegrityViolationException e) {
                response.put("message", "Error: " + e);
                System.out.println(e);
                return response;
            } catch (Exception e) {
                response.put("message", "Error: " + e);
                System.out.println(e);
                return response;
            }
        }
        return response;
    }

    @PostMapping
    public Map<String, String> saveInventory(@RequestBody Inventory inventory) {

        Map<String, String> response = new HashMap<>();

        try {
            if (serviceClass.validateInventory(inventory)) {
                inventoryRepository.save(inventory);
            } else {
                response.put("message", "Datos ya presentes en el inventario");
                return response;
            }
        } catch (DataIntegrityViolationException e) {
            response.put("message", "Error: " + e);
            System.out.println(e);
            return response;
        } catch (Exception e) {
            response.put("message", "Error: " + e);
            System.out.println(e);
            return response;
        }
        response.put("message", "Producto agregado al inventario con éxito");
        return response;
    }

    @GetMapping("/{storeId}")
    public Map<String, Object> getAllProducts(@PathVariable Long storeId) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findProductsByStoreId(storeId);
        if (products.isEmpty()) {
            response.put("message", "No products found for store with ID: " + storeId);
            response.put("products", products);
            return response;
        }
        response.put("products", products);
        return response;

    }

    @GetMapping("filter/{category}/{name}/{storeId}")
    public Map<String, Object> getProductName(@PathVariable String category, @PathVariable String name, @PathVariable Long storeId) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = new ArrayList<>();
        if (storeId == null) {
            response.put("Error", "There must be a store");
            return response;
        }
        if (category != null && name != null) {
            products = productRepository.findByNameAndCategory(storeId, category, name);
        } else if (category == null) {
            products =  productRepository.findByNameLike(storeId, name);
        } else {
            products = productRepository.findByCategoryAndStore(storeId, category);
        }
        if (products.isEmpty()) {
            response.put("message", "No products found for store with ID: " + storeId);
        }
        response.put("product", products);
        return response;
    }


    @GetMapping("search/{name}/{storeId}")
    public Map<String, Object> searchProduct(@PathVariable String name, @PathVariable Long storeId) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = new ArrayList<>();
        products = productRepository.findByNameLike(storeId, name);
        if (products.isEmpty()) {
            response.put("message", "No products found for store with ID: " + storeId);
        }
        response.put("product", products);
        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> removeProduct(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        if (!serviceClass.validateProductId(id)) {
            response.put("message", "Id " + id + " no presente en la base de datos");
            return response;
        }
        inventoryRepository.deleteByProductId(id);
        response.put("message", "Producto eliminado con éxito con id: " + id);
        return response;
    }

    @GetMapping("validate/{quantity}/{storeId}/{productId}")
    public boolean validateQuantity(@PathVariable Long quantity, @PathVariable Long storeId, @PathVariable Long productId) {
        Inventory inventory =  inventoryRepository.findByProductIdAndStoreId(productId, storeId);
        if (inventory == null) {
            return false;
        }
        return quantity <= inventory.getStockLevel();
    }
}
