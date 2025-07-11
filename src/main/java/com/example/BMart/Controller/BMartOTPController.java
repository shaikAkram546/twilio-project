package com.example.BMart.Controller;

import java.io.File;
import java.net.URI;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.BMart.Service.BMartOTPService;
import com.example.BMart.Service.BMartOTPServiceImpl;
import com.twilio.twiml.TwiMLException;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Play;
import com.twilio.twiml.voice.Say;
import com.twilio.twiml.voice.Say.Language;

import ch.qos.logback.core.status.Status;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/telepost")
@Slf4j
public class BMartOTPController {
	
	@Autowired
	BMartOTPService bMartService;
	
	@GetMapping("/test")
	public String Test() {
		System.out.println("I am from spring Controller")  ;
		return "Hi i am from springBoot Controller";
	}
	
//	@PostMapping("/processSMS")
//	public String processSMS(@RequestBody SMSSendRequest sendRequest) {
//		
//		log.info("processSMS started sendRequest: " + sendRequest.toString());
//		return bMartService.sendSMS(sendRequest.getDestinationSMSNumber(), sendRequest.getSmsMessage());
//	}
	@GetMapping("/processSMS/{name}/{phoneNum}")
	public String processSMS(@PathVariable String name,@PathVariable String phoneNum) throws Exception {
		
		log.info("processSMS started sendRequest: " + phoneNum);
		String response = bMartService.sendSMS(name,phoneNum);
		 JSONObject jsonResponse = new JSONObject();
		    jsonResponse.put("status",response); 
		
		return jsonResponse.toString();
	}
	
	@GetMapping("/otpValidation/{phoneNum}/{otp}")
  	public String otpValidation(@PathVariable String phoneNum, @PathVariable String otp) throws Exception {
  		String response= bMartService.otpVerification(phoneNum,otp);
  		JSONObject jsonResponse = new JSONObject();
	    jsonResponse.put("status",response); 
	
	return jsonResponse.toString();
  	}
	@PostMapping("/codeValidation/{phoneNum}/{enteredCode}")
  	public String codeValidation(@PathVariable String phoneNum,@PathVariable String enteredCode) throws Exception {
  		String response= bMartService.codeVerification(phoneNum,enteredCode);
  		JSONObject jsonResponse = new JSONObject();
	    jsonResponse.put("status",response); 
	
	return jsonResponse.toString();
  	}
	@PostMapping("/addphonenum/{phoneNum}")
	public String addCallingNum(@PathVariable String phoneNum) {
		String responseModel= bMartService.addCallingNum(phoneNum);
		return responseModel;
	}
	
	@PostMapping("/getcallResponse/{callSID}")
	public String getCallResponse(@PathVariable String callSID) {
		String responseModel= bMartService.getCallResponse(callSID);
		return responseModel;
	}
	
	@PostMapping("/callingtest/{callNum}/{phoneNum}/{voicenote}/{text}")
	public String callingApitest(@PathVariable String phoneNum, @PathVariable String callNum, @PathVariable String voicenote,
			@PathVariable String text) throws Exception {
		String responseModel= bMartService.CallingApiTest(phoneNum, callNum,voicenote,text);
  		return responseModel; 
  	}
	@PostMapping("/callingSingleUSNum/{voicenote}/{phoneNum}/{text}/{fileName}")
	public String callingApi(@PathVariable String voicenote, @PathVariable String phoneNum,
			@PathVariable String text,@PathVariable String fileName) throws Exception {
		String responseModel= bMartService.callingSingleUSNum(voicenote, phoneNum, text, fileName);
  		return responseModel; 
  	} 
	@PostMapping("/callingRanndomNum/{voicenote}/{phoneNum}/{text}/{fileName}")
	public String callingApi1(@PathVariable String voicenote, @PathVariable String phoneNum,
			@PathVariable String text,@PathVariable String fileName) throws Exception {
		String responseModel= bMartService.CallingApiRandomNum(voicenote, phoneNum, text, fileName);
  		return responseModel; 
  	}
//	@PostMapping("/callingOwnNumber/{voicenote}/{callNum}/{phoneNum}/{text}/{fileName}")
//	public String callingApiOwnNum1(@PathVariable String voicenote, @PathVariable String callNum, @PathVariable String phoneNum,
//			@PathVariable String text,@PathVariable String fileName) throws Exception {
//		String responseModel= bMartService.callingOwnNumber(voicenote, callNum, phoneNum, text, fileName);
//  		return responseModel; 
//  	}
	@PostMapping("/callingOwnNumber1122/{voicenote}/{callNum}/{phoneNum}/{text}/{fileName}")
	public String callingApiOwnNum(@PathVariable String voicenote, @PathVariable String callNum, @PathVariable String phoneNum,
			@PathVariable String text,@PathVariable String fileName) throws Exception {
		String responseModel= bMartService.callingOwnNumber(voicenote, callNum, phoneNum, text, fileName);
  		return responseModel; 
  	}
	@PostMapping("/textCallingSingleUSNum/{voicenote}/{phoneNum}/{text}")
	public String callingApiText(@PathVariable String voicenote, @PathVariable String phoneNum,
			@PathVariable String text) throws Exception {
		
		String responseModel= bMartService.textCallingSingleUSNum(voicenote, phoneNum, text);
  		return responseModel; 
  	}
	@PostMapping("/textCallingRanndomNum/{voicenote}/{phoneNum}/{text}")
	public String callingApi1Text(@PathVariable String voicenote, @PathVariable String phoneNum,
			@PathVariable String text) throws Exception {
		String responseModel= bMartService.textCallingApiRandomNum(voicenote, phoneNum, text);
  		return responseModel; 
  	}
	@PostMapping("/textCallingOwnNumber/{voicenote}/{callNum}/{phoneNum}/{text}")
	public String callingApiOwnNumText(@PathVariable String voicenote, @PathVariable String callNum, @PathVariable String phoneNum,
			@PathVariable String text) throws Exception {
		String responseModel= bMartService.textCallingOwnNumber(voicenote, callNum, phoneNum, text);
  		return responseModel; 
  	}
	
