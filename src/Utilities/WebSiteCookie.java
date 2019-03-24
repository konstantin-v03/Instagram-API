package Utilities;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchCookieException;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WebSiteCookie {
    public static Set<Cookie> readCookiesFromFile(String fileName) throws NoSuchCookieException{
        File file = new File(fileName);
        if(!file.exists()) throw new NoSuchCookieException("File with cookies not found");

        Set<Cookie> cookies = new LinkedHashSet<>();
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String strLine;

            while ((strLine = bufferedReader.readLine()) != null) {
                StringTokenizer token = new StringTokenizer(strLine, ";");
                while (token.hasMoreTokens()) {
                    String name = token.nextToken();
                    String value = token.nextToken();
                    String domain = token.nextToken();
                    String path = token.nextToken();
                    Date date = null;

                    String str;
                    if (!(str = token.nextToken()).equals("null")) {
                        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                        try {
                            date = df.parse(str);
                        }catch (ParseException ex){
                            ex.printStackTrace();
                        }
                    }

                    boolean isSecure = Boolean.parseBoolean(token.nextToken());
                    cookies.add(new Cookie(name, value, domain, path, date, isSecure));
                }
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }finally {
            try{
                fileReader.close();
                bufferedReader.close();
            }catch (NullPointerException|IOException ex){
                ex.printStackTrace();
            }
        }
        return cookies;
    }

    public static void writeCookiesToFile(Set<Cookie> cookies, String fileName){
        BufferedWriter bufferedWriter = null;
        try {
            File file = new File(fileName);
            if(!file.exists()) file.createNewFile();
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            for (Cookie cookie : cookies) {
                bufferedWriter.write((cookie.getName() + ";" + cookie.getValue() + ";" + cookie.getDomain() + ";" + cookie.getPath() + ";" + cookie.getExpiry() + ";" + cookie.isSecure()));
                bufferedWriter.newLine();
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }finally {
            try{
                bufferedWriter.flush();
                bufferedWriter.close();
            }catch (NullPointerException|IOException ex){
                ex.printStackTrace();
            }
        }
    }
}
