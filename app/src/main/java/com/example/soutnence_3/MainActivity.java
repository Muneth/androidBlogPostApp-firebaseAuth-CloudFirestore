package com.example.soutnence_3;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

// Widgets
    Button suivant;
    TextView t1,t2;
    Animation animate_btn,animate_txt, animate_txt2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        suivant = findViewById(R.id.suivant);
        t1  = findViewById(R.id.logintextview);
        t2  = findViewById(R.id.registertextview);

        // Load the animation from the xml file
        animate_btn = AnimationUtils.loadAnimation(this, R.anim.animate_btn);
        animate_txt = AnimationUtils.loadAnimation(this, R.anim.animate_texts);
        animate_txt2 = AnimationUtils.loadAnimation(this, R.anim.animate_texts2);

        // Set the animation to the widgets
        suivant.setAnimation(animate_btn);
        t1.setAnimation(animate_txt);
        t2.setAnimation(animate_txt2);

        suivant.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
        });
    }
}