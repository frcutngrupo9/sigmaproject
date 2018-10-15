/*
 * The MIT License
 *
 * Copyright (C) 2017 SigmaProject.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ar.edu.utn.sigmaproject.controller;

import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkex.zul.Jasperreport;
import org.zkoss.zul.Button;
import org.zkoss.zul.Include;

public class ReportViewerController  extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Button cancelButton;
	@Wire
	Jasperreport viewJasperreport;

	// atributes
	private JRDataSource jRDataSource;
	private String returnPageName;
	private Map<String, Object> returnParameters;
	private String reportSrcName;
	private String reportType;
	private Map<String, Object> reportParameters;

	// services

	// list

	// list models

	@SuppressWarnings("unchecked")
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		jRDataSource = (JRDataSource) Executions.getCurrent().getAttribute("jr_datasource");
		if(jRDataSource == null) {throw new RuntimeException("jRDataSource not found");}
		returnPageName = (String) Executions.getCurrent().getAttribute("return_page_name");
		if(returnPageName == null) {throw new RuntimeException("returnPageName not found");}
		reportSrcName = (String) Executions.getCurrent().getAttribute("report_src_name");
		if(reportSrcName == null) {throw new RuntimeException("reportSrcName not found");}
		reportType = (String) Executions.getCurrent().getAttribute("report_type");
		if(reportType == null) {throw new RuntimeException("reportType not found");}
		reportParameters = (Map<String, Object>) Executions.getCurrent().getAttribute("report_parameters");
		if(reportParameters == null) {throw new RuntimeException("reportParameters not found");}

		returnParameters = (Map<String, Object>) Executions.getCurrent().getAttribute("return_parameters");

		loadJasperreport();
	}

	private void loadJasperreport() {
		viewJasperreport.setSrc("/jasperreport/"+ reportSrcName + ".jasper");
		viewJasperreport.setParameters(reportParameters);
		viewJasperreport.setType(reportType);
		viewJasperreport.setDatasource(jRDataSource);
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		if(returnParameters != null) {
			for (Map.Entry<String, Object> entry : returnParameters.entrySet()) {
				Executions.getCurrent().setAttribute(entry.getKey(), entry.getValue());
			}
		}
		Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
		include.setSrc("/"+ returnPageName + ".zul");
	}
}