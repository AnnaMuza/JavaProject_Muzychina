/**
 * Project 12: Web Bot 
 * Done by Anna Muzychina (course 3, group comp. mat. 2)
 * 2021
 */

package src;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Timer;
import java.util.TimerTask;


public class WebBot {
    public static String FILENAME = "JavaProject_Muzychina/resources/data_input.json";
    public static String FILENAME_OUT = "JavaProject_Muzychina/resources/data_output.json";
    public static String MEDIA_OUT = "JavaProject_Muzychina/resources/media_output/";
    public static String[] EXTENSIONS = {".png", ".jpg", ".jpeg", ".pdf", ".doc", ".docx", ".mp3", ".mp4", ".gif", ".svg"};

    
    /** 
     * считывает ссылки на сайты из filename.json
     * @param filename
     * @return String[]
     */
    public static String[] get_urls(String filename) {
        try {
            String s = new String(Files.readAllBytes(Paths.get(filename)));
            JSONObject jo = new JSONObject(s);
            JSONArray arr = (JSONArray) jo.getJSONArray("urls");
            final int n = arr.length();
            String[] urls = new String[n];

            for (int i = 0; i < n; i++) urls[i] = arr.get(i).toString();
            return urls;

        } catch (Exception e) {
            e.getMessage();
            return new String[1];
        }
    }

    
    /** 
     * считывает HTML теги из filename.json
     * @param filename
     * @return String[]
     */
    public static String[] get_tags(String filename) {
        try {
            String s = new String(Files.readAllBytes(Paths.get(filename)));
            JSONObject jo = new JSONObject(s);
            JSONArray arr = (JSONArray) jo.getJSONArray("tags");
            final int n = arr.length();
            String[] tags = new String[n];

            for (int i = 0; i < n; i++) tags[i] = arr.get(i).toString();
            return tags;

        } catch (Exception e) {
            e.getMessage();
            return new String[1];
        }
    }

    
    /** 
     * возвращает исходный код страницы
     * @param url
     * @return Document
     * @throws IOException
     */
    public static Document get_html(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    
    /** 
     * возвращает текст внутри парных тегов или значения аттрибутов src/href
     * для изображений/ссылок на файлы
     * @param html
     * @param tag
     * @return JSONArray
     */
    public static JSONArray get_tag_value(Document html, String tag) {
        Elements els = html.getElementsByTag(tag);
        JSONArray arr = new JSONArray();
        String val;

        switch (tag) {
            case "img": for (Element el : els) {
                val = el.attr("abs:src");
                if (val != "") {
                    download_file(val);
                    arr.put(val);
                }
            }
            case "a": for (Element el : els) {
                val = el.attr("abs:href");
                if (val != "") {
                    download_file(val);
                    arr.put(val);
                }
            } 
            default: for (Element el : els) {
                val = el.text();
                if (val != "") arr.put(val);
            }
        }

        return arr;
    }

    
    /** 
     * скачивает файл по ссылке
     * @param url
     */
    private static void download_file(String url) {
        Pattern pattern = Pattern.compile("\\.\\w{3,4}$");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find() && Arrays.asList(EXTENSIONS).contains(matcher.group())) {
            String strImageName = url.substring(url.lastIndexOf("/") + 1);
            
            try {
                URL urlImage = new URL(url);
                InputStream in = urlImage.openStream();
                byte[] buffer = new byte[4096];
                int n = -1;
                
                OutputStream os = new FileOutputStream(MEDIA_OUT + strImageName);
                while ((n = in.read(buffer)) != -1) os.write(buffer, 0, n);

                os.close();
            } catch (IOException e) {
                System.out.println("Can't download file " + strImageName);
            }
        }
    }

    
    /** 
     * парсит страницу по списку тегов
     * @param url
     * @param tags
     * @param out
     */
    public static void parse_site(String url, String[] tags, JSONObject out) {
        JSONObject url_data = new JSONObject();

        try {
            Document html = get_html(url);
            JSONArray tag_values;
            url_data.put("error", false);

            for (String tag : tags) {
                tag_values = get_tag_value(html, tag);
                url_data.put(tag, tag_values);
            }
            out.put(url, url_data);

        } catch (IOException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
            url_data.put("error", e.getMessage());
            out.put(url, url_data);
        }
    }

    
    /** 
     * записывает полученные из parse_site данные в filename_out.json
     * @param out
     * @param filename_out
     */
    public static void write_output(JSONObject out, String filename_out) {
        try (FileWriter file = new FileWriter(filename_out)) {
            file.write(out.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    /** 
     * полный алгоритм обработки веб-страниц (без таймера)
     * @param urls
     * @param tags
     */
    public static void run_main(String[] urls, String[] tags, String filename_out) {
        JSONObject out = new JSONObject();

        for (String url : urls) if (url != null) {
            System.out.println("Site " + url + " in processing");
            parse_site(url, tags, out);
            System.out.println("Processed");
        }

        write_output(out, filename_out);
    }

    
    /** 
     * полный алгоритм обработки веб-страниц (с таймером)
     * @param urls
     * @param tags
     * @param delay
     */
    public static void run_timer(String[] urls, String[] tags, String filename_out, long delay) {
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Session started");
                run_main(urls, tags, filename_out);
                System.out.println("Session finished\n");
            }
        }, delay*1000, 10000);
    }


    /** 
     * @param args
     */
    public static void main(String[] args) {
        String[] urls = get_urls(FILENAME);
        String[] tags = get_tags(FILENAME);
        long delay = 20;

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter 1 if you want to read data from files and 2 for manual input: ");
        int type = Integer.parseInt(sc.nextLine());

        if (type == 2) {
            System.out.print("Enter number of sites: ");
            final int n = Integer.parseInt(sc.nextLine());
            urls = new String[n];

            System.out.print("Enter number of tags: ");
            final int m = Integer.parseInt(sc.nextLine());
            tags = new String[m];

            System.out.println("Enter urls:");
            for (int i = 0; i < n; i++) urls[i] = sc.nextLine();

            System.out.println("Enter tags:");
            for (int i = 0; i < m; i++) tags[i] = sc.nextLine();
        }
        sc.close();

        run_main(urls, tags, FILENAME_OUT);
        run_timer(urls, tags, FILENAME_OUT, delay);
    }
}