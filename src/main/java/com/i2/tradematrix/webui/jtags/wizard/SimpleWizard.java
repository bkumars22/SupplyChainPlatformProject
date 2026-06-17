/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.wizard;

import com.scplatform.testing.webui.jtags.BaseTagModel;
import java.util.ArrayList;

public class SimpleWizard extends BaseTagModel implements Wizard {
	protected ArrayList steps_;
	protected int currentStep_;
	protected int numRequiredSteps_;
	protected boolean summaryAvailable_;
	protected boolean isSummaryStep_;
	protected boolean isCancelStep_;
	protected boolean isFinishStep_;

	public SimpleWizard() {
		this("", false);
	}

	public SimpleWizard(String name, boolean isSummaryAvailable) {
		super(name, false, true);
		this.steps_ = null;
		this.currentStep_ = 0;
		this.numRequiredSteps_ = 0;
		this.summaryAvailable_ = false;
		this.isSummaryStep_ = false;
		this.isCancelStep_ = false;
		this.isFinishStep_ = false;
		this.summaryAvailable_ = isSummaryAvailable;
		this.steps_ = new ArrayList();
		this.currentStep_ = 0;
		this.numRequiredSteps_ = 0;
	}

	public String getName() {
		return this.getId();
	}

	public int getTotalStepCount() {
		return this.steps_.size();
	}

	public int getRequiredStepCount() {
		return this.numRequiredSteps_;
	}

	public int getCurrentStep() {
		return this.currentStep_;
	}

	public void setCurrentStep(int currentStep) {
		if (currentStep >= this.getTotalStepCount()) {
			throw new IllegalArgumentException("Cannot set wizard to non-existent step");
		} else {
			this.currentStep_ = currentStep;
		}
	}

	public Step get(int stepIndex) {
		return (Step) this.steps_.get(stepIndex);
	}

	public Step get(String stepName) {
		int pos = this.findStep(stepName);
		return pos < 0 ? null : (Step) this.steps_.get(pos);
	}

	public void add(Step aStep) {
		int pos = this.findStep(aStep);
		if (pos < 0) {
			if (aStep.isRequired()) {
				this.steps_.add(this.numRequiredSteps_, aStep);
				++this.numRequiredSteps_;
			} else {
				this.steps_.add(aStep);
			}

		}
	}

	public void add(int position, Step aStep) {
		if (position > this.getTotalStepCount()) {
			throw new IllegalArgumentException("Cannot add a step in a non-consecutive position");
		} else {
			int pos = this.findStep(aStep);
			if (pos < 0) {
				if (aStep.isRequired()) {
					if (position > this.numRequiredSteps_) {
						this.steps_.add(this.numRequiredSteps_, aStep);
					} else {
						this.steps_.add(position, aStep);
					}

					++this.numRequiredSteps_;
				} else {
					if (position < this.numRequiredSteps_) {
						throw new IllegalArgumentException("Cannot add optional step within required steps");
					}

					this.steps_.add(position, aStep);
				}

			}
		}
	}

	public void remove(String stepName) {
		int pos = this.findStep(stepName);
		if (pos >= 0) {
			this.remove(pos);
		}
	}

	public void remove(Step aStep) {
		int pos = this.findStep(aStep);
		if (pos >= 0) {
			this.remove(pos);
		}
	}

	protected void remove(int pos) {
		Step aStep = (Step) this.steps_.get(pos);
		this.steps_.remove(pos);
		if (aStep.isRequired()) {
			--this.numRequiredSteps_;
		}

	}

	public boolean isSummaryAvailable() {
		return this.summaryAvailable_;
	}

	public void setSummaryAvailable(boolean summaryAvailable) {
		this.summaryAvailable_ = summaryAvailable;
	}

	public boolean isSummaryStep() {
		return this.isSummaryStep_;
	}

	public void setSummaryStep(boolean isSummaryStep) {
		this.isSummaryStep_ = isSummaryStep;
		if (isSummaryStep) {
			this.setCancelStep(false);
			this.setFinishStep(false);
		}

	}

	public boolean isCancelStep() {
		return this.isCancelStep_;
	}

	public void setCancelStep(boolean isCancelStep) {
		this.isCancelStep_ = isCancelStep;
		if (isCancelStep) {
			this.setFinishStep(false);
			this.setSummaryStep(false);
		}

	}

	public boolean isFinishStep() {
		return this.isFinishStep_;
	}

	public void setFinishStep(boolean isFinishStep) {
		this.isFinishStep_ = isFinishStep;
		if (isFinishStep) {
			this.setCancelStep(false);
			this.setSummaryStep(false);
		}

	}

	public String toString() {
		return "Wizard: " + this.getId();
	}

	protected int findStep(String stepName) {
		for (int i = 0; i < this.steps_.size(); ++i) {
			Step aStep = (Step) this.steps_.get(i);
			if (aStep.getName().equals(stepName)) {
				return i;
			}
		}

		return -1;
	}

	protected int findStep(Step aStep) {
		for (int i = 0; i < this.steps_.size(); ++i) {
			Step currentStep = (Step) this.steps_.get(i);
			if (aStep.equals(currentStep)) {
				return i;
			}
		}

		return -1;
	}
}