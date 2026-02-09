package com.project.code.Controller;

import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ServiceClass serviceClass;
    @Autowired
    InventoryRepository inventoryRepository;

    @PostMapping()
    public Map<String,String> addProduct(@RequestBody Product product) {
        Map<String,String> response = new HashMap<>();
        if (!serviceClass.validateProduct(product)) {
            response.put("Error", "Product already exists");
            return response;
        }
        try {
            productRepository.save(product);
            response.put("Success", "Product has been added. ID: " + product.getId());
        } catch (DataIntegrityViolationException e) {
            response.put("Error", "Sku must be unique and not empty");
        }
        return response;
    }

    @GetMapping("{id}")
    public Map<String, Object> getProductById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        if (!serviceClass.validateProductId(id)) {
            response.put("Error", "Product id does not exist");
            return response;
        }
        Product product = productRepository.findById(id).get();
        response.put("products", product);
        return response;
    }

    @PutMapping
    public Map<String, String> updateProduct(@RequestBody Product product) {
        Map<String,String> response = new HashMap<>();
        if (serviceClass.validateProduct(product)) {
            response.put("Error", "Product does not exist");
            return  response;
        }
        try {
            productRepository.save(product);
            response.put("Success", "Product: "+product.getId()+" has been updated");
        } catch (DataIntegrityViolationException e) {
            response.put("Error", "There was an error validating the data. Please ensure that the product exists. ");
        }
        return response;
    }

    @GetMapping("/category/{name}/{category}")
    public Map<String, Object> filterByCategoryProduct(@PathVariable String name, @PathVariable String category) {
        Map<String, Object> response = new HashMap<>();
        if (name.equals("null") && category.equals("null")) {
            response.put("products",productRepository.findBySubNameAndCategory(name, category));
        } else if (name.equals("null")) {
            response.put("products",productRepository.findByCategory(category));
        } else {
            response.put("products",productRepository.findBySubName(name));
        }
        return response;
    }

    @GetMapping
    public Map<String, Object> listProduct() {
        Map<String, Object> response = new HashMap<>();
        response.put("products", productRepository.findAll());
        return response;
    }

    @GetMapping("filter/{category}/{storeId}")
    public Map<String, Object> getProdcutByCategoryAndStoreId(@PathVariable String category, @PathVariable Long storeId) {
        Map<String, Object> response = new HashMap<>();
        if (category.equals("null")) {
            response.put("product", productRepository.findProductsByStoreId(storeId));
            return response;
        } else if (storeId == null) {
            response.put("product", productRepository.findByCategory(category));
            return response;
        }
        response.put("products", productRepository.findByCategoryAndStore(storeId, category));
        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteProduct(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        if (!serviceClass.validateProductId(id)) {
            response.put("Error", "Product id does not exist");
        }
        inventoryRepository.deleteByProductId(id);
        productRepository.deleteById(id);
        response.put("Success", "Product has been deleted");
        return response;
    }

    @GetMapping("/searchProduct/{name}")
    public Map<String, Object> searchProduct(@PathVariable String name) {
        Map<String, Object> response = new HashMap<>();
        response.put("products", productRepository.findBySubName(name));
        return response;
    }
}
