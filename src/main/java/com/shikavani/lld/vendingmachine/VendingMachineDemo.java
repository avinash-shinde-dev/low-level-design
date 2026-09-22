package com.shikavani.lld.vendingmachine;

import com.shikavani.lld.vendingmachine.enums.Category;
import com.shikavani.lld.vendingmachine.enums.PaymentType;
import com.shikavani.lld.vendingmachine.model.*;
import com.shikavani.lld.vendingmachine.model.payment.*;
import com.shikavani.lld.vendingmachine.registry.StrategyRegistry;
import com.shikavani.lld.vendingmachine.repository.ProductRepository;
import com.shikavani.lld.vendingmachine.inventory.ProductInventory;
import com.shikavani.lld.vendingmachine.service.PaymentService;
import com.shikavani.lld.vendingmachine.service.ProductService;
import com.shikavani.lld.vendingmachine.state.VendingMachine;
import com.shikavani.lld.vendingmachine.inventory.CashInventory;
import com.shikavani.lld.vendingmachine.strategy.payment.CashPaymentStrategy;
import com.shikavani.lld.vendingmachine.strategy.payment.PaymentStrategy;
import com.shikavani.lld.vendingmachine.strategy.payment.UpiPaymentStrategy;
import com.shikavani.lld.vendingmachine.utils.ChangeCalculator;
import com.shikavani.lld.vendingmachine.utils.MoneyCalculator;

import java.math.BigDecimal;
import java.util.*;

public class VendingMachineDemo {
    private final static Currency currency = Currency.getInstance("INR");
    public static void main(String[] args) {

        ProductInventory productInventory = new ProductInventory();
        ProductRepository productRepository = new ProductRepository();
        ProductService productService = new ProductService(productRepository, productInventory);

        productService.addProduct(new Product("p101","product1", Category.SNACK, new Price(BigDecimal.valueOf(10), currency)));
        productService.addProduct(new Product("p102","product2", Category.CHOCOLATE, new Price(BigDecimal.valueOf(8), currency)));
        productService.addProduct(new Product("p103","product3", Category.BEVERAGE, new Price(BigDecimal.valueOf(15), currency)));
        productService.addProduct(new Product("p104","product4", Category.CHOCOLATE, new Price(BigDecimal.valueOf(25), currency)));
        productService.addProduct(new Product("p105","product5", Category.BEVERAGE, new Price(BigDecimal.valueOf(35), currency)));
        productService.addProduct(new Product("p106","product6", Category.SNACK, new Price(BigDecimal.valueOf(20), currency)));

        ChangeCalculator changeCalculator = new ChangeCalculator();
        CashInventory cashInventory = new CashInventory();

        StrategyRegistry<PaymentType, PaymentStrategy> paymentStrategies = StrategyRegistry.<PaymentType, PaymentStrategy>builder()
                .register(PaymentType.CASH,  new CashPaymentStrategy(changeCalculator, cashInventory ))
                .register(PaymentType.UPI, new UpiPaymentStrategy())
                .build();

        PaymentService paymentService = new PaymentService(paymentStrategies);

        // productInventory.displayInventory();
        VendingMachine vendingMachine = new VendingMachine(productInventory, cashInventory , productService, paymentService);


        Admin adminOperations = new Admin(productService, cashInventory);

        // stock products

        adminOperations.restockProducts("p104", 10);

        // add another product
        adminOperations.addProduct(new Product("p106", "Product6", Category.BEVERAGE, new Price(BigDecimal.valueOf(35), currency)));

        vendingMachine.display();

        User avinash = getUser(vendingMachine);

        avinash.selectProduct("p104");

        Map<Denomination, Integer> denominations = Map.of(
                new Coin(5, currency), 2, // 10
                new Coin(2, currency), 3, // 6
                new Coin(1, currency), 4, // 4
                new Note(10, currency), 5 // 50
        );
        System.out.println("Inserted: " + new MoneyCalculator(new Money(BigDecimal.ZERO, currency)).fromDenominations(denominations));

        Map<Denomination, Integer> refund = avinash.insertDenomination(denominations);
        // 45
        System.out.println("Refund: " +new MoneyCalculator(new Money(BigDecimal.ZERO, currency)).fromDenominations(refund));

        System.out.println(avinash.collect());


    }

    private static User getUser(VendingMachine vendingMachine){
        User user =  new User("u101", "Avinash", "98765543210", "avinash@gmail.com", vendingMachine);

        user.addDenomination(new Coin(5, currency));
        user.addDenomination(new Coin(1, currency));
        user.addDenomination(new Coin(2, currency));
        user.addDenomination(new Coin(5, currency));
        user.addDenomination(new Coin(5, currency));
        user.addDenomination(new Coin(1, currency));
        user.addDenomination(new Coin(2, currency));
        user.addDenomination(new Note(10, currency));
        user.addDenomination(new Note(10, currency));
        user.addDenomination(new Note(20, currency));
        user.addDenomination(new Note(50, currency));
        user.addDenomination(new Note(100, currency));
        user.addDenomination(new Note(100, currency));
        return user;
    }
}
