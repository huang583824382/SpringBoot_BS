package com.example.testserver.buildDataset;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.testserver.controller.LabelController;
import org.dom4j.Element;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class COCOBuilder {
    public static String createCOCO(String folderpath) throws IOException {
        String coco_path = folderpath+"coco\\";
        String annotations_path = coco_path+"annotations\\";
        String train2017_path = coco_path+"train2017\\";
        File coco = new File(coco_path);
        if(!coco.exists()){
            coco.mkdirs();
        }
        File train2017 = new File(train2017_path);
        if(!train2017.exists()){
            train2017.mkdirs();
        }
        File taskfolder = new File(folderpath);
        File[] files = taskfolder.listFiles();
        for(File item : files){
            if(!item.isDirectory()){
                String[] temp = item.getName().split("\\.");
                if(temp[1].equals("jpg")||temp[1].equals("jpeg")||temp[1].equals("png")){ //是图片
                    File desfile = new File(train2017_path+item.getName());
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
        File annotations = new File(annotations_path);
        if(!annotations.exists()){
            annotations.mkdirs();
        }
        File instances_train2017 = new File(annotations_path+"instances_train2017.json");
        if(instances_train2017.exists()){
            instances_train2017.delete();
        }
        instances_train2017.createNewFile();
        JSONObject root = new JSONObject();
        root.put("info", new JSONObject());
        root.put("licenses", new JSONArray());
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
        JSONArray resimages = new JSONArray();
        JSONArray resannotations = new JSONArray();
        JSONArray rescategories = new JSONArray();
        for(Object i : imgs){
            JSONObject label = (JSONObject) i;
            JSONObject image = new JSONObject();
            String[]tmp = label.getString("url").split("/");
            int imageid=Integer.parseInt(tmp[tmp.length-1].split("\\.")[0]);
            image.put("file_name", tmp[tmp.length-1]);
            //读取图片长宽
            File img = new File(train2017_path+tmp[tmp.length-1]);
            BufferedImage fimage = ImageIO.read(img);
            image.put("height", fimage.getHeight());
            image.put("width", fimage.getWidth());
            image.put("id", imageid);
            resimages.add(image);
            for(Object objcir : label.getJSONArray("circle")){
                JSONObject circle = (JSONObject)objcir;
                int cid = getCategoryID(rescategories, circle.getString("label"));
                JSONObject annoitem = new JSONObject();
                JSONArray segmentation = new JSONArray();
                List<Float> points = new ArrayList<Float>();
                float x = circle.getFloatValue("x");
                float y = circle.getFloatValue("y");
                float r = circle.getFloatValue("r");
                points.add(x);
                points.add(y);
                segmentation.add(points);
                List<Float> bboxinfo = new ArrayList<Float>();
                bboxinfo.add(x-r);
                bboxinfo.add(y-r);
                bboxinfo.add(2*r);
                bboxinfo.add(2*r);
                annoitem.put("area", cirarea(r));
                annoitem.put("image_id", imageid);
                annoitem.put("category_id", cid);
                annoitem.put("iscrowd", 0);
                annoitem.put("id", circle.getIntValue("ID"));
                annoitem.put("segmentation", segmentation);
                annoitem.put("bbox", bboxinfo);
                resannotations.add(annoitem);
            }
            for(Object objrec : label.getJSONArray("rectangular")){
                JSONObject rect = (JSONObject)objrec;
                int cid = getCategoryID(rescategories, rect.getString("label"));
                JSONObject annoitem = new JSONObject();
                JSONArray segmentation = new JSONArray();
                float x = rect.getFloatValue("x");
                float y = rect.getFloatValue("y");
                float wid = rect.getFloatValue("wid");
                float hei = rect.getFloatValue("hei");
                List<Float> points = new ArrayList<Float>();
                points.add(x);
                points.add(y);
                points.add(x+wid);
                points.add(y);
                points.add(x);
                points.add(y+hei);
                points.add(x+wid);
                points.add(y+hei);
                segmentation.add(points);
                List<Float> bboxinfo = new ArrayList<Float>();
                bboxinfo.add(x);
                bboxinfo.add(y);
                bboxinfo.add(wid);
                bboxinfo.add(hei);

                annoitem.put("area", rectarea(wid, hei));
                annoitem.put("image_id", imageid);
                annoitem.put("category_id", cid);
                annoitem.put("iscrowd", 0);
                annoitem.put("id", rect.getIntValue("ID"));
                annoitem.put("segmentation", segmentation);
                annoitem.put("bbox", bboxinfo);
                resannotations.add(annoitem);
            }
            for(Object objpol : label.getJSONArray("polygon")){
                JSONObject polygon = (JSONObject)objpol;
                int cid = getCategoryID(rescategories, polygon.getString("label"));
                List<Float> points = new ArrayList<Float>();
                float minx=10000000;
                float miny=10000000;
                float maxx=0;
                float maxy=0;
                for(Object p : polygon.getJSONArray("points")){
                    float x= ((JSONObject)p).getFloatValue("X");
                    float y= ((JSONObject)p).getFloatValue("Y");
                    points.add(x);
                    points.add(y);
                    if(x<minx){
                        minx=x;
                    }
                    if(y<miny){
                        miny=y;
                    }
                    if(x>maxx){
                        maxx=x;
                    }
                    if(y>maxy){
                        maxy=y;
                    }
                }
                JSONObject annoitem = new JSONObject();
                JSONArray segmentation = new JSONArray();
                segmentation.add(points);
                List<Float> bboxinfo = new ArrayList<Float>();
                bboxinfo.add(minx);
                bboxinfo.add(miny);
                bboxinfo.add(maxx-minx);
                bboxinfo.add(maxy-miny);

                annoitem.put("area", rectarea(maxx-minx, maxy-miny));
                annoitem.put("image_id", imageid);
                annoitem.put("category_id", cid);
                annoitem.put("iscrowd", 0);
                annoitem.put("id", polygon.getIntValue("ID"));
                annoitem.put("segmentation", segmentation);
                annoitem.put("bbox", bboxinfo);
                resannotations.add(annoitem);
            }
        }
        root.put("images", resimages);
        root.put("annotations", resannotations);
        root.put("categories", rescategories);

        LabelController.savejson(instances_train2017.getPath(), root.toString());
        return "";
    }
    private static int getCategoryID(JSONArray categories, String label){
        int i=0;
        for(Object item : categories){
            JSONObject catelogy = (JSONObject) item;
            if(catelogy.getString("name").equals(label)){
                return i;
            }
            i++;
        }
        JSONObject newcate = new JSONObject();
        newcate.put("id", i);
        newcate.put("name", label);
        categories.add(newcate);
        return i;
    }

    private static float cirarea(float r){
        return (float)(Math.PI*r*r);
    }

    private static float rectarea(float wid, float hei){
        return (float)(wid*hei);
    }
}
