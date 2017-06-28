package sample.test_espresso.com.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.microsoft.azure.mobile.MobileCenter;
import com.microsoft.azure.mobile.analytics.Analytics;
import com.microsoft.azure.mobile.crashes.Crashes;
import com.microsoft.azure.mobile.push.Push;


import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Push.setListener(new MyPushListener());
<<<<<<< HEAD
<<<<<<< HEAD
        UUID installId = MobileCenter.getInstallId();
=======
>>>>>>> 0b88b39d846ed5c764aa196f107cdb40e958a34c
=======
>>>>>>> 0b88b39d846ed5c764aa196f107cdb40e958a34c
        MobileCenter.start(getApplication(), "60795832-9269-49de-90ba-421a56e46ea3",
                Analytics.class, Crashes.class, Push.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.inputField);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.changeText:
                editText.setText("Lalala");
                break;
            case R.id.switchActivity:
                Intent intent = new Intent(this, SecondActivity.class);
                intent.putExtra("input", editText.getText().toString());
                startActivity(intent);
                break;
        }

    }
}
