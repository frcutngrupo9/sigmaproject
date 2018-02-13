package ar.edu.utn.sigmaproject.domain;

import java.util.Arrays;
import java.util.List;

public class ReportType {
	private String value;
    private String label;
 
    public ReportType(String label, String value) {
        super();
        this.value = value;
        this.label = label;
    }
 
    public String getValue() {
        return value;
    }
 
    public String getLabel() {
        return label;
    }
    
    public static List<ReportType> getReportTypeList() {
    	return Arrays.asList(
                new ReportType("PDF", "pdf"), 
                new ReportType("HTML", "html"), 
                new ReportType("Word (RTF)", "rtf"), 
                new ReportType("Excel", "xls"), 
                new ReportType("Excel (JXL)", "jxl"), 
                new ReportType("CSV", "csv"), 
                new ReportType("OpenOffice (ODT)", "odt"));
    }
}
