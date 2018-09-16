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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

import static android.view.View.GONE;

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
                selectedPhrases.add(phrase);
                addPhraseToPhraseBox(phrase);
                phraseTextBox.setText("");
                Log.d("[Dev] HomeActivity", "Added a new phrase: \"" + phrase + "\"");
                Log.d("[Dev] HomeActivity", "The list of active phrases is now: " + selectedPhrases.toString());
            }
        });
        // TODO: Add enter key listener to EditText
    }

    private void addPhraseToPhraseBox(final String phrase) {
        LinearLayout phraseRoot = new LinearLayout(getApplicationContext());
        phraseRoot.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView phraseText = new TextView(getApplicationContext());
        phraseText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        phraseText.setText(phrase);
        phraseText.setTextSize(18);
        phraseRoot.addView(phraseText);

        ImageButton phraseDeleteImage = new ImageButton(getApplicationContext());
        phraseDeleteImage.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        phraseDeleteImage.setOnClickListener(new View.OnClickListener() {

            private LinearLayout phraseRoot;

            private View.OnClickListener init(LinearLayout phraseRoot) {
                this.phraseRoot = phraseRoot;
                return this;
            }

            @Override
            public void onClick(View view) {
                phraseRoot.setVisibility(View.GONE);
                selectedPhrases.remove(phrase);
                Log.d("[Dev] HomeActivity", "Removed a phrase: \"" + phrase + "\"");
                Log.d("[Dev] HomeActivity", "The list of active phrases is now: " + selectedPhrases.toString());
            }
        }.init(phraseRoot));
        phraseRoot.addView(phraseDeleteImage);

        phraseContainerLayout.addView(phraseRoot);
    }
}
