package com.dotzipfile.renderer3d.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JPanel;

import com.dotzipfile.renderer3d.engine.RenderEngine;
import com.dotzipfile.renderer3d.models.Tetrahedron;
import com.dotzipfile.renderer3d.models.Triangle;
import com.dotzipfile.renderer3d.utilities.Matrix3;

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

		Tetrahedron tet = new Tetrahedron();
		List<Triangle> tris = tet.getTriangles();
		
		RenderEngine engine = new RenderEngine();

		Matrix3 transform = engine.transformations(headingSliderVal, pitchSliderVal);

		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

		double[] zBuffer = engine.initialiseZBuffer(img);

		for(Triangle t : tris) {

			img = engine.renderPixels(t, transform, img, zBuffer, getWidth(), getHeight());
		}

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
