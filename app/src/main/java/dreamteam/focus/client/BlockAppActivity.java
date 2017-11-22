package dreamteam.focus.client;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import dreamteam.focus.R;

public class BlockAppActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_app);

        Intent intent = getIntent();
        String app = intent.getStringExtra("app");
        String number = intent.getStringExtra("number");

        TextView appName = (TextView) findViewById(R.id.appNameLabel);
        appName.setText(app);

        TextView numLabel = (TextView) findViewById(R.id.numberLabel);
        numLabel.setText(number);

    }
}
