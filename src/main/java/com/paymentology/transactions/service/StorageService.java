package com.paymentology.transactions.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public interface StorageService {

    void init();

    Path load(String filename);

    Resource loadAsResource(String filename);

}