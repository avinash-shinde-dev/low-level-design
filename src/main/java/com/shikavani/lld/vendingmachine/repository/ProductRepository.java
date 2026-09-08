package com.shikavani.lld.vendingmachine.repository;

import com.shikavani.lld.vendingmachine.model.Product;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryRepository implements InMemoryRepository<String, Product> {
    private final Map<String, Product> productMap = new ConcurrentHashMap<>();
    @Override
    public Product save(Product product) {
        productMap.put(product.getId(), product);
        return product;
    }

    @Override
    public Product findById(String id) {
        return productMap.get(id);
    }

    @Override
    public List<Product> findAll() {
        return productMap.values().stream().toList();
    }

    @Override
    public Product update(Product product) {
        return productMap.put(product.getId(), product);
    }

    @Override
    public void delete(String productId) {
        productMap.remove(productId);
    }

}
