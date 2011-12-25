package src.com.compressor;

import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.sun.media.jai.codec.JPEGDecodeParam;
import com.sun.media.jai.codecimpl.JPEGImageDecoder;


public class Decoder {

	public RenderedImage decode(ByteArrayInputStream input) {
		JPEGDecodeParam decodeParam = new JPEGDecodeParam();
		
		//probar si esta bien el decodeToCSM en false, deberia ser mas rapido... 
		decodeParam.setDecodeToCSM(false);
		JPEGImageDecoder decoder = new JPEGImageDecoder(input, decodeParam);
		
		RenderedImage image = null;
		try {
			image = decoder.decodeAsRenderedImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
}
