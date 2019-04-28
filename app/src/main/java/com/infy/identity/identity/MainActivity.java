package com.infy.identity.identity;

        import android.app.Activity;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.SharedPreferences;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.google.zxing.BarcodeFormat;
        import com.google.zxing.ResultPoint;
        import com.google.zxing.client.android.BeepManager;
        import com.journeyapps.barcodescanner.BarcodeCallback;
        import com.journeyapps.barcodescanner.BarcodeResult;
        import com.journeyapps.barcodescanner.DecoratedBarcodeView;
        import com.journeyapps.barcodescanner.DefaultDecoderFactory;

        import java.util.Arrays;
        import java.util.Collection;
        import java.util.List;

/**
 * This sample performs continuous scanning, displaying the barcode and source image whenever
 * a barcode is scanned.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;
    private String ip;
    private String employeeId;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            //hit the url from here
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.main_menu, menu);
        return true;
    }
    public void getSavedValues() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        if (sharedPref.contains(getString(R.string.empid))) {
            employeeId = sharedPref.getString(getResources().getString(R.string.empid),"");
        }
        else{
            employeeId="";
        }
        if (sharedPref.contains(getString(R.string.ip))){
        ip = sharedPref.getString(getResources().getString(R.string.ip),"");}
        else{
            ip=getString(R.string.blank_ip);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSavedValues();
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(employeeId);
        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);

        beepManager = new BeepManager(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ip:
                // User chose the "Settings" item, show the app settings UI...
                showAddItemDialog(MainActivity.this,getString(R.string.ip),getString(R.string.ip_sub),ip);
                return true;

            case R.id.empid:
                showAddItemDialog(MainActivity.this,getString(R.string.empid),getString(R.string.empid_sub),employeeId);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
    private void showAddItemDialog(Context c,String title,String subtitle,String text) {
        final EditText taskEditText = new EditText(c);
        final String key =title;
        taskEditText.setText(text);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle(title)
                .setMessage(subtitle)
                .setView(taskEditText)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        saveValues(key,task);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
    private void saveValues(String key,String value)
    {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
        getSavedValues();
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(employeeId);

    }
}
