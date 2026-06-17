/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.renderers.puncto;

import com.scplatform.testing.webui.jtags.TagModel;
import com.scplatform.testing.webui.jtags.TagRenderer;
import com.scplatform.testing.webui.jtags.TagRendererException;
import com.scplatform.testing.webui.jtags.TagRendererFactory;
import com.scplatform.testing.webui.jtags.button.ButtonbarDivider;
import com.scplatform.testing.webui.jtags.button.SimpleButton;
import com.scplatform.testing.webui.jtags.button.SimpleButtonbar;
import com.scplatform.testing.webui.jtags.wizard.SimpleStep;
import com.scplatform.testing.webui.jtags.wizard.Step;
import com.scplatform.testing.webui.jtags.wizard.Wizard;
import com.scplatform.testing.webui.taglib.Settings;
import java.io.IOException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.BodyContent;

public class WizardTagRenderer implements TagRenderer {
	public void render(TagModel model, BodyContent tagBody, JspWriter out, Settings settings) throws JspException {
		if (model.isVisible()) {
			Wizard wizard = (Wizard) model;

			try {
				this.startWizard(wizard, settings, out);
				this.createTitleBar(wizard, settings, out);
				this.createSteps(wizard, settings, out);
				this.startContentArea(wizard, settings, out);
				if (tagBody != null) {
					tagBody.writeOut(out);
				}

				this.closeContentArea(wizard, settings, out);
				this.createFooter(wizard, settings, out);
				this.closeWizard(wizard, settings, out);
			} catch (IOException var7) {
				throw new JspException(var7.getMessage());
			}
		}
	}

