package com.WebElement.AddWebElement;

import java.io.FileInputStream;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class AddWebElement 
{
	public static GenericLib glib = new GenericLib();
	public WebDriver driver;
	public int salesForce = 8; //project
	public String page = "Login1"; //main page
	//Excel related Variables
	public String excelPath = "./Excel/AddElements.xlsx";
	public String sheetName = "Home";
	
	//public FileLib flib = new FileLib();			
	//public String sheetName = "Sample";
	//public String page = flib.getExcelData("Sample", 1, 0);
	//String expectedPage = flib.getExcelData("Sample", 1, 0);
	
	@BeforeClass
	public void configBC() throws InterruptedException
	{
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("http://localhost:9998/index.html");
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.findElement(By.name("username")).sendKeys("manjunath.m@testyantra.com");
		driver.findElement(By.name("password")).sendKeys("Admin@5ty",Keys.ENTER);
		glib.selectByName(driver.findElement(By.id("userProject")), salesForce);
		Thread.sleep(3000);
		driver.findElement(By.xpath("//a[contains(@href,'repository')]")).click();
		driver.findElement(By.linkText(page)).click();
	}
	
	@DataProvider
	public Object[][] getExcelDataForGivenPage() throws Exception
	{
		FileInputStream ip = new FileInputStream(excelPath);
		Workbook wb = WorkbookFactory.create(ip);
		Sheet sh = wb.getSheet(sheetName);

		int rowNum = sh.getLastRowNum();
		int cellNum = sh.getRow(1).getLastCellNum();
		Object[][] data = new Object[rowNum][cellNum];
		for(int i=0;i<rowNum;i++)
		{
				for(int j=0;j<cellNum;j++)
				{
					 data[i][j] = sh.getRow(i+1).getCell(j).getStringCellValue();
				}
		}
		return data;
	}

	@Test(dataProvider = "getExcelDataForGivenPage")
	public void addingElements(String page, String WebElementName, String type, String locator1, String value1, String locator2, String value2) throws Exception 
	{
		WebElement element = driver.findElement(By.xpath("//span[text()='"+page+"']/../../../following-sibling::tr//span[@class='fancytree-title' and text()='"+WebElementName+"']"));
		
		glib.doubleClick(driver, element);
		
		//Select Type
		WebElement SelectType = driver.findElement(By.xpath("//label[text()='Type']/..//select"));
		glib.selectByValue(SelectType, type);
		
		for(int i=1;i<=2;i++)
		{
			if(i==1)
			{
				//Select Locator
				WebElement locator = driver.findElement(By.xpath("//label[text()='"+i+"']/../..//select[@formcontrolname='LocatorName']"));
				glib.selectByValue(locator, locator1);
			
				//Enter Locator value
				WebElement locatorValue = driver.findElement(By.xpath("(//input[contains(@name,'locatorValue')])["+i+"]"));
				locatorValue.clear();
				locatorValue.sendKeys(value1);
			}
			
			else
			{
				//Click add locator below
				driver.findElement(By.xpath("//button[@title='Add Locator below']")).click();
				
				//Select Locator
				WebElement locator = driver.findElement(By.xpath("//label[text()='"+i+"']/../..//select[@formcontrolname='LocatorName']"));
				glib.selectByValue(locator, locator2);
			
				//Enter Locator value
				WebElement locatorValue = driver.findElement(By.xpath("(//input[contains(@name,'locatorValue')])["+i+"]"));
				locatorValue.clear();
				locatorValue.sendKeys(value2);
				
				//click update button
				driver.findElement(By.xpath("//button[contains(text(),'Update')]")).click();	
			}
		}
	}
}
