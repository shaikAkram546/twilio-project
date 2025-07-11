# Twilio Calling App - Spring Boot

This project demonstrates how to integrate Twilio's Programmable Voice API with a Spring Boot backend to make international phone calls and play pre-recorded audio messages to the receiver.

## Features

- Make automated phone calls to international numbers
- Play pre-recorded audio files (e.g., MP3 or WAV) during the call
- RESTful API to trigger and manage calls
- Configurable sender number, receiver number, and audio file URL
- Secure integration with Twilio credentials

## Technologies Used

- Java 17
- Spring Boot
- Twilio Java SDK
- Maven
- RESTful API

## How It Works

1. A client sends a request to the Spring Boot API with the destination phone number and the URL of the audio file.
2. The application uses Twilio’s API to initiate a call to the recipient.
3. Twilio connects the call and plays the specified audio message to the recipient.
4. The receiver hears the audio as part of the incoming phone call.

## Sample API Usage

**POST** `/api/call`

**Request Body**:
```json
{
  "to": "+14155552671",
  "audioUrl": "https://yourdomain.com/audio/announcement.mp3"
}
