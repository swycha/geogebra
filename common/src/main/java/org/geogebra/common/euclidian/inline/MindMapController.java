package org.geogebra.common.euclidian.inline;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.draw.HasTextFormat;

public interface MindMapController extends HasTextFormat {
	void update();

	void draw(GGraphics2D g2, GAffineTransform directTransform);

	void removeFromDom();

	void toBackground();

	void toForeground(int x, int y);

	void saveContent();

	void updateContent();
}
