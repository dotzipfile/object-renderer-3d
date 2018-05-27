package com.dotzipfile.renderer3d.models;

import java.awt.Color;

public class Triangle {
	public Vector3 v1;
	public Vector3 v2;
	public Vector3 v3;
	public Color color;

	public Triangle(Vector3 v1, Vector3 v2, Vector3 v3, Color color) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
		this.color = color;
	}
}
