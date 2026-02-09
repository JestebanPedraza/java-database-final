package com.project.code.Repo;


import com.project.code.Model.Inventory;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {


    public Inventory findByProductIdAndStoreId(Long productId, Long storeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.product.id IN :productIds AND i.store.id = :storeId")
    Optional<Inventory> findByProductIdsAndStoreIdWithLock(@Param("productId") List<Long> productIds,
                                                          @Param("storeId") Long storeId);
    public List<Inventory> findByStoreId(Long storeId);

    @Modifying
    @Transactional
    public void deleteByProductId(Long productId);
}
