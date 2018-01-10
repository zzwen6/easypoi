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
package cn.afterturn.easypoi.pdf.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.Item;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import cn.afterturn.easypoi.cache.ImageCache;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.excel.export.base.ExportCommonService;
import cn.afterturn.easypoi.pdf.entity.PdfExportParams;
import cn.afterturn.easypoi.pdf.styler.IPdfExportStyler;
import cn.afterturn.easypoi.pdf.styler.PdfExportStylerDefaultImpl;
import cn.afterturn.easypoi.util.PoiPublicUtil;

/**
 * PDF导出服务,基于Excel基础的导出
 * @author JueYue
 *  2015年10月6日 下午8:21:08
 */
public class PdfExportServer extends ExportCommonService {

    private static final Logger LOGGER     = LoggerFactory.getLogger(PdfExportServer.class);

    private Document            document;
    private IPdfExportStyler    styler;

    private boolean             isListData = false;

    private OutputStream 		outStream;
    
    public PdfExportServer(OutputStream outStream, PdfExportParams entity) {
        try {
            styler = entity.getStyler() == null ? new PdfExportStylerDefaultImpl()
                : entity.getStyler();
            document = styler.getDocument();
            // 这里修改了下，通过导出模板地址是否为空为判断是不是模板导出
            if (StringUtils.isBlank(entity.getTemplatePath())) {
            	PdfWriter.getInstance(document, outStream);
            	document.open();
			}
            this.outStream = outStream;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 创建Pdf的表格数据
     * @param entity
     * @param pojoClass
     * @param dataSet
     * @return
     */
    public Document createPdf(PdfExportParams entity, Class<?> pojoClass, Collection<?> dataSet) {
        try {
            List<ExcelExportEntity> excelParams = new ArrayList<ExcelExportEntity>();
            if (entity.isAddIndex()) {
                //excelParams.add(indexExcelEntity(entity));
            }
            // 得到所有字段
            Field[] fileds = PoiPublicUtil.getClassFields(pojoClass);
            ExcelTarget etarget = pojoClass.getAnnotation(ExcelTarget.class);
            String targetId = etarget == null ? null : etarget.value();
            getAllExcelField(entity.getExclusions(), targetId, fileds, excelParams, pojoClass,
                null, null);
            createPdfByExportEntity(entity, excelParams, dataSet);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            try {
                document.close();
            } catch (Exception e) {
                //可能之前已经关闭过了
            }
        }
        return document;
    }

    public Document createPdfByExportEntity(PdfExportParams entity,
                                            List<ExcelExportEntity> excelParams,
                                            Collection<?> dataSet) {
        try {
            sortAllParams(excelParams);
            for (int k = 0, paramSize = excelParams.size(); k < paramSize; k++) {
                if (excelParams.get(k).getList() != null) {
                    isListData = true;
                    break;
                }
            }
            //设置各个列的宽度
            float[] widths = getCellWidths(excelParams);
            PdfPTable table = new PdfPTable(widths.length);
            table.setTotalWidth(widths);
            //table.setLockedWidth(true);
            //设置表头
            createHeaderAndTitle(entity, table, excelParams);
            int rowHeight = getRowHeight(excelParams) / 50;
            Iterator<?> its = dataSet.iterator();
            while (its.hasNext()) {
                Object t = its.next();
                createCells(table, t, excelParams, rowHeight);
            }
            document.add(table);
        } catch (DocumentException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            document.close();
        }
        return document;
    }
    public Document createPdfByTemplate(PdfExportParams entity, Object dto, Map<String, String> map) {
        PdfReader reader;
        ByteArrayOutputStream bos;
        PdfStamper stamper;
         
        try {
			reader = new PdfReader(entity.getTemplatePath());
			bos = new ByteArrayOutputStream();
			stamper = new PdfStamper(reader, bos);
			// 表单域
			AcroFields form = stamper.getAcroFields();
			setFormValues(form, dto, map);
			// 导出来的字总是很奇怪 TODO
			form.setSubstitutionFonts(styler.getBaseFont(null, null));
			
			stamper.setFormFlattening(true);
			stamper.close();
			
			 
			PdfCopy copy = new PdfCopy(document, outStream);
			int pages = reader.getNumberOfPages();
			document.open();
			for(int i=1;i<=pages ;i++){
				PdfImportedPage importedPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), i);
				copy.addPage(importedPage);
			}
			document.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
		return document;
	}
    
    // 值设置优先级，优先设置Map的值，再设置 obj值, 有冲突将会覆盖
	private void setFormValues(AcroFields form, Object dto, Map<String, String> map) throws IOException, DocumentException {
		Map<String, Item> fields = form.getFields();
		if (map != null && map.size() > 0) {
			for(Map.Entry<String, Item> entry : fields.entrySet()){
				if (map.containsKey(entry.getKey())) {
					form.setField(entry.getKey(), map.get(entry.getKey()));
				}
			}
		} 
		
		// 设置Object属性值
		if (dto != null) {
			
			for(Map.Entry<String, Item> entry : fields.entrySet()){
				 Object value = getValue(dto, entry.getKey());
				 if (value != null) {
					form.setField(entry.getKey(), value + "");
				}
			}
		}
	}
	private  Object getValue(Object dto, String key) {
        
        Object value = null;
        
        Method[] methods = dto.getClass().getDeclaredMethods();
        
        for(Method method : methods){
            
            String methodName = method.getName();
            if (methodName.startsWith("get") && methodName.substring(3).equalsIgnoreCase(key))
                try {
                     
                    value = method.invoke(dto, new Object[]{});
                    return value;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }  
            
            
        }
        return value;
    }
	private void createCells(PdfPTable table, Object t, List<ExcelExportEntity> excelParams,
                             int rowHeight) throws Exception {
        ExcelExportEntity entity;
        int maxHeight = getThisMaxHeight(t, excelParams);
        for (int k = 0, paramSize = excelParams.size(); k < paramSize; k++) {
            entity = excelParams.get(k);
            if (entity.getList() != null) {
                Collection<?> list = getListCellValue(entity, t);
                for (Object obj : list) {
                    createListCells(table, obj, entity.getList(), rowHeight);
                }
            } else {
                Object value = getCellValue(entity, t);
                if (entity.getType() == 1) {
                    createStringCell(table, value == null ? "" : value.toString(), entity,
                        rowHeight, 1, maxHeight);
                } else {
                    createImageCell(table, value == null ? "" : value.toString(), entity, rowHeight,
                        1, maxHeight);
                }
            }
        }
    }

    /**
     * 创建集合对象
     * @param table
     * @param obj 
     * @param rowHeight 
     * @param excelParams
     * @throws Exception 
     */
    private void createListCells(PdfPTable table, Object obj, List<ExcelExportEntity> excelParams,
                                 int rowHeight) throws Exception {
        ExcelExportEntity entity;
        for (int k = 0, paramSize = excelParams.size(); k < paramSize; k++) {
            entity = excelParams.get(k);
            Object value = getCellValue(entity, obj);
            if (entity.getType() == 1) {
                createStringCell(table, value == null ? "" : value.toString(), entity, rowHeight);
            } else {
                createImageCell(table, value == null ? "" : value.toString(), entity, rowHeight, 1,
                    1);
            }
        }
    }

    /**
     * 获取这一列的高度
     * @param t             对象
     * @param excelParams   属性列表
     * @return
     * @throws Exception    通过反射过去值得异常
     */
    private int getThisMaxHeight(Object t, List<ExcelExportEntity> excelParams) throws Exception {
        if (isListData) {
            ExcelExportEntity entity;
            int maxHeight = 1;
            for (int k = 0, paramSize = excelParams.size(); k < paramSize; k++) {
                entity = excelParams.get(k);
                if (entity.getList() != null) {
                    Collection<?> list = getListCellValue(entity, t);
                    maxHeight = (list == null || maxHeight > list.size()) ? maxHeight : list.size();
                }
            }
            return maxHeight;
        }
        return 1;
    }

    /**
     * 获取Cells的宽度数组
     * @param excelParams
     * @return
     */
    private float[] getCellWidths(List<ExcelExportEntity> excelParams) {
        List<Float> widths = new ArrayList<Float>();
        for (int i = 0; i < excelParams.size(); i++) {
            if (excelParams.get(i).getList() != null) {
                List<ExcelExportEntity> list = excelParams.get(i).getList();
                for (int j = 0; j < list.size(); j++) {
                    widths.add((float) (20 * list.get(j).getWidth()));
                }
            } else {
                widths.add((float) (20 * excelParams.get(i).getWidth()));
            }
        }
        float[] widthArr = new float[widths.size()];
        for (int i = 0; i < widthArr.length; i++) {
            widthArr[i] = widths.get(i);
        }
        return widthArr;
    }

    private void createHeaderAndTitle(PdfExportParams entity, PdfPTable table,
                                      List<ExcelExportEntity> excelParams) throws DocumentException {
        int feildWidth = getFieldLength(excelParams);
        if (entity.getTitle() != null) {
            createHeaderRow(entity, table, feildWidth);
        }
        createTitleRow(entity, table, excelParams);
    }

    /**
     * 创建表头
     * 
     * @param title
     * @param table
     */
    private int createTitleRow(PdfExportParams title, PdfPTable table,
                               List<ExcelExportEntity> excelParams) {
        int rows = getRowNums(excelParams);
        for (int i = 0, exportFieldTitleSize = excelParams.size(); i < exportFieldTitleSize; i++) {
            ExcelExportEntity entity = excelParams.get(i);
            if (entity.getList() != null) {
                if (StringUtils.isNotBlank(entity.getName())) {
                    createStringCell(table, entity.getName(), entity, 10, entity.getList().size(),
                        1);
                }
                List<ExcelExportEntity> sTitel = entity.getList();
                for (int j = 0, size = sTitel.size(); j < size; j++) {
                    createStringCell(table, sTitel.get(j).getName(), sTitel.get(j), 10);
                }
            } else {
                createStringCell(table, entity.getName(), entity, 10, 1, rows == 2 ? 2 : 1);
            }
        }
        return rows;

    }

    private void createHeaderRow(PdfExportParams entity, PdfPTable table, int feildLength) {
        PdfPCell iCell = new PdfPCell(
            new Phrase(entity.getTitle(), styler.getFont(null, entity.getTitle())));
        iCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        iCell.setVerticalAlignment(Element.ALIGN_CENTER);
        iCell.setFixedHeight(entity.getTitleHeight());
        iCell.setColspan(feildLength + 1);
        table.addCell(iCell);
        if (entity.getSecondTitle() != null) {
            iCell = new PdfPCell(
                new Phrase(entity.getSecondTitle(), styler.getFont(null, entity.getSecondTitle())));
            iCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            iCell.setVerticalAlignment(Element.ALIGN_CENTER);
            iCell.setFixedHeight(entity.getSecondTitleHeight());
            iCell.setColspan(feildLength + 1);
            table.addCell(iCell);
        }
    }

    private PdfPCell createStringCell(PdfPTable table, String text, ExcelExportEntity entity,
                                      int rowHeight, int colspan, int rowspan) {
        PdfPCell iCell = new PdfPCell(new Phrase(text, styler.getFont(entity, text)));
        styler.setCellStyler(iCell, entity, text);
        iCell.setFixedHeight((int) (rowHeight * 2.5));
        if (colspan > 1) {
            iCell.setColspan(colspan);
        }
        if (rowspan > 1) {
            iCell.setRowspan(rowspan);
        }
        table.addCell(iCell);
        return iCell;
    }

    private PdfPCell createStringCell(PdfPTable table, String text, ExcelExportEntity entity,
                                      int rowHeight) {
        PdfPCell iCell = new PdfPCell(new Phrase(text, styler.getFont(entity, text)));
        styler.setCellStyler(iCell, entity, text);
        iCell.setFixedHeight((int) (rowHeight * 2.5));
        table.addCell(iCell);
        return iCell;
    }

    private PdfPCell createImageCell(PdfPTable table, String text, ExcelExportEntity entity,
                                     int rowHeight, int rowSpan, int colSpan) {

        try {
            Image image = Image.getInstance(ImageCache.getImage(text));
            PdfPCell iCell = new PdfPCell(image);
            styler.setCellStyler(iCell, entity, text);
            iCell.setFixedHeight((int) (rowHeight * 2.5));
            table.addCell(iCell);
            return iCell;
        } catch (BadElementException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (MalformedURLException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return new PdfPCell();

    }

	 
	

}
