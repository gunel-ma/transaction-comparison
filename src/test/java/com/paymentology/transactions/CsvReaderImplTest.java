package com.paymentology.transactions;

import com.paymentology.transactions.model.TransactionDTO;
import com.paymentology.transactions.service.implementations.CsvReaderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class CsvReaderImplTest {

    @InjectMocks
    private CsvReaderServiceImpl csvReader;

    ClassPathResource resourceFile1 = new ClassPathResource("37777aa6-fae8-48cb-aa8b-55220d03cab2.csv");
    ClassPathResource resourceFile2 = new ClassPathResource("b1bfec9a-34bf-490a-8edc-caac0660a4eb.csv");

    TransactionDTO transactionDTO = TransactionDTO.builder()
                                                    .id("0584011808649511")
                                                    .profileName("Card Campaign")
                                                    .date("2014-01-11 22:27:44")
                                                    .amount(new BigDecimal("-20000.0"))
                                                    .narrative("*MOLEPS ATM25             MOLEPOLOLE    BW")
                                                    .description("DEDUCT")
                                                    .type("1")
                                                    .walletReference("P_NzI2ODY2ODlfMTM4MjcwMTU2NS45MzA5")
                                                .build();

    @Test
    void testParseCsv()  {
        List<TransactionDTO> result = null;//( "/home/gunel/IdeaProjects/transaction-comparison/src/test/resources/37777aa6-fae8-48cb-aa8b-55220d03cab2.csv");
        try {
            MultipartFile file = new MockMultipartFile("37777aa6-fae8-48cb-aa8b-55220d03cab2.csv",
                    "37777aa6-fae8-48cb-aa8b-55220d03cab2.csv", "text/csv", Files.readAllBytes(resourceFile1.getFile().toPath()));
            result = csvReader.parseCsv (file);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(result);
        assertThat(result.size()).isGreaterThan(1);
        assertEquals(305, result.size());
        assertEquals(transactionDTO, result.get(0));
    }

    @Test
    void testCompareTransactions()  {
        List<List<TransactionDTO>> resultCompareTransactions = null;
        try {
            MultipartFile file1 = new MockMultipartFile("37777aa6-fae8-48cb-aa8b-55220d03cab2.csv",
                    "37777aa6-fae8-48cb-aa8b-55220d03cab2.csv", "text/csv", Files.readAllBytes(resourceFile1.getFile().toPath()));
            MultipartFile file2 = new MockMultipartFile("b1bfec9a-34bf-490a-8edc-caac0660a4eb.csv",
                    "b1bfec9a-34bf-490a-8edc-caac0660a4eb.csv", "text/csv", Files.readAllBytes(resourceFile2.getFile().toPath()));

            resultCompareTransactions = csvReader.compareTransactions (file1, file2);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(resultCompareTransactions);
        assertEquals(2, resultCompareTransactions.size());
        assertEquals(4, resultCompareTransactions.get(0).size());
        assertEquals(3, resultCompareTransactions.get(1).size());

    }
}
