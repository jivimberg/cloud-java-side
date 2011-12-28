package src.com;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import src.com.utils.ImageUtils;

public class Server implements Runnable {
	
	public static final String ANDROID_IP = "127.0.0.1"; 
	public static final int SERVER_PORT = 5000;
	public static final int TCP_PORT = 4444;
	public boolean sending = false;
	
	private static final String FILE_NAME = "Java";
	
	private List<File> files;
	
	public Server(){
		files = new ArrayList<File>();
		File file;
		for(int i = 9; i > 1; i--){
			file = new File("src/resources/" + FILE_NAME + i + ".jpg");
			/*byte[] uncompressedBytes = imgToByte(file);
			System.out.println("Normal File: "+uncompressedBytes.length);
			byte[] compressedBytes = compressByteArray(uncompressedBytes);
			System.out.println("Compressed File: "+ compressedBytes.length);*/
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
				//Thread.sleep(500);
				/* Prepare some data to be sent. */
				file = files.get(i-2);
				buf = ImageUtils.compressByteArray(ImageUtils.imgToByte(file));
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

}
