package com.paymentology.transactions.service;

import com.paymentology.transactions.model.TransactionDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
public interface CsvReader {

    List<TransactionDTO> getCsvResult(MultipartFile filePath);

    List<TransactionDTO> compareTransactions(MultipartFile file1, MultipartFile file2);

}
