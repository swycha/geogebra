package org.geogebra.common.euclidian;

public class CoordSystemInfo {
	private EuclidianView view;
	private boolean axisZoom = false;
	private boolean centerView;

	public CoordSystemInfo(EuclidianView view) {
		this.view = view;
	}

	public double deltaX() {
		return view.xZero - view.xZeroOld;
	}

	public double deltaY() {
		return view.yZero - view.yZeroOld;
	}

	public boolean isAxisZoom() {
		return axisZoom;
	}

	public void setAxisZoom(boolean axisZoom) {
		this.axisZoom = axisZoom;
	}

	@Override
	public String toString() {
		return "CoordSystemInfo{"
				+ "dx: " + deltaX()
				+ ", dy: " + deltaY()
				+ ", axisZoom: " + isAxisZoom()
				+ '}';
	}

	public boolean isCenterVew() {
		return centerView;
	}

	public void setCenterView(boolean value) {
		this.centerView = value;
	}
}
