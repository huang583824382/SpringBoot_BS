package com.example.testserver.buildDataset;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;

public class PVOCBuilder {
    public static String createPVOC(String folderpath) throws IOException{
        //创建PVOC文件夹
        String VOC2007_path = folderpath+"VOC2007\\";
        String Annotations_path = VOC2007_path+"Annotations\\";
        String ImageSets_path = VOC2007_path+"ImageSets\\";
        String Main_path = ImageSets_path+"Main\\";
        String JPEGImages_path = VOC2007_path+"JPEGImages\\";
        File VOC2007 = new File(VOC2007_path);
        File JPEGImages = new File(JPEGImages_path);
        //拷贝图片文件
        if(!JPEGImages.exists()){
            JPEGImages.mkdirs();
        }
        File taskfolder = new File(folderpath);
        File[] files = taskfolder.listFiles();
        for(File item : files){
            if(!item.isDirectory()){
                String[] temp = item.getName().split("\\.");
                if(temp[1].equals("jpg")||temp[1].equals("jpeg")||temp[1].equals("png")){ //是图片
                    File desfile = new File(JPEGImages_path+item.getName());
                    if(desfile.exists()){
                        desfile.delete();
                    }
                    Files.copy(item.toPath(), desfile.toPath());
                }
//                else if(temp[1].equals("png")){
//                    //转换后拷贝
//                }
            }
        }
        String img_content="";
        for(File item : new File(JPEGImages_path).listFiles()){
            img_content+=item.getName().split("\\.")[0]+" 1\n";
        }
        //Main生成
        File Main = new File(Main_path);
        if(!Main.exists()){
            Main.mkdirs();
        }
        File train = new File(Main_path+"train.txt");
        if (!train.exists())
        {
            train.createNewFile();
        }
        System.out.println(img_content);
        FileWriter fw = new FileWriter(Main_path+"train.txt");
        fw.write(img_content);
        fw.close();
        //xml生成
        FileInputStream fis = new FileInputStream(folderpath+"label.json");
        InputStreamReader inputStreamReader = new InputStreamReader(fis, "UTF-8");
        BufferedReader in  = new BufferedReader(inputStreamReader);
        StringBuffer strbuffer = new StringBuffer();
        String str;
        while ((str = in.readLine()) != null) {
            strbuffer.append(str);  //new String(str,"UTF-8")
        }
        in.close();
        JSONArray imgs =JSONArray.parseArray(strbuffer.toString().trim());
        System.out.println("json: "+imgs.toString());
        for(Object item : imgs){
            JSONObject label = (JSONObject) item;
            Document document = DocumentHelper.createDocument();
            Element annotation = document.addElement("annotation");
            Element folder = annotation.addElement("folder");
            folder.setText("VOC2007");
            Element filename = annotation.addElement("filename");
            String[]tmp = label.getString("url").split("/");
            filename.setText(tmp[tmp.length-1]);
            Element source = annotation.addElement("source");
            Element database = source.addElement("database");
            database.setText("Unknown");
            //设置图片size
            Element size = annotation.addElement("size");
            Element width = size.addElement("width");
            Element height = size.addElement("height");
            Element depth = size.addElement("depth");
            File img = new File(JPEGImages_path+tmp[tmp.length-1]);
            System.out.println(img.getName());
            BufferedImage image = ImageIO.read(img);
            width.setText(""+image.getWidth());
            height.setText(""+image.getHeight());
            depth.setText("3");
            Element segmented = annotation.addElement("segmented");
            segmented.setText("0");
            for(Object objcir : label.getJSONArray("circle")){
                JSONObject circle = (JSONObject)objcir;
                Element object = annotation.addElement("object");
                Element name = object.addElement("name");
                name.setText(circle.getString("label"));
                object.addElement("pose").setText("Unspecified");
                object.addElement("truncated").setText("0");
                object.addElement("difficult").setText("0");
                Element bndbox = object.addElement("bndbox");
                float x=Float.parseFloat(circle.getString("x"));
                float y=Float.parseFloat(circle.getString("y"));
                float r=Float.parseFloat(circle.getString("r"));
                bndbox.addElement("xmin").setText(""+(x-r));
                bndbox.addElement("ymin").setText(""+(y-r));
                bndbox.addElement("xmax").setText(""+(x+r));
                bndbox.addElement("ymax").setText(""+(y+r));
            }
            for(Object objrec : label.getJSONArray("rectangular")){
                JSONObject rect = (JSONObject)objrec;
                Element object = annotation.addElement("object");
                Element name = object.addElement("name");
                name.setText(rect.getString("label"));
                object.addElement("pose").setText("Unspecified");
                object.addElement("truncated").setText("0");
                object.addElement("difficult").setText("0");
                Element bndbox = object.addElement("bndbox");
                float x=Float.parseFloat(rect.getString("x"));
                float y=Float.parseFloat(rect.getString("y"));
                float wid=Float.parseFloat(rect.getString("wid"));
                float hei=Float.parseFloat(rect.getString("hei"));
                bndbox.addElement("xmin").setText(""+x);
                bndbox.addElement("ymin").setText(""+y);
                bndbox.addElement("xmax").setText(""+(x+wid));
                bndbox.addElement("ymax").setText(""+(y+hei));
            }
            for(Object objpol : label.getJSONArray("polygon")){
                JSONObject polygon = (JSONObject)objpol;
                Element object = annotation.addElement("object");
                Element name = object.addElement("name");
                name.setText(polygon.getString("label"));
                object.addElement("pose").setText("Unspecified");
                object.addElement("truncated").setText("0");
                object.addElement("difficult").setText("0");
                Element polygonel = object.addElement("polygon");
                int num=1;
                for(Object p : polygon.getJSONArray("points")){
                    System.out.println(num);
                    JSONObject points = (JSONObject) p;
                    float x=Float.parseFloat(points.getString("X"));
                    float y=Float.parseFloat(points.getString("Y"));
                    polygonel.addElement("x"+num).setText(""+x);
                    polygonel.addElement("y"+num).setText(""+y);
                    num++;
                }
            }
            //写xml
            File Annotations = new File(Annotations_path);
            if(!Annotations.exists()){
                Annotations.mkdirs();
            }
            File xmlfile = new File(Annotations_path+tmp[tmp.length-1].split("\\.")[0]+".xml");
            if(xmlfile.exists()){
                xmlfile.delete();
            }
            xmlfile.createNewFile();
            OutputFormat format = OutputFormat.createPrettyPrint();
            // 设置编码格式
            format.setEncoding("UTF-8");
            XMLWriter writer = new XMLWriter(new FileOutputStream(xmlfile), format);
            // 设置是否转义，默认使用转义字符
            writer.setEscapeText(false);
            writer.write(document);
            writer.close();
            System.out.println("生成xml成功");
        }
        return "";
    }

}
