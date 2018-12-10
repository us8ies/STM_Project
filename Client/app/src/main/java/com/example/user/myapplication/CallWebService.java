package com.example.user.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

class CallWebService extends AsyncTask<Double, Void, Bitmap> {

    interface BitmapDisplay{
        void display(Bitmap bitmap);
    }

    static class Configuration{
        private String namespace;
        private String server_address;
        private String method_name;
        private String parameter_i;
        private String parameter_j;
        private String soap_action;
        private BitmapDisplay bitmap_display;

        Configuration(String namespace,
                      String method_name,
                      String parameter_i,
                      String parameter_j,
                      String soap_action,
                      String server_address,
                      BitmapDisplay bitmap_display) {
            this.namespace = namespace;
            this.server_address = server_address;
            this.method_name = method_name;
            this.parameter_i = parameter_i;
            this.parameter_j = parameter_j;
            this.soap_action = soap_action;
            this.bitmap_display = bitmap_display;
        }
    }

    private Configuration configuration;

    CallWebService(Configuration configuration){
        this.configuration = configuration;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        configuration.bitmap_display.display(bitmap);
    }

    @Override
    protected Bitmap doInBackground(Double... params) {
        String result = "";

        SoapObject soapObject = new SoapObject(configuration.namespace, configuration.method_name);

        PropertyInfo propertyInfoI = new PropertyInfo();
        propertyInfoI.setName(configuration.parameter_i);
        propertyInfoI.setValue(params[0]);
        propertyInfoI.setType(Double.class);

        soapObject.addProperty(propertyInfoI);

        PropertyInfo propertyInfoJ = new PropertyInfo();
        propertyInfoJ.setName(configuration.parameter_j);
        propertyInfoJ.setValue(params[1]);
        propertyInfoJ.setType(Double.class);

        soapObject.addProperty(propertyInfoJ);

        SoapSerializationEnvelope envelope =  new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(soapObject);

        MarshalDouble md = new MarshalDouble();
        md.register(envelope);

        HttpTransportSE httpTransportSE = new HttpTransportSE(configuration.server_address);

        try {
            httpTransportSE.call(configuration.soap_action, envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive)envelope.getResponse();
            result = soapPrimitive.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] bytearray = Base64.decode(result);

        return BitmapFactory.decodeByteArray(bytearray, 0, bytearray.length);
    }
}
