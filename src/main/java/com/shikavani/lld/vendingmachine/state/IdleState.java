package com.shikavani.lld.vendingmachine.state;

import com.shikavani.lld.vendingmachine.model.transaction.PaymentOutcome;
import com.shikavani.lld.vendingmachine.model.transaction.PaymentRequest;
import com.shikavani.lld.vendingmachine.model.Product;
import com.shikavani.lld.vendingmachine.service.ProductService;

public class IdleState implements VendingMachineState{

    private final VendingMachine vendingMachine;
    private final ProductService productService;
    public IdleState(VendingMachine vendingMachine, ProductService productService) {
        this.vendingMachine = vendingMachine;
        this.productService = productService;
    }

    @Override
    public void selectProduct(String productId) {
        System.out.println("Current State: Idle State");
        System.out.println("Selected product: " + productId);
        if(this.vendingMachine.isProductAvailable(productId)){
            Product product = this.productService.getProductById(productId);
            // update the product
            this.vendingMachine.setSelectedProduct(product);
            System.out.println("Moving to ProductSelectedState");
            this.vendingMachine.nextState(new ProductSelectedState(this.vendingMachine, this.vendingMachine.getPaymentService()));
        }else{
            System.out.println("Moving to SoldOutState");
            this.vendingMachine.nextState(new SoldOutState(this.vendingMachine));
        }
    }

    @Override
    public PaymentOutcome pay(PaymentRequest paymentRequest) {
        // we haven't committed the tender
        throw new IllegalStateException("Please select the product first");
    }

    @Override
    public Product dispenseProduct() {
        throw new IllegalStateException("Please select the product first before performing dispense operation");
    }

    @Override
    public void cancel() {
        // we haven't committed the tender
        throw new IllegalStateException("Nothing to do here");
    }
}
