package com.example.user.myapplication;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

class CallWebService extends AsyncTask<CallWebService.RequestParameters, Void, ViewPortInfo> {

    interface BitmapDisplay{
        void display(ViewPortInfo viewPortInfo);
    }

    static class RequestParameters{
        Double i;
        Double j;
        Integer zoom_level;
        Integer image_width;
        Integer image_height;
    }

    static class Configuration{
        private String namespace;
        private String server_address;
        private String method_name;
        private String parameter_i;
        private String parameter_j;
        private String parameter_zoom_level;
        private String parameter_image_width;
        private String parameter_image_height;
        private String soap_action;
        private BitmapDisplay bitmap_display;

        Configuration(String namespace,
                      String method_name,
                      String parameter_i,
                      String parameter_j,
                      String parameter_image_width,
                      String soap_action,
                      String server_address,
                      String parameter_zoom_level,
                      String parameter_image_height,
                      BitmapDisplay bitmap_display) {
            this.namespace = namespace;
            this.parameter_image_width = parameter_image_width;
            this.server_address = server_address;
            this.method_name = method_name;
            this.parameter_i = parameter_i;
            this.parameter_j = parameter_j;
            this.soap_action = soap_action;
            this.parameter_zoom_level = parameter_zoom_level;
            this.parameter_image_height = parameter_image_height;
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
    protected ViewPortInfo doInBackground(RequestParameters... params) {
        RequestParameters parameters = params[0];

        SoapObject soapObject = new SoapObject(configuration.namespace, configuration.method_name);

        populateArguments(parameters, soapObject);

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

    private void populateArguments(RequestParameters parameters, SoapObject soapObject) {
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName(configuration.parameter_i);
        propertyInfo.setValue(parameters.i);
        propertyInfo.setType(parameters.i.getClass());
        soapObject.addProperty(propertyInfo);

        propertyInfo = new PropertyInfo();
        propertyInfo.setName(configuration.parameter_j);
        propertyInfo.setValue(parameters.j);
        propertyInfo.setType(parameters.j.getClass());
        soapObject.addProperty(propertyInfo);

        propertyInfo = new PropertyInfo();
        propertyInfo.setName(configuration.parameter_zoom_level);
        propertyInfo.setValue(parameters.zoom_level);
        propertyInfo.setType(parameters.zoom_level.getClass());
        soapObject.addProperty(propertyInfo);

        propertyInfo = new PropertyInfo();
        propertyInfo.setName(configuration.parameter_image_width);
        propertyInfo.setValue(parameters.image_width);
        propertyInfo.setType(parameters.image_width.getClass());
        soapObject.addProperty(propertyInfo);

        propertyInfo = new PropertyInfo();
        propertyInfo.setName(configuration.parameter_image_height);
        propertyInfo.setValue(parameters.image_height);
        propertyInfo.setType(parameters.image_height.getClass());
        soapObject.addProperty(propertyInfo);
    }
}
