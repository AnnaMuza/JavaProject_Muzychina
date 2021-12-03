package tests;

import src.WebBot;


public class Tests extends WebBot {
    public static String FILENAME1 = "JavaProject_Muzychina/tests/test1_input.json";
    public static String FILENAME1_OUT = "JavaProject_Muzychina/tests/test1_output.json";

    public static String FILENAME2 = "JavaProject_Muzychina/tests/test2_input.json";
    public static String FILENAME2_OUT = "JavaProject_Muzychina/tests/test2_output.json";

    public static String FILENAME3 = "JavaProject_Muzychina/tests/test3_input.json";
    public static String FILENAME3_OUT = "JavaProject_Muzychina/tests/test3_output.json";

    public static long delay = 10;


    public static void test1() {
        String[] urls = get_urls(FILENAME1);
        String[] tags = get_tags(FILENAME1);

        run_main(urls, tags, FILENAME1_OUT);
        // run_timer(urls, tags, delay);
    }


    public static void test2() {
        String[] urls = get_urls(FILENAME2);
        String[] tags = get_tags(FILENAME2);

        run_main(urls, tags, FILENAME2_OUT);
        // run_timer(urls, tags, delay);
    }


    public static void test3() {
        String[] urls = get_urls(FILENAME3);
        String[] tags = get_tags(FILENAME3);

        run_main(urls, tags, FILENAME3_OUT);
        // run_timer(urls, tags, delay);
    }


    public static void main(String[] args) {
        test1();
        test2();
        test3();
    }
}