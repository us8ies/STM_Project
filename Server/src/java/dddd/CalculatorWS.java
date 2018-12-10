/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dddd;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author User
 */
@WebService(serviceName = "CalculatorWS")
@Stateless()
public class CalculatorWS {

    //@XmlAccessorType(XmlAccessType.FIELD)
    //@XmlType(name = "ViewPortInfo", namespace="http://beans.book.acme.com/")
    @XmlAccessorType(XmlAccessType.FIELD)
    //@XmlType(name = "ViewPortInfo")
    static public class ViewPortInfo{
        protected String ImageData;
        protected Double TopX;
        protected Double TopY;
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "add")
    public ViewPortInfo add(@WebParam(name = "i") double i, @WebParam(name = "j") double j) {
        ViewPortInfo result = new ViewPortInfo();
        
        //String result = "";
        
        ClassLoader classLoader = getClass().getClassLoader();
	File file = new File(classLoader.getResource("Capture.PNG").getFile());
       
        try {
            BufferedImage image = ImageIO.read(file);
            
            int x = (int)Math.round(i);
            int y = (int)Math.round(j);
            int width = 500;
            int height = 500;
            
            if(x < 0)
            {
                x=0;
            }
            
            if(x + width> image.getWidth())
            {
                x = image.getWidth() - width;
            }
            
            if(y < 0)
            {
                y = 0;
            }
            
            if(y + height > image.getHeight())
            {
                y = image.getHeight() - height;
            }
            
            image = image.getSubimage(x, y, width, height);
            
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            baos.flush();

            String base64String=Base64.encode(baos.toByteArray());
            baos.close();
            
            result.ImageData = base64String;
            result.TopX = (double)x;
            result.TopY = (double)y;
            
            
            Logger.getLogger(CalculatorWS.class.getName()).log(Level.INFO, null, image.getHeight());   
        } catch (IOException ex) {
            Logger.getLogger(CalculatorWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
}
