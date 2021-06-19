package com.gox.basemodule.scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.Result;
import com.gox.basemodule.R;

import java.util.HashSet;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class SimpleScannerActivity extends Activity implements ZXingScannerView.ResultHandler, View.OnClickListener {

    private Toast toast;
    private ZXingScannerView mScannerView;
    private HashSet<String> hashSet = new HashSet<>();
    private Button btnSave;
    private Button btnCancel;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_simple_scanner);
        initView();
    }

    private void initView() {
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        LinearLayout linearLayout = findViewById(R.id.linear_layout);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        linearLayout.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();

        // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        String result = rawResult.getText() + " " + rawResult.getBarcodeFormat().toString();
        if (hashSet != null && !hashSet.contains(result)) {
            if (hashSet.size() > 5) {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(this, "Exceed the max limit 5", Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent();
                Log.e("stringggggg1", hashSet.toString());
                intent.putExtra("hashset", hashSet.toString());
                setResult(0, intent);
                finish();
            } else {
                hashSet.add(result);
                Log.e("scan","-----"+ rawResult.getText()); // Prints scan results
                Log.e("scan", "---"+rawResult.getBarcodeFormat().toString());// Prints the scan format (qrcode, pdf417 etc.)
                Intent intent = new Intent();
                intent.putExtra("QRjson", rawResult.getText());
                setResult(250, intent);
                finish();
            }
        }
        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        int i = v.getId();
        if (i == R.id.btn_save) {
            intent = new Intent();
            intent.putExtra("hashset", hashSet.toString());
            setResult(0, intent);
            finish();
        } else if (i == R.id.btn_cancel) {
            intent = new Intent();
            intent.putExtra("hashset", hashSet.toString());
            setResult(0, intent);
            finish();
        } else {
        }
    }
}