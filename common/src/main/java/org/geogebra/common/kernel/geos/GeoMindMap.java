package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.draw.DrawInline;
import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.euclidian.inline.InlineTableController;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;

public class GeoMindMap extends GeoInline implements TextStyle, HasTextFormatter {

	private static final double MIN_WIDTH = 400;
	private static final double MIN_HEIGHT = 200;
	private String content;
	private boolean defined = true;

	public GeoMindMap(Construction cons, GPoint2D location) {
		super(cons);
		setLocation(location);
		setSize(MIN_WIDTH, MIN_HEIGHT);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.MIND_MAP;
	}

	@Override
	public GeoElement copy() {
		GeoElement copy = new GeoMindMap(cons, null);
		copy.set(this);
		return copy;
	}

	@Override
	public void set(GeoElementND geo) {
		if (geo instanceof GeoMindMap) {
			setLocation(new GPoint2D(((GeoMindMap) geo).getLocation().x,
					((GeoMindMap) geo).getLocation().y));
		}
	}

	@Override
	public boolean isDefined() {
		return defined;
	}

	@Override
	public void setUndefined() {
		defined = false;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return null;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public double getMinWidth() {
		return MIN_WIDTH;
	}

	@Override
	public double getMinHeight() {
		return MIN_HEIGHT;
	}

	@Override
	public int getFontStyle() {
		return GeoInlineText.getFontStyle(getFormatter());
	}

	@Override
	public double getFontSizeMultiplier() {
		return GeoText.getRelativeFontSize(GeoText.FONTSIZE_SMALL);
	}
}
