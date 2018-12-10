package com.example.user.myapplication;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

class CallWebService extends AsyncTask<Double, Void, ViewPortInfo> {

    interface BitmapDisplay{
        void display(ViewPortInfo viewPortInfo);
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
    protected void onPostExecute(ViewPortInfo bitmap) {
        configuration.bitmap_display.display(bitmap);
    }

    @Override
    protected ViewPortInfo doInBackground(Double... params) {
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

        ViewPortInfo viewPortInfo = null;

        try {
            httpTransportSE.call(configuration.soap_action, envelope);
            SoapObject responseObject = (SoapObject)envelope.getResponse();
            viewPortInfo = new ViewPortInfo(responseObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] bytearray = Base64.decode(viewPortInfo.ImageData);

        viewPortInfo.ReceivedImage = BitmapFactory.decodeByteArray(bytearray, 0, bytearray.length);

        return viewPortInfo;
    }
}
