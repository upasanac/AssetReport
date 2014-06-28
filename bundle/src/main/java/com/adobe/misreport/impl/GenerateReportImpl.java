package com.adobe.misreport.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.activation.DataSource;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.mail.util.ByteArrayDataSource;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.misreport.GenerateReport;
import com.adobe.misreport.QueryService;
import com.adobe.misreport.UserManagement;
import com.adobe.misreport.constants.MISReportConstant;
import com.day.cq.search.result.SearchResult;

@Component(immediate=true,metatype=false)
@Service(GenerateReport.class)
public class GenerateReportImpl implements GenerateReport {

	@Reference
	QueryService queryService;
	
	@Reference
	UserManagement userManagement;	
	
	private static final Logger log = LoggerFactory.getLogger(GenerateReport.class);
	
	@Override
	public DataSource generateReport(String lowerBoundDate, String upperBoundDate, String reportType, Session session) throws Exception {
		
		log.info("Processing [GenerateReport] service to build the Asset Report");
		
		//first get the assert result from the repository
		SearchResult resultNodes = queryService.executeQuery(lowerBoundDate, upperBoundDate, reportType, session);
		
		//Generate data to be written in the file
        Map<String, Object[]> resultData = new LinkedHashMap<String, Object[]>();
        
        String reportSheetName = MISReportConstant.DEFAULT_SHEET_NAME;
       
        if(reportType.equals(MISReportConstant.ASSET_ADDED)) {
        	
        	reportSheetName = MISReportConstant.ASSET_ADDED_SHEET_NAME;
	        resultData.put("1", new Object[] {
								        		MISReportConstant.FIELD_TITLE,
								        		MISReportConstant.FIELD_TYPE,
								        		MISReportConstant.FIELD_SIZE,
								        		MISReportConstant.FIELD_ADDED,
								        		MISReportConstant.FIELD_ADDED_BY,
								        		MISReportConstant.FIELD_PATH
	        				});
		
        }
        else if(reportType.equals(MISReportConstant.ASSET_MODIFIED)) {
        	reportSheetName = MISReportConstant.ASSET_MODIFIED_SHEET_NAME;
	        resultData.put("1", new Object[] {
								        		MISReportConstant.FIELD_TITLE,
								        		MISReportConstant.FIELD_TYPE,
								        		MISReportConstant.FIELD_SIZE,
								        		MISReportConstant.FIELD_MODIFIED,
								        		MISReportConstant.FIELD_MODIFIED_BY,
								        		MISReportConstant.FIELD_PATH
	        				});
		
        }
		
		
        resultData = buildAssetReport (resultNodes, reportType, resultData, session);
        
        XSSFWorkbook workbook = writeDataMapToFile(resultData, reportSheetName);
	    
	    ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
	    workbook.write(outByteStream);
        
	    DataSource reportDataSource = new ByteArrayDataSource(outByteStream.toByteArray(), "application/vnd.ms-excel");
	    outByteStream.flush();
	    outByteStream.close();
        
	    log.info("Finished processing [GenerateReport] Service >>>");
	    return reportDataSource;
	
	}

	private Map<String, Object[]> buildAssetReport (SearchResult resultNodes, String reportType, Map<String, Object[]> resultData, Session session) throws Exception {
		
		Iterator<Node> assetNodeIterator = resultNodes.getNodes();
		
		while(assetNodeIterator.hasNext()) {
			Node assetNode = assetNodeIterator.next();
			String [] reportLineItem = extractAssetNodeDetails(assetNode, reportType, session); 
			
			resultData.put(resultData.size()+1 +"", reportLineItem);
		}
	
		return resultData;
	}
	
	
	private String[] extractAssetNodeDetails(Node assetNode, String reportType, Session session) throws Exception {
		
		String[] employeeBDODataArray = new String[6];
		
		employeeBDODataArray[0] = assetNode.getName();
		
		if(assetNode.hasProperty("jcr:content/dam:s7damType")) {
			employeeBDODataArray[1] = assetNode.getProperty("jcr:content/dam:s7damType").getValue().getString();
		}
						
		employeeBDODataArray[2] =  "100.00 KB";
		
		if(reportType.equals(MISReportConstant.ASSET_ADDED)) {
			employeeBDODataArray[3] = assetNode.getProperty("jcr:created").getValue().getString();
			String authorID = assetNode.getProperty("jcr:createdBy").getValue().getString();
			employeeBDODataArray[4] = userManagement.getUserName(authorID, session);
		}
		else if(reportType.equals(MISReportConstant.ASSET_MODIFIED)) {
			employeeBDODataArray[3] = assetNode.getProperty("jcr:content/jcr:lastModified").getValue().getString();
			String authorID = assetNode.getProperty("jcr:content/jcr:lastModifiedBy").getValue().getString();
			employeeBDODataArray[4] = userManagement.getUserName(authorID, session);		
		}
		
		employeeBDODataArray[5] = assetNode.getPath();
		
		
		return employeeBDODataArray;
	}
	
	private XSSFWorkbook writeDataMapToFile( Map<String, Object[]> resultData, String reportSheetName) throws IOException, Exception {
    	//Blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook(); 
		
		//Create a blank sheet
		XSSFSheet sheet = workbook.createSheet(reportSheetName);
				
		//Iterate over data and write to sheet
		Set<String> keyset = resultData.keySet();
		int rownum = 0;
		for (String key : keyset)
		{
		    Row row = sheet.createRow(rownum++);
		    Object [] objArr = resultData.get(key);
		    int cellnum = 0;
		    for (Object obj : objArr)
		    {
		       Cell cell = row.createCell(cellnum++);
		       if(obj instanceof String)
		            cell.setCellValue((String)obj);
		        else if(obj instanceof Integer)
		            cell.setCellValue((Integer)obj);
		       
		    }
		}
	
		styleExcelSheet(workbook, sheet);
		
		return workbook;    	
	}
	 
	private void styleExcelSheet(XSSFWorkbook workbook, Sheet sheet) throws Exception{
	    	
    	//Define style for the report header
    	CellStyle  headerStyle = workbook.createCellStyle();
      
    	headerStyle.setFillForegroundColor(HSSFColor.GREEN.index);
    	headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	headerStyle.setWrapText(true);
      	
    	//every sheet has rows, iterate over them
        Iterator<Row> rowIterator = sheet.iterator();
        
        //Style only the Report Header
        if (rowIterator.hasNext()) {
        	//Get the row object
            Row row = rowIterator.next();
            //Every row has columns, get the column iterator and iterate over them
            Iterator<Cell> cellIterator = row.cellIterator();        
            while (cellIterator.hasNext()) {
                //Get the Cell object
                Cell cell = cellIterator.next();
                cell.setCellStyle(headerStyle);
            }         
        }
        
       //Style for the Report cells
    	CellStyle  cellStyle = workbook.createCellStyle();
    	cellStyle.setWrapText(true);
        
        while (rowIterator.hasNext()) {
        	//Get the row object
            Row row = rowIterator.next();
            //Every row has columns, get the column iterator and iterate over them
            Iterator<Cell> cellIterator = row.cellIterator();        
            while (cellIterator.hasNext()) {
                //Get the Cell object
                Cell cell = cellIterator.next();
                cell.setCellStyle(cellStyle);
            }         
        }
        
        //Auto-size the columns
        for (int i=0; i<6; i++) {
        	sheet.autoSizeColumn(i);
        }
        
	}
	    
	
}
