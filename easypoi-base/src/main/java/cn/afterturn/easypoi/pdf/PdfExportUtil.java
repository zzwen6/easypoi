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
package cn.afterturn.easypoi.pdf;

import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.Document;

import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.pdf.entity.PdfExportParams;
import cn.afterturn.easypoi.pdf.export.PdfExportServer;

/**
 * PDF 导出工具类
 *  
 * @author JueYue
 *  2015年10月6日 下午8:14:01
 * @version 1.0
 */
public class PdfExportUtil {

    /**
     * 根据注解导出数据
     * @param entity
     *            表格标题属性
     * @param pojoClass
     *            PDF对象Class
     * @param dataSet
     *            PDF对象数据List
     */
    public static Document exportPdf(PdfExportParams entity, Class<?> pojoClass,
                                     Collection<?> dataSet, OutputStream outStream) {
        return new PdfExportServer(outStream, entity).createPdf(entity, pojoClass, dataSet);
    }

    /**
     * 根据Map创建对应的PDF
     * @param entity
     *            表格标题属性
     * @param entityList
     *            PDF对象Class
     * @param dataSet
     *            PDF对象数据List
     */
    public static Document exportPdf(PdfExportParams entity, List<ExcelExportEntity> entityList,
                                     Collection<? extends Map<?, ?>> dataSet,
                                     OutputStream outStream) {

        return new PdfExportServer(outStream, entity).createPdfByExportEntity(entity, entityList,
            dataSet);
    }

    
    /**
     * PDF模板导出，需要自己制作模板文件
     * exportPdf:<br>
     * 
     * @CreateTime 2018年1月10日 下午2:26:36
     * @param entity 导出参数，主要是模板文件
     * @param dataSet Map数据集合
     * @param dto 实体类对象，包含实体属性值
     * @param outStream 
     * @return
     * @author zzwen6
     * @修改记录: <br/>
     */
    public static Document exportPdf(PdfExportParams entity,    Map<String, String>  map,
    								Object dto, OutputStream outStream){
    	return new PdfExportServer(outStream, entity).createPdfByTemplate(entity, dto, map  );
    }
    
    
    
    
    
    
    
    
    
}
