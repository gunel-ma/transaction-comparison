package com.paymentology.transactions.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.paymentology.transactions.model.TransactionDTO;
import com.paymentology.transactions.service.CsvReader;
import com.paymentology.transactions.utils.CsvGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.paymentology.transactions.exception.StorageFileNotFoundException;
import com.paymentology.transactions.service.StorageService;

@Controller
@RequiredArgsConstructor
public class TransactionCompareController {

	private final StorageService storageService;
	private final CsvReader csvReader;
	private final CsvGeneratorUtil csvGeneratorUtil;

	List<TransactionDTO> unmatchedTransactions = new ArrayList<>();


	@GetMapping("/")
	public String listUploadedFiles(Model model) {
		return "transactionCompare";
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

		Resource file = storageService.loadAsResource(filename);

		if (file == null)
			return ResponseEntity.notFound().build();

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}

	@PostMapping("/")
	public String handleFileUpload(@RequestParam("file1") MultipartFile file1,
								   @RequestParam("file2") MultipartFile file2,
								   RedirectAttributes redirectAttributes) {

		unmatchedTransactions = csvReader.compareTransactions( file1,
				file2);

		List<TransactionDTO> firstFileTransactions = csvReader.getCsvResult(file1);
		List<TransactionDTO> secondFileTransactions = csvReader.getCsvResult(file2);

		redirectAttributes.addFlashAttribute("unmatchedCount", unmatchedTransactions.size());
		redirectAttributes.addFlashAttribute("file1_totalCount", firstFileTransactions.size());
		redirectAttributes.addFlashAttribute("file2_totalCount", secondFileTransactions.size());

		redirectAttributes.addFlashAttribute("file1_unmatchedCount", firstFileTransactions.size()
				- csvReader.compareTransactions( file1,
				file2).size());
		redirectAttributes.addFlashAttribute("file2_unmatchedCount", secondFileTransactions.size()
				- csvReader.compareTransactions( file1,
				file2).size());
		redirectAttributes.addFlashAttribute("file1_name", file1.getOriginalFilename());
		redirectAttributes.addFlashAttribute("file2_name", file2.getOriginalFilename());

		return "redirect:/";
	}

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
