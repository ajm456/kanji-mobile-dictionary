package com.mobile.andrew.dissertationtest;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity
{
    private EditText phraseTextBox;
    private Button submitPhrase;
    private FlexboxLayout phraseContainerLayout;

    private ArrayList<String> selectedPhrases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phraseTextBox = findViewById(R.id.et_phrase_enter_text);
        submitPhrase = findViewById(R.id.btn_submit_phrase);
        phraseContainerLayout = findViewById(R.id.fb_phrases_container);
        selectedPhrases = new ArrayList<>();

        submitPhrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phrase = phraseTextBox.getText().toString();
                addPhraseToPhraseBox(phrase);
            }
        });
        // TODO: Add enter key listener to EditText
    }

    private void addPhraseToPhraseBox(String phrase) {
        selectedPhrases.add(phrase);
        LinearLayout phraseRoot = new LinearLayout(getApplicationContext());
        phraseRoot.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView phraseText = new TextView(getApplicationContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        phraseText.setLayoutParams(layoutParams);
        phraseText.setText(phrase);
        phraseRoot.addView(phraseText);

        ImageView phraseDeleteImage = new ImageView(getApplicationContext());
        phraseDeleteImage.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        phraseRoot.addView(phraseDeleteImage);

        phraseContainerLayout.addView(phraseRoot);
    }
}
