package com.example.BMart.Service;

import java.io.InputStream;

public interface StorageS3Service {

	
	public String uploadFileToS3(String key, InputStream inputStream);
	
}
