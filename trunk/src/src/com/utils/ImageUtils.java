package src.com.utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.zip.Deflater;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

public class ImageUtils {
	
	public static byte[] imgToByte(File file) {
		byte[] img = null;
		
		try {
			FileInputStream fis = new FileInputStream(file);
			
			
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        byte[] buf = new byte[1024];
	        try {
	            for (int readNum; (readNum = fis.read(buf)) != -1;) {
	                /*Writes len bytes from the specified byte array starting at offset 
	                off to this byte array output stream.*/
	            	bos.write(buf, 0, readNum); //no doubt here is 0
	                //System.out.println("read " + readNum + " bytes,");
	            }
	        } catch (IOException ex) {
	        	ex.printStackTrace();
	        }
	        
	        //Esto es sin usar encoding
	        img = bos.toByteArray();

		} catch (FileNotFoundException e) {
			System.err.println("File " + file.getAbsolutePath() + " was not found!");
		}
		return img;
	}
	
	public static byte[] compressByteArray(byte[] arrayToCompress){
		// Compressor with highest level of compression
	    Deflater compressor = new Deflater();
	    compressor.setLevel(Deflater.BEST_COMPRESSION);
	    
	    // Give the compressor the data to compress
	    compressor.setInput(arrayToCompress);
	    compressor.finish();
	    
	    // Create an expandable byte array to hold the compressed data.
	    // It is not necessary that the compressed data will be smaller than
	    // the uncompressed data.
	    ByteArrayOutputStream bos = new ByteArrayOutputStream(arrayToCompress.length);
	    
	    // Compress the data
	    byte[] buf = new byte[1024];
	    while (!compressor.finished()) {
	        int count = compressor.deflate(buf);
	        bos.write(buf, 0, count);
	    }
	    try {
	        bos.close();
	    } catch (IOException e) {
	    }
	    
	    // Return the compressed data
		return bos.toByteArray();
	}
	
	public static BufferedImage createBufferedImageFrom(File file, int width, int height){
		return new BufferedImage(width, height,  BufferedImage.TYPE_3BYTE_BGR);
	}
	
	public static BufferedImage[] splitImage(BufferedImage img, int cols, int rows) {
		int w = img.getWidth()/cols; //w of the mini-image
		int h = img.getHeight()/rows;//h of the mini-image
		int num = 0;
		BufferedImage imgs[] = new BufferedImage[cols*rows];
		for(int y = 0; y < rows; y++) {
			for(int x = 0; x < cols; x++) {
				imgs[num] = new BufferedImage(w, h, img.getType());
				// Tell the graphics to draw only one block of the image
				Graphics2D g = imgs[num].createGraphics();
				g.drawImage(img, 0, 0, w, h, w*x, h*y, w*x+w, h*y+h, null);
				g.dispose();
				num++;
			}
		}
		return imgs;
	}

	public static byte[] bufferedImageToByteArray(BufferedImage bufferedImage, int imageNumber, int x, int y) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(imageNumber); //Image id
			dos.writeInt(x); //X offset
			dos.writeInt(y); //Y offset
			ImageIO.write(bufferedImage, "jpg", dos);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baos.toByteArray();
	}


	
}
