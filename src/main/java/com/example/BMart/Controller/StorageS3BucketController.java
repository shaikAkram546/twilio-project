package com.example.BMart.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.BMart.Service.StorageS3Service;

@RestController
@RequestMapping("/s3upload")
public class StorageS3BucketController {

	@Autowired
	private StorageS3Service s3Service;
	@PostMapping("/upload")
	public String uploadFile(@RequestParam("file") MultipartFile file) {
System.out.println("qwertyuiopasdfghjklzxcvbn");
		try {
			File tempFile = File.createTempFile("temp", null);

			// Write the content of the uploaded file to the temporary file
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

				// Upload the temporary file to S3 inside the try-with-resources block

				s3Service.uploadFileToS3(file.getOriginalFilename(), new FileInputStream(tempFile));

				tempFile.delete();
				JSONObject jsonResponse = new JSONObject();
				jsonResponse.put("status", "Succes");

				return jsonResponse.toString();
				// Delete the temporary file
//	            tempFile.delete();

			}

		} catch (Exception e) {
			JSONObject jsonResponse = new JSONObject();
			JSONObject exception = jsonResponse.put("status", "Failed to upload file");
			return exception.toString();
		}
	}
	


	 @PostMapping("/callsinch")
	    public String callingWithSINCH() {
	        String key = "b1bc1ce4-7f69-482e-a4d7-624ec588bb07";
	        String secret = "hu7WDEn0HU_xt_nfTrDC0GVO0B";
	        String fromNumber = "9666636715";
	        String to = "6304714737";
	        String locale = "en-IN";  // Set a default locale

	        try {
	        	var httpClient = HttpClient.newBuilder().build();

	            var payload = String.join("\n"
	              , "{"
	              , " \"method\": \"ttsCallout\","
	              , " \"ttsCallout\": {"
	              , "  \"cli\": \"" + fromNumber + "\","
	              , "  \"destination\": {"
	              , "   \"type\": \"number\","
	              , "   \"endpoint\": \"" + to + "\""
	              , "  },"
	              , "  \"locale\": \"" + locale + "\","
	              , "  \"text\": \"Hello, this is a call from Sinch. Congratulations! You made your first call.\""
	              , " }"
	              , "}"
	            );

	            var host = "https://calling.api.sinch.com";
	            var pathname = "/calling/v1/callouts";
	            var request = HttpRequest.newBuilder()
	              .POST(HttpRequest.BodyPublishers.ofString(payload))
	              .uri(URI.create(host + pathname ))
	              .header("Content-Type", "application/json")
	              .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((key + ":" + secret).getBytes()))
	              .build();

	            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

	            System.out.println(response.body());
	            return pathname;

	        } catch (Exception e) {
	            e.printStackTrace();
	            return "Error: " + e.getMessage();
	        }
	    }
	}

