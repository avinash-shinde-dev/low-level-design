package com.shikavani.lld.vendingmachine.model;

import com.shikavani.lld.vendingmachine.inventory.CashInventory;
import com.shikavani.lld.vendingmachine.model.payment.Money;
import com.shikavani.lld.vendingmachine.model.payment.Price;
import com.shikavani.lld.vendingmachine.service.ProductService;
import com.shikavani.lld.vendingmachine.utils.MoneyCalculator;

import java.math.BigDecimal;
import java.util.Currency;

public class Admin {

    private final ProductService productService;
    private final CashInventory cashInventory;

    public Admin(ProductService productService, CashInventory cashInventory) {
        this.productService = productService;
        this.cashInventory = cashInventory;
    }

    public void restockProducts(String productId, Integer quantity){
      this.productService.restockProducts(productId, quantity);
    }

    public void addProduct(Product product){
        Product savedProduct =  this.productService.addProduct(product);
        System.out.println("Product added: " + savedProduct);
    }

    public Product updatePrice(String productId, Price newPrice) {
        return this.productService.updateProductPrice(productId, newPrice);
    }

    public Money collectMoney(){
        return new MoneyCalculator(new Money(BigDecimal.ZERO, Currency.getInstance("INR"))).fromDenominations(this.cashInventory.getDenominations());
    }
}
