import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class main {
    static WebDriver driver;
    public static final String SEARCH_BUTTON = "//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'search')]";
    public static final String SEARCH_INPUT = "//input[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'search')]";
    public static final String SEARCH_A = "//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'search')]";
    public static String inputSource;
    public static String outputSource;
    public static void main(String[] args) throws InterruptedException, IOException {
        inputSource = Actions.getEnv("INPUT");
        outputSource = Actions.getEnv("OUTPUT");
       List<HashMap<String, String>> result =  Actions.data(inputSource,"Sheet1");
        for (HashMap<String,String> element:result
             ) {
            String status;
            String url = element.get("site");
            if(!url.contains("http")){url = "http://"+url;}
            driver = Actions.Chrome();
            driver.get(url);
            try{
                status = getStatus(SEARCH_BUTTON);

            }catch (TimeoutException e){
                try {
                    status = getStatus(SEARCH_INPUT);
                }catch (TimeoutException e2){
                    try {
                        status = getStatus(SEARCH_A);
                    }catch (TimeoutException e3){
                        status="No Search button";
                    }
                }

            }

            System.out.println(result);
            element.put("status",status);
            driver.quit();
        }
        System.out.println(result);
        Actions.writeResultToExcel(result,outputSource);
    }

    private static String getStatus(String Xpath){
        String current = driver.getCurrentUrl();
        Actions.click(Xpath,driver);
        try {
            Actions.waitUntilUrlChanged(current,driver);
            return  "OK";
        }
        catch (TimeoutException e){
            return   "Can't click";
        }


    }
}
