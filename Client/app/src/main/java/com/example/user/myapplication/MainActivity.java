package com.example.user.myapplication;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Formatter;

public class MainActivity extends AppCompatActivity implements CallWebService.BitmapDisplay {

    EditText num_i;
    EditText num_j;
    EditText text_server_name;
    Button button;
    ImageView image_view;

    //String URL = "http://10.0.2.2:8080/CalculatorWS/CalculatorWS?WSDL";
    //String URL = "http://mkonvisar.ddns.net:8080/CalculatorWS/CalculatorWS?WSDL";
    //String URL = "http://109.241.140.113:8080/CalculatorWS/CalculatorWS?WSDL";
    String URL_FORMAT_STRING = "http://%s:8080/CalculatorWS/CalculatorWS?WSDL";
    String EMULATOR_SERVER_ADDRESS = "10.0.2.2";
    String REAL_SERVER_ADDRESS = "mkonvisar.ddns.net";

    String NAMESPACE = "http://dddd/";
    String SOAP_ACTION = "http://dddd/CalculatorWS/add";
    String METHOD_NAME = "add";
    String PARAMETER_NAME_I = "i";
    String PARAMETER_NAME_J = "j";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        num_i = (EditText) findViewById(R.id.num_i);
        num_j = (EditText)findViewById(R.id.num_j);
        text_server_name = (EditText)findViewById(R.id.text_server_name);
        button = (Button)findViewById(R.id.button);
        image_view = (ImageView) findViewById(R.id.image_view);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double i = Double.parseDouble(num_i.getText().toString());
                Double j = Double.parseDouble(num_j.getText().toString());

                getSoapService().execute(i,j);
            }
        });

        initServerAddress();
    }

    private CallWebService getSoapService(){
        CallWebService.Configuration result = new CallWebService.Configuration(NAMESPACE,
                METHOD_NAME,
                PARAMETER_NAME_I,
                PARAMETER_NAME_J,
                SOAP_ACTION,
                new Formatter().format(URL_FORMAT_STRING, text_server_name.getText().toString()).toString(),
                this);

        return new CallWebService(result);
    }

    private void initServerAddress() {
        if(isEmulator()){
            text_server_name.setText(EMULATOR_SERVER_ADDRESS);
        }
        else
        {
            text_server_name.setText(REAL_SERVER_ADDRESS);
        }
    }

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    @Override
    public void display(Bitmap bitmap) {
        this.image_view.setImageBitmap(bitmap);
    }
}
