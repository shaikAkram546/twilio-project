package com.example.BMart.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.BMart.Controller.BMartOTPController;
import com.example.BMart.Entity.CallingNumbers;
import com.example.BMart.Entity.SMSSendRequest;
import com.example.BMart.Repository.CallingNumbersRepository;
import com.example.BMart.Repository.OTPRepository;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.rest.api.v2010.account.Call.Status;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.ValidationRequest;
import com.twilio.rest.api.v2010.account.message.*;
import com.twilio.rest.preview.sync.service.syncmap.SyncMapItem;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import com.twilio.twiml.TwiMLException;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.fax.Receive.MediaType;
import com.twilio.twiml.voice.Gather.Language;
import com.twilio.twiml.voice.Play;
import com.twilio.twiml.voice.Say;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BMartOTPServiceImpl implements BMartOTPService {

	@Value("${TWILIO_ACCOUNT_SID}")
	String ACCOUNT_SID;

	@Value("${TWILIO_AUTH_TOKEN}")
	String AUTH_TOKEN;

	@Value("${TWILIO_OUTGOING_SMS_NUMBER}")
	String OUTGOING_SMS_NUMBER;

	@Autowired
	OTPRepository otpRepository;
	
	@Autowired
	CallingNumbersRepository callingNumbersRepository;

	public BMartOTPServiceImpl() {
		log.info("Creating class for SendSMS");
		log.info("Account_SID  " + ACCOUNT_SID);
	}

	@PostConstruct
	private void setup() {

		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

		log.info("Account_SID  " + ACCOUNT_SID);
	}
	

	@Override
	public String sendSMS(String name,String phoneNum) throws Exception {
		int min = 1000;
		int max = 9999;
		String status = "inprogress";
		String sendStatus =null;
		
		String pattern = "yyyy:MM:dd:hh:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String formattedDate = simpleDateFormat.format(new Date());


		String indPhoneNum ="+91"+phoneNum;
		Random random = new Random();
		int otp = random.nextInt((max - min) + 1) + min;
		SMSSendRequest smsrRequest = new SMSSendRequest();

		Message message = Message
				.creator(new PhoneNumber(indPhoneNum), 
					new PhoneNumber(OUTGOING_SMS_NUMBER), "Your OTP is "+otp)
						//new PhoneNumber(OUTGOING_SMS_NUMBER), "గౌరవనీయులైన సర్వేశ్వరరావు గారికి! నమస్కారం సార్ మున్సిపాలిటీలో వేకెన్సీస్ తొందర చేసి ఫిలప్ చేయొచ్చు కదా, లేట్ అయ్యే కొద్దీ ఆశావాహులు మరింత పెరుగుతూనే ఉంటారు.అలా అని లేట్ చేసే కొద్ది అందరికీ సమయము వేస్ట్ అవుతుంది కదా. పార్టీని నమ్ముకున్న వాళ్ళు నిరాశ చెందుతున్నారు. కొన్ని కొన్ని ఊళ్ళల్లో ఖాళీలను పూర్తి చేసి ఉన్నారు,మన పిడుగురాళ్లలో మాత్రం ఖాళీ ఏమాత్రం పట్టించుకోవడం లేదు కాబట్టి తొందర చేసి కాలిని భర్తీ చేయవలసిందిగా కోరుకుంటున్నాం ఇట్లు  @పార్టీ విధేయులు")
				.create();
		boolean phoneNumberExist = getphoneNumFromOTPTable(indPhoneNum);
		if(phoneNumberExist) {
			updatepindetails(indPhoneNum, otp);

			
		}else {
			insertOTPIntoTable(name, indPhoneNum, otp, status, formattedDate);

		}
		if(message.getStatus().toString().equals("queued")) {
			sendStatus ="True";
		}else {
			sendStatus ="False";
		}
		return sendStatus;

	}
	@Override
	public String otpVerification(String phoneNum, String otp) throws Exception {

		String ResponceMessage = null;
		String pattern = "yyyy:MM:dd:hh:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String formattedDate = simpleDateFormat.format(new Date());

		String indPhoneNum ="+91"+phoneNum;

       System.out.println("date----->"+formattedDate);
		SMSSendRequest findbyPhoneNum = otpRepository.findByPhoneNum(indPhoneNum);

		if(findbyPhoneNum !=null) {
		String existingOTP = findbyPhoneNum.getOtp();
		if (otp.equals(String.valueOf(existingOTP))) {
			
			findbyPhoneNum.setStatus("SUCCESS");
			findbyPhoneNum.setRecentlogin(formattedDate);
			otpRepository.save(findbyPhoneNum);
			ResponceMessage ="True";
		} else {

			ResponceMessage ="False";
		}
		}else {
			ResponceMessage ="Number Not Available";
		}

	
	return ResponceMessage;
}
	

	@Override
	public String CallingApiTest(String phoneNum, String callNum, String voicenote, String text) throws Exception {
		String INDphoneNum ="+91"+phoneNum;
		String INDcallNum ="+91"+callNum;
		List<String> randomNum = getServiceNumbers();
		String encodedText = UriComponentsBuilder.fromUriString(text).build().encode().toString();
		System.out.println("randomNumrandomNum====> "+randomNum);
		 Random rand = new Random();
	        String selectedNumber = "+91"+randomNum.get(rand.nextInt(randomNum.size()));
	        System.out.println("selectedNumber====> "+callNum);
		// String encodedText =
		// UriComponentsBuilder.fromUriString(text).build().encode().toString();
		TwilioRestClient client = new TwilioRestClient.Builder(ACCOUNT_SID, AUTH_TOKEN).build();
		Call call = Call
				.creator(new PhoneNumber(INDphoneNum), 
						new PhoneNumber(INDcallNum),
						URI.create(" http://13.201.132.244:8080/TeleCall/telepost/"+voicenote+"/"+encodedText))
				.setStatusCallback(URI.create("http://postb.in/1234abcd")).create(client);
		System.out.println(call);
		System.out.println(call.getStatus());
		System.out.println(call.getSid());
		return "Call Intiated";
	}  
	
	@Override
	public String addCallingNum(String phoneNum) {
		String indPhoneNum = "+91"+phoneNum;
	        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
			ValidationRequest validationRequest = ValidationRequest.creator(
	                new com.twilio.type.PhoneNumber(indPhoneNum))
	            .setFriendlyName("UserAddedNumber")
	            .create();

			String code = validationRequest.getValidationCode();
			SMSSendRequest smsrRequest = new SMSSendRequest();

			Message message = Message
					.creator(new PhoneNumber(indPhoneNum), new PhoneNumber(OUTGOING_SMS_NUMBER), "Your OTP is : " + code)
					.create();
	        System.out.println(validationRequest.getAccountSid());
	        System.out.println(validationRequest.getCallSid());
	        System.out.println(validationRequest.getFriendlyName());
	        System.out.println(validationRequest.getValidationCode());
	        System.out.println(validationRequest.getPhoneNumber());


			return code;
	    }

	@Override
	public String codeVerification(String phoneNum, String enteredCode) {
//		 String[] parts = phoneNum.split("\\|");
//	        String indPhoneNum = parts[1];

	        // Initialize Twilio and verify the entered code
		// Install the Java helper library from twilio.com/docs/java/install

		    // Find your Account SID and Auth Token at twilio.com/console
		    // and set the environment variables. See http://twil.io/secure

		        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		        SyncMapItem syncMapItem = SyncMapItem.creator(
		                "ISXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
		                "MPXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
		                "+14158675310",
		                new HashMap<String, Object>()
		                {{
		                put("verification_code", "123456");
		                }})
		            .create();

		        System.out.println(syncMapItem.getKey());
		        System.out.println(syncMapItem.getAccountSid());
		        System.out.println(syncMapItem.getMapSid());
		        System.out.println(syncMapItem.getServiceSid());

		        
				return null;
		    }
	


	
	@Override
	public String getCallResponse(String callSID) {
	    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

	    Call call = Call.fetcher(callSID).fetch();

	    
	    System.out.println("to"+call.getTo());
	    Status callRes=call.getStatus();
	    System.out.println(callRes);
	    System.out.println(call.getDuration());
	    System.out.println(call.getAnsweredBy());
	    System.out.println(call.getCallerName());
	    System.out.println(call.getDateCreated());
	    JSONObject jsonResponse = new JSONObject();
	    jsonResponse.put("status",callRes.toString()); 
	
	return jsonResponse.toString();
	  }

	@Override
	public String CallingApiRandomNum(String voicenote, String phoneNum, String text, String fileName) throws Exception {

		String INDphoneNum ="+91"+phoneNum;
		List<String> randomNum = getServiceNumbers();
		System.out.println("randomNumrandomNum====> "+randomNum);
		 Random rand = new Random();
	        String selectedNumber = "+91"+randomNum.get(rand.nextInt(randomNum.size()));
	        System.out.println("selectedNumber====> "+selectedNumber);
		
		//String randomNum = generateRandomPhoneNumber();

		String encodedText = UriComponentsBuilder.fromUriString(text).build().encode().toString();
		// String encodedText = URLEncoder.encode(text, "UTF-8");
		if (encodedText.equalsIgnoreCase("audio")) {
			TwilioRestClient client = new TwilioRestClient.Builder(ACCOUNT_SID, AUTH_TOKEN).build();
			Call call = Call
					.creator(new PhoneNumber(INDphoneNum), 
							new PhoneNumber(selectedNumber),
							URI.create(" http://13.201.132.244:8080/TeleCall/telepost/" + voicenote+"/"+fileName))
					.setStatusCallback(URI.create("http://postb.in/1234abcd")).create(client);
			System.out.println(call);
		}
		
		return "Call Intiated";
	}

	@Override
	public String callingSingleUSNum(String voicenote, String phoneNum, String text, String fileName) throws Exception {

		String INDphoneNum ="+91"+phoneNum;
		//String INDcallNum ="+91"+callNum;
		//String randomNum = generateRandomPhoneNumber();

		String encodedText = UriComponentsBuilder.fromUriString(text).build().encode().toString();
		// String encodedText = URLEncoder.encode(text, "UTF-8");
		if (encodedText.equalsIgnoreCase("audio")) {
			TwilioRestClient client = new TwilioRestClient.Builder(ACCOUNT_SID, AUTH_TOKEN).build();
			Call call = Call
					.creator(new PhoneNumber(INDphoneNum), 
							new PhoneNumber(OUTGOING_SMS_NUMBER),
							URI.create("http://13.201.132.244:8080/TeleCall/telepost/" + voicenote+"/"+fileName))
					.setStatusCallback(URI.create("http://postb.in/1234abcd")).create(client);
			System.out.println(call);
		
		}
		return "Call Intiated";
	}
	@Override
	public String callingOwnNumber(String voicenote, String callNum, String phoneNum, String text, String fileName) throws Exception {

		String INDphoneNum ="+91"+phoneNum;
		String INDcallNum ="+91"+callNum;
		//String randomNum = generateRandomPhoneNumber();
		List<CallingNumbers> callNumbers = getphoneNumFromTable();

		String encodedText = UriComponentsBuilder.fromUriString(text).build().encode().toString();
		// String encodedText = URLEncoder.encode(text, "UTF-8");
		if (encodedText.equalsIgnoreCase("audio")) {
			TwilioRestClient client = new TwilioRestClient.Builder(ACCOUNT_SID, AUTH_TOKEN).build();
			 for (CallingNumbers number : callNumbers) {
		            String destinationNumber = "+91" + number.getPhoneNum();
		            System.out.println("Calling number======> "+destinationNumber);
			Call call = Call
					.creator(new PhoneNumber(destinationNumber), 
							new PhoneNumber(INDcallNum),
							URI.create("http://13.201.132.244:8080/TeleCall/telepost/" + voicenote+"/"+fileName))
					.setStatusCallback(URI.create("http://postb.in/1234abcd")).create(client);
			//System.out.println(call);
			

		}
		return "Call Intiated";
		}
		return "Call Intiated";
	}
	
	@Override
	public String textCallingApiRandomNum(String voicenote, String phoneNum, String text) throws Exception {

		String INDphoneNum ="+91"+phoneNum;
		//String INDcallNum ="+91"+callNum;
		List<String> randomNum = getServiceNumbers();
		System.out.println("randomNumrandomNum====> "+randomNum);
		 Random rand = new Random();
	        String selectedNumber = "+91"+randomNum.get(rand.nextInt(randomNum.size()));
			String encodedText = UriComponentsBuilder.fromUriString(text).build().encode().toString();

	        //System.out.println("selectedNumber====> "+callNum);
		// String encodedText =
		// UriComponentsBuilder.fromUriString(text).build().encode().toString();
		TwilioRestClient client = new TwilioRestClient.Builder(ACCOUNT_SID, AUTH_TOKEN).build();
		Call call = Call
				.creator(new PhoneNumber(INDphoneNum), 
						new PhoneNumber(selectedNumber),
						URI.create("http://13.201.132.244:8080/TeleCall/telepost/"+voicenote+"/"+encodedText))
				.setStatusCallback(URI.create("http://postb.in/1234abcd")).create(client);
		System.out.println(call);
		return "Call Intiated with Text";
	}

	@Override
	public String textCallingSingleUSNum(String voicenote, String phoneNum, String text) throws Exception {

		String INDphoneNum ="+91"+phoneNum;
		String encodedText = UriComponentsBuilder.fromUriString(text).build().encode().toString();
		// String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
		TwilioRestClient client = new TwilioRestClient.Builder(ACCOUNT_SID, AUTH_TOKEN).build();
		Call call = Call
				.creator(new PhoneNumber(INDphoneNum), 
						new PhoneNumber(OUTGOING_SMS_NUMBER),
						URI.create("http://13.201.132.244:8 080/TeleCall/telepost/"+voicenote+"/"+encodedText))
				.setStatusCallback(URI.create("http://postb.in/1234abcd")).create(client);
		System.out.println(call);
		return "Call Intiated with Text";
	}
	@Override
	public String textCallingOwnNumber(String voicenote, String callNum, String phoneNum, String text) throws Exception {

		String INDphoneNum ="+91"+phoneNum;
		String INDcallNum ="+91"+callNum;
		//String randomNum = generateRandomPhoneNumber();
		String encodedText = UriComponentsBuilder.fromUriString(text).build().encode().toString();

		TwilioRestClient client = new TwilioRestClient.Builder(ACCOUNT_SID, AUTH_TOKEN).build();
		Call call = Call
				.creator(new PhoneNumber(INDphoneNum), 
						new PhoneNumber(INDcallNum),
						URI.create("http://13.201.132.244:8080/TeleCall/telepost/"+voicenote+"/"+encodedText))
				.setStatusCallback(URI.create("http://postb.in/1234abcd")).create(client);
		System.out.println(call);
		return "Call Intiated with Text";
	}

	@Override
	public String whatsAppMessage(String phoneNum) {
		// Find your Account SID and Auth Token at twilio.com/console
		// and set the environment variables. See http://twil.io/secure

		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		Message message = Message.creator(new com.twilio.type.PhoneNumber("whatsapp:" + OUTGOING_SMS_NUMBER),
				new com.twilio.type.PhoneNumber("whatsapp:" + phoneNum),
				"Your appointment is coming up on July 21 at 3PM").create();

		System.out.println(message.getSid());
		return "Message send to user ";
	}

	public SMSSendRequest insertOTPIntoTable(String name, String phoneNum, int otp, String status,String formattedDate) {
		SMSSendRequest smsRequest = new SMSSendRequest();
		smsRequest.setOtp(String.valueOf(otp));
		smsRequest.setPhoneNum(phoneNum);
		smsRequest.setName(name);
		smsRequest.setStatus(status);
		smsRequest.setFirstlogin(formattedDate);

		return otpRepository.save(smsRequest);
	}
	 public boolean getphoneNumFromOTPTable(String phoneNum) throws Exception  {
	    	boolean phoneNumberExists = false;
	    	
	    	SMSSendRequest customerOTP = new SMSSendRequest();
	    	SMSSendRequest phoneNumberExists1=otpRepository.findByPhoneNum(phoneNum);
	    	if(phoneNumberExists1 != null) {
	    		phoneNumberExists =true;
	    	}
	    	return phoneNumberExists;
	    }
	 public List<CallingNumbers> getphoneNumFromTable() throws Exception  {
	    	
	    	CallingNumbers callingNum = new CallingNumbers();

	    	return callingNumbersRepository.findAll();
	    }
	 public String updatepindetails(String phoneNum, int otp) throws Exception {

			SMSSendRequest customerOTP = new SMSSendRequest();
			SMSSendRequest findByphoneNumber = otpRepository.findByPhoneNum(phoneNum);
			findByphoneNumber.setOtp(String.valueOf(otp));
		

			otpRepository.save(findByphoneNumber);

			return "Pin data Updated";
		}

	

//	public String deleteOTPFromTable(String phoneNum) throws Exception {
//		String ResponceMessage = null;
//		Connection connection = null;
//		ResultSet result = null;
//		String jsonResponseString = null;
//		StringBuffer jsonString = new StringBuffer();
//		String str = null;
//
//		String jdbcUrl = "jdbc:mysql://44.228.192.138:8251/bmz";
//		String dbUser = "root";
//		String dbPassword = "Vasmaster@2022";
//
//		connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
//
//		String deleteQuery = "DELETE FROM otp_table WHERE phone_num = ?";
//
//		PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
//		preparedStatement.setString(1, phoneNum);
//
//		int resultSet = preparedStatement.executeUpdate();
//		if (resultSet > 0) {
//			System.out.println("OTP deleted successfully.");
//		} else {
//			System.out.println("Failed to delete OTP.");
//		}
//		preparedStatement.close();
//		connection.close();
//
//		return deleteQuery;
//	}

//	public String insertingOTPdetails(String phoneNum, int otp, String status) throws Exception {
//		Connection connection = null;
//
//		String jdbcUrl = "jdbc:mysql://44.228.192.138:8251/bmz";
//		String dbUser = "root";
//		String dbPassword = "Vasmaster@2022";
//
//		connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
//
//		String insertQuery1 = "INSERT INTO otp_table (phoneNum, otp, status) VALUES (? , ?, ?)";
//
//		PreparedStatement preparedStatement = connection.prepareStatement(insertQuery1);
//		preparedStatement.setString(1, phoneNum);
//		preparedStatement.setInt(2, otp);
//		preparedStatement.setString(3, status);
//
//		preparedStatement.executeUpdate();
//
//		preparedStatement.close();
//		connection.close();
//
//		return insertQuery1;
//
//	}
//	public static String generateRandomPhoneNumber() {
//
//		Random rand = new Random();
//		StringBuilder phoneNumber = new StringBuilder();
//
//		// Generate the first digit (9 or 6)
//		int firstDigit = rand.nextInt(2) == 0 ? 9 : 6;
//		phoneNumber.append(firstDigit);
//
//		// Generate the remaining 9 digits
//		for (int i = 0; i < 9; i++) {
//			int digit = rand.nextInt(10); // Generate a random digit (0-9)
//			phoneNumber.append(digit);
//		}
//
//		return phoneNumber.toString();
//	}
	
	 public static List<String> getServiceNumbers() {
	        List<String> numbers = new ArrayList<>();
	        Connection connection = null;

	        String jdbcUrl = "jdbc:mysql://44.228.192.138:8251/bmz";
	        String dbUser = "root";
	        String dbPassword = "Vasmaster@2022";

	        try {
	            connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);

	            String selectQuery = "SELECT phoneNum FROM service_numbers";
	            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);

	            ResultSet resultSet = preparedStatement.executeQuery();

	            while (resultSet.next()) {
	                String phoneNumber = resultSet.getString("phoneNum");
	                numbers.add(phoneNumber);
	            }

	            resultSet.close();
	            preparedStatement.close();
	        } catch (SQLException e) {
	            e.printStackTrace(); // Handle any potential exceptions here
	        } finally {
	            try {
	                if (connection != null) {
	                    connection.close();
	                }
	            } catch (SQLException ex) {
	                ex.printStackTrace(); // Handle any potential exceptions here
	            }
	        }

	        return numbers;
	    }

	
	    

}
