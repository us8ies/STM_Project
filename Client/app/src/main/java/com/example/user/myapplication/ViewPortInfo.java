package com.example.user.myapplication;

import android.graphics.Bitmap;

import org.ksoap2.serialization.SoapObject;

class ViewPortInfo{
    protected String ImageData;
    protected Double TopX;
    protected Double TopY;
    protected Integer ZoomLevel;
    protected Bitmap ReceivedImage;

    ViewPortInfo(SoapObject soapObject){
        this.ImageData = soapObject.getPropertyAsString("ImageData");
        this.TopX = Double.parseDouble(soapObject.getPropertyAsString("TopX"));
        this.TopY = Double.parseDouble(soapObject.getPropertyAsString("TopY"));
        this.ZoomLevel = Integer.parseInt(soapObject.getPropertyAsString("ZoomLevel"));
    }
}
