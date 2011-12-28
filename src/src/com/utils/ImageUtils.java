package src.com.utils;

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
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
	        
	        //ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
	        //esto es usando encoding
//	        ByteArrayOutputStream encodedImage = encoder.encode(bis);
//	        img = encodedImage.toByteArray();
	        
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
	
	//Not useful if only compresses...
	public static void compressJpegFile(File infile, File outfile, float compressionQuality) {
	    try {
	        // Retrieve jpg image to be compressed
	        RenderedImage rendImage = ImageIO.read(infile);

	        // Find a jpeg writer
	        ImageWriter writer = null;
	        @SuppressWarnings("rawtypes")
			Iterator iter = ImageIO.getImageWritersByFormatName("jpg");
	        if (iter.hasNext()) {
	            writer = (ImageWriter)iter.next();
	        }

	        // Prepare output file
	        ImageOutputStream ios = ImageIO.createImageOutputStream(outfile);
	        writer.setOutput(ios);

	        // Set the compression quality
	        ImageWriteParam iwparam = new JPEGImageWriteParam(Locale.getDefault());
	        iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT) ;
	        iwparam.setCompressionQuality(compressionQuality);
	        
	        // Write the image
	        writer.write(null, new IIOImage(rendImage, null, null), iwparam);

	        // Cleanup
	        ios.flush();
	        writer.dispose();
	        ios.close();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
	
}
