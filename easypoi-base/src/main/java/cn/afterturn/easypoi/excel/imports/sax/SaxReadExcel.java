/**
 * Copyright 2013-2015 JueYue (qrb.jueyue@gmail.com)
 *   
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.afterturn.easypoi.excel.imports.sax;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.imports.sax.parse.ISaxRowRead;
import cn.afterturn.easypoi.excel.imports.sax.parse.SaxRowRead;
import cn.afterturn.easypoi.exception.excel.ExcelImportException;
import cn.afterturn.easypoi.handler.inter.IExcelReadRowHanlder;

/**
 * 基于SAX Excel大数据读取,读取Excel 07版本,不支持图片读取
 * @author JueYue
 *  2014年12月29日 下午9:41:38
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
public class SaxReadExcel {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaxReadExcel.class);

    public <T> List<T> readExcel(InputStream inputstream, Class<?> pojoClass, ImportParams params,
                                 ISaxRowRead rowRead, IExcelReadRowHanlder hanlder) {
        try {
            OPCPackage opcPackage = OPCPackage.open(inputstream);
            return readExcel(opcPackage, pojoClass, params, rowRead, hanlder);
            
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExcelImportException(e.getMessage());
        }
    }
    /**
     * 多线程读取sheet内容
     */
	public <T> List<T> readExcelByThreads(InputStream inputstream, Class<?> pojoClass, ImportParams params, ISaxRowRead rowRead,
			IExcelReadRowHanlder hanlder) {
		try {
			OPCPackage opcPackage = OPCPackage.open(inputstream);
			return readExcelByThreads(opcPackage, pojoClass, params, rowRead, hanlder);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new ExcelImportException(e.getMessage());
		}
	}
    private <T> List<T> readExcel(OPCPackage opcPackage, Class<?> pojoClass, ImportParams params,
                                  ISaxRowRead rowRead, IExcelReadRowHanlder hanlder) {
        try {
            XSSFReader xssfReader = new XSSFReader(opcPackage);
            SharedStringsTable sst = xssfReader.getSharedStringsTable();
            if (rowRead == null) {
                rowRead = new SaxRowRead(pojoClass, params, hanlder);
            }
			StylesTable stylesTable = xssfReader.getStylesTable();

            XMLReader parser = fetchSheetParser(sst, rowRead);
            Iterator<InputStream> sheets = xssfReader.getSheetsData();
            
            
            int sheetIndex = 0;
            while (sheets.hasNext() && sheetIndex < params.getSheetNum()) {
                sheetIndex++;
                InputStream sheet = sheets.next();
                InputSource sheetSource = new InputSource(sheet);
                parser.parse(sheetSource);
                sheet.close();
            }
            return rowRead.getList();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExcelImportException("SAX导入数据失败");
        }
    }
    private <T> List<T> readExcelByThreads(OPCPackage opcPackage, Class<?> pojoClass, ImportParams params,
            ISaxRowRead rowRead, IExcelReadRowHanlder hanlder) {
    	int sheetNum = params.getSheetNum(); // 需要读取的sheet数负值为全部，正值才有用
    	int[] sheetRange = params.getSheetRange(); // 读取sheet的范围
    	boolean threadFlag = true; // 多线程读取标志
    	if (sheetNum <= 0 && sheetRange == null) { //想读取全部
    		threadFlag = false;
		} 
    	ISaxRowRead temp = rowRead;
    	List   result = new ArrayList ();
    	
    	if (threadFlag) { //  使用线程
			// 实际要读取的sheet数
    		// int num = sheetRange == null || sheetRange.length == 0 ? sheetNum : sheetRange.length;
			int[] reallySheetRange = sheetRange == null || sheetRange.length == 0 ? generateSheetNo(sheetNum) : sheetRange;
			try {
				XSSFReader xssfReader = new XSSFReader(opcPackage);
				SharedStringsTable sst = xssfReader.getSharedStringsTable();
				StylesTable stylesTable = xssfReader.getStylesTable();
				Iterator<InputStream> sheets = xssfReader.getSheetsData();

				// 这个不能共享 rowRead
//				if (rowRead == null) {
//					rowRead = new SaxRowRead(pojoClass, params, hanlder);
//				}
				int count = reallySheetRange.length;
				
				//XMLReader parser = fetchSheetParser(sst, rowRead,stylesTable);
	            // 线程池
				ExecutorService service = Executors.newFixedThreadPool(count);
	            // 计数器
				CountDownLatch latch = new CountDownLatch(count);
				for(int i : reallySheetRange){
					InputStream sht = xssfReader.getSheet("rId" + i);
					InputSource input = new InputSource(sht);
					// 这个不能共享 rowRead,每个线程应该有自己的数据
					if (rowRead == null) {
						temp = new SaxRowRead(pojoClass, params, hanlder);
					}
					XMLReader parser = fetchSheetParser(sst, temp);
					
					Future<List<?>> list = service.submit(new ProcessExcelTask(temp,parser,input));
					
					result.addAll(list.get());
					latch.countDown(); // 计算器减少
					sht.close();
				}
				latch.await(); // 等待所有线程执行完成
				service.shutdown();
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
	            throw new ExcelImportException("SAX导入数据失败");
			}
			
			
		}else { // 单个线程跑完数据 
			try {
				XSSFReader xssfReader = new XSSFReader(opcPackage);
				SharedStringsTable sst = xssfReader.getSharedStringsTable();
				StylesTable stylesTable = xssfReader.getStylesTable();
				Iterator<InputStream> sheets = xssfReader.getSheetsData();

	            
	            int sheetIndex = 0;
	            while (sheets.hasNext()) {
	            	// 这个不能共享 rowRead
	            	if (rowRead == null) {
	            		temp = new SaxRowRead(pojoClass, params, hanlder);
	            	}
	            	XMLReader parser = fetchSheetParser(sst, temp);
	            	
	                sheetIndex++;
	                InputStream sheet = sheets.next();
	                InputSource sheetSource = new InputSource(sheet);
	                parser.parse(sheetSource);
	                sheet.close();
	                result.addAll( temp.getList());
	            }
				
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
	            throw new ExcelImportException("SAX导入数据失败");
			}  

			
		}
    	
    	return result;
    }
    /**
	 * generateSheetNo:<br>
	 * 生成一个序列
	 */
	private int[] generateSheetNo(int sheetNum) {
		int[] N = new int[sheetNum];
		for(int i=0;i<sheetNum;i++) N[i] = i+1;
		return N;
	}

	private XMLReader fetchSheetParser(SharedStringsTable sst,
                                       ISaxRowRead rowRead) throws SAXException {
        XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
		ContentHandler handler = new SheetHandler(sst, rowRead);
        parser.setContentHandler(handler);
        return parser;
    }

}

class ProcessExcelTask implements Callable<List<?>> {
	ISaxRowRead rowRead;
	XMLReader parser;
	InputSource input;
	public ProcessExcelTask(ISaxRowRead rowRead, XMLReader parser,InputSource input) {
		this.rowRead = rowRead;
		this.parser = parser;
		this.input = input;
	}
	/**
	 * 
	 */
	@Override
	public List<?> call() throws Exception {
		parser.parse(input);
		return rowRead.getList();
	}

	 
 

}

