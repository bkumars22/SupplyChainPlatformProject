/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags;

public abstract class BaseClickableTagModel extends BaseTagModel implements ClickableTagModel {
	protected String label_;
	protected String target_;
	protected String tooltip_;
	protected String onClick_;
	protected String onMouseOver_;
	protected String onMouseOut_;

	protected BaseClickableTagModel(String id, String onClick, String label, String target, String tooltip) {
		super(id, false, true);
		this.onClick_ = onClick;
		this.label_ = label;
		this.target_ = target;
		this.tooltip_ = tooltip;
	}

	public final String getLabel() {
		return this.label_;
	}

	public final void setLabel(String label) {
		this.label_ = label;
	}

	public final String getOnClick() {
		return this.onClick_;
	}

	public final void setOnClick(String onClick) {
		this.onClick_ = onClick;
	}

	public final String getOnMouseOver() {
		return this.onMouseOver_;
	}

	public final void setOnMouseOver(String onMouseOver) {
		this.onMouseOver_ = onMouseOver;
	}

	public final String getOnMouseOut() {
		return this.onMouseOut_;
	}

	public final void setOnMouseOut(String onMouseOut) {
		this.onMouseOut_ = onMouseOut;
	}

	public final String getTarget() {
		return this.target_;
	}

	public final void setTarget(String target) {
		this.target_ = target;
	}

	public final String getTooltip() {
		return this.tooltip_;
	}

	public void setTooltip(String tooltip) {
		this.tooltip_ = tooltip;
	}

	public static String getTextLink(ClickableTagModel model, boolean addRollover) {
		StringBuffer sb = new StringBuffer("<a href=\"");
		sb.append(model.getOnClick());
		sb.append("\"");
		String onMouseOut = null;
		if (addRollover) {
			if (model.getOnMouseOver() != null) {
				sb.append(" onmouseover=\"");
				sb.append(model.getOnMouseOver());
				sb.append("\"");
			}

			if (model.getOnMouseOut() != null) {
				sb.append(" onmouseout=\"");
				sb.append(model.getOnMouseOut());
				sb.append("\"");
			} else if (model.getOnMouseOver() != null) {
				sb.append(" onmouseout=\"javascript:self.status='';return true;\"");
			}
		}

		if (model.getTarget() != null) {
			sb.append(" target=\"");
			sb.append(model.getTarget());
			sb.append("\"");
		}

		if (model.getTooltip() != null) {
			sb.append(" title=\"");
			sb.append(model.getTooltip());
			sb.append("\"");
		}

		sb.append(">");
		sb.append(model.getLabel());
		sb.append("</a>");
		return sb.toString();
	}
}