package com.shikavani.lld.vendingmachine.model;

import com.shikavani.lld.vendingmachine.enums.Category;
import com.shikavani.lld.vendingmachine.model.payment.Price;

public class Product {
    private final String id;
    private final String name;
    private final Category category;
    private Price price;

    public Product(String id, String name, Category category, Price price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", price=" + price +
                '}';
    }
}