	@RequestMapping(value="/voicenoteWomen/{text}", method=RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<Object> getVoiceNoteWomen(@PathVariable String text) throws TwiMLException {
		System.out.println("notenote1111=======> "+text);
		Say say = new Say.Builder(text).voice(Say.Voice.WOMAN).language(Language.EN_IN).build();
		VoiceResponse response = new VoiceResponse.Builder().say(say).build();
		return new ResponseEntity<>(response.toXml(), HttpStatus.OK); 
	}
	@RequestMapping(value="/voicenoteMen/{text}", method=RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<Object> getVoiceNoteMen(@PathVariable String text) throws TwiMLException {
		System.out.println("notenote2222=======> "+text);
		Say say = new Say.Builder(text).voice(Say.Voice.MAN).language(Language.EN_IN).build();
		VoiceResponse response = new VoiceResponse.Builder().say(say).build();
		return new ResponseEntity<>(response.toXml(), HttpStatus.OK); 
	}
	@RequestMapping(value="/voicenotegoogle/{text}", method=RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<Object> getVoiceNotegoogle(@PathVariable String text) throws TwiMLException {
		System.out.println("notenote3333=======> "+text);
		Say say = new Say.Builder(text).voice(Say.Voice.GOOGLE_EN_IN_STANDARD_B).language(Language.EN_IN).build();
		VoiceResponse response = new VoiceResponse.Builder().say(say).build();
		return new ResponseEntity<>(response.toXml(), HttpStatus.OK); 
	}
	
	@RequestMapping(value = "/voicenoteurl/{fileName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<Object> getVoiceNote3(@PathVariable String fileName) throws TwiMLException {
		Play play = new Play.Builder("https://zenkaratesting.s3.ap-south-1.amazonaws.com/"+fileName)
				.build();
		
		VoiceResponse response = new VoiceResponse.Builder().play(play).build();

		return new ResponseEntity<>(response.toXml(), HttpStatus.OK);
	}
	@RequestMapping(value = "/kanisuno/{fileName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<Object> getkhanisuno(@PathVariable String fileName) throws TwiMLException {
		Play play = new Play.Builder("https://soundcloud.com/zahid-ali-khan/kaifi-khalil-kahani-suno-2-0?utm_source=clipboard&utm_medium=text&utm_campaign=social_sharing")
				.build();
		
		VoiceResponse response = new VoiceResponse.Builder().play(play).build();

		return new ResponseEntity<>(response.toXml(), HttpStatus.OK);
	}
	
	
	@PostMapping("/whatupmessage/{phoneNum}")
	public String whatsAppMessage(@PathVariable String phoneNum) throws Exception {
		String responseModel= bMartService.whatsAppMessage(phoneNum);
  		return responseModel; 
  	}
	
	@GetMapping("/matter/{matter}")
	public String matter(@PathVariable String matter) {
		
		return matter;

	}

}
