package com.example.BMart.Controller;

import java.io.IOException;
import java.util.Locale;

import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;

public class VoiceAssistant {
	
	
@SuppressWarnings("deprecation")
public static void main(String[] st) {
		
		Configuration config = new Configuration();
		
		config.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		config.setDictionaryPath("src\\main\\resources\\8768.dic");
		config.setLanguageModelPath("src\\main\\resources\\8768.lm");
		
		try {
			LiveSpeechRecognizer speech = new LiveSpeechRecognizer(config);
			speech.startRecognition(true);
			
			SpeechResult speechResult = null;
			
			while ((speechResult = speech.getResult()) != null) {
				String voiceCommand = speechResult.getHypothesis();
				System.out.println("Voice Command is " + voiceCommand);
				
				if (voiceCommand.equalsIgnoreCase("HEY BUJJI OPEN GOOGLE")) {
					speak("Hi bharath opening google"); 
					Runtime.getRuntime().exec("cmd.exe /c start chrome https://grapigo.com/");
				} else if (voiceCommand.equalsIgnoreCase("HEY BUJJI OPEN YOUTUBE")) {
					speak("Hi bharath closing google"); 
					Runtime.getRuntime().exec("cmd.exe /c TASKKILL /IM chrome.exe");
				}
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


private static void speak(String text) {
    try {
        System.setProperty("freetts.voices",
                "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");

        Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");

        Synthesizer synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(Locale.of("en", "IN")));

        synthesizer.allocate();
        synthesizer.resume();

        SynthesizerModeDesc desc = (SynthesizerModeDesc) synthesizer.getEngineModeDesc();
        Voice[] voices = desc.getVoices();
        Voice selectedVoice = null;

        for (Voice voice : voices) {
            if (voice.getName().equals("kevin16")) {
                selectedVoice = voice;
                break;
            }
        }

        if (selectedVoice != null) {
            synthesizer.getSynthesizerProperties().setVoice(selectedVoice);
        }

        synthesizer.speakPlainText(text, null);
        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);

        synthesizer.deallocate();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}
