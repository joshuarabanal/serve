/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package joshuarabanal;

import android.util.Log;
import basicServer.Preprocessor.Dir_JSOn_builder;
import basicServer.Preprocessor.GZipFIles;
import basicServer.Preprocessor.InlineCSS.InlineCSS;
import basicServer.Preprocessor.SiteMapBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import preProcessor.ConvertMusicXml;

/**
 *
 * @author Joshua
 */
public class PreProcessor {
    
    public static void process(File f, ArrayList<String> dynamicUrls) throws FileNotFoundException, IOException, Exception{
        Log.i("PreProcessor.process", "starting");
        long time = System.currentTimeMillis();
        listMusicxmlFiles(f, dynamicUrls);
        Log.i("list musicxml files", "lasted:"+((System.currentTimeMillis()-time)/1000f)+" seconds"); time = System.currentTimeMillis();
        convertMusicXmlFiles(f);
        Log.i("convert musicxml files", "lasted:"+((System.currentTimeMillis()-time)/1000f)+" seconds"); time = System.currentTimeMillis();
        
        //inline css
        InlineCSS css = new InlineCSS(f);
        css.run();
        Log.i("inline css", "lasted:"+((System.currentTimeMillis()-time)/1000f)+" seconds"); time = System.currentTimeMillis();
        css.deleteCssFiles();
        Log.i("delete css", "lasted:"+((System.currentTimeMillis()-time)/1000f)+" seconds"); time = System.currentTimeMillis();
        
        
        //build sitemap
            SiteMapBuilder smb;
            smb = new SiteMapBuilder(f);
            smb.addExcludedFileExtensions("gdraw","xml","mxl");
            smb.build("http://joshuarabanal.info",dynamicUrls);
        Log.i("sitemap builder", "lasted:"+((System.currentTimeMillis()-time)/1000f)+" seconds"); time = System.currentTimeMillis();
        
            
            //build dir.json used for client side searching
            Dir_JSOn_builder.buildDir_JSONinDirs(f);
        Log.i("dir.json", "lasted:"+((System.currentTimeMillis()-time)/1000f)+" seconds"); time = System.currentTimeMillis();
        
        GZipFIles.GZipFilesInFolderInNewThread(f);
        
        
        
        
        
        Log.i("preprocessing complete", "preprocessing complete");
    }
    private static void deleteCssFiles(File root){
        if(root.isDirectory()){
            File[] children = root.listFiles();
            for(int i = 0; i<children.length; i++){
                deleteCssFiles(children[i]);
            }
        }
        else if(root.getName().contains(".css")){
            Log.i("deleteing file", root.toString());
            root.delete();
        }
    }
    
    private static void convertMusicXmlFiles(File rootDir) throws IOException, Exception{
        
        //write final file
        File finalOutput = new File(rootDir,"raw/music_xml/library.json");
        FileOutputStream out = new FileOutputStream(finalOutput);
        out.write('[');
        File[] musicxmFlies = new File(rootDir,"raw/music_xml").listFiles();
        int count = 0;
        for(int i = 0; i<musicxmFlies.length; i++){
            if(musicxmFlies[i].getName().contains(".mxl")){
                if(count>0){
                out.write(",\n\r".getBytes());
                }
                count++;
                String json = ConvertMusicXml.convert(musicxmFlies[i]);
                out.write(json.getBytes());
                
            }
        }
        
        out.write(']');
        out.close();
        
    }
    
    /**
     * 
     * @param f
     * @param dynamicUrls list of urls to add to the sitemap: "http://www.joshuarabanal.info/view_song.html?q=sample.xml"
     * @throws FileNotFoundException
     * @throws IOException 
     */
    private static void listMusicxmlFiles(File f, ArrayList<String> dynamicUrls) throws FileNotFoundException, IOException{
        
        File raw = new File(f,"raw/music_xml");
        File[] rawFiles = raw.listFiles();
        ArrayList<File> outputFiles = new ArrayList<File>();
        for(int i = 0 ;i<rawFiles.length; i++){//accquire all files
            if(
                    rawFiles[i].getName().contains(".mxl") 
                    && !rawFiles[i].getName().contains(".mxl.gz")
                    && !rawFiles[i].getName().contains(".mxl.data")
            ){
                outputFiles.add(rawFiles[i]);
                dynamicUrls.add("http://www.joshuarabanal.info/view_song.html?q="+rawFiles[i].getName());
            }
        }
        
        
        
    }
    
    public static void deleteFolder(File f){
        
        if(f.isDirectory()){
            File[] files = f.listFiles();
            for(int i = 0; i<files.length; i++){
                deleteFolder(files[i]);
            }
        }
            f.delete();
    }
}
