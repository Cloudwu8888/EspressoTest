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
import com.microsoft.azure.mobile.distribute.Distribute;
import com.microsoft.azure.mobile.push.Push;
import android.util.Log;


import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Push.setListener(new MyPushListener());
        Distribute.setListener(new MyDistributeListener());

        MobileCenter.setLogUrl("https://in-staging-south-centralus.staging.avalanch.es");
        MobileCenter.start(getApplication(), "d636b457-b9f7-487f-93aa-2e66fdc72eed",
                Analytics.class, Crashes.class, Push.class, Distribute.class);
        Analytics.trackEvent("Change Text");
        super.onCreate(savedInstanceState);
        //Log.e("installID","" + MobileCenter.getInstallId().get());
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.inputField);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.changeText:
                editText.setText("Chang Test Click");
                break;
            case R.id.switchActivity:
                Intent intent = new Intent(this, SecondActivity.class);
                intent.putExtra("input", editText.getText().toString());
                startActivity(intent);
                break;
        }

    }
}
