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

package ar.edu.utn.sigmaproject.desktop;

import java.util.Stack;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.DesktopInit;

/**
 *
 * @author gfzabarino
 */
public class SigmaDesktopInit implements DesktopInit {
    
    private static String PAGES_STACK_ATTRIBUTE_NAME = "pages";

    public void init(Desktop desktop, Object request) throws Exception {
        desktop.setAttribute(PAGES_STACK_ATTRIBUTE_NAME, new Stack<String>());
    }
    
    @SuppressWarnings("unchecked")
	public static Stack<String> pagesStack() {
        return (Stack<String>)Executions.getCurrent().getDesktop().getAttribute(SigmaDesktopInit.PAGES_STACK_ATTRIBUTE_NAME);
    }
    
}