	protected void startWizard(Wizard wizard, Settings settings, JspWriter out) throws IOException {
		out.write(
				"<table nowrap style=\"border-right:1px solid #cccccc; border-left:1px solid #cccccc;\" width=\"100%\" height=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
	}

	protected void closeWizard(Wizard wizard, Settings settings, JspWriter out) throws IOException {
		out.write("</table>");
	}

	protected void createTitleBar(Wizard wizard, Settings settings, JspWriter out) throws IOException {
		Step theStep = wizard.get(wizard.getCurrentStep());
		out.write("<tr valign=\"top\"><td class=\"containerHeader\" id=\"containerOuter\" nowrap colspan=\"2\">");
		out.write("&nbsp;");
		out.write(wizard.getName());
		out.write(":&nbsp;");
		if (wizard.isSummaryStep()) {
			out.write(settings.getResourceBundle().getString("wizard.summaryTitle"));
		} else if (wizard.isFinishStep()) {
			out.write(settings.getResourceBundle().getString("wizard.finishedTitle"));
		} else if (wizard.isCancelStep()) {
			out.write(settings.getResourceBundle().getString("wizard.cancelledTitle"));
		} else {
			out.write(theStep.getName());
			out.write(" <span style=\"font-weight:normal\">(");
			out.print(wizard.getCurrentStep() + 1);
			out.write(" ");
			out.write(settings.getResourceBundle().getString("wizard.stepOf"));
			out.write(" ");
			out.print(wizard.getTotalStepCount());
			out.write(")</span>");
			out.write("</td></tr>");
		}

	}

	protected void createSteps(Wizard wizard, Settings settings, JspWriter out) throws IOException {
		out.write(
				"<tr bgcolor=\"f7f8fd\" height=\"100%\"><td class=\"shadow\" valign=\"top\" align=\"left\" width=\"25%\">");
		out.write("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
		Step theStep = null;

		for (int i = 0; i < wizard.getTotalStepCount(); ++i) {
			if (i == wizard.getRequiredStepCount() && wizard.getRequiredStepCount() < wizard.getTotalStepCount()) {
				SimpleStep optionalStep = new SimpleStep("Optional Steps:", false);
				optionalStep.setName(settings.getResourceBundle().getString("wizard.optionalLabel"));
				this.createStep(wizard, settings, optionalStep, wizard.getTotalStepCount(), out, true);
			}

			theStep = wizard.get(i);
			this.createStep(wizard, settings, theStep, i, out, false);
		}

		out.write("</table></td>");
	}

	protected void createStep(Wizard wizard, Settings settings, Step theStep, int stepIndex, JspWriter out,
			boolean isOptionLabel) throws IOException {
		String img = this.getStepImage(wizard, stepIndex, isOptionLabel);
		String styleClass = null;
		if (stepIndex == wizard.getCurrentStep() && !wizard.isSummaryStep() && !wizard.isFinishStep()
				&& !wizard.isCancelStep()) {
			if (stepIndex < wizard.getRequiredStepCount()) {
				styleClass = "wizardStepSelected";
			} else {
				styleClass = "wizardStepSelectedOptional";
			}
		} else if (stepIndex < wizard.getRequiredStepCount()) {
			styleClass = "wizardStep";
		} else {
			styleClass = "wizardStepOptional";
		}

		out.write("<tr class=\"");
		out.write(styleClass);
		if (isOptionLabel) {
			out.write("\" style=\"font-weight:bold");
		}

		out.write("\"><td valign=\"top\" width=\"100%\" nowrap><img src=\"");
		out.write(settings.getImageDirectory());
		out.write(img);
		out.write("\" align=\"top\">");
		out.write(theStep.getName());
		out.write("</td></tr>");
	}

	protected String getStepImage(Wizard wizard, int stepIndex, boolean isOptionLabel) {
		if (isOptionLabel) {
			return "/step_opt_sep.png";
		} else {
			String img = null;
			if (!wizard.isCancelStep()
					&& (stepIndex < wizard.getCurrentStep() || wizard.isSummaryStep() || wizard.isFinishStep())) {
				img = "/step_inbetwn_complete.png";
				if (this.isFirstStep(stepIndex)) {
					img = "/step_first_complete.png";
				} else if (this.isLastStep(wizard, stepIndex)) {
					img = "/step_last_complete.png";
				}
			} else if (stepIndex <= wizard.getCurrentStep() && !wizard.isCancelStep()) {
				img = "/step_inbetwn_current.png";
				if (this.isFirstStep(stepIndex)) {
					img = "/step_first_current.png";
				} else if (this.isLastStep(wizard, stepIndex)) {
					img = "/step_last_current.png";
				}
			} else {
				img = "/step_inbetwn_incomplete.png";
				if (this.isFirstStep(stepIndex)) {
					img = "/step_first_incomplete.png";
				} else if (this.isLastStep(wizard, stepIndex)) {
					img = "/step_last_incomplete.png";
				}
			}

			return img;
		}
	}

	protected boolean isFirstStep(int stepIndex) {
		return stepIndex == 0;
	}

	protected boolean isLastStep(Wizard wizard, int stepIndex) {
		return stepIndex == wizard.getTotalStepCount() - 1;
	}

	protected void startContentArea(Wizard wizard, Settings settings, JspWriter out) throws IOException {
		String bgcolor = "ffffff";
		if (wizard.isSummaryStep() || wizard.isCancelStep() || wizard.isFinishStep()) {
			bgcolor = "f7f8fd";
		}

		out.write("<td bgcolor=\"");
		out.write(bgcolor);
		out.write("\" valign=\"top\" align=\"left\" width=\"75%\"");
		out.write(" style=\"padding-left:8px;padding-right:8px;padding-bottom:8px;\">");
	}

	protected void closeContentArea(Wizard wizard, Settings settings, JspWriter out) throws IOException {
		out.write("</td></tr>");
	}

	protected void createFooter(Wizard wizard, Settings settings, JspWriter out) throws IOException, JspException {
		out.write(
				"<tr><td nowrap width=\"100%\" class=\"containerFooter\" id=\"containerOuter\" colspan=\"2\" align=\"right\">");
		out.write("<table border=\"0\" cellspacing=\"1\" cellpadding=\"0\"><tr>");
		if (!wizard.isFinishStep() && !wizard.isCancelStep()) {
			SimpleButton summaryBtn = new SimpleButton("wizsummary", "javascript:summary()", (String) null);
			summaryBtn.setPadded(true);
			SimpleButton cancelBtn = new SimpleButton("wizcancel", "javascript:cancel()", (String) null);
			cancelBtn.setPadded(true);
			SimpleButton prevBtn = new SimpleButton("wizprev", "javascript:previous()", (String) null);
			prevBtn.setPadded(true);
			SimpleButton nextBtn = new SimpleButton("wiznext", "javascript:next()", (String) null);
			nextBtn.setPadded(true);
			SimpleButton finishBtn = new SimpleButton("wizfinish", "javascript:finish()", (String) null);
			finishBtn.setPadded(true);
			int nextToLastStep = wizard.getTotalStepCount() - 1;
			int nextToLastRequiredStep = wizard.getRequiredStepCount() - 1;
			boolean readyToFinish = wizard.getCurrentStep() >= nextToLastRequiredStep;
			SimpleButtonbar bar = new SimpleButtonbar("wizbar");
			bar.setStandalone(false);
			String btnLabel = null;
			if (wizard.isSummaryAvailable()) {
				btnLabel = settings.getResourceBundle().getString("wizard.viewSummary");
				summaryBtn.setLabel(btnLabel);
				summaryBtn.setOnMouseOver("self.status='" + btnLabel + "';return true;");
				if (!wizard.isSummaryStep() && readyToFinish) {
					summaryBtn.setDisabled(false);
				} else {
					summaryBtn.setDisabled(true);
				}

				bar.add(summaryBtn);
				bar.add(ButtonbarDivider.DIVIDER);
			}

			btnLabel = settings.getResourceBundle().getString("wizard.cancel");
			cancelBtn.setLabel(btnLabel);
			cancelBtn.setOnMouseOver("self.status='" + btnLabel + "';return true;");
			bar.add(cancelBtn);
			bar.add(ButtonbarDivider.DIVIDER);
			btnLabel = settings.getResourceBundle().getString("wizard.previous");
			prevBtn.setLabel(btnLabel);
			prevBtn.setOnMouseOver("self.status='" + btnLabel + "';return true;");
			prevBtn.setDisabled(wizard.getCurrentStep() <= 0);
			bar.add(prevBtn);
			btnLabel = settings.getResourceBundle().getString("wizard.next");
			nextBtn.setLabel(btnLabel);
			nextBtn.setOnMouseOver("self.status='" + btnLabel + "';return true;");
			nextBtn.setDisabled(false);
			nextBtn.setEmphasized(true);
			if (readyToFinish) {
				nextBtn.setEmphasized(false);
			}

			if (wizard.isSummaryStep() || wizard.getCurrentStep() >= nextToLastStep) {
				nextBtn.setDisabled(true);
			}

			bar.add(nextBtn);
			bar.add(ButtonbarDivider.DIVIDER);
			btnLabel = settings.getResourceBundle().getString("wizard.finish");
			finishBtn.setLabel(btnLabel);
			finishBtn.setOnMouseOver("self.status='" + btnLabel + "';return true;");
			finishBtn.setDisabled(true);
			finishBtn.setEmphasized(false);
			if (readyToFinish || wizard.isSummaryStep()) {
				finishBtn.setDisabled(false);
				finishBtn.setEmphasized(true);
			}

			bar.add(finishBtn);

			try {
				TagRenderer buttonbarRenderer = TagRendererFactory.getRenderer("buttonbar");
				buttonbarRenderer.render(bar, (BodyContent) null, out, settings);
			} catch (TagRendererException var15) {
				throw new JspException(var15.getMessage());
			}

			out.write("</tr></table></td></tr>");
		} else {
			out.write("<td>&nbsp;</td></tr></table></td></tr>");
		}
	}

	public String toString() {
		return "Puncto wizard tag renderer";
	}
}