package com.kivicube.webar.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText text = findViewById(R.id.url);
        text.setText("https://www.kivicube.com/scenes/KnUpLGBbpOz4qmS3GgKYaX8A7njLesn6");

        Button start = findViewById(R.id.start);
        start.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WebARSampleActivity.class);
                intent.putExtra("url", text.getText().toString());
                startActivity(intent);
            }
        });
    }
}
