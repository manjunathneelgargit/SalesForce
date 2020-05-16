package com.WebElement.AddWebElement;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class FileLib 
{
	/**
	 * This method is used to read the data from excel file
	 * @param sheetName
	 * @param rowIndex
	 * @param cellIndex
	 * @return String
	 */
	
	public String getExcelData(String sheetName, int rowIndex, int cellIndex)
	{
		Sheet sh = null;
		try 
		{
			FileInputStream ip = new FileInputStream("./Excel/Salesforce.xlsx");
			Workbook wb = WorkbookFactory.create(ip);
			sh = wb.getSheet(sheetName);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return sh.getRow(rowIndex).getCell(cellIndex).getStringCellValue();
		
	}
	
	
}
