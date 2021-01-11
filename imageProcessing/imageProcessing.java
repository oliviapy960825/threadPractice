package imageProcessing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class imageProcessing {
	public static final String SOURCE_FILE = "/many-flowers.jpg";
	public static final String DESTINATION_FILE = "/many-flowers2.jpg";
	public static void recolorMultiThread(BufferedImage original, BufferedImage result, int numberOfThreads){
		List<Thread> threads = new ArrayList();
		int width = original.getWidth();
		int height = result.getHeight() / numberOfThreads;
		for(int i = 0; i <numberOfThreads; i++){
			final int threadMultiplier = i;
			Thread thread = new Thread(() ->{
				int leftCorner = 0;
				int topCorner = height * threadMultiplier;
				
				recolorImage(original, result, leftCorner, topCorner, width, height);
			});
			
			threads.add(thread);
		}
		
		for(Thread thread : threads){
			thread.start();
		}
		
		for(Thread thread : threads){
			try{
				thread.join();
			}
			catch(InterruptedException e){
				
			}
		}
	}
	public static int getBlue(int rgb){
		return rgb & 0x000000FF;
	}
	public static int getGreen(int rgb){
		return rgb & 0x0000FF00 >>8;
	}
	public static int getRed(int rgb){
		return rgb & 0x00FF0000 >>16;
	}
	public static int createRGBFromColor(int red, int green, int blue){
		int rgb = 0;
		rgb |= blue;
		rgb |= green << 8;
		rgb |= red << 16;
		
		rgb |= 0xFF000000;
		
		return rgb;
	}
	public static void recolorSingleThread(BufferedImage original, BufferedImage result){
		recolorImage(original, result, 0, 0, original.getWidth(), original.getHeight());
	}
	public static boolean isShadeGrey(int green, int blue, int red){
		return Math.abs(green - blue) < 30 && Math.abs(green - red) < 30 && Math.abs(blue - red) < 30;
	}
	public static void recolorImage(BufferedImage original, BufferedImage result, int left, int top, int width, int height){
		for(int x = left; x < left + width && x < original.getWidth(); x++){
			for(int y = top; y < top + height && y < original.getHeight(); y++){
				replacePixel(original, result, x, y);
			}
		}
	}
	public static void replacePixel(BufferedImage original, BufferedImage result, int x, int y){
		int rgb = original.getRGB(x, y);
		int red = getRed(rgb);
		int green = getGreen(rgb);
		int blue = getBlue(rgb);
		
		int newRed;
		int newGreen;
		int newBlue;
		
		if(isShadeGrey(red, green, blue)){
			newRed = Math.min(255, red + 10);
			newGreen = Math.max(0, green - 80);
			newBlue = Math.max(0, blue - 20);
		}
		else{
			newRed = red;
			newGreen = green; 
			newBlue = blue;
		}
		
		int newRGB = createRGBFromColor(newRed, newGreen, newBlue);
		setRGB(result, x, y, newRGB);
	}
	public static void setRGB(BufferedImage image, int x, int y, int rgb){
		image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
	}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedImage originalImage = ImageIO.read(new File(SOURCE_FILE));
		BufferedImage resultImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		long startTime = System.currentTimeMillis();
		recolorSingleThread(originalImage, resultImage);
		long endTime = System.currentTimeMillis();
		
		long duration = endTime - startTime;
		File output = new File(DESTINATION_FILE);
		ImageIO.write(resultImage, "jpg", output);
		
		System.out.println(String.valueOf(duration));
	}

}
