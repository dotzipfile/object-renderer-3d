package com.dotzipfile.renderer3d.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JPanel;

import com.dotzipfile.renderer3d.models.Tetrahedron;
import com.dotzipfile.renderer3d.models.Triangle;
import com.dotzipfile.renderer3d.models.Vector3;
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

		Matrix3 transform = transformations();

		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

		double[] zBuffer = initialiseZBuffer(img);

		for(Triangle t : tris) {

			img = renderPixels(t, transform, img, zBuffer);
		}

		g2.drawImage(img, 0, 0, null);
	}

	public void customRepaint(double headingSliderVal, double pitchSliderVal) {
		
		setHeadingSliderVal(headingSliderVal);
		setPitchSliderVal(pitchSliderVal);
		
		super.repaint();
	}
	
	public Matrix3 transformations() {

		double heading = Math.toRadians(headingSliderVal);

		Matrix3 headingTransform = new Matrix3(new double[] { 
			Math.cos(heading), 
			0, 
			Math.sin(heading), 
			0, 1, 0, 
			-Math.sin(heading), 
			0, 
			Math.cos(heading) 
		});

		double pitch = Math.toRadians(pitchSliderVal);

		Matrix3 pitchTransform = new Matrix3(new double[] {
			1, 0, 0, 0, 
			Math.cos(pitch), 
			Math.sin(pitch), 
			0, 
			-Math.sin(pitch), 
			Math.cos(pitch)
		});

		Matrix3 transform = headingTransform.multiply(pitchTransform);
		
		return transform;
	}
	
	public double[] initialiseZBuffer(BufferedImage img) {
		double[] zBuffer = new double[img.getWidth() * img.getHeight()];
		
		// initialize array with extremely far away depths
		for(int q = 0; q < zBuffer.length; q++) {
			zBuffer[q] = Double.NEGATIVE_INFINITY;
		}
		
		return zBuffer;
	}
	
	public BufferedImage renderPixels(Triangle t, Matrix3 transform, BufferedImage img, double[] zBuffer) {
		Vector3 v1 = transform.transform(t.v1);
		Vector3 v2 = transform.transform(t.v2);
		Vector3 v3 = transform.transform(t.v3);

		// since we are not using Graphics2D anymore,
		// we have to do translation manually
		v1.x += getWidth() / 2;
		v1.y += getHeight() / 2;
		v2.x += getWidth() / 2;
		v2.y += getHeight() / 2;
		v3.x += getWidth() / 2;
		v3.y += getHeight() / 2;

		// compute rectangular bounds for triangle
		int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
		int maxX = (int) Math.min(img.getWidth() - 1, Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
		int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
		int maxY = (int) Math.min(img.getHeight() - 1, Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

		double triangleArea = (v1.y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - v1.x);

		for(int y = minY; y <= maxY; y++) {

			for(int x = minX; x <= maxX; x++) {

				double b1 = ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
				double b2 = ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
				double b3 = ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;

				if(b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {

					double depth = b1 * v1.z + b2 * v2.z + b3 * v3.z;
					int zIndex = y * img.getWidth() + x;

					if(zBuffer[zIndex] < depth) {
						img.setRGB(x, y, t.color.getRGB());
						zBuffer[zIndex] = depth;
					}
				}
			}
		}
		
		return img;
	}
	
	private void setHeadingSliderVal(double newSliderVal) {
		
		this.headingSliderVal = newSliderVal;
	}
	
	private void setPitchSliderVal(double newSliderVal) {
		
		this.pitchSliderVal = newSliderVal;
	}
}
