package com.dotzipfile.renderer3d.models;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Tetrahedron {
	
	private List<Triangle> tris;

	public Tetrahedron() {
		tris = new ArrayList<Triangle>();
		tris.add(new Triangle(new Vector3(100, 100, 100),
			new Vector3(-100, -100, 100),
			new Vector3(-100, 100, -100),
			Color.WHITE));
		tris.add(new Triangle(new Vector3(100, 100, 100),
			new Vector3(-100, -100, 100),
			new Vector3(100, -100, -100),
			Color.RED));
		tris.add(new Triangle(new Vector3(-100, 100, -100),
			new Vector3(100, -100, -100),
			new Vector3(100, 100, 100),
			Color.GREEN));
		tris.add(new Triangle(new Vector3(-100, 100, -100),
			new Vector3(100, -100, -100),
			new Vector3(-100, -100, 100),
			Color.BLUE));
	}
	
	public List<Triangle> getTriangles() {
		return tris;
	}
}
