package com.shikavani.lld.vendingmachine.state;

import com.shikavani.lld.vendingmachine.exception.ExactChangeUnavailableException;
import com.shikavani.lld.vendingmachine.exception.InsufficientMoneyException;
import com.shikavani.lld.vendingmachine.model.*;
import com.shikavani.lld.vendingmachine.model.payment.Denomination;
import com.shikavani.lld.vendingmachine.model.transaction.PaymentOutcome;
import com.shikavani.lld.vendingmachine.model.transaction.PaymentRequest;
import com.shikavani.lld.vendingmachine.service.PaymentService;
import com.shikavani.lld.vendingmachine.model.transaction.Transaction;

import java.util.*;

public class ProductSelectedState implements VendingMachineState{

    private final VendingMachine vendingMachine;
    private final PaymentService paymentService;

    public ProductSelectedState(VendingMachine vendingMachine, PaymentService paymentService) {
        this.vendingMachine = vendingMachine;
        this.paymentService = paymentService;
    }

    @Override
    public void selectProduct(String productId) {
        throw new IllegalStateException("You have already selected the product.");
    }

    @Override
    public PaymentOutcome pay(PaymentRequest paymentRequest) {

        Transaction transaction = paymentRequest.transaction();
        PaymentOutcome paymentOutcome = paymentService.pay(paymentRequest);

        return switch (paymentOutcome) {
            case PaymentOutcome.Insufficient i -> {
                // nothing was ever committed to the till, so nothing to unwind
                throw new InsufficientMoneyException(
                        String.format("Short by %s. Insert more or cancel.", i.money()));
            }
            case PaymentOutcome.Exact e -> {
                this.vendingMachine.commitTender(transaction.getTender());
                System.out.println("Moving to dispensing the product");
                this.vendingMachine.nextState(new DispenseProductState(this.vendingMachine));
                yield e;
            }

            case PaymentOutcome.Overpaid o -> {
                // update balance.
                this.vendingMachine.commitTender(transaction.getTender()); // take it money
                vendingMachine.nextState(new DispenseProductState(vendingMachine));
                this.vendingMachine.commitChange(o.change());
                yield o;
            }
        };
    }

    @Override
    public Product dispenseProduct() {
        throw new IllegalStateException("cannot dispense product during insert money state");
    }

    @Override
    public void cancel() {
        this.vendingMachine.nextState(new IdleState(this.vendingMachine, this.vendingMachine.getProductService()));
    }
}
