package com.paymentology.transactions.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

	// Folder location for storing files
	private String location = "upload-dir";

	public String getLocation() {
		return location;
	}
}
