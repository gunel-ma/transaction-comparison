package com.paymentology.transactions.service;

import org.springframework.core.io.Resource;
import java.nio.file.Path;

public interface StorageService {

    Path load(String filename);

    Resource loadAsResource(String filename);

}