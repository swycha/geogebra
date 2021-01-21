package org.geogebra.web.full.euclidian.inline;

import static com.google.gwt.dom.client.Style.Visibility.HIDDEN;
import static com.google.gwt.dom.client.Style.Visibility.VISIBLE;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.inline.MindMapController;
import org.geogebra.common.kernel.geos.GeoMindMap;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.richtext.impl.Carota;
import org.geogebra.web.richtext.impl.CarotaTable;
import org.geogebra.web.richtext.impl.CarotaUtil;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;

public class MindMapControllerW implements MindMapController {

	// TODO common base class for this and
	private final Element tableElement;
	private CarotaTable tableImpl;

	private final Style style;
	private final EuclidianView view;
	private GeoMindMap mindMap;

	public MindMapControllerW(GeoMindMap map, EuclidianView view, Element parent) {
		CarotaUtil.ensureInitialized(view.getFontSize());
		tableElement = DOM.createDiv();
		mindMap = map;
		this.view = view;
		tableElement.addClassName("mowWidget");
		style = tableElement.getStyle();
		style.setProperty("transformOrigin", "0 0");
		parent.appendChild(tableElement);
		tableImpl = Carota.get().getMindMap().create(tableElement);
		tableImpl.init((int) map.getWidth(), (int) map.getHeight());
	}

	@Override
	public void update() {
		if (mindMap.getLocation() != null) {
			GPoint2D location = mindMap.getLocation();
			setLocation(view.toScreenCoordX(location.x),
					view.toScreenCoordY(location.y));
			setWidth(mindMap.getWidth());
			setHeight(mindMap.getHeight());
			setAngle(mindMap.getAngle());
		}
	}

	public void setAngle(double angle) {
		style.setProperty("transform", "rotate(" + angle + "rad)");
	}

	public void setWidth(double width) {
		style.setWidth(width, Style.Unit.PX);
		//TODO tableImpl.setWidth(width);
	}

	public void setHeight(double height) {
		style.setHeight(height, Style.Unit.PX);
		//TODO tableImpl.setHeight(height);
	}

	public void setLocation(int x, int y) {
		style.setLeft(x, Style.Unit.PX);
		style.setTop(y, Style.Unit.PX);
	}

	@Override
	public void removeFromDom() {
		if (tableElement != null) {
			tableElement.removeFromParent();
		}
	}

	@Override
	public void toBackground() {
		if (style != null) {
			style.setVisibility(HIDDEN);
			// TODO tableImpl.stopEditing();
			// TODO tableImpl.removeSelection();
		}
	}

	@Override
	public void toForeground(int x, int y) {
		if (style != null) {
			style.setVisibility(VISIBLE);
			// TODO tableImpl.startEditing(x, y);
		}
	}

	@Override
	public void draw(GGraphics2D g2, GAffineTransform transform) {
		if (!isInEditMode()) {
			g2.saveTransform();
			g2.transform(transform);
			tableImpl.draw(((GGraphics2DW) g2).getContext());
			g2.restoreTransform();
		}
	}

	public boolean isInEditMode() {
		return VISIBLE.getCssName().equals(style.getVisibility());
	}

	@Override
	public void format(String key, Object val) {
		tableImpl.setFormatting(key, val);
	}

	@Override
	public <T> T getFormat(String key, T fallback) {
		return tableImpl.getFormatting(key, fallback);
	}

	@Override
	public String getHyperLinkURL() {
		return null;
	}

	@Override
	public void setHyperlinkUrl(String url) {

	}

	@Override
	public String getHyperlinkRangeText() {
		return null;
	}

	@Override
	public void insertHyperlink(String url, String text) {

	}

	@Override
	public String getListStyle() {
		return null;
	}

	@Override
	public void switchListTo(String listType) {

	}

	@Override
	public boolean copySelection() {
		return false;
	}

	@Override
	public void setSelectionText(String text) {

	}
}
