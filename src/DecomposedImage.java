import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.imageio.ImageIO;


public class DecomposedImage {

	private BufferedImage img;
	private int[][] avgRgbMat;
	private int[][] avgBlockMat;
	private char[][] asciiMat;
	private int blockSize;
	private int imgWidth;
	private int imgHeight;
	private BufferedImage asciiImg;
	private int blockWidth;
	private int blockHeight;
	private char[] asciiCharSet = {'#', '@', '%', '+', '*', '-', '.', ' '};
	//private char[] asciiCharSet = {' ', '.', '-', '*', '+', '%', '@', '#'};
	

	
	private DecomposedImage() {}
	
	public DecomposedImage(BufferedImage img, int blockSize)
	{
		this.img = img;
		this.blockSize = blockSize;
		this.imgWidth = img.getWidth();
		this.imgHeight = img.getHeight();
		
		initMat();	
		processImg();
	}
	
	
	
	private void initMat()
	{
		
		int width = getImgWidth();
		int height = getImgHeight();
		
		while(width % getBlockSize() != 0)
		{
			width++;
		}
		while(height % getBlockSize() != 0)
		{
			height++;
		}
		avgRgbMat = new int[height][width];
		
		blockWidth = getImgWidth() / getBlockSize();
		blockHeight = getImgHeight() / getBlockSize();
		
		if((getImgWidth() % getBlockSize()) != 0)
		{
			blockWidth++;
		}
		
		if((getImgHeight() % getBlockSize()) != 0)
		{
			blockHeight++;
		}
		
		asciiMat = new char[blockHeight][blockWidth];
		avgBlockMat = new int[blockHeight][blockWidth];
		
	}
	
	private void processImg()
	{
		populateRgbMat();
		populateAvgBlockMat();
		populateAsciiMat();
	}
	
	private void populateRgbMat()
	{
		for (int i = 0; i < this.getImgHeight(); i++)
		{
			for (int j = 0; j < this.getImgWidth(); j++)
			{
				avgRgbMat[i][j] = getAvgPixelValue(getImg().getRGB(j, i));
			}
		}
	}
	
	private int getAvgPixelValue(int pixel)
	{
		//int alpha = (pixel >> 24) & 0xff;
	    int red = (pixel >> 16) & 0xff;
	    int green = (pixel >> 8) & 0xff;
	    int blue = (pixel) & 0xff;
	    
	    return (red + green + blue) / 3;
	}

	private void populateAvgBlockMat()
	{
		
		for (int i = 0; i < getBlockHeight(); i++)
		{
			for (int j = 0; j < getBlockWidth(); j++)
			{
				int sum = 0;
				
				for(int n = 0; n < getBlockSize(); n++)
				{
					for (int m = 0; m < getBlockSize(); m++)
					{
						sum = sum + avgRgbMat[(i * getBlockSize()) + n][(j * getBlockSize()) + m];
					}
				}
				
				avgBlockMat[i][j] = sum / (getBlockSize() * getBlockSize());
				
			}
		}
	}

	private void populateAsciiMat()
	{
		
		int val = 0;
		
		for (int i = 0; i< getBlockHeight(); i++)
		{
			for (int j = 0; j < getBlockWidth(); j++)
			{
				val = avgBlockMat[i][j] * (asciiCharSet.length - 1) / 255;
				asciiMat[i][j] = asciiCharSet[val];
			}
		}
	}
	
	public void createAsciiImg()
	{
		String output = "";
		int size = 20;
		for (int i = 0; i < getBlockHeight(); i++)
		{
			for (int j = 0; j < getBlockWidth(); j++)
			{
				output = output + asciiMat[i][j];
			}
			
			//g2d.drawString(output, (i * size), fm.getAscent());
			output = output + "\n";
		}
		
		//output = "TESTTESTTESTSTESSET";
		
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font("Arial", Font.PLAIN, 20);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(output);
        int height = fm.getHeight();
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(Color.BLACK);
        
        
        for (int i = 0; i < getBlockHeight(); i++)
		{
			for (int j = 0; j < getBlockWidth(); j++)
			{
				output = output + asciiMat[i][j];
			}
			
			//g2d.drawString(output, (i * size), fm.getAscent() * size);
			g2d.drawString(output, 0, i * size);
			output = "";
		}
        
        
        g2d.dispose();
        try {
            ImageIO.write(img, "png", new File("Text.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

	}
	
	public void createAsciiTextFile(String fileName)
	{
		FileOutputStream fs;
		PrintStream ps;
		
		try {
			fs = new FileOutputStream(fileName);
			ps = new PrintStream(fs);
			String output = "";
			
			for (int i = 0; i < getBlockHeight(); i++)
			{
				for (int j = 0; j < getBlockWidth(); j++)
				{
					output = output + asciiMat[i][j];
				}
				
				ps.println(output);
				output = "";
			}
			
			ps.close();
		}catch (Exception e) {
			
		}
	}
	
	//setter and getters
	
	public BufferedImage getImg() {
		return img;
	}

	public void setImg(BufferedImage img) {
		this.img = img;
	}
	
	public int getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	public int[][] getAvgRgbMat() {
		return avgRgbMat;
	}

	public char[][] getasciiMat() {
		return asciiMat;
	}

	public int getImgWidth() {
		return imgWidth;
	}

	public int getImgHeight() {
		return imgHeight;
	}
	
	public int getBlockHeight()
	{
		return this.blockHeight;
	}
	
	public int getBlockWidth()
	{
		return this.blockWidth;
	}
	
	public BufferedImage getAsciiImg()
	{
		return this.asciiImg;
	}
}
