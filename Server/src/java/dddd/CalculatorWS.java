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
    
    private static final Double offsetX = 53.132401d;
    private static final Double offsetY = 17.983770d;
    private static final int[] zoom_width = {1000, 900, 800, 700, 600, 500, 400, 300, 200, 100};
    private static final String BYDGOSZCZ_PNG = "Bydgoszcz.PNG";
    

    @XmlAccessorType(XmlAccessType.FIELD)
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

        zoom_level = ValidateZoomLevel(zoom_level);
        
        int ref_width = zoom_width[zoom_level];
        int ref_height = (int)((double)ref_width/((double)image_width/(double)image_height));
        
        int ref_x = LatitudeToWidth(i);
        int ref_y = LongitudeToHeight(j); 
        
        try {
            BufferedImage refImage = GetReferenceImage();
            
            ref_x = ValidateWidth(ref_x, ref_width, refImage);
            ref_y = ValidateHeight(ref_y, ref_height, refImage);
            
            BufferedImage clientImage = GetClientImage(refImage, ref_x, ref_y, ref_width, ref_height, image_width, image_height);
                        
            result.ImageData = SerializeImage(clientImage);
            result.TopX = WidthToLatitude(ref_x);
            result.TopY = HeightToLongitude(ref_y);
            result.ZoomLevel = zoom_level;  
        } catch (IOException ex) {
            Logger.getLogger(CalculatorWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }

    private BufferedImage GetReferenceImage() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(BYDGOSZCZ_PNG).getFile());
        BufferedImage image = ImageIO.read(file);
        return image;
    }

    private int ValidateZoomLevel(int zoom_level) {
        if(zoom_level < 0)
            zoom_level = 0;
        if(zoom_level > zoom_width.length)
            zoom_level = zoom_width.length;
        return zoom_level;
    }

    private String SerializeImage(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        baos.flush();
        String base64String=Base64.encode(baos.toByteArray());
        baos.close();
        return base64String;
    }

    private BufferedImage GetClientImage(BufferedImage source_image, int x, int y, int width, int height, int scaled_width, int scaled_height) {
        source_image = source_image.getSubimage(x, y, width, height);
        Image scaledImage = source_image.getScaledInstance(scaled_width, scaled_height, width);
        BufferedImage result = new BufferedImage(scaled_width, scaled_height, source_image.getType());
        result.getGraphics().drawImage(scaledImage, 0, 0 , null);
        return result;
    }

    private int ValidateHeight(int y, int height, BufferedImage image) {
        if(y < 0)
        {
            y = 0;
        }
        if(y + height > image.getHeight())
        {
            y = image.getHeight() - height;
        }
        return y;
    }

    private int ValidateWidth(int x, int width, BufferedImage image) {
        if(x < 0)
        {
            x=0;
        }
        if(x + width> image.getWidth())
        {
            x = image.getWidth() - width;
        }
        return x;
    }
    
    int LatitudeToWidth(Double latitude){
        return (int)Math.round(latitude - offsetX);
    }
    
    int LongitudeToHeight(Double longitude){
        return (int)Math.round(longitude - offsetY);
    }
    
    double WidthToLatitude(int width){
        return (double)width + offsetX;
    }
    
    double HeightToLongitude(int height){
        return (double)height + offsetY;
    }
}
