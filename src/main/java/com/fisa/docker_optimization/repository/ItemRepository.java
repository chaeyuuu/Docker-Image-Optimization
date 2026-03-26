package com.fisa.docker_optimization.repository;


import com.fisa.docker_optimization.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByNameContainingIgnoreCase(String name);

    List<Item> findByPriceBetween(Integer minPrice, Integer maxPrice);

    @Query("SELECT i FROM Item i WHERE i.quantity > 0 ORDER BY i.createdAt DESC")
    List<Item> findAvailableItems();

    @Query("SELECT COUNT(i) FROM Item i")
    long countAllItems();

    @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM Item i")
    long sumAllQuantity();
}