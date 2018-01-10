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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.collect.Lists;

import cn.afterturn.easypoi.excel.entity.enmus.CellValueType;
import cn.afterturn.easypoi.excel.entity.sax.SaxReadCellEntity;
import cn.afterturn.easypoi.excel.imports.sax.parse.ISaxRowRead;

/**
 * 回调接口
 * @author JueYue
 *  2014年12月29日 下午9:50:09
 */
public class SheetHandler extends DefaultHandler {
	//样式表
	// private StylesTable stylesTable;
    private SharedStringsTable      sst;
    private String                  lastContents;

    //当前行  
    private int                     curRow  = 0;
    //当前列  
    private int                     curCol  = 0;

    private CellValueType           type;
    private boolean isTElement;
    private ISaxRowRead             read;

    
    // 定义上一次读到的列序号
    private int lastColumnNumber = -1;
    private boolean flag = false ;
    private int thisColumn = -1;
    //存储行记录的容器  
    private List<SaxReadCellEntity> rowlist = Lists.newArrayList();

    public SheetHandler(SharedStringsTable sst, ISaxRowRead rowRead) {
        this.sst = sst;
        this.read = rowRead;
    }

    @Override
    public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException {
		
		// 置空
		lastContents = "";
		// c => 单元格
		if ("c".equals(name)) {
			 
			
			int firstDigit = -1;
            String r = attributes.getValue("r");

            for (int c = 0; c < r.length(); ++c) {
                if (Character.isDigit(r.charAt(c))) {
                    firstDigit = c;
                    break;
                }
            }
            thisColumn  = nameToColumn(r.substring(0, firstDigit));//获取当前读取的列数
            
			
             
			// 如果下一个元素是 SST 的索引，则将nextIsString标记为true
			String cellType = attributes.getValue("t");
			type = CellValueType.NUMBER;
			if ("s".equals(cellType)) {
				type = CellValueType.SSTINDEX;
				// return;
			}else if ("str".equals(cellType)) {
				type = CellValueType.FORMULA;
			}else if("inlineStr".equals(cellType)){
				type = CellValueType.INLINESTR;
			}else if("e".equals(cellType)){
				type = CellValueType.ERROR;
			}else if("b".equals(cellType)){
				type = CellValueType.BOOL;
			}
			
			// 样式
			String cellStyle = attributes.getValue("s");
			int styleIndex = Integer.parseInt(cellStyle);
             
			
			
			
//			if ("1".equals(cellType)) {
//				type = CellValueType.Date; // fixed by zzwen6
//			} else if ("2".equals(cellType)) {
//				type = CellValueType.Number;
//			}
		}
		if ("t".equals(name)) {// 当元素为t时
			type = CellValueType.TELEMENT;
			isTElement = true;
		}

	}

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {

		// 根据SST的索引值的到单元格的真正要存储的字符串
		// 这时characters()方法可能会被调用多次
		/*if (CellValueType.String.equals(type)) {
			try {
				int idx = Integer.parseInt(lastContents);
				// by zzwen6 有个BUG,若当前单元格为文本类型，但设置0,1,2,3这样的可寻找到表头的值，则会读取到表头单元格 TODO
				lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
			} catch (Exception e) {

			}
		}*/
		// t元素也包含字符串
		if (CellValueType.TELEMENT.equals(type) && isTElement) {
			
			String value = lastContents.trim();
			rowlist.add(thisColumn, new SaxReadCellEntity(CellValueType.String, value));
			curCol++;
			type = CellValueType.NULL;
			isTElement = false;
			// v => 单元格的值，如果单元格是字符串则v标签的值为该字符串在SST中的索引
			// 将单元格内容加入rowlist中，在这之前先去掉字符串前后的空白符
		} else if ("v".equals(name)) {
			 
			// 计算单元格之前的空白值，进行填充 TODO
			if(thisColumn - lastColumnNumber > 1){
                flag = true ;
                curCol = thisColumn;
            }
            for (int i = lastColumnNumber;flag &&  i < thisColumn && rowlist.size() < thisColumn; ++i){
            	 
                if( i > lastColumnNumber){
                    rowlist.add(i, new SaxReadCellEntity(CellValueType.String, " "));
                }
            }

            // Update column
            if (thisColumn > 0){
            	flag = false;
                lastColumnNumber = thisColumn;
                
            } 
			 
			
			String value = lastContents.trim();
			value = value.equals("") ? " " : value;
			
			switch (type) {
			case BOOL:
			case ERROR:
			case FORMULA:
				rowlist.add(curCol, new SaxReadCellEntity(CellValueType.String, value));
				break;

			case INLINESTR:
				XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
				value = rtsi.toString();
				rowlist.add(curCol, new SaxReadCellEntity(CellValueType.String, value));
				break;
			case SSTINDEX:
				String sstIndex = value.toString();
				try {
					int idx = Integer.parseInt(sstIndex);
					XSSFRichTextString rtss = new XSSFRichTextString(sst.getEntryAt(idx));
					value = rtss.toString();
					rtss = null;
				} catch (Exception ex) {
					value = value.toString();
				}finally {
					
					rowlist.add(curCol, new SaxReadCellEntity(CellValueType.String, value));
				}
				break;
			case NUMBER:
				rowlist.add(curCol, new SaxReadCellEntity(CellValueType.String, value.replace(",", "")));
				break;
			case DATE:
				rowlist.add(curCol, new SaxReadCellEntity(CellValueType.String, value));
				break;

			default:
				rowlist.add(curCol, new SaxReadCellEntity(CellValueType.String, value));

				break;
			}
			
			
			
			 curCol++;
		} else if (name.equals("row")) {// 如果标签名称为 row ，这说明已到行尾，调用 optRows() 方法
			// System.out.println(rowlist);
			read.parse(curRow, rowlist);
			rowlist.clear();
			curRow++;
			curCol = 0;
			lastColumnNumber = -1;
			thisColumn = -1;
		}
		
	}

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //得到单元格内容的值  
        lastContents += new String(ch, start, length);
    }
    private int nameToColumn(String name) {
        int column = -1;
        for (int i = 0; i < name.length(); ++i) {
            int c = name.charAt(i);
            column = (column + 1) * 26 + c - 'A';
        }
        return column;
    }
}
