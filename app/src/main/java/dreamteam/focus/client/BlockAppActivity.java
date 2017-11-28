package dreamteam.focus.client;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
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

        if(number.equals("1"))
        {
            TextView textTimes = (TextView) findViewById(R.id.textView11);
            textTimes.setText("time");
        }

    }

    //back button pressed functionality
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
            // go back to main screen
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(startMain);

        }
        return super.onKeyDown(keyCode, event);
    }
}
