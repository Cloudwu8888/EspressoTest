package sample.test_espresso.com.myapplication;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AlertDialog;
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
import android.support.test.espresso.idling.CountingIdlingResource;

import com.microsoft.azure.mobile.crashes.CrashesListener;
import com.microsoft.azure.mobile.crashes.ingestion.models.ErrorAttachmentLog;
import com.microsoft.azure.mobile.crashes.model.ErrorReport;
import com.microsoft.azure.mobile.distribute.Distribute;
import com.microsoft.azure.mobile.push.Push;
import com.microsoft.azure.mobile.utils.async.MobileCenterConsumer;

import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import static com.microsoft.azure.mobile.crashes.Crashes.LOG_TAG;


public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = "MobileCenterSasquatch";

    @VisibleForTesting

    static final CountingIdlingResource crashesIdlingResource = new CountingIdlingResource("crashes");
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Push.setListener(new MyPushListener());
        Distribute.setListener(new MyDistributeListener());

        CrashesListener customListener = new CrashesListener() {
            @Override
            public boolean shouldProcess(ErrorReport report) {
                return true;
            }

            @Override
            public boolean shouldAwaitUserConfirmation() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder
                        .setTitle(R.string.crash_confirmation_dialog_title)
                        .setMessage(R.string.crash_confirmation_dialog_message)
                        .setPositiveButton(R.string.crash_confirmation_dialog_send_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Crashes.notifyUserConfirmation(Crashes.SEND);
                                Analytics.trackEvent("onClick");
                            }
                        })
                        .setNegativeButton(R.string.crash_confirmation_dialog_not_send_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Crashes.notifyUserConfirmation(Crashes.DONT_SEND);
                            }
                        })
                        .setNeutralButton(R.string.crash_confirmation_dialog_always_send_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Crashes.notifyUserConfirmation(Crashes.ALWAYS_SEND);
                            }
                        });
                builder.create().show();
                return true;
            }

            @Override
            public Iterable<ErrorAttachmentLog> getErrorAttachments(ErrorReport report) {
                /* Attach some text. */
                ErrorAttachmentLog textLog = ErrorAttachmentLog.attachmentWithText("This is a text attachment.", "text.txt");

                 /* Attach app icon. */
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitMapData = stream.toByteArray();
                ErrorAttachmentLog binaryLog = ErrorAttachmentLog.attachmentWithBinary(bitMapData, "ic_launcher.jpeg", "image/jpeg");

                /* Return attachments as list. */
                return Arrays.asList(textLog, binaryLog);
            }

            @Override
            public void onBeforeSending(ErrorReport report) {
                Toast.makeText(MainActivity.this, R.string.crash_before_sending, Toast.LENGTH_SHORT).show();
                crashesIdlingResource.increment();
            }

            @Override
            public void onSendingFailed(ErrorReport report, Exception e) {
                Toast.makeText(MainActivity.this, R.string.crash_sent_failed, Toast.LENGTH_SHORT).show();
                crashesIdlingResource.decrement();
            }

            @Override
            public void onSendingSucceeded(ErrorReport report) {
                String message = String.format("%s\nCrash ID: %s", getString(R.string.crash_sent_succeeded), report.getId());
                if (report.getThrowable() != null){
                    message += String.format("\nThrowable: %s", report.getThrowable().toString());
                }
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                crashesIdlingResource.decrement();
            }
            // Implement all callbacks here.
        };
        Crashes.setListener(customListener);
            /* Print last crash. */
        Crashes.hasCrashedInLastSession().thenAccept(new MobileCenterConsumer<Boolean>() {

            @Override
            public void accept(Boolean crashed) {
                Log.i(LOG_TAG, "Crashes.hasCrashedInLastSession=" + crashed);
            }
        });
        Crashes.getLastSessionCrashReport().thenAccept(new MobileCenterConsumer<ErrorReport>() {

            @Override
            public void accept(ErrorReport data) {
                if (data != null) {
                    Log.i(LOG_TAG, "Crashes.getLastSessionCrashReport().getThrowable()=", data.getThrowable());
                }
            }
        });

        Crashes.hasCrashedInLastSession();
        Crashes.getLastSessionCrashReport();
        // Depending on the user's choice, call Crashes.notifyUserConfirmation() with the right value.
        Crashes.notifyUserConfirmation(Crashes.DONT_SEND);
        Crashes.notifyUserConfirmation(Crashes.SEND);
        Crashes.notifyUserConfirmation(Crashes.ALWAYS_SEND);

        // MobileCenter.setLogUrl("https://in-staging-south-centralus.staging.avalanch.es");
        MobileCenter.start(getApplication(), "f6b74362-a6fd-4eda-952d-6f50da3086b3",
                Analytics.class, Crashes.class, Push.class, Distribute.class);

        super.onCreate(savedInstanceState);
        Log.e("installID","" + MobileCenter.getInstallId().get());
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.inputField);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.changeText:
                editText.setText("Lalala");
                Analytics.trackEvent("Click New button");
                break;
            case R.id.switchActivity:
                Intent intent = new Intent(this, SecondActivity.class);
                intent.putExtra("input", editText.getText().toString());
                startActivity(intent);
                break;
        }

    }
}
