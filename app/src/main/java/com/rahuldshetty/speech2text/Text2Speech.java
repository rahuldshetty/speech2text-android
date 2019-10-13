package com.rahuldshetty.speech2text;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class Text2Speech {

    private TextToSpeech tts;

    public Text2Speech(Context context){
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=TextToSpeech.ERROR)
                {
                    tts.setLanguage(Locale.UK);
                    tts.setSpeechRate(0.9f);
                }
            }
        });
    }

    void speak(String s){
        if(!tts.isSpeaking())
        {
            tts.speak(s,TextToSpeech.QUEUE_FLUSH,null);
        }
    }


    void pause(){
        if(tts.isSpeaking())
        {
            tts.stop();
        }
    }

    void release(){
        if(tts.isSpeaking())
            tts.stop();
        tts.shutdown();
    }

}
