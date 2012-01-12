package src.com;

import java.awt.image.BufferedImage;
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
	
	private static final int MAX_UDP_PACKET_SIZE = 65507;
	public static final String ANDROID_IP = "127.0.0.1"; 
	public static final int SERVER_PORT = 5000;
	public static final int TCP_PORT = 4444;
	public boolean sending = false;
	
	private static final int COLS = 4;
	private static final int ROWS = 4;
	
	private static final String FILE_NAME = "MailmenTeaser.flv_f00";
	
	private List<File> files;
	
	public Server(){
		files = new ArrayList<File>();
		File file;
		for(int i = 150; i < 954; i++){
			String filePrefix = "";
			if(i < 10){
				filePrefix = "00";
			}else if(i < 100){
				filePrefix = "0";
			}
			file = new File("src/resources/mailmen/"+ FILE_NAME + filePrefix + i + ".jpg");		
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
            /* Finish signal Thread*/ 
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

			int i = 0;
			int idx = 0; //for benchmarking
			File file = null;
			byte[] buf = null;
			DatagramPacket packet = null;
			while(sending){
//				Thread.sleep(100);
				/* Prepare some data to be sent. */
				file = files.get(i);
				BufferedImage[] splitedBufferedImageArray = ImageUtils.splitImage(file, COLS, ROWS);
				
				for(int j = 0; j < splitedBufferedImageArray.length; j++){
					Thread.sleep(150);
					//System.out.println(splitedBufferedImageArray.length);
					//buf = ImageUtils.compressByteArray(ImageUtils.bufferedImageToByteArray(splitedBufferedImageArray[j], i, j % 2,j / 2));
					buf = ImageUtils.bufferedImageToByteArray(splitedBufferedImageArray[j], i, j % ROWS,j / COLS);
					assert buf.length < MAX_UDP_PACKET_SIZE;

					/*
					 * Create UDP-packet with data & destination(url+port)
					 */
					packet = new DatagramPacket(buf, buf.length, serverAddr, SERVER_PORT);
					//System.out.println("Creating packet...");

					/* Send out the packet */
					socket.send(packet);
					System.out.println("Sent packet... img " + i + " (" + j%ROWS + "," + j / COLS + ")");
					//System.out.println(System.currentTimeMillis());
				}
				
				if(i == 952-150){
					i = 0;
				}else{
					i++;
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			socket.close();
		}
	}

}
