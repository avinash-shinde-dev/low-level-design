package com.shikavani.lld.parkinglot.strategy.payment;

import com.shikavani.lld.parkinglot.model.CardDetails;
import com.shikavani.lld.parkinglot.model.PaymentDetails;
import com.shikavani.lld.parkinglot.model.PaymentRequest;
import com.shikavani.lld.parkinglot.model.PaymentResponse;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CardStrategy implements PaymentStrategy{
    @Override
    public final PaymentResponse pay(PaymentRequest paymentRequest) {
        System.out.println("*** Payment processing ***");
        CardDetails cardDetails = validate(paymentRequest.paymentDetails());
        return executePayment(cardDetails, paymentRequest);
    }

    private CardDetails validate(PaymentDetails paymentDetails){
        if(! (paymentDetails instanceof CardDetails cardDetails)){
            throw new IllegalArgumentException("Card payment required card details");
        }

        if(cardDetails.cardNo() == null || cardDetails.cardNo().isBlank()){
            throw new IllegalArgumentException("Card Number is required");
        }

        if(cardDetails.cvv() == null || cardDetails.cvv().length() != 3){
            throw new IllegalArgumentException("Invalid cvv");
        }

        if(cardDetails.expDate() == null || isExpired(cardDetails.expDate())){
            throw new IllegalArgumentException("Invalid expiry date");
        }

        return cardDetails;
    }

    private boolean isExpired(String expDate){
        YearMonth cardExpiry;
        try {
            cardExpiry = YearMonth.parse(expDate, DateTimeFormatter.ofPattern("MM/yy"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Expiry date must be in MM/yy format", e);
        }
        return cardExpiry.isBefore(YearMonth.now());
    }

    protected PaymentResponse executePayment(CardDetails cardDetails, PaymentRequest paymentRequest){
         throw new UnsupportedOperationException("Payment execution should be taken care by subclasses");
    }
}
