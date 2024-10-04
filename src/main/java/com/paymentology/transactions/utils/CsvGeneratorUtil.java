package com.paymentology.transactions.utils;

import com.paymentology.transactions.model.TransactionDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component

public class CsvGeneratorUtil {

    private static final String CSV_HEADER = "Transaction ID,Profile Name,Transaction Date,Transaction Amount,Narrative,Description," +
            "Transaction type,Wallet Reference\n";

    public String generateCsv(List<TransactionDTO> transactionDTOs) {
        StringBuilder csvContent = new StringBuilder();
        csvContent.append(CSV_HEADER);

        transactionDTOs
                .forEach(transactionDTO -> csvContent.append(transactionDTO.getId()).append(",")
                                                    .append(transactionDTO.getProfileName()).append(",")
                                                    .append(transactionDTO.getDate()).append(",")
                                                    .append(transactionDTO.getAmount()).append(",")
                                                    .append(transactionDTO.getNarrative()).append(",")
                                                    .append(transactionDTO.getDescription()).append(",")
                                                    .append(transactionDTO.getType()).append(",")
                                                    .append(transactionDTO.getWalletReference()).append("\n"));


        return csvContent.toString();
    }
}
