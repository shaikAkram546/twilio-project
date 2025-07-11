package com.example.BMart.Service;

public interface BMartOTPService {

	
	
	public String addCallingNum(String phoneNum);
	public String getCallResponse(String callSID);
	public String codeVerification(String phoneNum, String enteredCode);
	public String sendSMS(String name,String phoneNum) throws Exception;
	public String otpVerification(String phoneNum, String otp) throws Exception;
	public String CallingApiTest(String callNum, String phoneNum, String voicenote, String text) throws Exception;
	public String callingSingleUSNum(String voicenote,String phoneNum, String text, String fileName) throws Exception;
	public String CallingApiRandomNum(String voicenote,String phoneNum, String text, String fileName) throws Exception;
	public String callingOwnNumber(String voicenote, String callNum, String phoneNum, String text, String fileName) throws Exception;
	
	public String textCallingSingleUSNum(String voicenote,String phoneNum, String text) throws Exception;
	public String textCallingApiRandomNum(String voicenote,String phoneNum, String text) throws Exception;
	public String textCallingOwnNumber(String voicenote, String callNum, String phoneNum, String text) throws Exception;
	
	
	
	public String whatsAppMessage(String phoneNum);
	
}
