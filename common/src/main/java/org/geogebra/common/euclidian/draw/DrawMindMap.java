package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.MediaBoundingBox;
import org.geogebra.common.euclidian.inline.MindMapController;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoMindMap;

public class DrawMindMap extends Drawable implements DrawInline {
	private final TransformableRectangle rectangle;
	private final GeoMindMap mindMap;
	private MindMapController mindMapController;

	public DrawMindMap(EuclidianView ev, GeoMindMap geo) {
		super(ev, geo);
		rectangle = new TransformableRectangle(ev, geo, false);
		this.mindMap = geo;
		update();
	}

	@Override
	public void update() {
		rectangle.updateSelfAndBoundingBox();
		if (mindMapController == null && mindMap.getLocation() != null) {
			// make sure we don't initialize the controller during paste XML parsing
			// to avoid inconsistent state
			mindMapController = view.getApplication().createMindMapController(view, mindMap);
		}
		if (mindMapController != null) {
			mindMapController.update();
		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (geo.isEuclidianVisible() && mindMapController != null) {
			mindMapController.draw(g2, rectangle.getDirectTransform());
		}
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		return rectangle.hit(x, y);
	}

	@Override
	public boolean isInside(GRectangle rect) {
		return rect.contains(getBounds());
	}

	@Override
	public GRectangle getBounds() {
		return rectangle.getBounds();
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public void remove() {
		mindMapController.removeFromDom();
	}

	@Override
	public void updateContent() {

	}

	@Override
	public void toForeground(int x, int y) {
		if (mindMapController != null) {
			GPoint2D p = rectangle.getInversePoint(x, y);
			mindMapController.toForeground((int) p.getX(), (int) p.getY());
		}
	}

	@Override
	public void toBackground() {
		if (mindMapController != null) {
			mindMapController.toBackground();
		}
	}

	@Override
	public String urlByCoordinate(int x, int y) {
		return null;
	}

	@Override
	public void saveContent() {

	}

	@Override
	public GAffineTransform getTransform() {
		return rectangle.getDirectTransform();
	}

	@Override
	public void fromPoints(ArrayList<GPoint2D> points) {
		rectangle.fromPoints(points);
	}

	@Override
	protected List<GPoint2D> toPoints() {
		return rectangle.toPoints();
	}

	@Override
	public MediaBoundingBox getBoundingBox() {
		return rectangle.getBoundingBox();
	}

	@Override
	public MindMapController getController() {
		return mindMapController;
	}
}
