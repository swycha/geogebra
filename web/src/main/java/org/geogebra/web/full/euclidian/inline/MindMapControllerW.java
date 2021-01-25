package org.geogebra.web.full.euclidian.inline;

import static com.google.gwt.dom.client.Style.Visibility.HIDDEN;
import static com.google.gwt.dom.client.Style.Visibility.VISIBLE;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.inline.MindMapController;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoMindMap;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.richtext.EditorChangeListener;
import org.geogebra.web.richtext.impl.Carota;
import org.geogebra.web.richtext.impl.CarotaUtil;
import org.geogebra.web.richtext.impl.EventThrottle;
import org.geogebra.web.richtext.impl.HasContentAndFormat;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;

import elemental2.core.Global;

public class MindMapControllerW implements MindMapController {

	private final Element mindMapElement;
	private HasContentAndFormat mindMapImpl;

	private final Style style;
	private final EuclidianView view;
	private GeoMindMap mindMap;

	public MindMapControllerW(GeoMindMap map, EuclidianView view, Element parent) {
		CarotaUtil.ensureInitialized(view.getFontSize());
		mindMapElement = DOM.createDiv();
		mindMap = map;
		this.view = view;
		mindMapElement.addClassName("mowWidget");
		style = mindMapElement.getStyle();
		style.setProperty("transformOrigin", "0 0");
		parent.appendChild(mindMapElement);
		mindMapImpl = Carota.get().getMindMap().create(mindMapElement);
		mindMapImpl.init((int) map.getWidth(), (int) map.getHeight());
		updateContent();
		new EventThrottle(mindMapImpl).setListener(new EditorChangeListener() {
			@Override
			public void onContentChanged(String content) {
				onEditorChanged(content);
			}

			@Override
			public void onInput() {
				// not needed
			}

			@Override
			public void onSelectionChanged() {
				mindMap.getKernel().notifyUpdateVisualStyle(mindMap, GProperty.TEXT_SELECTION);
			}
		});
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

	private void setAngle(double angle) {
		style.setProperty("transform", "rotate(" + angle + "rad)");
	}

	private void setWidth(double width) {
		style.setWidth(width, Style.Unit.PX);
		//TODO tableImpl.setWidth(width);
	}

	private void setHeight(double height) {
		style.setHeight(height, Style.Unit.PX);
		//TODO tableImpl.setHeight(height);
	}

	private void setLocation(int x, int y) {
		style.setLeft(x, Style.Unit.PX);
		style.setTop(y, Style.Unit.PX);
	}

	@Override
	public void removeFromDom() {
		if (mindMapElement != null) {
			mindMapElement.removeFromParent();
		}
	}

	@Override
	public void toBackground() {
		if (style != null) {
			style.setVisibility(HIDDEN);
			mindMapImpl.stopEditing();
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
	public void saveContent() {
		mindMap.setContent(getContent());
	}

	@Override
	public void updateContent() {
		if (mindMap.getContent() != null && !mindMap.getContent().isEmpty()) {
			mindMapImpl.load(Global.JSON.parse(mindMap.getContent()), false);
			//updateSizes();
		}
	}

	@Override
	public void draw(GGraphics2D g2, GAffineTransform transform) {
		if (!isInEditMode()) {
			g2.saveTransform();
			g2.transform(transform);
			mindMapImpl.draw(((GGraphics2DW) g2).getContext());
			g2.restoreTransform();
		}
	}

	public boolean isInEditMode() {
		return VISIBLE.getCssName().equals(style.getVisibility());
	}

	@Override
	public void format(String key, Object val) {
		mindMapImpl.setFormatting(key, val);
	}

	@Override
	public <T> T getFormat(String key, T fallback) {
		return mindMapImpl.getFormatting(key, fallback);
	}

	@Override
	public String getHyperLinkURL() {
		return null;
	}

	@Override
	public void setHyperlinkUrl(String url) {
		mindMapImpl.setHyperlinkUrl(url);
	}

	@Override
	public String getHyperlinkRangeText() {
		return null;
	}

	@Override
	public void insertHyperlink(String url, String text) {
		mindMapImpl.insertHyperlink(url, text);
	}

	@Override
	public String getListStyle() {
		return null;
	}

	@Override
	public void switchListTo(String listType) {
		mindMapImpl.switchListTo(listType);
	}

	@Override
	public boolean copySelection() {
		return false;
	}

	@Override
	public void setSelectionText(String text) {
		mindMapImpl.insert(text);
	}

	private String getContent() {
		return Global.JSON.stringify(mindMapImpl.save());
	}

	private void onEditorChanged(String content) {
		if (!content.equals(mindMap.getContent())) {
			mindMap.setContent(content);
			view.getApplication().storeUndoInfo();
			mindMap.notifyUpdate();
		}
	}
}
