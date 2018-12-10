package com.example.user.myapplication;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.user.myapplication.CallWebService.Configuration;

import java.util.Formatter;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class MainActivity extends AppCompatActivity implements CallWebService.BitmapDisplay {

    EditText text_server_name;
    Button zoom_in;
    Button zoom_out;
    ImageView image_view;

    //String URL = "http://109.241.140.113:8080/CalculatorWS/CalculatorWS?WSDL";
    String URL_FORMAT_STRING = "http://%s:8080/CalculatorWS/CalculatorWS?WSDL";
    String EMULATOR_SERVER_ADDRESS = "10.0.2.2";
    String REAL_SERVER_ADDRESS = "mkonvisar.ddns.net";

    String NAMESPACE = "http://dddd/";
    String SOAP_ACTION = "http://dddd/CalculatorWS/add";
    String METHOD_NAME = "add";
    String PARAMETER_NAME_I = "i";
    String PARAMETER_NAME_J = "j";
    String PARAMETER_IMAGE_WIDTH = "image_width";
    String PARAMETER_IMAGE_HEIGHT = "image_height";
    String PARAMETER_ZOOM_LEVEL = "zoom_level";

    Double topX = 53.132401d;
    Double topY = 17.983770d;
    Integer zoomLevel = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_server_name = (EditText)findViewById(R.id.text_server_name);
        zoom_in = (Button)findViewById(R.id.zoom_in);
        zoom_out = (Button)findViewById(R.id.zoom_out);
        image_view = (ImageView) findViewById(R.id.image_view);

        initializeSwipeOperations();

        initializeButtons();

        initServerAddress();

        delayInitialLoad();
    }

    private void delayInitialLoad() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadImage();
            }
        }, 1000);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeSwipeOperations() {
        image_view.setOnTouchListener(new View.OnTouchListener() {
            Float moveStartedX = 0f;
            Float moveStartedY = 0f;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case ACTION_DOWN:
                        moveStartedX = event.getX();
                        moveStartedY = event.getY();
                        break;
                    case ACTION_UP:

                        float dX = moveStartedX - event.getX();
                        float dY = moveStartedY - event.getY();

                        moveViewPort(dX, dY);
                        break;
                }

                return true;
            }
        });
    }

    private void initializeButtons() {
        zoom_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomLevel++;

                loadImage();
            }
        });

        zoom_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomLevel--;

                loadImage();
            }
        });
    }

    private void moveViewPort(float dX, float dY){
        this.topX += dX;
        this.topY += dY;

        loadImage();
    }

    private void loadImage(){

        CallWebService.RequestParameters parameters = new CallWebService.RequestParameters();
        parameters.i = this.topX;
        parameters.j = this.topY;
        parameters.zoom_level = zoomLevel;
        parameters.image_width = image_view.getMeasuredWidth();
        parameters.image_height = image_view.getMeasuredHeight();

        getSoapService().execute(parameters);
    }

    private CallWebService getSoapService(){
        Configuration result = new Configuration(NAMESPACE,
                METHOD_NAME,
                PARAMETER_NAME_I,
                PARAMETER_NAME_J,
                PARAMETER_IMAGE_WIDTH,
                SOAP_ACTION,
                new Formatter().format(URL_FORMAT_STRING, text_server_name.getText().toString()).toString(),
                PARAMETER_ZOOM_LEVEL,
                PARAMETER_IMAGE_HEIGHT, this);

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
    public void display(ViewPortInfo viewPortInfo) {
        this.image_view.setImageBitmap(viewPortInfo.ReceivedImage);
        this.topX = viewPortInfo.TopX;
        this.topY = viewPortInfo.TopY;
        this.zoomLevel = viewPortInfo.ZoomLevel;
    }
}

