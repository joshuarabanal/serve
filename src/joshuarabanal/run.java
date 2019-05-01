/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package joshuarabanal;

import android.util.Log;
import basicServer.GoogleDDNS;
import basicServer.HttpHelpers;
import basicServer.ServerSock;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.net.InetAddress;

/**
 *
 * @author Joshua 
 */
public class run {
    /**
	 * @param args
	 * @throws Exception 
	 */ 
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
               
                
                
		ServerSock serv;
		File path = new File(System.getProperty("user.dir"));
		
		if(false){
			System.setOut(new PrintStream(new File(path,"logs.txt")));
		}
                System.setErr(System.out);
                
                HttpHelpers.addMimeType("sf2", "application/soundfont2");
                HttpHelpers.addMimeType("mxl", "application/vnd.recordare.musicxml");
                
		GoogleDDNS DDNS = new GoogleDDNS("nRmzBwxtqW1V2p9v", "o9zHdAsmU2hkTjFk","joshuarabanal.info");
		DDNS.start();
		serv = new ServerSock(new JoshuaOrderHandler(),path);
                /*
                serv.setSSL(
                        "C:\\Users\\Joshua\\Google Drive\\program stuff\\music xml\\website\\SSL\\joshuarabanal.info.csr",
                        "SU0798ni"
                );
		*/
		serv.startServer();
		//stream.close();
	}
        
}
