package com.dotzipfile.renderer3d.engine;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.dotzipfile.renderer3d.models.Tetrahedron;
import com.dotzipfile.renderer3d.models.Triangle;
import com.dotzipfile.renderer3d.models.Vector3;
import com.dotzipfile.renderer3d.utilities.Matrix3;

public class RenderEngine {

	public BufferedImage render(double headingSliderVal, double pitchSliderVal, int imgWidth, int imgHeight) {

		Tetrahedron tet = new Tetrahedron();
		List<Triangle> tris = tet.getTriangles();

		for(int i = 0; i < 4; i ++) {

			tris = inflate(tris);
		}

		Matrix3 transform = transformations(headingSliderVal, pitchSliderVal);

		BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);

		double[] zBuffer = initialiseZBuffer(img);

		for(Triangle t : tris) {

			img = renderPixels(t, transform, img, zBuffer, imgWidth, imgHeight);
		}

		return img;
	}

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

		Vector3 ab = new Vector3(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
		Vector3 ac = new Vector3(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);
		Vector3 norm = new Vector3(
			ab.y * ac.z - ab.z * ac.y,
			ab.z * ac.x - ab.x * ac.z,
			ab.x * ac.y - ab.y * ac.x
		);

		double normalLength = Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z);

		norm.x /= normalLength;
		norm.y /= normalLength;
		norm.z /= normalLength;

		double angleCos = Math.abs(norm.z);

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

						img.setRGB(x, y, getShade(t.color, angleCos).getRGB());
						zBuffer[zIndex] = depth;
					}
				}
			}
		}

		return img;
	}

	public Color getShade(Color color, double shade) {

		double redLinear = Math.pow(color.getRed(), 2.4) * shade;
		double greenLinear = Math.pow(color.getGreen(), 2.4) * shade;
		double blueLinear = Math.pow(color.getBlue(), 2.4) * shade;

		int red = (int) Math.pow(redLinear, 1 / 2.4);
		int green = (int) Math.pow(greenLinear, 1 / 2.4);
		int blue = (int) Math.pow(blueLinear, 1 / 2.4);

		return new Color(red, green, blue);
	}

	public List<Triangle> inflate(List<Triangle> tris) {

		List<Triangle> result = new ArrayList<Triangle>();

		for (Triangle t : tris) {

			Vector3 m1 = new Vector3((t.v1.x + t.v2.x) / 2, (t.v1.y + t.v2.y) / 2, (t.v1.z + t.v2.z) / 2);
			Vector3 m2 = new Vector3((t.v2.x + t.v3.x) / 2, (t.v2.y + t.v3.y) / 2, (t.v2.z + t.v3.z) / 2);
			Vector3 m3 = new Vector3((t.v1.x + t.v3.x) / 2, (t.v1.y + t.v3.y) / 2, (t.v1.z + t.v3.z) / 2);
			result.add(new Triangle(t.v1, m1, m3, t.color));
			result.add(new Triangle(t.v2, m1, m2, t.color));
			result.add(new Triangle(t.v3, m2, m3, t.color));
			result.add(new Triangle(m1, m2, m3, t.color));
		}

		for (Triangle t : result) {

			for (Vector3 v : new Vector3[] { t.v1, t.v2, t.v3 }) {

				double l = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z) / Math.sqrt(30000);
				v.x /= l;
				v.y /= l;
				v.z /= l;
			}
		}

		return result;
	}
}
