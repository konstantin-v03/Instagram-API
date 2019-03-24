package Wrappers;

import WebSiteDrivers.SeleniumInstagramDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.volvenkov.Main.*;

public class SeleniumInstagramWrapper extends InstagramWrapper {

    private static final String TEMP_FILE_NAME = "temp.txt";

    private static final String FOLLOWING_FILE_PATH = "assets/following.txt";
    private static final String UNFOLLOWING_FILE_PATH = "assets/unfollowing.txt";
    private static final String POSTING_FILE_PATH = "assets/posting.txt";

    private static final Random random = new Random();

    private int FOLLOWING_PER_DAY = 100;
    private int FOLLOWING_ONE_TIME = 2;
    private int MAX_FOLLOWING_OFFSET = 10;
    private int MINUTES_BETWEEN_FOLLOW = (24 * 60) / (FOLLOWING_PER_DAY / FOLLOWING_ONE_TIME) + 10;
    private int MAX_MINUTES_BETWEEN_FOLLOW = MINUTES_BETWEEN_FOLLOW + 10;
    private int MIN_MINUTES_BETWEEN_FOLLOW = MINUTES_BETWEEN_FOLLOW - 10;

    private int UNFOLLOWING_PER_DAY = 100;
    private int UNFOLLOWING_ONE_TIME = 2;
    private int MAX_UNFOLLOWING_OFFSET = 10;
    private int MINUTES_BETWEEN_UNFOLLOW = (24 * 60) / (UNFOLLOWING_PER_DAY / UNFOLLOWING_ONE_TIME) + 10;
    private int MAX_MINUTES_BETWEEN_UNFOLLOW = MINUTES_BETWEEN_UNFOLLOW + 10;
    private int MIN_MINUTES_BETWEEN_UNFOLLOW = MINUTES_BETWEEN_UNFOLLOW - 10;

    private int POSTING_PER_DAY = 4;
    private int POSTING_ONE_TIME = 1;
    private int MAX_POSTING_OFFSET = 1;
    private int MINUTES_BETWEEN_POST = (24 * 60) / (POSTING_PER_DAY / POSTING_ONE_TIME) + 10;
    private int MAX_MINUTES_BETWEEN_POST = MINUTES_BETWEEN_POST + 10;
    private int MIN_MINUTES_BETWEEN_POST = MINUTES_BETWEEN_POST - 10;

    private String username;
    private String password;

    private int followingOffSet;
    private int unFollowingOffSet;
    private int postOffSet;

    private Thread followingThread;
    private Thread unFollowingThread;
    private Thread postingThread;

    public SeleniumInstagramWrapper(String username , String password) {
        this.username = username;
        this.password = password;
    }

    public synchronized boolean verifyData(){
        seleniumInstagramDriver = createSeleniumInstagramDriver();
        boolean value;

        if(seleniumInstagramDriver.loginUsingCookie(username) || seleniumInstagramDriver.login(username, password))
            value = true;
        else value = false;

        seleniumInstagramDriver.close();
        return value;
    }

    @Override
    public synchronized void clearFollowingFile() {
        if(clearFileContent(FOLLOWING_FILE_PATH))
            System.out.println(FOLLOWING_FILE_PATH + " cleared");
        else System.err.println(FOLLOWING_FILE_PATH + " not cleared");
    }

    @Override
    public synchronized void clearUnFollowingFile() {
        if(clearFileContent(UNFOLLOWING_FILE_PATH))
            System.out.println(UNFOLLOWING_FILE_PATH + " cleared");
        else System.err.println(UNFOLLOWING_FILE_PATH + " not cleared");
    }

    @Override
    public synchronized void clearPostingFile() {
        if(clearFileContent(POSTING_FILE_PATH))
            System.out.println(POSTING_FILE_PATH + " cleared");
        else System.err.println(POSTING_FILE_PATH + " not cleared");
    }

