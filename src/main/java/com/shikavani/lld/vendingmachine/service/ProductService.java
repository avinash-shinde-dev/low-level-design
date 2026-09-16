package com.shikavani.lld.vendingmachine.service;

import com.shikavani.lld.vendingmachine.exception.ProductNotFoundException;
import com.shikavani.lld.vendingmachine.inventory.ProductInventory;
import com.shikavani.lld.vendingmachine.model.payment.Price;
import com.shikavani.lld.vendingmachine.model.Product;
import com.shikavani.lld.vendingmachine.repository.ProductRepository;

public class ProductService {

    private final ProductRepository productRepository;
    private  final ProductInventory productInventory;

    public ProductService(ProductRepository productRepository, ProductInventory productInventory) {
        this.productRepository = productRepository;
        this.productInventory = productInventory;
    }

    public Product addProduct(Product product){
       Product savedProduct =  this.productRepository.save(product);
       this.productInventory.incrementQuantity(savedProduct.getId());
       return savedProduct;
    }

    public Product addProductInStock(Product product, Integer quantity){
        Product savedProduct =  this.productRepository.save(product);
        restockProducts(product.getId(), quantity);
        return savedProduct;
    }

    public void restockProducts(String productId, Integer quantity) {
        this.productInventory.incrementByQuantity(productId, quantity);
    }

    public void removeProduct(String productId){
        this.productRepository.delete(productId);
        this.productInventory.removeProduct(productId);
    }


    public Product getProductById(String productId){
        return this.productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(String.format("Product: %s doesn't exist", productId)));
    }

    public Product updateProductPrice(String productId, Price newPrice){
         Product product = this.getProductById(productId);
        product.setPrice(newPrice);
        return this.productRepository.save(product);
    }

}
