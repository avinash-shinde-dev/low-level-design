package com.shikavani.lld.vendingmachine.state;

import com.shikavani.lld.vendingmachine.enums.StockStatus;
import com.shikavani.lld.vendingmachine.inventory.CashInventory;
import com.shikavani.lld.vendingmachine.model.*;
import com.shikavani.lld.vendingmachine.model.payment.*;
import com.shikavani.lld.vendingmachine.model.transaction.PaymentOutcome;
import com.shikavani.lld.vendingmachine.model.transaction.PaymentRequest;
import com.shikavani.lld.vendingmachine.inventory.ProductInventory;
import com.shikavani.lld.vendingmachine.service.PaymentService;
import com.shikavani.lld.vendingmachine.service.ProductService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class VendingMachine {
    private ReentrantLock lock = new ReentrantLock(true);
    private volatile VendingMachineState currentState;
    private final ProductInventory productInventory;
    private final CashInventory cashInventory;
    private final ProductService productService;
    private final PaymentService paymentService;
    private Product selectedProduct;

    public VendingMachine(ProductInventory productInventory, CashInventory cashInventory, ProductService productService, PaymentService paymentService) {
        this.cashInventory = cashInventory;
        this.productService = productService;
        this.productInventory = productInventory;
        this.paymentService = paymentService;
        this.currentState = new IdleState(this, productService);
    }

    void nextState(VendingMachineState vendingMachineState){
        this.currentState = vendingMachineState;
    }

    void commitTender(Map<Denomination, Integer> denominations){
        this.cashInventory.addDenomination(denominations);
    }

    void commitChange(Map<Denomination, Integer> denominations){
        this.cashInventory.removeDenomination(denominations);
    }


    public Product getSelectedProduct() {
        return selectedProduct;
    }

    void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public void decrementQuantity(String productId){
        this.productInventory.decrementQuantity(productId);
    }

    // user
    public void selectProduct(String productId){
        lock.lock();
        try {
            this.currentState.selectProduct(productId);
        }finally {
            lock.unlock();
        }
    }


    public Map<Denomination, Integer> pay(PaymentRequest paymentRequest){
        lock.lock();
        try {
            PaymentOutcome outcome = this.currentState.pay(paymentRequest);

            return switch (outcome) {
                case PaymentOutcome.Exact e -> Map.of(); // nothing to return to user
                case PaymentOutcome.Overpaid o -> o.change(); // return the change
                default -> throw new IllegalStateException("Unexpected value: " + outcome);
            };
        }finally {
            lock.unlock();
        }
    }

    public Product collect() {
        lock.lock();
        try {
            return this.currentState.dispenseProduct();
        }finally {
            lock.unlock();
        }
    }

    public void cancel() {
        lock.lock();
        try {
            this.currentState.cancel();
        }finally {
            lock.unlock();
        }
    }

    boolean isProductAvailable(String productId){
        return this.productInventory.isProductAvailable(productId);
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public ProductInventory getProductInventory() {
        return productInventory;
    }

    public VendingMachineState getCurrentState() {
        return currentState;
    }

    public void display() {
        System.out.println("**** Vending Machine **** ");
        System.out.println("No. | Product Id | Product Name | Price | Quantity | StockStatus");
        List<String> productIds = List.copyOf(this.productInventory.getAllProductIds());
        for (int i = 0; i < productIds.size(); i++) {
            String productId = productIds.get(i);
            Product product = this.productService.getProductById(productId);
            System.out.printf("%d | %s | %s | %s | %s | %s %n",
                    i + 1, productId, product.getName(), product.getPrice(),
                    productInventory.getQuantity(productId),
                    isProductAvailable(productId) ? StockStatus.IN_STOCK : StockStatus.OUT_OF_STOCK);
        }
    }

}
