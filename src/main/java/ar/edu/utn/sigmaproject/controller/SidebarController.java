/*
 * The MIT License
 *
 * Copyright 2014 gfzabarino.
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

import ar.edu.utn.sigmaproject.desktop.SigmaDesktopInit;

import java.util.Arrays;
import java.util.Stack;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Include;
import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.Treeitem;

/**
 *
 * @author gfzabarino
 */
public class SidebarController extends SelectorComposer<Component> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static String SIDEBAR_CONTROLLER_PAGE_ATTRIBUTE_NAME = "mainPage";

	@Wire 
	private Tree tree;

	public static Page mainPage() {
		return (Page)Executions.getCurrent().getDesktop().getAttribute(SIDEBAR_CONTROLLER_PAGE_ATTRIBUTE_NAME);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		Executions.getCurrent().getDesktop().setAttribute(SIDEBAR_CONTROLLER_PAGE_ATTRIBUTE_NAME, getPage());

		@SuppressWarnings("unchecked")
		TreeNode<MenuTreeRow> rootNode = new DefaultTreeNode<MenuTreeRow>(null, Arrays.asList(
				new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("", "Productos"), Arrays.asList(
						new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("/img/Paste.png", "Listado Productos", "/product_list.zul")),
						new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("/img/Stationery.png", "Crear Producto", "/product_creation.zul"))
						)),
						new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("", "Pedidos"), Arrays.asList(
								new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("", "Listado Pedidos", "/order_list.zul")),
								new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("", "Crear Pedido", "/order_creation.zul"))

								)),
								new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("", "Produccion"), Arrays.asList(
										new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("", "Listado Planes de Produccion", "/production_plan_list.zul")),
										new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("", "Crear Plan de Produccion", "/production_plan_creation.zul"))
										)),
										new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("", "Stock"), Arrays.asList(
												new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("", "Stock Productos", "/product_stock.zul")),
												new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("", "Stock Materia Prima", "/raw_material_stock.zul")),
												new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("", "Stock Insumos", "/supply_stock.zul")),
												new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("", "Listado Maquinas", "/machine_stock.zul"))
												)),
												new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("", "Clientes", "/client.zul")),
												new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("", "Materias Primas", "/raw_material.zul")),
												new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("", "Insumos", "/supply.zul")),
												new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("", "Maquinas", "/machine.zul")),
												new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("", "Herramientas", "/tool.zul")),
												new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("", "Procesos", "/process.zul")),
												new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("", "Empleados", "/worker.zul")),
												new DefaultTreeNode<MenuTreeRow>(new MenuTreeRow("", "Reportes"))
				));
		tree.setModel(new DefaultTreeModel<MenuTreeRow>(rootNode));
		tree.addEventListener(Events.ON_CLICK, new EventListener<MouseEvent>() {
			public void onEvent(MouseEvent event) throws Exception {
				Treeitem treeitem = tree.getSelectedItem();
				DefaultTreeNode<MenuTreeRow> treeNode = treeitem.<DefaultTreeNode<MenuTreeRow>>getValue();
				MenuTreeRow row = treeNode.getData();
				if (row.destination != null) {
					//redirect current url to new location
					if (row.destination.startsWith("http")) {
						//open a new browser tab
						Executions.getCurrent().sendRedirect(row.destination);
					} else {
						//use iterable to find the first include only
						Include include = (Include) Selectors.iterable(tree.getPage(), "#mainInclude")
								.iterator().next();

						// clear and set this destination as the first page
						Stack<String> pages = SigmaDesktopInit.pagesStack();
						pages.clear();
						pages.push(row.destination);

						include.setSrc(row.destination);
					}
				}
				if (!treeNode.isLeaf()) {
					treeitem.setOpen(!treeitem.isOpen());
				}
			}

		});

	}

	public class MenuTreeRow {
		private String image;
		private String name;
		private String destination;

		public MenuTreeRow(String image, String name, String destination) {
			this.image = image;
			this.name = name;
			this.destination = destination;
		}

		public MenuTreeRow(String image, String name) {
			this(image, name, null);
		}

		public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = image;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDestination() {
			return destination;
		}

		public void setDestination(String destination) {
			this.destination = destination;
		}
	}
}
