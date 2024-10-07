package com.paymentology.transactions;

import com.paymentology.transactions.config.StorageProperties;
import com.paymentology.transactions.exception.FileStorageException;
import com.paymentology.transactions.service.StorageService;
import com.paymentology.transactions.service.implementations.StorageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StorageServiceTests {

    private StorageProperties properties = new StorageProperties();
    private StorageService service;

    @BeforeEach
    public void init() {
        properties.setLocation("target/files/" + Math.abs(new Random().nextLong()));
        service = new StorageServiceImpl(properties);
        service.init();
    }

    @Test
    public void emptyUploadLocation() {
        service = null;
        properties.setLocation("");
        assertThrows(FileStorageException.class, () -> {
            service = new StorageServiceImpl(properties);
        });
    }

    @Test
    public void loadNonExistent() {
        assertThat(service.load("foo.txt")).doesNotExist();
    }
}
