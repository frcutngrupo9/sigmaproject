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
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Include;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.util.ReportType;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ReportSelectionModalController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Window reportSelectionModalWindow;
	@Wire
	Listbox reportTypeListbox;
	@Wire
	Button acceptButton;
	@Wire
	Button cancelButton;

	// atributes
	private JRDataSource jRDataSource;
	private String returnPageName;
	private String reportSrcName;
	private Map<String, Object> reportParameters;
	private Map<String, Object> returnParameters;

	@SuppressWarnings("unchecked")
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		jRDataSource = (JRDataSource) Executions.getCurrent().getAttribute("jr_datasource");
		returnPageName = (String) Executions.getCurrent().getAttribute("return_page_name");
		reportSrcName = (String) Executions.getCurrent().getAttribute("report_src_name");
		reportParameters = (Map<String, Object>) Executions.getCurrent().getAttribute("report_parameters");
		returnParameters = (Map<String, Object>) Executions.getCurrent().getAttribute("return_parameters");
	}

	@Listen("onClick = #acceptButton")
	public void acceptButtonClick() {
		if(reportTypeListbox.getSelectedItem() != null) {
			ReportType reportType = (ReportType)(reportTypeListbox.getSelectedItem().getValue());
			Executions.getCurrent().setAttribute("report_type", reportType.getValue());

			Executions.getCurrent().setAttribute("jr_datasource", jRDataSource);
			Executions.getCurrent().setAttribute("return_page_name", returnPageName);
			Executions.getCurrent().setAttribute("report_src_name", reportSrcName);
			Executions.getCurrent().setAttribute("report_parameters", reportParameters);
			Executions.getCurrent().setAttribute("return_parameters", returnParameters);
			
			Include include = (Include) Selectors.iterable(this.getPage(), "#mainInclude").iterator().next();
			include.setSrc("/report_viewer.zul");
			reportSelectionModalWindow.detach();
		} else {
			Clients.showNotification("Tipo de reporte no seleccionado");
		}
	}

	@Listen("onClick = #cancelButton")
	public void cancelButtonClick() {
		reportSelectionModalWindow.detach();
	}
}
