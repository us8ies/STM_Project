/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dddd;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.awt.Image;
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
        protected int ZoomLevel;
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "add")
    public ViewPortInfo add(@WebParam(name = "i") double i,
            @WebParam(name = "j") double j,
            @WebParam(name = "zoom_level") int zoom_level,
            @WebParam(name = "image_width") int image_width,
            @WebParam(name = "image_height") int image_height) {
        ViewPortInfo result = new ViewPortInfo();
                
        ClassLoader classLoader = getClass().getClassLoader();
	File file = new File(classLoader.getResource("Bydgoszcz.PNG").getFile());
        
        int[] zoom_width = {1000, 900, 800, 700, 600, 500, 400, 300, 200, 100};
       
        if(zoom_level < 0)
            zoom_level = 0;
        
        if(zoom_level > 9)
            zoom_level = 9;
        
        try {
            BufferedImage image = ImageIO.read(file);
            
            int x = (int)Math.round(i);
            int y = (int)Math.round(j);
            
            int width = zoom_width[zoom_level];
            int height = (int)((double)width/((double)image_width/(double)image_height));
            
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
            
            Image scaledImage = image.getScaledInstance(image_width, image_height, width);
            
            image = new BufferedImage(image_width, image_height, image.getType());
            image.getGraphics().drawImage(scaledImage, 0, 0 , null);
            
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            baos.flush();

            String base64String=Base64.encode(baos.toByteArray());
            baos.close();
            
            result.ImageData = base64String;
            result.TopX = (double)x;
            result.TopY = (double)y;
            result.ZoomLevel = zoom_level;
            
            
            Logger.getLogger(CalculatorWS.class.getName()).log(Level.INFO, null, image.getHeight());   
        } catch (IOException ex) {
            Logger.getLogger(CalculatorWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
}
