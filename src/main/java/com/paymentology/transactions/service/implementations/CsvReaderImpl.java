package com.paymentology.transactions.service.implementations;

import com.paymentology.transactions.model.TransactionDTO;
import com.paymentology.transactions.service.CsvReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.paymentology.transactions.utils.Constants.COMMA;

@Service
@AllArgsConstructor
@Slf4j
public class CsvReaderImpl implements CsvReader {

    private static final Logger logger = LoggerFactory.getLogger(CsvReaderImpl.class);

    public List<TransactionDTO> parseCsv(MultipartFile filePath) {

        List<TransactionDTO> transactionDTOs = new ArrayList<>();

        try {
            InputStream inputStream = filePath.getInputStream();
                    new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                            .lines()
                                .forEach(line -> {
                                                TransactionDTO transactionDTO = new TransactionDTO();
                                                String[] values = line.split(COMMA);
                                                try {
                                                    transactionDTO.setProfileName(values[0]);
                                                    transactionDTO.setDate(values[1]);
                                                    transactionDTO.setAmount(BigDecimal.valueOf(Double.parseDouble(values[2])));
                                                    transactionDTO.setNarrative(values[3]);
                                                    transactionDTO.setDescription(values[4]);
                                                    transactionDTO.setId(values[5]);
                                                    transactionDTO.setType(values[6]);
                                                    transactionDTO.setWalletReference(values[7]);
                                                    transactionDTOs.add(transactionDTO);
                                }
                                catch (Exception e) {
                                    logger.warn("Parsing exception occurred : " + e.getMessage());

                                }
                            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return transactionDTOs;
    }

    public List<List<TransactionDTO>> compareTransactions(MultipartFile file1, MultipartFile file2) {

        List<List<TransactionDTO>> nonMatchingTransactionsLists = new ArrayList<>();
        List<TransactionDTO> firstUnmatchedList = new ArrayList<>();
        List<TransactionDTO> secondUnmatchedList = new ArrayList<>();
        try {
            List<TransactionDTO> firstList = parseCsv(file1);
            List<TransactionDTO> secondList = parseCsv(file2);

            firstList.stream()
                    .filter(transactionDTO -> secondList.stream()
                            .noneMatch(secondListTransaction ->
                                    secondListTransaction.getId().equals(transactionDTO.getId())))
                    .filter(transactionDTO -> secondList.stream()
                            .noneMatch(secondListTransaction ->
                                    secondListTransaction.getDate().equals(transactionDTO.getDate()) &&
                                    secondListTransaction.getAmount().equals(transactionDTO.getAmount()) &&
                                    secondListTransaction.getWalletReference().equals(transactionDTO.getWalletReference())))
                    .forEach(firstUnmatchedList::add);

            secondList.stream()
                    .filter(transactionDTO -> firstList.stream()
                            .noneMatch(firstTransaction ->
                                    firstTransaction.getId().equals(transactionDTO.getId())))
                    .filter(transactionDTO -> firstList.stream()
                            .noneMatch(firstTransaction ->
                                    firstTransaction.getDate().equals(transactionDTO.getDate()) &&
                                    firstTransaction.getAmount().equals(transactionDTO.getAmount()) &&
                                    firstTransaction.getWalletReference().equals(transactionDTO.getWalletReference())))
                    .forEach(secondUnmatchedList::add);
        }
        catch (Exception exception) {
            logger.error(exception.getMessage());
        }
        nonMatchingTransactionsLists.add(firstUnmatchedList);
        nonMatchingTransactionsLists.add(secondUnmatchedList);

        return nonMatchingTransactionsLists;
    }
}
