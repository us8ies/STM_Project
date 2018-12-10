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

/**
 *
 * @author User
 */
@WebService(serviceName = "CalculatorWS")
@Stateless()
public class CalculatorWS {

    /**
     * Web service operation
     */
    @WebMethod(operationName = "add")
    public String add(@WebParam(name = "i") double i, @WebParam(name = "j") double j) {
        
        String result = "";
        
        ClassLoader classLoader = getClass().getClassLoader();
	File file = new File(classLoader.getResource("Capture.PNG").getFile());
        
        try {
            BufferedImage image = ImageIO.read(file);
            
            image = image.getSubimage(300, 500, 500, 500);
            
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            baos.flush();

            String base64String=Base64.encode(baos.toByteArray());
            baos.close();
            
            result = base64String;
            
            
            Logger.getLogger(CalculatorWS.class.getName()).log(Level.INFO, null, image.getHeight());   
        } catch (IOException ex) {
            Logger.getLogger(CalculatorWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        double k = i + j;
        return result;
    }
}
