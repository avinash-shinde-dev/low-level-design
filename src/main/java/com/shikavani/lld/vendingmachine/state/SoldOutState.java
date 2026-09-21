package com.shikavani.lld.vendingmachine.state;

import com.shikavani.lld.vendingmachine.exception.OutOfStockException;
import com.shikavani.lld.vendingmachine.model.transaction.PaymentOutcome;
import com.shikavani.lld.vendingmachine.model.transaction.PaymentRequest;
import com.shikavani.lld.vendingmachine.model.Product;

public class SoldOutState implements VendingMachineState {

    private final VendingMachine vendingMachine;

    public SoldOutState(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    @Override
    public void selectProduct(String productId) {
        this.vendingMachine.nextState(new IdleState(this.vendingMachine, this.vendingMachine.getProductService()));
        throw new OutOfStockException(String.format("Product: %s is not available.", productId));
    }

    @Override
    public PaymentOutcome pay(PaymentRequest paymentRequest) {
        throw new IllegalStateException("Cannot perform payment ");
    }

    @Override
    public Product dispenseProduct() {
        throw new IllegalStateException("Product has been sold out..");
    }

    @Override
    public void cancel() {
        this.vendingMachine.nextState(new IdleState(this.vendingMachine, this.vendingMachine.getProductService()));
    }
}
