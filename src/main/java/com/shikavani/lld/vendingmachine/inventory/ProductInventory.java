package com.shikavani.lld.vendingmachine.service;


import com.shikavani.lld.vendingmachine.enums.StockStatus;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryService {
    private final Map<String, Integer> inventoryMap;
    public InventoryService() {
        inventoryMap = new ConcurrentHashMap<>();
    }

    public void incrementQuantity(String productId){
        inventoryMap.merge(productId,1, Integer::sum);
    }

    public void decrementQuantity(String productId){
        inventoryMap.computeIfPresent(
                productId,
                (key, quantity) -> quantity > 1 ? quantity - 1 : null
        );
    }

    public boolean isProductAvailable(String productId){
        return inventoryMap.get(productId) != null ?  true : false;
    }

    public Set<String> getAllProductIds(){
        return this.inventoryMap.keySet();
    }

    public Integer getQuantity(String productId){
        return this.inventoryMap.get(productId);
    }
    public void removeProduct(String productId){
        this.inventoryMap.remove(productId);
    }
    public void displayInventory() {
        System.out.println("**** Inventory *****");
        this.inventoryMap.forEach((k, v) -> System.out.println(String.format("Product ID : %s Stock: %s ", k, v > 0 ? StockStatus.IN_STOCK: StockStatus.OUT_OF_STOCK)));
    }


}
