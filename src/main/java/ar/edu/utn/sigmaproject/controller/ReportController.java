package ar.edu.utn.sigmaproject.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

public class ReportController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Label informationLabel;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		informationLabel.setValue("Reportes");
	}
}
