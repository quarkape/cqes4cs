package club.hue.utils;

/**
 * @creator: quarkape
 * @create date: 2021/8/10 09:25
 * @desc: 对上传的成绩文件进行处理，存入数据库
 **/

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

public class ReadGradeXlsFiles {

    private static String basePath = "G:\\Projects\\Materials\\cqes4cs\\files\\grades\\";
    // private static String basePath = "/www/apps/cqes4cs/files/grades/";

    public static List<HashMap<String, Object>> readGradeXlsx(String fileFinalName) throws FileNotFoundException {
        // 加载文件的位置
        String filePath = basePath + fileFinalName;
        File file = new File(filePath);
        FileInputStream fs = new FileInputStream(file);
        List<HashMap<String, Object>> lst = new ArrayList<>();
        try {
            String fileName = file.getName();
            String[] fileParts = fileName.split("\\.");
            Workbook sheets;
            if (fileParts[fileParts.length - 1].equals("xls")) {
                sheets = new HSSFWorkbook(fs);
            } else {
                sheets = new XSSFWorkbook(fs);
            }
            Sheet sheet = sheets.getSheetAt(0);
            int rowCount = sheet.getLastRowNum();
            int colNum = sheet.getRow(0).getPhysicalNumberOfCells();
            // 遍历行
            for (int i=1; i<=rowCount; i++) {
//                int stepNum = 0;
                HashMap<String, Object> map = new HashMap<>();
                Row row = sheet.getRow(i);
                // 遍历列
                for(int j = 0; j < colNum; j++){
                    if(row.getCell(j) != null){
                        row.getCell(j).setCellType(CellType.STRING);
                        String cellValue = row.getCell(j).getStringCellValue();
                        switch (j) {
                            case 0:
                                map.put("id", cellValue);
                                break;
                            case 1:
                                map.put("code", cellValue);
                                break;
                            case 2:
                                map.put("year", cellValue);
                                break;
                            case 3:
                                map.put("grade", cellValue);
                                break;
                            case 4:
                                map.put("score", cellValue);
                                break;
                            case 5:
                                map.put("semester", cellValue);
                                break;
                            default: break;
                        }
                    }
                }
                lst.add(map);
            }
            fs.close();
            return lst;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}