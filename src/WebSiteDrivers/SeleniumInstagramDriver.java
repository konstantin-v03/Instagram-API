package WebSiteDrivers;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

import static com.volvenkov.Main.*;
import static com.volvenkov.Utilities.WebSiteCookie.readCookiesFromFile;
import static com.volvenkov.Utilities.WebSiteCookie.writeCookiesToFile;

public class SeleniumInstagramDriver implements InstagramDriver {

    private static final String INSTAGRAM_CORE_PAGE_URL = "https://www.instagram.com";
    private static final String INSTAGRAM_INTERNAL_LOGIN_PAGE_URL = "/accounts/login/";

    public final WebDriver webDriver;
    private final JavascriptExecutor javascriptExecutor;

    private boolean isLogin;
    private String username;

    public SeleniumInstagramDriver(WebDriver webDriver){
        this.webDriver = webDriver;
        javascriptExecutor = (JavascriptExecutor) webDriver;
    }

    @Override
    public boolean login(String username, String password) {
        openPage(INSTAGRAM_INTERNAL_LOGIN_PAGE_URL);
        try {
            webDriver.findElement(By.name("username")).sendKeys(username);
            webDriver.findElement(By.name("password")).sendKeys(password);
            webDriver.findElement(By.xpath("//button[text()='Log in']")).click();
            delay(DELAY_BETWEEN_TWO_ACTION);
            openPage("");
            delay(DELAY_BETWEEN_TWO_ACTION);
            webDriver.findElement(By.cssSelector("span[aria-label='Profile'"));
            saveCookies(username);
            this.username = username;
            isLogin = true;
            return true;
        }catch (NoSuchElementException ex){
            return false;
        }
    }

    @Override
    public boolean loginUsingCookie(String username) {
        openPage("");
        delay(DELAY_BETWEEN_TWO_ACTION);
        this.username = username;
        addCookies(username);
        try{
            webDriver.get(INSTAGRAM_CORE_PAGE_URL);
            delay(DELAY_BETWEEN_TWO_ACTION);
            webDriver.findElement(By.cssSelector("span[aria-label='Profile'"));
            isLogin = true;
            return true;
        }catch (NoSuchElementException ex){
            return false;
        }
    }

    @Override
    public boolean like(String internalUrl) {
        if(!isLogin) throw new NullPointerException("Not Authorized Exception");
        openPage(internalUrl);

        try {
            webDriver.findElement(By.cssSelector("span[aria-label='Like']")).click();
            return true;
        }catch (Exception ex){
            return false;
        }
    }

    @Override
    public boolean unLike(String internalUrl) {
        if(!isLogin) throw new NullPointerException("Not Authorized Exception");
        openPage(internalUrl);

        try {
            webDriver.findElement(By.cssSelector("span[aria-label='Unlike']")).click();
            return true;
        } catch (Exception ex){
            return false;
        }
    }

    @Override
    public boolean follow(String internalUrl) {
        if(!isLogin) throw new NullPointerException("Not Authorized Exception");
        openPage(internalUrl);

        try {
            webDriver.findElement(By.xpath("//button[text()='Follow Back']")).click();
            return true;
        }catch (NoSuchElementException ex1){
            try {
                webDriver.findElement(By.xpath("//button[text()='Follow']")).click();
                return true;
            }catch (NoSuchElementException ex2){
                return false;
            }
        }
    }

    @Override
    public boolean unFollow(String internalUrl) {
        if(!isLogin) throw new NullPointerException("Not Authorized Exception");
        openPage(internalUrl);

        try {
            webDriver.findElement(By.xpath("//button[text()='Following']")).click();
            delay(DELAY_BETWEEN_TWO_ACTION);
            webDriver.findElement(By.xpath("//button[text()='Unfollow']")).click();
            return true;
        }catch (NoSuchElementException ex1){
            try {
                webDriver.findElement(By.xpath("//button[text()='Requested']")).click();
                delay(DELAY_BETWEEN_TWO_ACTION);
                webDriver.findElement(By.xpath("//button[text()='Unfollow']")).click();
                return true;
            }catch (NoSuchElementException ex2){
                return false;
            }
        }
    }

