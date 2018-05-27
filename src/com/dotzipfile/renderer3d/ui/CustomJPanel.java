package com.dotzipfile.renderer3d.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.dotzipfile.renderer3d.engine.RenderEngine;

public class CustomJPanel extends JPanel {

	private static final long serialVersionUID = 598841022132379283L;

	private double headingSliderVal;
	private double pitchSliderVal;

	public CustomJPanel(double headingSliderVal, double pitchSliderVal) {

		this.headingSliderVal = headingSliderVal;
		this.pitchSliderVal = pitchSliderVal;
	}

	@Override
	public void paintComponent(Graphics g) {

		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, getWidth(), getHeight());

		RenderEngine engine = new RenderEngine();

		BufferedImage img = engine.render(headingSliderVal, pitchSliderVal, getWidth(), getHeight());

		g2.drawImage(img, 0, 0, null);
	}

	public void customRepaint(double headingSliderVal, double pitchSliderVal) {

		setHeadingSliderVal(headingSliderVal);
		setPitchSliderVal(pitchSliderVal);

		super.repaint();
	}

	private void setHeadingSliderVal(double newSliderVal) {

		this.headingSliderVal = newSliderVal;
	}

	private void setPitchSliderVal(double newSliderVal) {

		this.pitchSliderVal = newSliderVal;
	}
}
