package com.dotzipfile.renderer3d.engine;

import java.awt.image.BufferedImage;

import com.dotzipfile.renderer3d.models.Triangle;
import com.dotzipfile.renderer3d.models.Vector3;
import com.dotzipfile.renderer3d.utilities.Matrix3;

public class RenderEngine {
	
	public Matrix3 transformations(double headingSliderVal, double pitchSliderVal) {
		
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
	
	public BufferedImage renderPixels(Triangle t, Matrix3 transform, BufferedImage img, double[] zBuffer, int width, int height) {
		Vector3 v1 = transform.transform(t.v1);
		Vector3 v2 = transform.transform(t.v2);
		Vector3 v3 = transform.transform(t.v3);

		// since we are not using Graphics2D anymore,
		// we have to do translation manually
		v1.x += width / 2;
		v1.y += height / 2;
		v2.x += width / 2;
		v2.y += height / 2;
		v3.x += width / 2;
		v3.y += height / 2;

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
}
