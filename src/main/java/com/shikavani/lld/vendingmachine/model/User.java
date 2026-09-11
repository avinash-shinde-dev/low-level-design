package com.shikavani.lld.vendingmachine.model;


import com.shikavani.lld.vendingmachine.state.VendingMachineContext;

public class User {
    private final String userId;
    private final String name;
    private String phoneNumber;
    private String email;
    private final VendingMachineContext vendingMachineContext;
    public User(String userId, String name, String phoneNumber, String email, VendingMachineContext vendingMachineContext) {
        this.userId = userId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.vendingMachineContext = vendingMachineContext;
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

    public VendingMachineContext getVendingMachineContext() {
        return vendingMachineContext;
    }


    @Override
    public String toString() {
        return "AbstractUser{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
