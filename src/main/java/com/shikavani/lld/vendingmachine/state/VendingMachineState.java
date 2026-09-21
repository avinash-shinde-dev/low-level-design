package com.shikavani.lld.vendingmachine.state;

import com.shikavani.lld.vendingmachine.model.transaction.PaymentOutcome;
import com.shikavani.lld.vendingmachine.model.transaction.PaymentRequest;
import com.shikavani.lld.vendingmachine.model.Product;

public interface VendingMachineState {

    void selectProduct(String productId);
    PaymentOutcome pay(PaymentRequest paymentRequest);
    Product dispenseProduct();
    void cancel();
}
