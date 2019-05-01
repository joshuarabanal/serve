/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package template;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Joshua
 */
public class Builder {
    public static final String id="id", innerHTML = "innerHTML", clase = "class";
    
    private FileOutputStream out;
    private ArrayList<String> attrs = new ArrayList<String>();
    
    public Builder(File output) throws FileNotFoundException, IOException{
        if(!output.getName().contains(".template")){
            throw new NullPointerException("template file must be of extension '.template'");
        }
        out = new FileOutputStream(output);
        out.write(("{").getBytes());
        out.write(("\n \"elements\":[\n").getBytes());
        
    }
    
    void addAttribute(String name, String val) {
        attrs.add("\""+name+"\":\""+val+"\"");
    }
    
    int itemCount = 0;
    /**
     *  using: write("id", "mama", "innerHTML", "papa") 
     * <br/> on "<div id=\"mama\"> lala</div><div id=\"papa\"> dada</div>"
     * <br/> creates  "<div id=\"mama\"> papa</div><div id=\"papa\"> dada</div>"
     * @param searchAttributeType the attribute to use to find 
     * @param identifierAttributeValue the value of the attribute to find
     * @param replacementAttribute what attribute to replace
     * @param replacementAttributeValue the new value of the attribute
     */
    public void writeTemplateItem(String idValue, String replacementAttribute, String replacementAttributeValue) throws IOException{
        if(itemCount>0){
            out.write(',');
        }
        itemCount++;
        out.write(
                (
                        "{"+
                        "\"searchAttr\":\"id\""+
                        ", \"searchAttrValue\":\""+idValue+
                        "\", \"replacementAttr\":\""+replacementAttribute+
                        "\", \"replacementAttrVal\":\""+replacementAttributeValue+
                        "\"}\n"
                ).getBytes()
        );
        
    }
    public void close() throws IOException{
        out.write(("]\n").getBytes());
        for(int i = 0; i<attrs.size(); i++){
            out.write(',');
            out.write(attrs.get(i).getBytes());
        }
        out.write(("\n}").getBytes());
        out.close();
    }

    
    
}
