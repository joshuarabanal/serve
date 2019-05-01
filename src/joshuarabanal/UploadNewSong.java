/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package joshuarabanal;

import android.util.Log;
import basicServer.HttpHelpers;
import basicServer.Request;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.MalformedInputException;

/**
 *
 * @author Joshua
 */
public class UploadNewSong {
    private static final String composer_name = "composer-name",image_url = "image-URL", account = "account",description = "description",title = "title";
    
    public static void uploadNewSong(Request sock) throws Exception{
        if(!sock.getURL().equals("/upload")){
            throw new NullPointerException("wrong url to upload new song:"+sock.getURL());
        }
        String account = sock.getHeaderByName(UploadNewSong.account).trim();
        String imageURL = sock.getHeaderByName(image_url).trim();
        String composer = sock.getHeaderByName(composer_name).trim();
        String songDescription = sock.getHeaderByName(description).trim();
        String songTitle = sock.getHeaderByName(title).trim();
        Log.i("new upload", "imageurl:"+imageURL);
        File f = createXmlFile(songTitle);
        FileOutputStream out = new FileOutputStream(f);
        String songFileName = f.getName();
        out.write(sock.getData().getBytes());
        out.close();
        copyFile(f, new File(JoshuaOrderHandler.rawPath,"/userUploads/"+ songFileName));
        
        
        Log.i("xml file",f.toString());
        f = new File(f.getParentFile(),songFileName+".template");
        TemplateFileBuilder template = new TemplateFileBuilder(f);
        template.addAttribute("account", account);
        template.addAttribute("title",title);
        template.addAttribute("description", songDescription); 
        template.writeTemplateItem( "songTitle", TemplateFileBuilder.innerHTML, songTitle);
        template.writeTemplateItem("songDescription", TemplateFileBuilder.innerHTML, songDescription);
        template.writeTemplateItem( "composerName", TemplateFileBuilder.innerHTML, composer);
        template.writeTemplateItem( "composerImage", "src", imageURL);
        template.writeTemplateItem("downloadLink", "href",
                FirebaseFunctions.firebaseDynamicUrl(
                        "http://joshuarabanal.info/userUploads/"+songFileName,
                        songTitle, songDescription, imageURL
                )
        );
        template.close();
        copyFile(f, new File(JoshuaOrderHandler.rawPath,"/userUploads/"+ FirebaseFunctions.htmlEncode(f.getName())));
        
        HttpHelpers.httpGetResponse(sock,HttpHelpers.MimeTxt, "http://joshuarabanal.info/userSong.html?q="+songFileName);
        
        
        
        
        
    }
    private static void copyFile(File from, File to) throws FileNotFoundException, IOException{
        if(!to.getParentFile().exists()){ to.getParentFile().mkdirs(); }
        FileInputStream in = new FileInputStream(from);
        FileOutputStream out = new FileOutputStream(to);
        byte[] b = new byte[1024];
        int howMany;
        while((howMany = in.read(b))>0){
            out.write(b,0,howMany);
        }
        in.close();
        out.close();
    }
    private static File createXmlFile(String title){
        title = FirebaseFunctions.htmlEncode(title);
        int i = 0;
        File retu = new File(JoshuaOrderHandler.path,"/userUploads/");
        retu.mkdirs();
        retu = new File(retu,title+".xml");
        while(retu.exists()){
            retu = new File(retu.getParentFile(),title+i+".xml");
            i++;
        }
        return retu;
    }
}
