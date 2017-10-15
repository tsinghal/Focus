package dreamteam.focus.client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import java.sql.Time;

import dreamteam.focus.R;

public class timePicker extends AppCompatActivity {

    TimePicker timePicker;
    Button buttonSetTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);
        timePicker=(TimePicker)findViewById(R.id.timePicker);
        buttonSetTime=(Button)findViewById(R.id.buttonSetTime);

        buttonSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
