package com.example.user.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Formatter;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    EditText num_i;
    EditText num_j;
    EditText text_server_name;
    Button button;
    TextView text_result;
    ImageView image_view;

    //String URL = "http://10.0.2.2:8080/CalculatorWS/CalculatorWS?WSDL";
    //String URL = "http://mkonvisar.ddns.net:8080/CalculatorWS/CalculatorWS?WSDL";
    //String URL = "http://109.241.140.113:8080/CalculatorWS/CalculatorWS?WSDL";
    String URL = "http://%s:8080/CalculatorWS/CalculatorWS?WSDL";
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
        text_result = (TextView)findViewById(R.id.text_result);
        image_view = (ImageView) findViewById(R.id.image_view);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double i = Double.parseDouble(num_i.getText().toString());
                Double j = Double.parseDouble(num_j.getText().toString());

                String server_name = new Formatter().format(URL, text_server_name.getText().toString()).toString();

                Log.i("server_name", server_name);

                new CallWebService(server_name).execute(i, j);
            }
        });
    }

    class CallWebService extends AsyncTask<Double, Void, Bitmap> {

        String server_name;

        public  CallWebService(String server_name){
            this.server_name = server_name;
        }

        @Override
        protected void onPostExecute(Bitmap s) {

            image_view.setImageBitmap(s);
            //text_result.setText("Square = " + s);
        }

        @Override
        protected Bitmap doInBackground(Double... params) {
            String result = "";

            SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo propertyInfoI = new PropertyInfo();
            propertyInfoI.setName(PARAMETER_NAME_I);
            propertyInfoI.setValue(params[0]);
            propertyInfoI.setType(Double.class);

            soapObject.addProperty(propertyInfoI);

            PropertyInfo propertyInfoJ = new PropertyInfo();
            propertyInfoJ.setName(PARAMETER_NAME_J);
            propertyInfoJ.setValue(params[1]);
            propertyInfoJ.setType(Double.class);

            soapObject.addProperty(propertyInfoJ);

            SoapSerializationEnvelope envelope =  new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(soapObject);

            MarshalDouble md = new MarshalDouble();
            md.register(envelope);

            HttpTransportSE httpTransportSE = new HttpTransportSE(this.server_name);

            try {
                httpTransportSE.call(SOAP_ACTION, envelope);
                SoapPrimitive soapPrimitive = (SoapPrimitive)envelope.getResponse();
                result = soapPrimitive.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] bytearray = Base64.decode(result);

            Bitmap bm = BitmapFactory.decodeByteArray(bytearray, 0, bytearray.length);

            return bm;
        }
    }
}
