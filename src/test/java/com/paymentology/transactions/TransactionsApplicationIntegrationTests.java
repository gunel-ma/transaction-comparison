package com.paymentology.transactions;

import com.paymentology.transactions.service.StorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TransactionsApplicationIntegrationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@MockBean
	private StorageService storageService;

	@LocalServerPort
	private int port;

	@Test
	public void shouldUploadFile() throws Exception {

		ClassPathResource resource1 = new ClassPathResource("37777aa6-fae8-48cb-aa8b-55220d03cab2.csv");
		ClassPathResource resource2 = new ClassPathResource("37777aa6-fae8-48cb-aa8b-55220d03cab2.csv");

		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		map.add("file1", resource1);
		map.add("file2", resource2);

		ResponseEntity<String> response = this.restTemplate.postForEntity("/", map,
				String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
		assertThat(response.getHeaders().getLocation().toString())
				.startsWith("http://localhost:" + "5000" + "/");
	}

}
