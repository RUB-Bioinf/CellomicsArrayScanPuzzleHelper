package de.rub.bph.ui.component;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;

public class DoubleNumberModel extends AbstractSpinnerModel {
	
	private double currentValue;
	private double stepSize;
	private double min;
	private double max;
	
	public DoubleNumberModel(double value, double min, double max,double stepSize) {
		super();
		this.stepSize = stepSize;
		this.min = min;
		this.max = max;
		this.currentValue=value;
	}
	
	@Override
	public Object getValue() {
		return currentValue;
	}
	
	@Override
	public void setValue(Object value) {
		currentValue = Double.parseDouble(value.toString());
		fireStateChanged();
	}
	
	@Override
	public Object getNextValue() {
		fireStateChanged();
		return currentValue + stepSize;
	}
	
	@Override
	public Object getPreviousValue() {
		fireStateChanged();
		return currentValue - stepSize;
	}
	
	
	public double getStepSize() {
		return stepSize;
	}
	
	public void setStepSize(double stepSize) {
		this.stepSize = stepSize;
	}
	
	public double getMin() {
		return min;
	}
	
	public void setMin(double min) {
		this.min = min;
	}
	
	public double getMax() {
		return max;
	}
	
	public void setMax(double max) {
		this.max = max;
	}
}
