package com.shikavani.lld.vendingmachine.model;


import com.shikavani.lld.vendingmachine.exception.ProductNotSelectedException;
import com.shikavani.lld.vendingmachine.model.payment.Denomination;
import com.shikavani.lld.vendingmachine.model.payment.Money;
import com.shikavani.lld.vendingmachine.model.payment.Price;
import com.shikavani.lld.vendingmachine.model.transaction.CashTender;
import com.shikavani.lld.vendingmachine.model.transaction.PaymentRequest;
import com.shikavani.lld.vendingmachine.model.transaction.Transaction;
import com.shikavani.lld.vendingmachine.state.VendingMachine;
import com.shikavani.lld.vendingmachine.utils.MoneyCalculator;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class User {
    private final String userId;
    private final String name;
    private String phoneNumber;
    private String email;
    private final VendingMachine vendingMachine;
    private Map<Denomination, Integer> cash;

    public User(String userId, String name, String phoneNumber, String email, VendingMachine vendingMachine) {
        this.userId = userId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.vendingMachine = vendingMachine;
        this.cash = new ConcurrentHashMap<>();
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCash(Map<Denomination, Integer> cash) {
        this.cash = cash;
    }

    public VendingMachine getVendingMachine() {
        return vendingMachine;
    }

    public void addDenomination(Denomination denomination){
        this.cash.merge(denomination, 1, Integer::sum);
    }

    public Money getMoney() {
        return new MoneyCalculator(new Money(BigDecimal.ZERO, Currency.getInstance("INR"))).fromDenominations(this.cash);
    }

    public void selectProduct(String productId){
        this.vendingMachine.selectProduct(productId);
    }

    public Map<Denomination, Integer> insertDenomination(Map<Denomination, Integer> denominations){
        Product product = this.vendingMachine.getSelectedProduct();
        Price price = null;
        if(product != null) {
            price = product.getPrice();
        } else {
            throw new ProductNotSelectedException("Product not selected");
        }

        Transaction transaction = new Transaction(UUID.randomUUID().toString(), product);
        PaymentRequest paymentRequest = new PaymentRequest(UUID.randomUUID().toString(), price, transaction, new CashTender(denominations));
        return this.vendingMachine.pay(paymentRequest);
    }

    public Product collect() {
        return this.vendingMachine.collect();
    }



    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
