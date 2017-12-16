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

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.service.PieceRepository;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class PieceSelectionModalController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	Window modalDialog;
	@Wire
	Button closeButton;
	@Wire
	Listbox pieceListbox;

	// services
	@WireVariable
	private PieceRepository pieceRepository;

	// atributes

	// list
	private List<Piece> pieceList;

	// list models
	private ListModelList<Piece> pieceListModel;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		pieceList = pieceRepository.findAll();
		pieceListModel = new ListModelList<>(pieceList);
		pieceListbox.setModel(pieceListModel);
	}

	@Listen("onSelect = #pieceListbox")
	public void doListBoxSelect() {
		Piece selected_piece = pieceListbox.getSelectedItem().getValue();
		EventQueue<Event> eq = EventQueues.lookup("Piece Selection Queue", EventQueues.DESKTOP, true);
		eq.publish(new Event("onPieceSelect", null, selected_piece));
		modalDialog.detach();
	}

	@Listen("onClick = #closeButton")
	public void doCloseModal() {
		modalDialog.detach();
	}
}
