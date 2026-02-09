package com.project.code.Service;

import com.project.code.Model.*;
import com.project.code.Repo.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;


    @Transactional
    public void saveOrder(PlaceOrderRequestDTO request) {

        validateRequest(request);

        Customer customer = getOrCreateCustomer(request);

        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found")); //new StoreNotFoundException(request.getStoreId()));

        //obtiene el id de todos los productos
        List<Long> productIds = request.getPurchaseProduct().stream()
                .map(PurchaseProductDTO::getId)
                .collect(Collectors.toList());

        //Obtiene los inventarios (la clave es el id del producto) de los productos en la respectiva tienda
        Map<Long, Inventory> inventoryMap = inventoryRepository
                .findByProductIdsAndStoreIdWithLock(productIds, request.getStoreId())
                .stream()
                .collect(Collectors.toMap(i -> i.getProduct().getId(), i -> i));

        // Validar stock ANTES de cualquier modificación
        request.getPurchaseProduct().forEach(productDTO -> {
            Inventory inventory = inventoryMap.get(productDTO.getId());
            if (inventory == null) {
                throw new RuntimeException("Inventory not found");//ProductNotFoundInStoreException(productDTO.getId(), request.getStoreId());
            }
            if (inventory.getStockLevel() < productDTO.getQuantity()) {
                throw new RuntimeException("Insufficient stock"); //InsufficientStockException(productDTO.getName(), inventory.getStockLevel(), productDTO.getQuantity());
            }
        });

        //Calcular precio total
        Double totalPrice = request.getPurchaseProduct().stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();

        // Crear y guardar orden
        OrderDetails orderDetails = new OrderDetails(customer, store, totalPrice, LocalDateTime.now());
        OrderDetails savedOrder = orderDetailsRepository.save(orderDetails);

        // Actualizar inventario y crear items
        List<OrderItem> orderItems = new ArrayList<>();

        request.getPurchaseProduct().forEach(productDTO -> {
            Inventory inventory = inventoryMap.get(productDTO.getId());
            inventory.setStockLevel(inventory.getStockLevel() - productDTO.getQuantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(inventory.getProduct());
            orderItem.setQuantity(productDTO.getQuantity());
            orderItem.setPrice(productDTO.getPrice());
            orderItems.add(orderItem);
        });

        // Guardar todo en batch
        inventoryRepository.saveAll(inventoryMap.values());
        orderItemRepository.saveAll(orderItems);
    }

    private void validateRequest(PlaceOrderRequestDTO request) {
        if (request.getPurchaseProduct() == null || request.getPurchaseProduct().isEmpty()) {
            throw new RuntimeException("Error incorrect Order");//EmptyOrderException("Order must contain at least one product");
        }

    }

    private Customer getOrCreateCustomer(PlaceOrderRequestDTO request) {
        Customer existingCustomer = customerRepository.findByEmail(request.getCustomerEmail());
        if (existingCustomer == null) {
            Customer newCustomer = new Customer();
            newCustomer.setName(request.getCustomerName());
            newCustomer.setEmail(request.getCustomerEmail());
            newCustomer.setPhone(request.getCustomerPhone());

            return customerRepository.save(newCustomer);
        }
        return existingCustomer;
    }


   
}
