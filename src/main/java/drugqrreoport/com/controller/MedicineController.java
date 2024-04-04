package drugqrreoport.com.controller;



import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

 
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

 

 



@RestController
@RequestMapping("/report")
public class MedicineController {
	
	
	@Autowired
	private DataSource datasource;
	
	@Autowired
	private JdbcTemplate jdbctemplate;



	//MAIN API
	
	@GetMapping("/QR_CODE_SHW/{stockNo}/{print_qty}")
	public void disCharge(HttpServletResponse response, @PathVariable("stockNo") String StockNo,
														@PathVariable("print_qty") String print_qty)
	        throws IOException, JRException, SQLException {

	    // First, establish a connection to your database using Spring's JdbcTemplate
	    try (Connection con = jdbctemplate.getDataSource().getConnection()) {

	        // Check if a valid admission number was provided in the URL
	        if (StockNo != null) {

	            
	       // 	InputStream reportStream = getClass().getResourceAsStream("/MMI_QRINFO.jasper"); // PREVIOUS REPORT
	        	InputStream reportStream = getClass().getResourceAsStream("/MMI_QRINFO_N.jasper");
	        	
	            // Set any parameters that need to be passed to the Jasper report
	            Map<String, Object> parameters = new HashMap<String, Object>();
	            parameters.put("stock_no", StockNo);
	            parameters.put("PRNT_QTY", print_qty);
	  // 		  Generate the Jasper report based on the template and input parameters
	 //           JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, parameters, con);
	            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, con);
	// 			  Export the Jasper report as a PDF document and write it to the HTTP response
	            JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
	            response.setContentType("application/pdf");
	            response.addHeader("Content-Disposition", "inline; filename=jasper.pdf;");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	 
	@GetMapping(value="/QR_CODE_DWN/{stockNo}/{print_qty}", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport(@PathVariable String stockNo,
												 @PathVariable("print_qty") String print_qty) 
			throws Exception {
	    
	     Connection con = jdbctemplate.getDataSource().getConnection();
	    
	     String sourceFileName = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "MMI_QRINFO.jasper")
	             .getAbsolutePath();
	     
	   // 	Load the SolaimanLipi font file from the classpath
	  //   	InputStream fontStream = MedicineController.class.getClassLoader().getResourceAsStream("fonts/SolaimanLipi.ttf");

	     Map<String, Object> parameters = new HashMap<>();
	     parameters.put("stock_no", stockNo);
	     parameters.put("PRNT_QTY", print_qty);
	     // Embed the SolaimanLipi font in the report
	    // parameters.put("REPORT_FONT", Font.createFont(Font.TRUETYPE_FONT, fontStream));
	     
	     JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, parameters, con);
	    
	     byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

	     
	     HttpHeaders headers = new HttpHeaders();
	     headers.setContentType(MediaType.APPLICATION_PDF);
	     headers.setContentDispositionFormData("attachment", "stockInfo.pdf");
	     headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

	     return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	}
	
	
	
	@GetMapping("/personal_info")
	public void personalInfo(HttpServletResponse response)
	        throws IOException, JRException, SQLException {

	    // First, establish a connection to your database using Spring's JdbcTemplate
	    try (Connection con = jdbctemplate.getDataSource().getConnection()) {
	    	String fontName = "Bradley Hand ITC";

            // Load the JasperReports template
            InputStream reportStream = MedicineController.class.getResourceAsStream("/personal_info.jasper");
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, null, con);
	            JRStyle[] styles = jasperPrint.getStyles();
	            for (JRStyle style : styles) {
	                style.setFontName(fontName);
	                style.setPdfFontName(fontName);
	            }
            
	            JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
	            response.setContentType("application/pdf");
	            response.addHeader("Content-Disposition", "inline; filename=jasper.pdf;");
	       
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	
	@GetMapping("/welcome")
	public String test()  { 
		
		return "Welcome to Rest API...";
		
		
	}
	
	


	
	
 
 
  
	
	
	
}
