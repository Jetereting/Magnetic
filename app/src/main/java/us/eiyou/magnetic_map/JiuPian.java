package us.eiyou.magnetic_map;

import java.lang.reflect.Method;

/**
 * Created by Au on 2016/3/17.
 */
public class JiuPian {
    public static String getjiupian(double lo, double la) {
        return Http.Result("http://api.map.baidu.com/ag/coord/convert?from=0&to=4&x=" + lo + "&y=" + la, null);
    }

    public static String[] jiupian(double lo, double la) throws Exception {
        String re[] = new String[2];
        String result[] = getjiupian(lo, la).split(":\"");
        String x=(result[1].substring(0,result[1].length()-5));
        String y=(result[2].substring(0,result[2].length()-2));
        re[0]=decodeBase64(x);
        re[1]=decodeBase64(y);
        return re;
    }
    public static String decodeBase64(String input) throws Exception{
        Class clazz=Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
        Method mainMethod= clazz.getMethod("decode", String.class);
        mainMethod.setAccessible(true);
        Object retObj=mainMethod.invoke(null, input);
        String result=new String((byte[])retObj);
        return result;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(jiupian(112, 22)[1]);

    }
}
