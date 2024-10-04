package com.paymentology.transactions.model;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDTO {

    private String id;

    private String profileName;

    private String date;

    private BigDecimal amount;

    private String narrative;

    private String description;

    private String type;

    private String walletReference;

}
