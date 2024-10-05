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

    public List<TransactionDTO> getCsvResult(MultipartFile filePath) {

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
                                    logger.error("Parsing exception occurred : " + e.getMessage());

                                }

                            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return transactionDTOs;
    }

    public List<TransactionDTO> compareTransactions(MultipartFile file1, MultipartFile file2) {

        List<TransactionDTO> nonMatchingTransactions = new ArrayList<>();

        try {
            List<TransactionDTO> firstList = getCsvResult(file1);
            List<TransactionDTO> secondList = getCsvResult(file2).stream()
                    .toList();
            List<String> firstListIDs = firstList.stream()
                    .map(TransactionDTO::getId)
                    .toList();
            List<String> secondListIDs = secondList.stream()
                    .map(TransactionDTO::getId)
                    .toList();


            firstList.stream()
                    .filter(transactionDTO -> !secondListIDs.contains(transactionDTO.getId()))
                    .forEach(nonMatchingTransactions::add);
            secondList.stream()
                    .filter(transactionDTO -> !firstListIDs.contains(transactionDTO.getId()))
                    .forEach(nonMatchingTransactions::add);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            logger.error(illegalArgumentException.getMessage());
        }
        catch (Exception exception) {
            logger.warn(exception.getMessage());
        }

        return nonMatchingTransactions;
    }
}
