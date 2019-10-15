/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package joshuarabanal;

import arise.server.AriseOrderHandler;
import basicServer.GoogleDDNS;
import basicServer.HttpHelpers;
import basicServer.ServerSock;
import basicServer.custom.MultiDomainRequestHandler;
import basicServer.custom.multiDomainRequestsHandler.Domain;
import vpn.VpnOrderHandler;

import java.io.File;
import java.io.PrintStream;

import android.util.Log;

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
		File srcFolder = new File(path,"src"); srcFolder.mkdirs();
		Log.i("root directory set", ""+srcFolder);
		
		if(false){
			System.setOut(new PrintStream(new File(path,"logs.txt")));
		}
                System.setErr(System.out);
                
                HttpHelpers.addMimeType("sf2", "application/soundfont2");
                HttpHelpers.addMimeType("mxl", "application/vnd.recordare.musicxml");
                
                
		GoogleDDNS DDNS_joshuarabanal = new GoogleDDNS("nRmzBwxtqW1V2p9v", "o9zHdAsmU2hkTjFk","joshuarabanal.info");
		DDNS_joshuarabanal.start();
		
		GoogleDDNS DDNS_servicefromhome = new GoogleDDNS("aJaiWrNJvbYDUiSk", "NiaWyks9OvTyrpk7", "servicefromhome.com");
		DDNS_servicefromhome.start();
		
		GoogleDDNS DDNS_composemusic = new GoogleDDNS("UJX0Suq5KCMIvo6m", "eFGwLlcuAu4gyepw", "composemusic.org");
		DDNS_composemusic.start();

		GoogleDDNS DDNS_portalVPN = new GoogleDDNS("E0hRT2eU57UXFGTH", "pionFtMG2Dho9nEJ", "*.servicefromhome.com");
		DDNS_portalVPN.start();
		
		
		
		
		MultiDomainRequestHandler root = new MultiDomainRequestHandler();
			Domain d = new Domain(new JoshuaOrderHandler(), 
					"joshuarabanal.info", "www.joshuarabanal.info", "www.joshuarabanal.info:4244",
					"composemusic.org","composemusic.org:4244",
					"www.composemusic.org"
				);
		root.add(d);
		
		d = new Domain(new AriseOrderHandler(), 
				"servicefromhome.com","servicefromhome.com:4244",
				"www.servicefromhome.com"
			);
		root.add(d);
	
		d = new Domain(new VpnOrderHandler("arise.com", "servicefromhome.com"), 
				"portal.servicefromhome.com",
				"starmatic.servicefromhome.com",
				"link.servicefromhome.com"
			);
		root.add(d);
		
			

		serv = new ServerSock(root,srcFolder);
		serv.startServer();
		//stream.close();
	}
        
}
