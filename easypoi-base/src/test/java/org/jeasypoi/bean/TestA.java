/**
 * File Name: org.jeasypoi.bean.Test.java

 * @Date:2017年12月2日下午3:05:57
 */
package org.jeasypoi.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;



/**
 * File Name: org.jeasypoi.bean.Test.java
 * 
 * @Date:2017年12月2日下午3:05:57
 */
public class TestA {

	Logger logger = LoggerFactory.getLogger(TestA.class);
	
	@Test
	public void test() {
		FileInputStream stream;
		try {
			String filename = "C:/Users/hellio/Desktop/excel/lost.xlsx";
			stream =  new FileInputStream(new File(filename));
			ImportParams params = new ImportParams();
			
			params.setTitleRows(0);
			params.setHeadRows(1);
			params.setStartRows(1);
			params.setSheetNum(7);
			long curr = System.currentTimeMillis();
			
			List<FinanceReports> list = ExcelImportUtil.importExcelBySax(stream, FinanceReports.class, params);
			System.out.println(System.currentTimeMillis()-curr + "ms----");
			System.out.println(list.size()+"----");
			 
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 

	
}
