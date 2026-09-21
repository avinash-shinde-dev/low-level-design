package com.shikavani.lld.vendingmachine.state;

import com.shikavani.lld.vendingmachine.exception.ProductNotSelectedException;
import com.shikavani.lld.vendingmachine.model.*;
import com.shikavani.lld.vendingmachine.model.transaction.PaymentOutcome;
import com.shikavani.lld.vendingmachine.model.transaction.PaymentRequest;

public class DispenseProductState implements VendingMachineState{

    private final VendingMachine vendingMachine;

    public DispenseProductState(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    @Override
    public void selectProduct(String productId) {
        throw new IllegalStateException(String.format("Product has already been selected : %s", productId));
    }

    @Override
    public PaymentOutcome pay(PaymentRequest paymentRequest) {
        throw new IllegalStateException("Payment has been already completed");
    }

    @Override
    public Product dispenseProduct() {
        System.out.println("Dispense Operation going on. Please collect your product");
        // commit the quantity
        if(this.vendingMachine.getSelectedProduct() == null){
            throw new ProductNotSelectedException("Product not selected exception");
        }
        final String productId = this.vendingMachine.getSelectedProduct().getId();
        Product product = this.vendingMachine.getProductService().getProductById(productId);
        this.vendingMachine.decrementQuantity(productId);
        this.vendingMachine.nextState(new IdleState(this.vendingMachine, this.vendingMachine.getProductService()));
        return product;
    }

    @Override
    public void cancel() {
        throw new IllegalStateException("You can't cancel this operation as dispensing of product is going on");
    }
}
