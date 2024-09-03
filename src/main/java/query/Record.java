package query;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Record {
    private Workbook workbook;
    private Sheet sheet;
    private int rowNum;

    private int sheetNum;

    public Record() {
        workbook = new XSSFWorkbook();
    }
    public void record(String algorithm, String queryType, long average, long all) {
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(algorithm);

        Cell cell1 = row.createCell(1);
        cell1.setCellValue(queryType);

        Cell cell2 = row.createCell(2);
        cell2.setCellValue(average);

        Cell cell3 = row.createCell(3);
        cell3.setCellValue(all);
        rowNum++;
    }

    public void record_node(String algorithm,long average) {
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(algorithm);

        Cell cell2 = row.createCell(1);
        cell2.setCellValue(average);

        rowNum++;
    }

    public void record_path(String algorithm,long average) {
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(algorithm);

        Cell cell2 = row.createCell(1);
        cell2.setCellValue(average);

        rowNum++;
    }

    public void record_construct(String algorithm, long time, long matrix, long size) {
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(algorithm);
        Cell cell1 = row.createCell(1);
        cell1.setCellValue(time);
        Cell cell2= row.createCell(2);
        cell2.setCellValue(matrix);
        Cell cell3 = row.createCell(3);
        cell3.setCellValue(size);
        rowNum++;
    }

    public void record_detail(String algorithm, String type, long time){
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(algorithm);
        Cell cell1 = row.createCell(1);
        cell1.setCellValue(type);
        Cell cell2 = row.createCell(2);
        cell2.setCellValue(time);
        rowNum++;
    }

    public void createNewSheet(int blockTxNum) {
        sheet = workbook.createSheet("sheet-" + sheetNum);
        sheetNum++;
        rowNum = 0;
        // 添加表头
        Row header = sheet.createRow(rowNum);
        Cell cell = header.createCell(0);
        cell.setCellValue("索引");
        Cell cell1 = header.createCell(1);
        cell1.setCellValue("查询类型");
        Cell cell2 = header.createCell(2);
        cell2.setCellValue("总查询时间");
        Cell cell3 = header.createCell(3);
        cell3.setCellValue("平均查询时间");
        Cell cell4 = header.createCell(4);
        cell4.setCellValue("区块交易数:" + blockTxNum);
        rowNum++;
    }

    public void createNodeSkyline(int blockTxNum){
        sheet=workbook.createSheet("sheet-" + sheetNum);
        sheetNum++;
        rowNum=0;
        Row header = sheet.createRow(rowNum);
        Cell cell = header.createCell(0);
        cell.setCellValue("索引");
        Cell cell1 = header.createCell(1);
        cell1.setCellValue("平均查询时间");
        Cell cell2 = header.createCell(2);
        cell2.setCellValue("区块交易数:" + blockTxNum);
        rowNum++;
    }

    public void createPathSkyline(int blockTxNum){
        sheet=workbook.createSheet("sheet-" + sheetNum);
        sheetNum++;
        rowNum=0;
        Row header = sheet.createRow(rowNum);
        Cell cell = header.createCell(0);
        cell.setCellValue("索引");
        Cell cell1 = header.createCell(1);
        cell1.setCellValue("平均查询时间");
        Cell cell2 = header.createCell(2);
        cell2.setCellValue("区块交易数:" + blockTxNum);
        rowNum++;
    }




    public void createconstructsheet(int num){
        sheet = workbook.createSheet("sheet-" + sheetNum);
        sheetNum++;
        rowNum = 0;
        // 添加表头
        Row header = sheet.createRow(rowNum);
        Cell cell = header.createCell(0);
        cell.setCellValue("索引");
        Cell cell1 = header.createCell(1);
        cell1.setCellValue("构建时间");
        Cell cell2 = header.createCell(2);
        cell2.setCellValue("内存大小");
        Cell cell3 = header.createCell(3);
        cell3.setCellValue("矩阵时间");
        Cell cell4 = header.createCell(4);
        cell4.setCellValue("区块交易数量"+num);
        rowNum++;
    }

    public void createdetailsheet(int num){
        sheet = workbook.createSheet("sheet-" + sheetNum);
        sheetNum++;
        rowNum = 0;
        // 添加表头
        Row header = sheet.createRow(rowNum);
        Cell cell = header.createCell(0);
        cell.setCellValue("索引");
        Cell cell1 = header.createCell(1);
        cell1.setCellValue("查询类型");
        Cell cell2 = header.createCell(2);
        cell2.setCellValue("查询时间");
        Cell cell3 = header.createCell(3);
        cell3.setCellValue("区块交易数:" + num);
        rowNum++;
    }

    public void save_construct() {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 创建DateTimeFormatter对象并定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");

        // 格式化当前时间为指定格式的字符串
        String formattedDateTime = now.format(formatter);
        String filename = "./data/construct-" + formattedDateTime + ".xlsx";
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(filename);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save_details() {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 创建DateTimeFormatter对象并定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");

        // 格式化当前时间为指定格式的字符串
        String formattedDateTime = now.format(formatter);
        String filename = "./data/details-" + formattedDateTime + ".xlsx";
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(filename);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 创建DateTimeFormatter对象并定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");

        // 格式化当前时间为指定格式的字符串
        String formattedDateTime = now.format(formatter);
        String filename = "./data/data-" + formattedDateTime + ".xlsx";
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(filename);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save_node() {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 创建DateTimeFormatter对象并定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");

        // 格式化当前时间为指定格式的字符串
        String formattedDateTime = now.format(formatter);
        String filename = "./data/node-" + formattedDateTime + ".xlsx";
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(filename);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save_path() {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 创建DateTimeFormatter对象并定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");

        // 格式化当前时间为指定格式的字符串
        String formattedDateTime = now.format(formatter);
        String filename = "./data/path-" + formattedDateTime + ".xlsx";
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(filename);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void record_mtquery(String algorithm,ArrayList<Long> times) {
        Row row = sheet.createRow(rowNum);
        int i=0;
        Cell cell0=row.createCell(0);
        cell0.setCellValue(algorithm);
        for (long time:times)
        {
            Cell cell=row.createCell(i+1);
            cell.setCellValue(time);
            i++;
        }
        rowNum++;
    }

    public void create_mtquery_sheet(int num)
    {
        sheet = workbook.createSheet("sheet-" + sheetNum);
        sheetNum++;
        rowNum = 0;
        // 添加表头
        Row header = sheet.createRow(rowNum);
        Cell cell = header.createCell(0);
        cell.setCellValue("索引");
        for (int i=2;i<23;i+=2)
        {
            Cell cell_num = header.createCell(i/2);
            cell_num.setCellValue("p="+i);
        }
        Cell cell_last = header.createCell(12);
        cell_last.setCellValue("区块交易数量"+num);
        rowNum++;
    }

    public void save_mtquery() {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 创建DateTimeFormatter对象并定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");

        // 格式化当前时间为指定格式的字符串
        String formattedDateTime = now.format(formatter);
        String filename = "./data/mtquery-" + formattedDateTime + ".xlsx";
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(filename);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
