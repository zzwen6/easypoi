/**
 * Copyright 2013-2015 JueYue (qrb.jueyue@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package cn.afterturn.easypoi.word.parse.excel;

import cn.afterturn.easypoi.entity.ImageEntity;
import cn.afterturn.easypoi.util.PoiPublicUtil;
import cn.afterturn.easypoi.util.PoiWordStyleUtil;
import cn.afterturn.easypoi.word.entity.MyXWPFDocument;
import com.google.common.collect.Maps;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.afterturn.easypoi.util.PoiElUtil.*;

/**
 * 处理和生成Map 类型的数据变成表格
 * @author JueYue
 *  2014年8月9日 下午10:28:46
 */
public final class ExcelMapParse {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelMapParse.class);

    /**
     * 添加图片
     *
     * @param obj
     * @param currentRun
     * @throws Exception
     * @author JueYue
     * 2013-11-20
     */
    public static void addAnImage(ImageEntity obj, XWPFRun currentRun) {
        try {
            // 判断需要添加的图片是inline 还是anchor类型，inline按原来方式添加,anchor用浮动方式添加
            if (ImageEntity.INLINE.equals(obj.getInsertType())) {
                Object[] isAndType = PoiPublicUtil.getIsAndType(obj);
                String   picId;
                picId = currentRun.getDocument().addPictureData((byte[]) isAndType[0],
                        (Integer) isAndType[1]);
                ((MyXWPFDocument) currentRun.getDocument()).createPicture(currentRun,
                        picId, currentRun.getDocument()
                                .getNextPicNameNumber((Integer) isAndType[1]),
                        obj.getWidth(), obj.getHeight());

            } else {
                addAnFloatImage(obj, currentRun);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    public static void addAnFloatImage(ImageEntity obj, XWPFRun currentRun) {
        try {
            // 拿到文件流和图片类型
            Object[] isAndType = PoiPublicUtil.getIsAndType(obj);
            // 在当前的run中添加一张图片
            currentRun.addPicture(new ByteArrayInputStream((byte[]) isAndType[0]),(Integer) isAndType[1], "sign", Units.toEMU(200),Units.toEMU(200));
            // 获取图片数据
            CTDrawing drawing = currentRun.getCTR().getDrawingArray(0);

            CTGraphicalObject graphic = drawing.getInlineArray(0).getGraphic();

            CTAnchor ctAnchor = getAnchorWithGraphic(graphic,
                    "sign",
                    Units.toEMU(obj.getWidth()),
                    Units.toEMU(obj.getHeight()),

                    Units.toEMU(obj.getLeftOffset()),
                    Units.toEMU(obj.getTopOffset()),
                    false);

            drawing.setAnchorArray(new CTAnchor[]{ctAnchor});

            drawing.removeInline(0);


        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }
    /**
     * 解析参数行,获取参数列表
     *
     * @author JueYue
     *  2013-11-18
     * @param currentRow
     * @return
     */
    private static String[] parseCurrentRowGetParams(XWPFTableRow currentRow) {
        List<XWPFTableCell> cells = currentRow.getTableCells();
        String[] params = new String[cells.size()];
        String text;
        for (int i = 0; i < cells.size(); i++) {
            text = cells.get(i).getText();
            params[i] = text == null ? ""
                    : text.trim().replace(START_STR, EMPTY).replace(END_STR, EMPTY);
        }
        return params;
    }

    /**
     * 解析下一行,并且生成更多的行
     * @param table
     * @param index
     * @param list
     */
    public static void parseNextRowAndAddRow(XWPFTable table, int index,
                                             List<Object> list) throws Exception {
        XWPFTableRow currentRow = table.getRow(index);
        String[] params = parseCurrentRowGetParams(currentRow);
        String listname = params[0];
        boolean isCreate = !listname.contains(FOREACH_NOT_CREATE);
        listname = listname.replace(FOREACH_NOT_CREATE, EMPTY).replace(FOREACH_AND_SHIFT, EMPTY)
                .replace(FOREACH, EMPTY).replace(START_STR, EMPTY);
        String[] keys = listname.replaceAll("\\s{1,}", " ").trim().split(" ");
        params[0] = keys[1];
        //保存这一行的样式是-后面好统一设置
        List<XWPFTableCell> tempCellList = new ArrayList<XWPFTableCell>();
        tempCellList.addAll(table.getRow(index).getTableCells());
        int cellIndex = 0;
        Map<String, Object> tempMap = Maps.newHashMap();
        LOGGER.debug("start for each data list :{}", list.size());
        for (Object obj : list) {
            currentRow = isCreate ? table.insertNewTableRow(index++) : table.getRow(index++);
            tempMap.put("t", obj);
            for (cellIndex = 0; cellIndex < currentRow.getTableCells().size(); cellIndex++) {
                String val = eval(params[cellIndex], tempMap).toString();
                currentRow.getTableCells().get(cellIndex).setText("");
                PoiWordStyleUtil.copyCellAndSetValue(tempCellList.get(cellIndex),
                        currentRow.getTableCells().get(cellIndex), val);
            }

            for (; cellIndex < params.length; cellIndex++) {
                String val = eval(params[cellIndex], tempMap).toString();
                PoiWordStyleUtil.copyCellAndSetValue(tempCellList.get(cellIndex),
                        currentRow.createCell(), val);
            }
        }
        table.removeRow(index);

    }

    public static CTAnchor getAnchorWithGraphic(CTGraphicalObject ctGraphicalObject,
                                                String deskFileName, int width, int height,
                                                int leftOffset, int topOffset, boolean behind) {
        String anchorXML =
                "<wp:anchor xmlns:wp=\"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing\" "
                        + "simplePos=\"0\" relativeHeight=\"0\" behindDoc=\"" + ((behind) ? 1 : 0) + "\" locked=\"0\" layoutInCell=\"1\" allowOverlap=\"1\">"
                        + "<wp:simplePos x=\"0\" y=\"0\"/>"
                        + "<wp:positionH relativeFrom=\"column\">"
                        + "<wp:posOffset>" + leftOffset + "</wp:posOffset>"
                        + "</wp:positionH>"
                        + "<wp:positionV relativeFrom=\"paragraph\">"
                        + "<wp:posOffset>" + topOffset + "</wp:posOffset>" +
                        "</wp:positionV>"
                        + "<wp:extent cx=\"" + width + "\" cy=\"" + height + "\"/>"
                        + "<wp:effectExtent l=\"0\" t=\"0\" r=\"0\" b=\"0\"/>"
                        + "<wp:wrapNone/>"
                        + "<wp:docPr id=\"1\" name=\"Drawing 0\" descr=\"" + deskFileName + "\"/><wp:cNvGraphicFramePr/>"
                        + "</wp:anchor>";

        CTDrawing drawing = null;
        try {

            drawing = CTDrawing.Factory.parse(anchorXML);
        } catch (XmlException e) {
            e.printStackTrace();
        }
        CTAnchor anchor = drawing.getAnchorArray(0);
        anchor.setGraphic(ctGraphicalObject);
        return anchor;
    }


}
