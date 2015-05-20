import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class ImageUtil {
	
	public ImageUtil()
	{
		
	}
	
	public static BufferedImage getImageFromFile(String imageName)
	{
		BufferedImage img = null;
		
		try {
			img = ImageIO.read(new File(imageName));
		}catch (IOException e)
		{
			
		}
		
		return img;
	}
	

}
