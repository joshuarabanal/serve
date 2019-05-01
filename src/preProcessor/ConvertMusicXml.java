/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preProcessor;

import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import joshuarabanal.FirebaseFunctions;
import joshuarabanal.PreProcessor;
import netscape.javascript.JSObject;
import xml.NameValuePairList;
import xml.XmlCursor;
import xml.unoptimized.NameValuePair;
import xml.unoptimized.Parser;

/**
 *
 * @author Joshua
 */
public class ConvertMusicXml implements XmlCursor{
    private File inFile;
    private int type;
    private OutputStream out;
    private static final int 
            type_containter = 0,
            type_xmlROOT = 1;
    
    private String container;
    private String description;
    private String composerName;
    private String imageLocation;
    private String workTitle;
    
    public ConvertMusicXml(File in,OutputStream out, int type) throws IOException {
        this.type = type;
        this.inFile = in;
        this.out = out;
        out.write(("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\r\n").getBytes());
    }
    
    private static File zipToFolder(File f) throws IOException{
        ZipInputStream in = new ZipInputStream(new FileInputStream(f));
        File temp = new File(File.createTempFile("temp","").getParentFile(),"tempMxl");
        PreProcessor.deleteFolder(temp);
        temp.mkdirs();
        //Log.i("makeing new temp",temp.toString());
        ZipEntry ent = null;
        byte[] buffer = new byte[1024];
        int howMany;
        while((ent = in.getNextEntry())!=null){
            if(ent.isDirectory()){ continue; }
            File output = new File(temp, ent.getName());
            output.getParentFile().mkdirs();
            //Log.i("making file", " "+ent.getName());
            FileOutputStream out = new FileOutputStream(output);
            while((howMany = in.read(buffer))>0){
                out.write(buffer,0,howMany);
            }
            out.close();
            in.closeEntry();
        }
        in.close();
        return temp;
    }
    
