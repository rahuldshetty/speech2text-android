package com.rahuldshetty.speech2text;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

public class SpeechRec {

    private SpeechRecognizer speechRecognizer;
    private Context context;
    private ArrayList<String> matches;
    private Intent mSpeechRecognizerIntent;

    public SpeechRec(Context context){
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        this.context = context;

        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {

                ArrayList<String> main_matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                matches = main_matches;

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

    }

    void startListening(){
        speechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    void stopListening(){
        speechRecognizer.stopListening();
    }

    ArrayList<String> getMatches(){
        return matches;
    }

}
