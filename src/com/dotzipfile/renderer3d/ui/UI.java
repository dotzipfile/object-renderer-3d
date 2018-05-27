package com.dotzipfile.renderer3d.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.*;

public class UI extends JFrame{

	private static final long serialVersionUID = 7042870317256010509L;

	public void createUI() {

		Container pane = getContentPane();
		pane.setLayout(new BorderLayout());

		// slider to control horizontal rotation
		JSlider headingSlider = new JSlider(0, 360, 180);
		pane.add(headingSlider, BorderLayout.SOUTH);

		// slider to control vertical rotation
		JSlider pitchSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
		pane.add(pitchSlider, BorderLayout.EAST);

		// panel to display render results
		CustomJPanel renderPanel = new CustomJPanel(headingSlider.getValue(), pitchSlider.getValue());

		pane.add(renderPanel, BorderLayout.CENTER);

		headingSlider.addChangeListener(e -> (renderPanel).customRepaint((double)headingSlider.getValue(), (double)pitchSlider.getValue()));
		pitchSlider.addChangeListener(e -> (renderPanel).customRepaint((double)headingSlider.getValue(), (double)pitchSlider.getValue()));

		setSize(400, 400);
		setVisible(true);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