    @Override
    public void startFollowing() {
        if(followingThread != null) return;
        followingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Following started");
                while (!Thread.interrupted()) {
                    seleniumInstagramDriver = SeleniumInstagramWrapper.this.createSeleniumInstagramDriver();
                    if (seleniumInstagramDriver.loginUsingCookie(username) || seleniumInstagramDriver.login(username, password))
                        for (int i = 0; i < FOLLOWING_ONE_TIME; i++) {
                            SeleniumInstagramWrapper.this.follow();
                            delay(DELAY_BETWEEN_TWO_ACTION);
                        }
                    seleniumInstagramDriver.close();
                    int timeOut = (random.nextInt(MAX_MINUTES_BETWEEN_FOLLOW - MIN_MINUTES_BETWEEN_FOLLOW) + MIN_MINUTES_BETWEEN_FOLLOW);
                    System.out.println("Next follow in " + timeOut + " minutes");
                    try {
                        TimeUnit.MINUTES.sleep(timeOut);
                    } catch (InterruptedException ex) {
                        break;
                    }
                }
                System.out.println("Following stopped");
            }
        });
        followingThread.start();
    }

    @Override
    public void stopFollowing() {
        if(followingThread != null) {
            followingThread.interrupt();
            clearFileByOffSet(FOLLOWING_FILE_PATH, followingOffSet);
            followingThread = null;
        }
    }

    @Override
    public void startUnFollowing() {
        if(unFollowingThread != null) return;
        unFollowingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Unfollowing started");
                while (!Thread.interrupted()) {
                    seleniumInstagramDriver = SeleniumInstagramWrapper.this.createSeleniumInstagramDriver();
                    if (seleniumInstagramDriver.loginUsingCookie(username) || seleniumInstagramDriver.login(username, password))
                        for (int i = 0; i < UNFOLLOWING_ONE_TIME; i++) SeleniumInstagramWrapper.this.unFollow();
                    seleniumInstagramDriver.close();
                    int timeOut = (random.nextInt(MAX_MINUTES_BETWEEN_UNFOLLOW - MIN_MINUTES_BETWEEN_UNFOLLOW) + MIN_MINUTES_BETWEEN_UNFOLLOW);
                    System.out.println("Next unFollow in " + timeOut + " minutes");
                    try {

                        TimeUnit.MINUTES.sleep(timeOut);
                    } catch (InterruptedException ex) {
                        break;
                    }
                }
                System.out.println("Unfollowing stopped");
            }
        });
        unFollowingThread.start();
    }

    @Override
    public void stopUnFollowing() {
        if(unFollowingThread != null) {
            unFollowingThread.interrupt();
            clearFileByOffSet(UNFOLLOWING_FILE_PATH, unFollowingOffSet);
            unFollowingThread = null;
        }
    }

    @Override
    public void startPosting() {
        if(postingThread != null) return;
        postingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    seleniumInstagramDriver = SeleniumInstagramWrapper.this.createSeleniumInstagramDriver();
                    if (seleniumInstagramDriver.loginUsingCookie(username) || seleniumInstagramDriver.login(username, password))
                        for (int i = 0; i < POSTING_ONE_TIME; i++) SeleniumInstagramWrapper.this.addImagePost();
                    seleniumInstagramDriver.close();
                    int timeOut = (random.nextInt(MAX_MINUTES_BETWEEN_POST - MIN_MINUTES_BETWEEN_POST) + MIN_MINUTES_BETWEEN_POST);
                    System.out.println("Next post in " + timeOut + " minutes");
                    try {
                        TimeUnit.MINUTES.sleep(timeOut);
                    } catch (InterruptedException ex) {
                        break;
                    }
                }
            }
        });
        postingThread.start();
    }

    @Override
    public void stopPosting() {
        if(postingThread != null) {
            postingThread.interrupt();
            clearFileByOffSet(POSTING_FILE_PATH, postOffSet);
            postingThread = null;
        }
    }

    @Override
    public synchronized void addFollowingToFollowingFile(String username, String amountStr) {
        int amount = Integer.parseInt(amountStr);

        seleniumInstagramDriver = createSeleniumInstagramDriver();

        if(!seleniumInstagramDriver.loginUsingCookie(this.username)) seleniumInstagramDriver.login(this.username, password);

        delay(DELAY_BETWEEN_TWO_ACTION);

        ArrayList<String> arrayList = new ArrayList<>(seleniumInstagramDriver.getFollowers("/" + username + "/", amount));

        writeListToFile(arrayList, FOLLOWING_FILE_PATH, amount);

        System.out.println(arrayList.size() + " following from " + username + " added");

        seleniumInstagramDriver.close();
    }

    @Override
    public synchronized void addFollowersToFollowingFile(String username, String amountStr) {
        int amount = Integer.parseInt(amountStr);

        seleniumInstagramDriver = createSeleniumInstagramDriver();

        if(!seleniumInstagramDriver.loginUsingCookie(this.username)) seleniumInstagramDriver.login(this.username, password);

        delay(DELAY_BETWEEN_TWO_ACTION);

        ArrayList<String> arrayList = new ArrayList<>(seleniumInstagramDriver.getFollowers("/" + username + "/", amount));

        writeListToFile(arrayList, FOLLOWING_FILE_PATH,  amount);

        System.out.println(arrayList.size() + " followers from " + username + " added");

        seleniumInstagramDriver.close();
    }

    @Override
    public synchronized void addFollowingToUnFollowingFile(String username, String amountStr) {
        int amount = Integer.parseInt(amountStr);

        seleniumInstagramDriver = createSeleniumInstagramDriver();

        if(!seleniumInstagramDriver.loginUsingCookie(this.username)) seleniumInstagramDriver.login(this.username, password);

        delay(DELAY_BETWEEN_TWO_ACTION);

        ArrayList<String> arrayList = new ArrayList<>(seleniumInstagramDriver.getFollowing("/" + username + "/", amount));

        writeListToFile(arrayList, UNFOLLOWING_FILE_PATH, amount);

        System.out.println(arrayList.size() + " unFollowing from " + username + " added");

        seleniumInstagramDriver.close();
    }

    @Override
    public synchronized void addPostToPostFile(String internalUrl, String caption) {
        FileWriter fileWriter = null;
        try{
            fileWriter = new FileWriter(new File(POSTING_FILE_PATH), true);
            fileWriter.write(internalUrl + " " + "null" + "\n");
        }catch (IOException ex){
            ex.printStackTrace();
        }finally {
            try{
                fileWriter.close();
            }catch (NullPointerException|IOException ex){
                ex.printStackTrace();
            }
        }
        System.out.println("Post added: \nInternalUrl: " + internalUrl + "\nCaption: " + caption);
    }

    @Override
    public synchronized void copyImagePost(String internalUrl, String caption) {
        seleniumInstagramDriver = createSeleniumInstagramDriver();

        if(!seleniumInstagramDriver.loginUsingCookie(username)) seleniumInstagramDriver.login(username, password);

        delay(DELAY_BETWEEN_TWO_ACTION);

        caption = (caption.equals("null")) ? "" : caption;

        seleniumInstagramDriver.copyImagePost(internalUrl, caption);

        System.out.println("Copied image: " + internalUrl + "\nCaption: " + caption);

        seleniumInstagramDriver.close();
    }

    @Override
    public synchronized void addComment(String internalUrl, String comment){
        seleniumInstagramDriver = createSeleniumInstagramDriver();

        if(!seleniumInstagramDriver.loginUsingCookie(username)) seleniumInstagramDriver.login(username, password);

        seleniumInstagramDriver.addComment(internalUrl, comment);

        seleniumInstagramDriver.close();
    }

    @Override
    public synchronized void setFollowingSettings(String FOLLOWING_PER_DAY, String FOLLOWING_ONE_TIME, String MAX_FOLLOWING_OFFSET){
        this.FOLLOWING_PER_DAY = Integer.parseInt(FOLLOWING_PER_DAY);
        this.FOLLOWING_ONE_TIME = Integer.parseInt(FOLLOWING_ONE_TIME);
        this.MAX_FOLLOWING_OFFSET = Integer.parseInt(MAX_FOLLOWING_OFFSET);

        MINUTES_BETWEEN_FOLLOW = (24 * 60) / (this.FOLLOWING_PER_DAY / this.FOLLOWING_ONE_TIME) + 10;
        MAX_MINUTES_BETWEEN_FOLLOW = MINUTES_BETWEEN_FOLLOW + 10;
        MIN_MINUTES_BETWEEN_FOLLOW = MINUTES_BETWEEN_FOLLOW - 10;

        System.out.println("Set: " +
                "\nFollowing per day: " + FOLLOWING_PER_DAY +
                "\nFollowing one time: " + FOLLOWING_ONE_TIME +
                "\nMax following offset: " + MAX_FOLLOWING_OFFSET);
    }

    @Override
    public synchronized void setUnFollowingSettings(String UNFOLLOWING_PER_DAY, String UNFOLLOWING_ONE_TIME, String MAX_UNFOLLOWING_OFFSET){
        this.UNFOLLOWING_PER_DAY = Integer.parseInt(UNFOLLOWING_PER_DAY);
        this.UNFOLLOWING_ONE_TIME = Integer.parseInt(UNFOLLOWING_ONE_TIME);
        this.MAX_UNFOLLOWING_OFFSET = Integer.parseInt(MAX_UNFOLLOWING_OFFSET);

        MINUTES_BETWEEN_UNFOLLOW = (24 * 60) / (this.UNFOLLOWING_PER_DAY / this.UNFOLLOWING_ONE_TIME) + 10;
        MAX_MINUTES_BETWEEN_UNFOLLOW = MINUTES_BETWEEN_UNFOLLOW + 10;
        MIN_MINUTES_BETWEEN_UNFOLLOW = MINUTES_BETWEEN_UNFOLLOW - 10;

        System.out.println("Set: " +
                "\nUnfollowing per day: " + UNFOLLOWING_PER_DAY +
                "\nUnfollowing one time: " + UNFOLLOWING_ONE_TIME +
                "\nMax unfollowing offset: " + MAX_UNFOLLOWING_OFFSET);
    }

    @Override
    public synchronized void setPostingSettings(String POSTING_PER_DAY, String POSTING_ONE_TIME, String MAX_POSTING_OFFSET){
        this.POSTING_PER_DAY = Integer.parseInt(POSTING_PER_DAY);
        this.POSTING_ONE_TIME = Integer.parseInt(POSTING_ONE_TIME);
        this.MAX_POSTING_OFFSET = Integer.parseInt(MAX_POSTING_OFFSET);

        MINUTES_BETWEEN_POST = (24 * 60) / (this.POSTING_PER_DAY / this.POSTING_ONE_TIME) + 10;
        MAX_MINUTES_BETWEEN_POST = MINUTES_BETWEEN_POST + 10;
        MIN_MINUTES_BETWEEN_POST = MINUTES_BETWEEN_POST - 10;

        System.out.println("Set: " +
                "\nPosting per day: " + POSTING_PER_DAY +
                "\nPosting one time: " + POSTING_ONE_TIME +
                "\nMax Posting offset: " + MAX_POSTING_OFFSET);
    }

    private synchronized void follow() {
        if(followingOffSet >= MAX_FOLLOWING_OFFSET) {
            clearFileByOffSet(FOLLOWING_FILE_PATH, followingOffSet);
            followingOffSet = 0;
        }

        File file = new File(FOLLOWING_FILE_PATH);
        if(!file.exists()) return;

        Scanner scanner = null;
        String str;
        int count = 0;

        try{
            scanner = new Scanner(file);
            while(scanner.hasNext() && (count + 1) <= followingOffSet) {
                scanner.next();
                count++;
            }
            if(scanner.hasNext()) {
                delay(DELAY_BETWEEN_TWO_ACTION);
                str = scanner.next();
                if(seleniumInstagramDriver.follow(str)) System.out.println("Followed: " + str + "\nTime: " + new Date());
                else System.err.println("Followed failed: " + str + "\nTime: " + new Date());
                followingOffSet++;
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
        finally {
            try {
                scanner.close();
            }catch (NullPointerException ex){

            }
        }

        delay(DELAY_BETWEEN_TWO_ACTION);
    }

    private synchronized void unFollow() {
        if(unFollowingOffSet >= MAX_UNFOLLOWING_OFFSET) {
            clearFileByOffSet(UNFOLLOWING_FILE_PATH, unFollowingOffSet);
            unFollowingOffSet = 0;
        }

        File file = new File(UNFOLLOWING_FILE_PATH);
        if(!file.exists()) return;

        Scanner scanner = null;
        String str;
        int count = 0;

        try{
            scanner = new Scanner(file);
            while(scanner.hasNext() && (count + 1) <= unFollowingOffSet) {
                scanner.next();
                count++;
            }
            if(scanner.hasNext()) {
                delay(DELAY_BETWEEN_TWO_ACTION);
                str = scanner.next();
                if(seleniumInstagramDriver.unFollow(str)) System.out.println("UnFollowed: " + str + "\nTime: " + new Date());
                else System.err.println("UnFollowed failed: " + str + "\nTime: " + new Date());
                unFollowingOffSet++;
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
        finally {
            try {
                scanner.close();
            }catch (NullPointerException ex){

            }
        }
    }

    private synchronized void addImagePost() {
        if(postOffSet >= MAX_POSTING_OFFSET) {
            clearFileByOffSet(POSTING_FILE_PATH, postOffSet);
            postOffSet = 0;
        }

        File file = new File(POSTING_FILE_PATH);
        if(!file.exists()) return;

        Scanner scanner = null;
        String str1, str2;
        int count = 0;

        try{
            scanner = new Scanner(file);
            while(scanner.hasNext() && (count + 1) <= postOffSet) {
                scanner.nextLine();
                count++;
            }
            if(scanner.hasNext()) {
                delay(DELAY_BETWEEN_TWO_ACTION);
                str1 = scanner.next();
                str2 = (str2 = scanner.next()).equals("null") ? "" : str2;
                if(seleniumInstagramDriver.copyImagePost(str1, str2))
                    System.out.println("Added: " + str1 + "\nCaption: " + str2 + "\nTime: " + new Date());
                else System.err.println("Added failed: " + str1 + "\nCaption: " + str2 + "\nTime: " + new Date());
                postOffSet++;
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
        finally {
            try {
                scanner.close();
            }catch (NullPointerException ex){

            }
        }

        delay(DELAY_BETWEEN_TWO_ACTION);
    }

    private synchronized boolean clearFileContent(String fileName){
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(fileName));
            fileWriter.write("");
            return true;
        }catch (IOException ex){
            ex.printStackTrace();
            return false;
        }finally {
            try{
                fileWriter.close();
            }catch (NullPointerException|IOException ex){
                ex.printStackTrace();
            }
        }
    }

    private synchronized void clearFileByOffSet(String fileName, int offSet){
        Scanner scanner = null;
        FileWriter fileWriter = null;

        File file1 = new File(fileName);
        File file2 = new File(TEMP_FILE_NAME);

        int count = 0;

        try{
            file2.createNewFile();
            scanner = new Scanner(file1);
            fileWriter = new FileWriter(file2);

            while(scanner.hasNext() && (count + 1) <= offSet){
                scanner.nextLine();
                count++;
            }

            while(scanner.hasNext()){
                fileWriter.write(scanner.nextLine() + "\n");
            }

        }catch (IOException ex){
            ex.printStackTrace();
        }finally {
            try {
                scanner.close();
                fileWriter.close();
            }catch (NullPointerException|IOException ex){
                ex.printStackTrace();
            }

            file1.delete();

            new File(TEMP_FILE_NAME).renameTo(new File(fileName));
        }

    }

    private synchronized void writeListToFile(List<String> list, String fileName, int amount){
        Iterator<String> iterator = list.iterator();
        FileWriter fileWriter = null;
        int count = 0;

        try {
            fileWriter = new FileWriter(new File(fileName), true);
            while (iterator.hasNext() && count < amount){
                fileWriter.write(iterator.next() + "\n");
                count++;
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }finally {
            try {
                fileWriter.close();
            }catch (NullPointerException|IOException ex){
                ex.printStackTrace();
            }
        }
    }

    private SeleniumInstagramDriver createSeleniumInstagramDriver(){

        Map<String, Object> deviceMetrics = new HashMap<>();
        deviceMetrics.put("width", MOBILE_DEVICE_WIDTH);
        deviceMetrics.put("height", MOBILE_DEVICE_HEIGHT);
        deviceMetrics.put("pixelRatio", 1.0);

        Map<String, Object> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceMetrics", deviceMetrics);
        mobileEmulation.put("userAgent", "Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 5 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19");

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
        chromeOptions.addArguments("--lang=en-GB");
        chromeOptions.addArguments("headless");

        return new SeleniumInstagramDriver(new ChromeDriver(chromeOptions));
    }

}