    @Override
    public boolean addComment(String internalUrl, String comment) {
        if(!isLogin) throw new NullPointerException("Not Authorized Exception");
        openPage(internalUrl);

        try {
            //webDriver.findElement(By.xpath("/html/body/span/section/main/div/div/article/div/section/span[2]/button")).click();
            webDriver.findElement(By.cssSelector("span[aria-label='Comment']")).click();
            delay(DELAY_BETWEEN_TWO_ACTION);
            //new Actions(webDriver).moveToElement(webDriver.findElement(By.xpath("/html/body/span/section/main/div/div/article/div/section/div/form"))).click().sendKeys(comment).sendKeys(Keys.ENTER).perform();
            new Actions(webDriver).moveToElement(webDriver.findElement(By.cssSelector("form[method='POST']"))).click().sendKeys(comment).sendKeys(Keys.ENTER).perform();
            return true;
        }catch (NoSuchElementException ex){
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeComment(String internalUrl, String comment) {
        return false;
    }

    @Override
    public boolean addPost(String internalPath, String caption) {
        if(!isLogin) throw new NullPointerException("Not Authorized Exception");
        openPage("/" + username + "/");
        javascriptExecutor.executeScript(
                "HTMLInputElement.prototype.click = function() {                     " +
                        "  if(this.type !== 'file') HTMLElement.prototype.click.call(this);  " +
                        "};");
        try {
            //webDriver.findElement(By.xpath("/html/body/span/section/nav/div/div/div/div/div/div[3]/span")).click();
            webDriver.findElement(By.cssSelector("span[aria-label='New Post']")).click();
            delay(DELAY_BETWEEN_TWO_ACTION);
            //webDriver.findElement(By.xpath("/html/body/span/section/nav/div/div/form/input")).sendKeys(new File("image.jpg").getAbsolutePath());
            webDriver.findElement(By.cssSelector("input[accept='image/jpeg']")).sendKeys(new File(internalPath).getAbsolutePath());
            delay(DELAY_BETWEEN_TWO_ACTION);
            webDriver.findElement(By.xpath("//button[text()='Next']")).click();
            //webDriver.findElement(By.xpath("/html/body/span/section/div/header/div/div[2]/button")).click();
            delay(DELAY_BETWEEN_TWO_ACTION);
            //webDriver.findElement(By.cssSelector("textarea[aria-label='Write a caption...']"));
            new Actions(webDriver).moveToElement(webDriver.findElement(By.xpath("/html/body/span/section/div/section/div/textarea"))).click().sendKeys(caption).perform();
            delay(DELAY_BETWEEN_TWO_ACTION);
            webDriver.findElement(By.xpath("//button[text()='Share']")).click();
            return true;
        }catch (NoSuchElementException ex){
            return false;
        }
    }

    @Override
    public boolean copyImagePost(String internalUrl, String caption) {
        if(!isLogin) throw new NullPointerException("Not Authorized Exception");
        openPage(internalUrl);
        try {
            //URL imageUrl = new URL(getFromHtmlBetween(webDriver.getPageSource(), "<article", "</article>", "src=").iterator().next());
            URL imageUrl = new URL(webDriver.findElement(By.xpath("/html/body/span/section/main/div/div/article/div/div/div/div/img")).getAttribute("src"));
            BufferedImage saveImage = ImageIO.read(imageUrl);
            ImageIO.write(saveImage, "jpg", new File("image.jpg"));
            delay(DELAY_BETWEEN_TWO_ACTION);
            addPost("image.jpg", caption);
            return true;
        }catch (IOException ex){
            ex.printStackTrace();
            return false;
        }finally {
            new File("image.jpg").delete();
        }
    }

    /*
    @Override
    public boolean copyVideoPost(String internalUrl, String caption) {
        if(!isLogin) throw new NullPointerException("Not Authorized Exception");
        openPage(internalUrl);

        URLConnection urlConnection;
        OutputStream outputStream;
        InputStream inputStream;

        try {
            URL url;
            byte[] buf;
            int byteRead;
            url = new URL(webDriver.findElement(By.xpath("/html/body/span/section/main/div/div/article/div/div/div/div/div/div/div/video")).getAttribute("src"));
            outputStream = new BufferedOutputStream(new FileOutputStream("video.mp4"));

            urlConnection = url.openConnection();
            inputStream = urlConnection.getInputStream();

            buf = new byte[1024];
            while ((byteRead = inputStream.read(buf)) != -1) outputStream.write(buf, 0, byteRead);

            addPost("video.mp4", caption);

            return true;
        }catch (IOException ex){
            ex.printStackTrace();
            return false;
        }finally {
            new File("video.mp4").delete();
        }
    }
    */

    @Override
    public boolean removePost(String internalUrl) {
        return false;
    }

    @Override
    public Set<String> getUrlsToAccountPosts(String internalUrl) {
        openPage(internalUrl);
        delay(DELAY_BETWEEN_TWO_ACTION);
        return getUrlsFromPageBetweenWithSynchronousLoading("<article", "</article>", "href", 1, 0);
    }

    @Override
    public Set<String> getUrlsToAccountPosts(String internalUrl, int amount) {
        if(amount <= 0) throw new IllegalArgumentException();
        openPage(internalUrl);
        delay(DELAY_BETWEEN_TWO_ACTION);
        return getUrlsFromPageBetweenWithSynchronousLoading("<article", "</article>", "href", 1, amount);
    }

    @Override
    public Set<String> getFollowers(String internalUrl) {
        if(!isLogin) throw new NullPointerException("Not Authorized Exception");
        openPage(internalUrl);
        try {
            //webDriver.findElement(By.xpath("/html/body/span/section/main/div/ul/li[2]")).click();
            webDriver.findElement(By.cssSelector("a[href='" + internalUrl + "followers/" + "']")).click();
        }catch (NoSuchElementException ex){
            ex.printStackTrace();
        }
        delay(DELAY_BETWEEN_TWO_ACTION);
        return getUrlsFromPageBetweenWithoutSynchronousLoading("<ul", "</ul>", "href=");
    }

    @Override
    public Set<String> getFollowers(String internalUrl, int amount) {
        if(!isLogin) throw new NullPointerException("Not Authorized Exception");
        if(amount <= 0) throw new IllegalArgumentException();
        openPage(internalUrl);

        try {
            //webDriver.findElement(By.xpath("/html/body/span/section/main/div/ul/li[2]")).click();
            webDriver.findElement(By.cssSelector("a[href='" + internalUrl + "followers/" + "']")).click();
        }catch (NoSuchElementException ex){
            ex.printStackTrace();
        }
        delay(DELAY_BETWEEN_TWO_ACTION);

        return getUrlsFromPageBetweenWithSynchronousLoading("<ul", "</ul>", "href=",2, amount);
    }

    @Override
    public Set<String> getFollowing(String internalUrl) {
        if(!isLogin) throw new NullPointerException("Not Authorized Exception");
        openPage(internalUrl);

        try {
            //webDriver.findElement(By.xpath("/html/body/span/section/main/div/ul/li[3]")).click();
            webDriver.findElement(By.cssSelector("a[href='" + internalUrl + "following/" + "']")).click();
        }catch (NoSuchElementException ex){
            ex.printStackTrace();
        }

        delay(DELAY_BETWEEN_TWO_ACTION);

        return getUrlsFromPageBetweenWithoutSynchronousLoading("<ul", "</ul>", "href=");
    }

    @Override
    public Set<String> getFollowing(String internalUrl, int amount) {
        if(!isLogin) throw new NullPointerException("Not Authorized Exception");
        if(amount <= 0) throw new IllegalArgumentException();
        openPage(internalUrl);

        try {
            //webDriver.findElement(By.xpath("/html/body/span/section/main/div/ul/li[3]")).click();
            webDriver.findElement(By.cssSelector("a[href='" + internalUrl + "following/" + "']")).click();
        }catch (NoSuchElementException ex){
            ex.printStackTrace();
        }
        delay(DELAY_BETWEEN_TWO_ACTION);

        return getUrlsFromPageBetweenWithSynchronousLoading("<ul", "</ul>", "href=",2, amount);
    }

    @Override
    public void close() {
        webDriver.close();
        webDriver.quit();
    }

    private boolean addCookies(String username) {
        try {
            for (Cookie cookie : readCookiesFromFile(getFileNameWithCookies(username)))
                webDriver.manage().addCookie(cookie);
            return true;
        }catch (NoSuchCookieException ex){
            return false;
        }
    }

    private void saveCookies(String username) {
        writeCookiesToFile(webDriver.manage().getCookies(), getFileNameWithCookies(username));
    }

    private String getFileNameWithCookies(String username){
        return "cookies/" + username + " cookies.txt";
    }

    private void openPage(String internalUrl){
        webDriver.get(INSTAGRAM_CORE_PAGE_URL + internalUrl);
    }

    private Set<String> getUrlsFromPageBetweenWithSynchronousLoading(String start, String end, String what, int changingCoefficient, int amount){
        Set<String> internalUrls = new LinkedHashSet<>();

        int nowHeight = Integer.parseInt(javascriptExecutor.executeScript("return document.body.scrollHeight").toString());
        int lastHeight;
        int changingHeight = MOBILE_DEVICE_HEIGHT * changingCoefficient;
        int countHeight = 0;
        boolean isIgnore = amount == 0;

        do{
            if(nowHeight >= countHeight){
                internalUrls.addAll(getFromHtmlBetween(webDriver.getPageSource(), start, end, what));
                if(!isIgnore && internalUrls.size() >= amount) break;
                countHeight += changingHeight;
            }
            lastHeight = nowHeight;
            javascriptExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            delay(DELAY_BETWEEN_PAGE_SCROLLS);
            nowHeight = Integer.parseInt(javascriptExecutor.executeScript("return document.body.scrollHeight").toString());
        } while(nowHeight != lastHeight);

        return internalUrls;
    }

    private Set<String> getUrlsFromPageBetweenWithoutSynchronousLoading(String start, String end, String what){
        int nowHeight = Integer.parseInt(javascriptExecutor.executeScript("return document.body.scrollHeight").toString()), lastHeight;

        do{
            lastHeight = nowHeight;
            javascriptExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            delay(DELAY_BETWEEN_PAGE_SCROLLS);
            nowHeight = Integer.parseInt(javascriptExecutor.executeScript("return document.body.scrollHeight").toString());

        }while(nowHeight != lastHeight);

        return new LinkedHashSet<>(getFromHtmlBetween(webDriver.getPageSource(), start, end, what));
    }

    private Set<String> getFromHtmlBetween(String html, String start, String end, String what){
        Set<String> internalPostUrls = new LinkedHashSet<>();
        Scanner scanner = new Scanner(html);

        String str, subStr;
        boolean isStart = false;

        while(scanner.hasNext()){
            str = scanner.next();
            if(str.contains(start)) isStart = true;
            if(isStart && str.contains(what)){
                subStr = str.substring(str.indexOf('"') + 1);
                subStr = subStr.substring(0, subStr.indexOf('"'));
                internalPostUrls.add(subStr);
            }
            if(str.contains(end)) break;
        }

        return internalPostUrls;
    }

}

