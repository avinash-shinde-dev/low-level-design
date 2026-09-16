package com.shikavani.lld.vendingmachine.inventory;


import com.shikavani.lld.vendingmachine.enums.StockStatus;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ProductInventory {

    private final Map<String, Integer> inventoryMap;

    public ProductInventory() {
        inventoryMap = new ConcurrentHashMap<>();
    }

    public void incrementQuantity(String productId){
        inventoryMap.merge(productId,1, Integer::sum);
    }

    public void incrementByQuantity(String productId, Integer quantity){
        inventoryMap.merge(productId,quantity, Integer::sum);
    }

    public void decrementQuantity(String productId){
        inventoryMap.computeIfPresent(
                productId,
                (key, quantity) -> quantity > 1 ? quantity - 1 : null
        );
    }

    public void decrementByQuantity(String productId, Integer quantity){
        inventoryMap.computeIfPresent(
                productId,
                (key, value) -> value > quantity ? value - quantity : null
        );
    }

    public boolean isProductAvailable(String productId){
        return inventoryMap.get(productId) != null;
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
