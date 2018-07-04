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

package ar.edu.utn.sigmaproject.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.Duration;

import org.zkoss.image.AImage;
import org.zkoss.image.Image;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

import ar.edu.utn.sigmaproject.domain.Piece;
import ar.edu.utn.sigmaproject.domain.Product;
import ar.edu.utn.sigmaproject.domain.ReportType;

public class RenderElHelper {

	public static String getFormattedTime(Duration time) {
		if(time != null) {
			int hours = time.getHours();
			int minutes = time.getMinutes();
			while(minutes >= 60) {
				minutes -= 60;
				hours += 1;
			}
			return String.format("%d Hrs, %d Min", hours, minutes);
		}
		return "";
	}

	public static String getFormattedProcessTime(Duration time) {
		if(time != null) {
			int hours = time.getHours();
			int minutes = time.getMinutes();
			while(minutes >= 60) {
				hours = hours + 1;
				minutes = minutes - 60;
			}
			return String.format("%d hrs  %d min", hours, minutes);
		} else {
			return "0 hrs 0 min";
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ListModel listModel(List items) {
		return new ListModelList(items);
	}
	
	public static String getFormattedDate(Date date) {
		if(date != null) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			return dateFormat.format(date);
		}
		return "";
	}
	
	public static String getFormattedDateTime(Date date) {
		if(date != null) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			return dateFormat.format(date);
		}
		return "";
	}
	
	public static Image getProductImage(Product product) {
		return getImage(product.getImageData());
	}
	
	public static Image getPieceImage(Piece piece) {
		return getImage(piece.getImageData());
	}
	
	private static Image getImage(byte[] imageData) {
		Image img = null;
		if(imageData == null) {
			return img;
		}
		try {
			img = new AImage("", imageData);
		} catch (IOException exception) {}
		return img;
	}
	
	public static int getPixelScaled(org.zkoss.image.Image img, int maxValue, boolean isHeight) {
		if(isHeight) {
			return getHeightAndWidthScaled(img, maxValue)[0];
		} else {
			return getHeightAndWidthScaled(img, maxValue)[1];
		}
	}
	
	public static int[] getHeightAndWidthScaled(org.zkoss.image.Image img, int maxValue) {
		int[] heightAndWidthArray = new int[2];
		int height = img.getHeight();
		int width = img.getWidth();
		Double heightScaled = (double)maxValue;
		Double widthScaled = (double)maxValue;
		if(height > width) {
			widthScaled = ((double)width * (double)maxValue) / (double)height;
		} else if (height < width) {
			heightScaled = ((double)height * (double)maxValue) / (double)width;
		}
		heightAndWidthArray[0] = heightScaled.intValue();
		heightAndWidthArray[1] = widthScaled.intValue();
		return heightAndWidthArray;
	}
	
	public static ListModelList<ReportType> getReportTypeListModelList() {
		return new ListModelList<ReportType>(ReportType.getReportTypeList());
	}

}
