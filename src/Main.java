import Wrappers.SeleniumInstagramWrapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;

public class Main {

    public static final long DELAY_BETWEEN_PAGE_SCROLLS = 3000;
    public static final long DELAY_BETWEEN_TWO_ACTION = 3000;

    public static final int MOBILE_DEVICE_HEIGHT = 800;
    public static final int MOBILE_DEVICE_WIDTH = 480;

    private static SeleniumInstagramWrapper seleniumInstagramWrapper;

    static{
        System.setProperty("webdriver.chrome.driver", "chromedriver");
    }

    public static void main(String[] args) {


        Thread thread = new Thread(){
            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);
                String str;
                String strArray[];
                while (!Thread.interrupted()) {
                    if (scanner.hasNext()) {
                        str = scanner.nextLine();
                        strArray = str.split(" ");
                        try {
                            if (strArray.length == 1 && strArray[0].equals("login")) {
                                String username, password;

                                System.out.print("Write your username: ");
                                username = System.console().readLine();
                                System.out.print("Write your password: ");
                                password = System.console().readPassword().toString();

                                seleniumInstagramWrapper = new SeleniumInstagramWrapper(username, password);

                                if (seleniumInstagramWrapper.verifyData())
                                    System.out.println("Successfully logged");
                                else {
                                    System.err.println("Unsuccessfully logged");
                                    seleniumInstagramWrapper = null;
                                }
                            }else if (strArray.length == 1 && strArray[0].equals("stop"))
                                interrupt();
                            else if(strArray.length == 1 && strArray[0].equals("help")){
                                for(Method method : SeleniumInstagramWrapper.class.getMethods()) {
                                    System.out.print(method.getName() + " ");
                                    Class parameters[] = method.getParameterTypes();
                                    for(int i = 0; i < parameters.length; i++){
                                        System.out.print("arg[" + (i + 1) + "] ");
                                    }
                                    System.out.print("\n");
                                }
                            }
                            else if (seleniumInstagramWrapper == null)
                                System.err.println("You don't authorized");
                            else {
                                if (strArray.length == 1)
                                    seleniumInstagramWrapper.getClass().getMethod(strArray[0]).invoke(seleniumInstagramWrapper);
                                else if (strArray.length == 3)
                                    seleniumInstagramWrapper.getClass().getMethod(strArray[0], String.class, String.class).invoke(seleniumInstagramWrapper, strArray[1], strArray[2]);
                                else if (strArray.length == 4)
                                    seleniumInstagramWrapper.getClass().getMethod(strArray[0], String.class, String.class, String.class).invoke(seleniumInstagramWrapper, strArray[1], strArray[2], strArray[3]);
                            }
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                scanner.close();
            }
        };
        thread.start();


    }

    public static void delay(long millis){
        try{
            Thread.sleep(millis);
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }
}
