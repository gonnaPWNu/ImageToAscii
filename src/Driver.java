import java.awt.image.BufferedImage;


public class Driver {

	public static void main(String[] args)
	{
		BufferedImage myImg = ImageUtil.getImageFromFile("monalisa.jpg");
		
		if(myImg != null)
		{
			DecomposedImage dImg = new DecomposedImage(myImg, 4);
			//dImg.createAsciiTextFile("monaLisa.txt");
			dImg.createAsciiImg();
		}
		else
		{
			System.out.println("myImg is null");
		}
		
	}
	
}
