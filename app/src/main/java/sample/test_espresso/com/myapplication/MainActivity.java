package sample.test_espresso.com.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.microsoft.azure.mobile.crashes.CrashesListener;
import com.microsoft.azure.mobile.crashes.ingestion.models.ErrorAttachmentLog;
import com.microsoft.azure.mobile.crashes.model.ErrorReport;
import com.microsoft.azure.mobile.distribute.Distribute;
import com.microsoft.azure.mobile.push.Push;
import android.util.Log;


import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Push.setListener(new MyPushListener());
        Distribute.setListener(new MyDistributeListener());

       // MobileCenter.setLogUrl("https://in-staging-south-centralus.staging.avalanch.es");
        MobileCenter.start(getApplication(), "f6b74362-a6fd-4eda-952d-6f50da3086b3",
                Analytics.class, Crashes.class, Push.class, Distribute.class);
        Analytics.trackEvent("Change Text");
        super.onCreate(savedInstanceState);
        //Log.e("installID","" + MobileCenter.getInstallId().get());
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.inputField);

        Crashes.getLastSessionCrashReport();
        CrashesListener customListener = new CrashesListener() {
            @Override
            public boolean shouldProcess(ErrorReport report) {
                return false;
            }

            @Override
            public boolean shouldAwaitUserConfirmation() {
                return false;
            }

            @Override
            public Iterable<ErrorAttachmentLog> getErrorAttachments(ErrorReport report) {
                    /* Attach some text. */
                ErrorAttachmentLog textLog = ErrorAttachmentLog.attachmentWithText("This is a text attachment.", "text.txt");

                Context context=getApplicationContext();

    /* Attach app icon. */
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitMapData = stream.toByteArray();
                ErrorAttachmentLog binaryLog = ErrorAttachmentLog.attachmentWithBinary(bitMapData, "ic_launcher.jpeg", "image/jpeg");

    /* Return attachments as list. */
                return Arrays.asList(textLog, binaryLog);

            }
            @Override
            public void onBeforeSending(ErrorReport report) {

            }

            @Override
            public void onSendingFailed(ErrorReport report, Exception e) {

            }

            @Override
            public void onSendingSucceeded(ErrorReport report) {

            }
            // Implement all callbacks here.
        };
        Crashes.setListener(customListener);
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