    /**
     * @param f this file will be replaced with a new mxl file that has been processed
     * @throws IOException 
     */
    public static String convert(File f) throws IOException, Exception{
        File dir = null;
        try{
            
                //write mxl file
                dir = zipToFolder(f);
                ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
                File container = new File(dir,"META-INF/container.xml");
                out.putNextEntry(new ZipEntry("META-INF/container.xml"));
                ConvertMusicXml xmlRead = new ConvertMusicXml(container,out, type_containter);
                xmlRead.run();
                String xmlRoot = xmlRead.container;
                out.closeEntry();

                out.putNextEntry(new ZipEntry(xmlRoot));
                xmlRead = new ConvertMusicXml(new File(dir,xmlRoot), out, type_xmlROOT);
                xmlRead.run();
                String description = FirebaseFunctions.htmlEncode(xmlRead.description);
                String composerName = xmlRead.composerName;
                String imageLocation = xmlRead.imageLocation;
                String workTitle = xmlRead.workTitle;
                out.closeEntry();
                out.close();

                //write image file 
                String newImageName = f.getName().substring(0,f.getName().indexOf("."))+imageLocation.substring(imageLocation.indexOf("."));
                File imageFileOut = new File(
                        f.getParentFile(),
                        newImageName
                );
                //Log.i("writing image file", imageFileOut.toString());
                FileInputStream fin = new FileInputStream(new File(dir,imageLocation));
                FileOutputStream fout = new FileOutputStream( imageFileOut);
                int howMany; byte[] b = new byte[1024];
                while((howMany = fin.read(b))>0){ 
                    fout.write(b,0,howMany); 
                }
                fin.close();
                fout.close();
                
                
                //write data file
                File dataFile = new File(f.getParentFile(), f.getName()+".data");
                fout = new FileOutputStream( dataFile);
                String json = "{"+
                        "\"songTitle\":\""+workTitle+"\",\"composerName\":\""+composerName+"\","
                        + "\"songDescription\":\""+description+"\",\"composerImageUrl\":\"http://joshuarabanal.info/raw/music_xml/"
                        +imageFileOut.getName()+"\",\"musicXmlUrl\":\""+
                        FirebaseFunctions.firebaseDynamicUrl(
                                "http://joshuarabanal.info/raw/music_xml/"+f.getName(), 
                                workTitle,
                                description,
                                "http://joshuarabanal.info/raw/music_xml/"+imageFileOut.getName()
                                )+
                        "\" , \"file_name\":"+"\""+f.getName()+"\"}";
                fout.write(json.getBytes());
                fout.close();
                return json;
        }
        catch(Exception e){
            if(dir!= null)Log.i("zip dir", ""+dir.toString());
            //if(dir!=null){ PreProcessor.deleteFolder(dir); }
            Log.i("failed to convert", f.toString());
            throw e;
       }
       // if(dir!=null){ PreProcessor.deleteFolder(dir); }
        //write data file
       // return null;
        
    }
    ArrayList<String> stack = new ArrayList<String>();
    @Override
    public void newElement(String name, NameValuePairList attributes, boolean autoClose) throws Exception {
        switch(this.type){
            case type_containter:
                if(name.equals("rootfile")){
                    container = attributes.getAttributeValue("full-path");
                }
                    writeNewElement(name, attributes,autoClose);
                break;
                
                
            case type_xmlROOT:
                      stack.add(name);
                      if(name.equals("creator") && attributes.getAttributeValue( "type").equals("composer")){
                          stack.set(stack.size()-1, "composerName");
                      }
                if( name.equals("miscellaneous") ){}//do not write muscelaneuous
                else if(name.equals("image") && stack.size() >0 && stack.get(stack.size()-2).equals("composerName")){//composer image
                    imageLocation = attributes.getAttributeValue( "source");
                }
                else{
                    writeNewElement(name, attributes,autoClose);
                }
                
        }
    }
    private static String getAttrValue(List<NameValuePair> attributes, String name){
        for(int i = 0; i<attributes.size(); i++){
                if(attributes.get(i).getName().equals(name)){
                    return attributes.get(i).getValue();
                }
            }
        return null;
    }
    private void writeNewElement(String name, NameValuePairList attributes, boolean autoClose) throws IOException{
        
        
        
        out.write(("<"+name).getBytes());
        
        for(int i = 0; (attributes != null && i<attributes.size()); i++){
            out.write(
                    (" "+attributes.get(i).getName()+"=\""+attributes.get(i).getValue()+"\"").getBytes()
            );
        }
        if(autoClose){
            out.write(("/>").getBytes());
        }
        else{
            out.write(">".getBytes());
        }
        
    }
    
    @Override
    public void closeElement(String name) throws Exception {
        switch(this.type){
            case type_containter:
                out.write(("</"+name+">").getBytes());
                break;
                
                
            case type_xmlROOT:
                if(name.equals("miscellaneous")){}//songdescription
                else { out.write(("</"+name+">").getBytes()); }
                stack.remove(stack.size()-1);
                break;
                
                
        }
    }

    @Override
    public void textElement(String text) {
        if(text.indexOf("<") == 0 ||text.indexOf(">") == 0){
            throw new IndexOutOfBoundsException("text element error:"+text);
        }
        
        try {
            switch(this.type){
                case type_containter:
                    out.write(text.getBytes());
                    break;
                case type_xmlROOT:
                    if(stack.size() == 0){
                    }
                    String name = stack.get(stack.size()-1);
                    if(name.equals("miscellaneous")){ //songdescription
                        if(this.description == null){
                            this.description = text; 
                        }
                        else{
                            this.description += text;
                        }
                    }
                    else{
                        if(name.equals("work-title")){ this.workTitle = text; }
                        if(name.equals("composerName")){ this.composerName = text; }
                        out.write(text.getBytes());
                    }
                    break;
            }
        }catch (IOException ex) {
            Logger.getLogger(ConvertMusicXml.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    private void run() throws Exception {
        Parser p = new Parser(inFile, this,null);
                p.read();
        
    }
    
}
