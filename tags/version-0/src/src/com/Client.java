package src.com;

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
import java.util.ArrayList;
import java.util.List;

public class Client implements Runnable {
	
	public static final String ANDROID_IP = "127.0.0.1"; 
	public static final int SERVER_PORT = 5000;
	public static final int TCP_PORT = 4444;
	public boolean sending = false;
	
	private static final String FILE_NAME = "Java";
	
	private List<File> files;
	
	public Client(){
		files = new ArrayList<File>();
		File file;
		for(int i = 9; i > 1; i--){
			file = new File("src/resources/" + FILE_NAME + i + ".jpg");
			files.add(file);
		}
	}
	
	@Override
	public void run() {
		while(true)
			startTransimission();
	}
	
	private void startTransimission() {
		/* TCP handshake */
		ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(TCP_PORT);
            System.out.println("Listening to first TCP handshake in port: "+TCP_PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port:"+TCP_PORT+".");
        }
        
        Socket clientSocket = null;
		try {
        	clientSocket = serverSocket.accept();
        	System.out.println("TCP handshake received!");
        	clientSocket.close();
            System.out.println("Sending packets!");
            /* Finish signal Thread */
            new Thread(){
            	@Override
            	public void run() {
            		ServerSocket serverSocket = null;
            		try {
                        serverSocket = new ServerSocket(TCP_PORT);
                    	System.out.println("listening to finish signal");
                    	Socket clientSocket = serverSocket.accept();
                        clientSocket.close();
                        System.out.println("Closing signal!!");
                        sending = false;
                    } catch (IOException e) {
                    	e.printStackTrace();
                    }
            	}
            };
            /* Send messages tx */
            startUDPTransaction();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
	
	 

	private void startUDPTransaction(){
		DatagramSocket socket = null;
		try {
			// Retrieve the ServerName
			sending = true;
			InetAddress serverAddr = InetAddress.getByName(ANDROID_IP);
			
			/* Create new UDP-Socket */
			socket = new DatagramSocket();
			System.out.println("Creating new socket in " + ANDROID_IP);

			int i = 9;
			int idx = 0; //for benchmarking
			File file = null;
			byte[] buf = null;
			DatagramPacket packet = null;
			while(sending){
				Thread.sleep(500);
				/* Prepare some data to be sent. */
				file = files.get(i-2);
				buf = imgToByte(file);
				//System.out.println("image size: " + buf.length);
				
				/* Create UDP-packet with 
				 * data & destination(url+port) */
				packet = new DatagramPacket(buf, buf.length, serverAddr, SERVER_PORT);
				System.out.println("Creating packet...");
				
				/* Send out the packet */
				socket.send(packet);
				System.out.println("Sending packet...");
				//System.out.println("packet index: " + idx + " -> " + System.currentTimeMillis());
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

}