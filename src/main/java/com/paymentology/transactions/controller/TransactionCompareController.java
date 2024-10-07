package com.paymentology.transactions.controller;

import java.util.ArrayList;
import java.util.List;
import com.paymentology.transactions.model.TransactionDTO;
import com.paymentology.transactions.service.CsvReaderService;
import com.paymentology.transactions.utils.CsvGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.paymentology.transactions.exception.StorageFileNotFoundException;
import com.paymentology.transactions.service.StorageService;

@Controller
@RequiredArgsConstructor
public class TransactionCompareController {

	private final StorageService storageService;
	private final CsvReaderService csvReader;
	private final CsvGeneratorUtil csvGeneratorUtil;

	List<TransactionDTO> unmatchedTransactions = new ArrayList<>();

	/*
	* Returns transactionCompare.html from the resources/templates folder
	 */
	@GetMapping("/")
	public String listUploadedFiles(Model model) {
		return "transactionCompare";
	}

	/*
	*  Uploads given two files, compares transactions between them and
	*  passes some statistics (number of total transactions, non-matching transactions,
	*  matching transactions, file names) to the html page: resources/templates/transactionCompare.html
	 */
	@PostMapping("/")
	public String handleFileUpload(@RequestParam("file1") MultipartFile file1,
								   @RequestParam("file2") MultipartFile file2,
								   RedirectAttributes redirectAttributes) {

		List<List<TransactionDTO>> comparisonResult = csvReader.compareTransactions(file1, file2);
		unmatchedTransactions = comparisonResult.get(0);
		unmatchedTransactions.addAll(comparisonResult.get(1));

		// calling parseCsv() function to get the statistics of matching transactions
		List<TransactionDTO> firstFileTransactions = csvReader.parseCsv(file1);
		List<TransactionDTO> secondFileTransactions = csvReader.parseCsv(file2);

		// Passing values to the html page
		redirectAttributes.addFlashAttribute("file1_totalCount", firstFileTransactions.size());
		redirectAttributes.addFlashAttribute("file2_totalCount", secondFileTransactions.size());
		redirectAttributes.addFlashAttribute("file1_matchingCount",
				firstFileTransactions.size() - comparisonResult.get(0).size());
		redirectAttributes.addFlashAttribute("file2_matchingCount",
				secondFileTransactions.size() - comparisonResult.get(1).size());
		redirectAttributes.addFlashAttribute("file1_unmatchedCount", comparisonResult.get(0).size());
		redirectAttributes.addFlashAttribute("file2_unmatchedCount", comparisonResult.get(1).size());
		redirectAttributes.addFlashAttribute("file1_name", file1.getOriginalFilename());
		redirectAttributes.addFlashAttribute("file2_name", file2.getOriginalFilename());
		return "redirect:/";
	}

	/*
	* This endpoint is used in the html page to download csv
	*  file containing non-matching transactions from both files
	 */
	@GetMapping("/files/csv")
	public ResponseEntity<byte[]> generateCsvFromUnmatchedTransactions() {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", "unmatchedReport.csv");
		byte[] csvBytes = csvGeneratorUtil.generateCsv(unmatchedTransactions).getBytes();
		return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}
}
