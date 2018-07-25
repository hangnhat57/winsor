import com.github.shyiko.dotenv.DotEnv;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class Actions {

    public static String getEnv(String keyword) {
        Map<String, String> dot = DotEnv.load();
        return (String)dot.get(keyword);
    }
    public static void click(String xpath,WebDriver driver) {
        waitFor(xpath,driver);
        driver.findElement(By.xpath(xpath)).click();
    }

    public static void waitFor(String xpath,WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
    }
    public static void waitUntilUrlChanged(String currentUrl,WebDriver driver){

        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(currentUrl)));
    }
    public static void writeResultToExcel(List<HashMap<String,String>> list) throws IOException {
        writeResultToExcel(list,"output.xlsx");
    }
    public static void writeResultToExcel(List<HashMap<String,String>> list,String outputSource) throws IOException {
        List<String> header = new ArrayList<String>(list.get(0).keySet());
        Workbook workbook = new XSSFWorkbook();
        CreationHelper createHelper = workbook.getCreationHelper();
        Sheet sheet = workbook.createSheet("Result");
        Row headerRow = sheet.createRow(0);

        for(int i = 0; i < header.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(header.get(i));
        }

        int rowNum = 1;
        for (HashMap<String,String> element:list
             ) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(element.get("site"));
            row.createCell(1).setCellValue(element.get("status"));
        }
        for(int i = 0; i < header.size(); i++) {
            sheet.autoSizeColumn(i);
        }
        FileOutputStream fileOut = new FileOutputStream(outputSource);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();

    }

    public static List<HashMap<String, String>> data(String filepath, String sheetName) {
        List<HashMap<String, String>> mydata = new ArrayList<>();
        try {
            File file = new File(filepath);
            FileInputStream fs = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fs);
            XSSFSheet sheet = workbook.getSheet(sheetName);
            Row HeaderRow = sheet.getRow(0);
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row currentRow = sheet.getRow(i);
                HashMap<String, String> currentHash = new HashMap<String, String>();
                for (int j = 0; j < currentRow.getPhysicalNumberOfCells(); j++) {
                    Cell currentCell = currentRow.getCell(j);
                    switch (currentCell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            currentHash.put(HeaderRow.getCell(j).getStringCellValue(), currentCell.getStringCellValue());
                            break;
                        case Cell.CELL_TYPE_BLANK:
                            currentHash.put(HeaderRow.getCell(j).getStringCellValue(), "");
                            break;
                    }
                }
                mydata.add(currentHash);
            }
            System.out.println(mydata);
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mydata;
    }

    public static WebDriver Chrome() {
        ChromeDriverManager.getInstance().arch64().setup();
        ChromeOptions option = new ChromeOptions();
        Map<String, Object> preferences = new HashMap<String, Object>();
        option.addArguments("--disable-web-security");
        option.addArguments("--allow-running-insecure-content");
        option.setAcceptInsecureCerts(true);
        return new ChromeDriver(option);
    }
}
