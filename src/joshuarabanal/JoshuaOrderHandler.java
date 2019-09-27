package joshuarabanal;


import Analytics.CrashReporter;
import android.util.Log;
import java.io.File;

import basicServer.HttpHelpers;
import basicServer.ProcessRequest;
import basicServer.Request;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

public class JoshuaOrderHandler implements ProcessRequest {
	public static File rawPath = null;
        public static File serverRoot;
        public static File path = null;
        
	@Override
	public int processRequest(Request sock) throws Exception {
		// TODO Auto-generated method stub
                
		switch(sock.getMethod()){
		case Request.METHOD_GET:
                        String url = sock.getURL();
			File f = HttpHelpers.getFileFromUrl(serverRoot, url);//new File(path, url);
			if (f.exists()){ 
				HttpHelpers.httpGetResponse(sock, f); 
			}
                        else if(new File(serverRoot,url+".html").exists()){
                            HttpHelpers.httpGetResponse(sock, new File(serverRoot,url+".html")); 
                        }
                        else if(url.contains("?") && url.contains("q=")){
                            handleQuery(sock);
                        }
			else{	
                            HttpHelpers.fileNotFound(sock);
                            System.out.println("file not found on server:"+f.toString());
                            Log.i("file exists?",""+f.exists());
                            System.out.println(sock.toString());
			}
			break;
		case Request.METHOD_POST : 
                    if(sock.getURL().equals("/upload")){
                        UploadNewSong.uploadNewSong(sock);
                    }
                    else{
                       
                        CrashReporter.log("unable to handle request:\n "+sock.toString());
                        
                    }
			break;
		case -1:
			System.out.println("blank headers error");
			HttpHelpers.httpGetResponse(sock,HttpHelpers.MimeTxt, "unknown method");
			
			break;
			default : 
				HttpHelpers.httpGetResponse(sock,HttpHelpers.MimeTxt, "unknown method");
				break;
		}
		
		
		
		
		
		System.out.println("finished message");
		
                sock.close();
		return 0;
	}
        
        /**
         * accepts get requests in the form of "/url.html?q=file.html"
         * @param sock
         * @throws Exception 
         */
    private void handleQuery(Request sock/*, String url*/)throws Exception{
        String url = sock.getURL();
        System.out.println("url:"+url);
                            String query = url.substring(url.indexOf("?")+1);
                            url = url.substring(0,url.indexOf("?"));
                            query = query.substring(query.indexOf("q=")+2);
                            if(query.contains("&")){
                                query = query.substring(0, query.indexOf("&"));
                            }
                            System.out.println(url+"/"+query);
        
        String folderUrl = url;
        if(folderUrl.contains(".html")){
            folderUrl = folderUrl.substring(0, folderUrl.indexOf(".html"));
        }
        if(!url.contains(".html")){
            url += ".html";
        }
        
        if(
                new File(serverRoot, folderUrl+"/"+query).exists()
        ){//get path to query string
            HttpHelpers.httpGetResponse(sock, new File(serverRoot, folderUrl+"/"+query)); 
        }
        else if(new File(serverRoot,url).exists()){//revert to original path
            HttpHelpers.httpGetResponse(sock, new File(serverRoot, url)); 
        }
        else{
            throw new FileNotFoundException("failed to find directory:"+sock.getURL());
        }
    }

        private byte[] b;
    private File MoveFile(File oldFile, File newDirectory) throws FileNotFoundException, IOException{
        if(newDirectory == null){
            newDirectory = serverRoot;
            if(newDirectory == null) {
            	throw new NullPointerException("server root was not set correctly");
            }
            PreProcessor.deleteFolder(newDirectory);
            newDirectory.mkdir();
            File[] childs = oldFile.listFiles();
            for(int i = 0; i<childs.length; i++){
                MoveFile(childs[i], newDirectory);
            }
            return newDirectory;
        }
        if(oldFile.isFile()){
            FileInputStream fis = new FileInputStream(oldFile);
            try{
                File f = new File(newDirectory, URLEncoder.encode(oldFile.getName()));
                f.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(f);

                int howMany;
                if(b == null){ b = new byte[8192];}
                while((howMany = fis.read(b)) >0){
                    fos.write(b,0,howMany);
                }
                fos.close();
                fis.close();
            }catch(Exception e){ Log.i("file", URLEncoder.encode(oldFile.getName())); throw e; }
        }
        else{
            newDirectory = new File(newDirectory, oldFile.getName());
            newDirectory.mkdir();
            File[] childs = oldFile.listFiles();
            for(int i = 0; i<childs.length; i++){
                MoveFile(childs[i], newDirectory);
            }
        }
        return newDirectory;
    }
    @Override
    /**
     * @param rawPath the root directory of the unprocessed server
     */
    public void preProcess(File rawPath) {
                this.rawPath = rawPath;
        try{
            ArrayList<String> dynamicUrls = new ArrayList<String>();
            Log.i("pre process", rawPath.toString());
            long time  = System.currentTimeMillis();
            serverRoot = MoveFile(rawPath,null);
            Log.i("moved entire directory in", ((System.currentTimeMillis()-time)/1000f)+"");
            PreProcessor.process(serverRoot, dynamicUrls);
        }
        catch(Exception e){
            Log.i("exception","exception");
            e.printStackTrace();
        }
        
        
        Log.i("preprocess", "finished");
    }

	@Override
	public void saveState() {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void setRoot(File root) {
		// TODO Auto-generated method stub
		this.path = root;
	}
    


	

}
