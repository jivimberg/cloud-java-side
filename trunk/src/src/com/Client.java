package src.com;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import com.sun.jmx.snmp.Timestamp;

import src.com.compressor.EncoderViejo;

public class Client implements Runnable {
	
	public static final String SERVERIP = "127.0.0.1"; 
	public static final int SERVERPORT = 5000;
	public static final int TCPPORT = 4444;
	public boolean sending = false;
	
	private static final String FILE_NAME = "Java";
	
	
	private EncoderViejo encoder = new EncoderViejo();
	
	@Override
	public void run() {
		while(true)
			startTCPSendListen();
			//startUDPTransaction();
	}
	
	private void startTCPSendListen() {
		ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(TCPPORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port:"+TCPPORT+".");
        }
 
        Socket clientSocket = null;
        try {
        	System.out.println("listening to first signal");
            clientSocket = serverSocket.accept();
            clientSocket.close();
            System.out.println("sending packets!");
            new Thread(){
            	@Override
            	public void run() {
            		ServerSocket serverSocket = null;
            		try {
                        serverSocket = new ServerSocket(TCPPORT);
                    } catch (IOException e) {
                        System.err.println("Could not listen on port:"+TCPPORT+".");
                    }
             
                    Socket clientSocket = null;
                    try {
                    	System.out.println("listening to second signal");
                        clientSocket = serverSocket.accept();
                        clientSocket.close();
                        System.out.println("Closing signal!!");
                        sending = false;
                    } catch (IOException e) {
                        System.err.println("Accept failed.");
                    }
            	}
            };
            startUDPTransaction();
        } catch (IOException e) {
            System.err.println("Accept failed.");
        }
		
	}
	
	 

	private void startUDPTransaction(){
		DatagramSocket socket = null;
		try {
			// Retrieve the ServerName
			sending = true;
			InetAddress serverAddr = InetAddress.getByName(SERVERIP);
			
			/* Create new UDP-Socket */
			socket = new DatagramSocket();
			System.out.println("creating new socket");

			int i = 9;
			int idx = 0; //for benchmarking 
			while(sending){
				//Thread.sleep(500);
				/* Prepare some data to be sent. */
				File file = new File("src/resources/" + FILE_NAME + i + ".jpg");
				byte[] buf = imgToByte(file);
				System.out.println("image size: " + buf.length);
				
				/* Create UDP-packet with 
				 * data & destination(url+port) */
				DatagramPacket packet = new DatagramPacket(buf, buf.length,	serverAddr, SERVERPORT);
				System.out.println("creating packet");
				
				/* Send out the packet */
				socket.send(packet);
				System.out.println("sending packet.");
				System.out.println("packet index: " + idx + " -> " + System.currentTimeMillis());
				idx++;
				
				if(idx == 500){
					break;
				}
				
				if(i == 2){
					i = 9;
				}else{
					i--;
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			socket.close();
		}
	}

	private byte[] imgToByte(File file) {
		byte[] img = null;
		
		try {
			FileInputStream fis = new FileInputStream(file);
			
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        byte[] buf = new byte[1024];
	        try {
	            for (int readNum; (readNum = fis.read(buf)) != -1;) {
	                /*Writes len bytes from the specified byte array starting at offset 
	                off to this byte array output stream.*/
	            	bos.write(buf, 0, readNum); //no doubt here is 0
	                System.out.println("read " + readNum + " bytes,");
	            }
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	        
	        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
	        //esto es usando encoding
//	        ByteArrayOutputStream encodedImage = encoder.encode(bis);
//	        img = encodedImage.toByteArray();
	        
	        //Esto es sin usar encoding
	        img = bos.toByteArray();

		} catch (FileNotFoundException e) {
			System.out.println("File " + file.getAbsolutePath() + " was not found!");
		}
		return img;
	}

}
