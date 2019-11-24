package fr.free.nrw.commons.db;

import android.net.Uri;
import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fr.free.nrw.commons.location.LatLng;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Converters {

    @TypeConverter
    public static String uriToString(Uri uri) {
        if(null==uri){
            return null;
        }
        return uri.toString();
    }

    @TypeConverter
    public static Uri stringToUri(String uriPath) {
        if(null==uriPath){
            return null;
        }
        return Uri.parse(uriPath);
    }

    @TypeConverter
    public static long dateToTimestamp(Date date){
        return date==null?0:date.getTime();
    }

    @TypeConverter
    public static Date timeStampToDate(long timeStamp){
        return new Date(timeStamp);
    }

    @TypeConverter
    public static ArrayList<String> jsonToList(String data) {
        Gson gson = new Gson();
        if (data == null) {
            return new ArrayList<>();
        }
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String listToJson(List<String> myObjects) {
        Gson gson = new Gson();
        return gson.toJson(myObjects);
    }

    @TypeConverter
    public static Map<String,String> stringToMapStringString(String data) {
        Gson gson = new Gson();
        if (data == null) {
            return new HashMap<>();
        }
        Type listType = new TypeToken<Map<String,String>>() {}.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String mapStringStringToJson(Map<String,String> myObjects) {
        Gson gson = new Gson();
        return gson.toJson(myObjects);
    }

    @TypeConverter
    public static Map<String,Object> stringToMapStringObject(String data) {
        Gson gson = new Gson();
        if (data == null) {
            return new HashMap<>();
        }
        Type listType = new TypeToken<Map<String,Object>>() {}.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String mapStringObjectToJson(Map<String,Object> myObjects) {
        Gson gson = new Gson();
        return gson.toJson(myObjects);
    }

    @TypeConverter
    public static LatLng jsonToLatLnt(String data) {
        Gson gson = new Gson();
        Type listType = new TypeToken<Map<String,Object>>() {}.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String latLngToJson(LatLng myObjects) {
        Gson gson = new Gson();
        return gson.toJson(myObjects);
    }


}
