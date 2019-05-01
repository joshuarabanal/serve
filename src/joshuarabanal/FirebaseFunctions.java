/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package joshuarabanal;

import java.net.URLDecoder;
import java.net.URLEncoder;


/**
 *
 * @author Joshua
 */
public class FirebaseFunctions {
    
    /**
     * https://firebase.google.com/docs/dynamic-links/create-manually?authuser=0
     * https://app_code.app.goo.gl/?link=your_deep_link&apn=package_name[&amv=minimum_version][&al=android_link][&afl=fallback_link]
     * @return 
     */
    public static final String firebaseDynamicUrl(String link, String title, String description,String imageurl){
        String retu = "https://k5hxy.app.goo.gl?";
        if(link!=null){retu+= "link="+htmlEncode(link);}
        retu+="&apn=com.musicxml";
        retu+="&efr=1";
        if(title!=null){retu+="&st="+htmlEncode(title);}
        if(description!=null){retu+="&sd="+htmlEncode(description);}
        if(imageurl!=null){retu+="&si="+htmlEncode(imageurl);}
        return retu;
        
        
    }
    public static final String htmlEncode(String st){
        return URLEncoder.encode(st.replaceAll("%20", "+"));
    }
    public static final String htmlDecode(String st){
        return URLDecoder.decode(st);
    }
    
}
