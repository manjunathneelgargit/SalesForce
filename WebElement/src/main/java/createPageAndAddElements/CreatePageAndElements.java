package createPageAndAddElements;

import java.io.FileInputStream;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CreatePageAndElements 
{
	public GenericLib glib = new GenericLib();
	public WebDriver driver;
	public int salesForce = 8; //project
	public String page = "Login1"; //main page
	//Excel related Variables
	public String excelPath = "./Excel/CreatePageAndElements.xlsx";
	public String sheetName = "AddressSearch;ListViewControls";
	public WebDriverWait wait;
	@BeforeClass
	public void configBC() throws InterruptedException
	{
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("http://localhost:9998/index.html");
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 5);
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
	public void addingElements(String parentPage, String newPageName, String WebElementName, String type, String locator1, String value1, String locator2, String value2) throws Exception 
	{
		try
		{
			driver.findElement(By.xpath("//span[text()='"+newPageName+"']"));
			System.out.println(newPageName+" -> Page Already Exist, So Adding webelements");
		}
		catch(Exception e)
		{
			//scroll down to parent page
			driver.findElement(By.xpath("//th[text()='Pages']")).click();
			glib.executeScript(driver, parentPage);
//			Thread.sleep(5000);
			
			System.out.println("Page Not found So creating new Page "+newPageName);
			//right click on parent page
			glib.rightClick(driver, driver.findElement(By.xpath("//span[text()='"+parentPage+"']")));
//			Thread.sleep(5000);
			
			//click on add child page option
			driver.findElement(By.xpath("//div[contains(text(),'Add Child Page ')]")).click();
			//driver.findElement(By.xpath("//ul[contains(@class,'contextmenu')]/li[@data-command='addChildPage']")).click();
			

			//Enter pageName
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Page Name']")));
			driver.findElement(By.xpath("//input[@placeholder='Page Name']")).sendKeys(newPageName);

			//Click on create button
			driver.findElement(By.xpath("//button[text()='Create']")).click();
			System.out.println(newPageName + " Page is created --> Pass");

			driver.findElement(By.xpath("//div[contains(text(),'Successfully')]")).click();
			driver.navigate().refresh();
		}

		//***************************************
		//Add Elements to newly created page
		System.out.println("Clicked on table header -> Page");
		System.out.println("Adding WebElement in to page -> "+newPageName);
		glib.executeScript(driver, newPageName);
		
//		Thread.sleep(5000);
		//right click on new page
		glib.rightClick(driver, driver.findElement(By.xpath("//span[text()='"+newPageName+"']")));
		
		
		//click on add AddWebElement option
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(text(),'Element')]")));
//		Thread.sleep(3000);
		driver.findElement(By.xpath("//div[contains(text(),'Element')]")).click();


		//Enter Element Name
		driver.findElement(By.xpath("//label[contains(text(),'Element Name')]/following-sibling::div//input")).sendKeys(WebElementName);

		//Select Element Type
		WebElement SelectType = driver.findElement(By.xpath("//label[text()='Type']/..//select"));
		glib.selectByValue(SelectType, type);

		//Add 2 locators and their values
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
				
				driver.findElement(By.xpath("//button[contains(text(),'Create & Close')]")).click();
				System.out.println("Created Element -> "+WebElementName+" into "+newPageName+" Page");
//				Thread.sleep(1500);
				driver.findElement(By.xpath("//div[contains(text(),'Successfully')]")).click();
				driver.navigate().refresh();
			}
		}
	}
}
