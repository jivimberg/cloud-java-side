package src.com.compressor;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sun.media.jai.codec.JPEGEncodeParam;
import com.sun.media.jai.codecimpl.JPEGImageEncoder;

public class EncoderViejo {

	private float quality;
	
	public EncoderViejo() {
		this.quality = (float) 0.20; 
	}

	/**
	 * Constructor. Quality must be greater than 0, and at most 1.
	 * @param quality. 0.75 high quality, 0.5  medium quality, 0.25 low quality.
	 */
	public EncoderViejo(float quality) {
		this.quality = quality;
	}

	public ByteArrayOutputStream encode(ByteArrayInputStream inputStream) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();//Size can be passed as argument
		JPEGEncodeParam encodeParam = new JPEGEncodeParam();
		encodeParam.setQuality(quality);
		
		JPEGImageEncoder encoder = new JPEGImageEncoder(output, encodeParam);
		
//		InputStream input = new ByteArrayInputStream(arg0);
		try {
			RenderedImage image = ImageIO.read(inputStream);
			encoder.encode(image);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (ByteArrayOutputStream) encoder.getOutputStream();
	}

	public float getQuality() {
		return quality;
	}

	public void setQuality(float quality) {
		this.quality = quality;
	}
}
