package com.dotzipfile.renderer3d;

import javax.swing.*;

import com.dotzipfile.renderer3d.models.Triangle;
import com.dotzipfile.renderer3d.models.Vector3;
import com.dotzipfile.renderer3d.utilities.Matrix3;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class DemoViewer {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        // slider to control horizontal rotation
        JSlider headingSlider = new JSlider(0, 360, 180);
        pane.add(headingSlider, BorderLayout.SOUTH);

        // slider to control vertical rotation
        JSlider pitchSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
        pane.add(pitchSlider, BorderLayout.EAST);

        // panel to display render results
        JPanel renderPanel = new JPanel() {
                public void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(Color.BLACK);
                    g2.fillRect(0, 0, getWidth(), getHeight());

            		List<Triangle> tris = new ArrayList<Triangle>();
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

            		// Render
                    double heading = Math.toRadians(headingSlider.getValue());
                    
                    Matrix3 headingTransform = new Matrix3(new double[] {
                            Math.cos(heading), 0, Math.sin(heading),
                            0, 1, 0,
                            -Math.sin(heading), 0, Math.cos(heading)
                        });
                    double pitch = Math.toRadians(pitchSlider.getValue());
                    Matrix3 pitchTransform = new Matrix3(new double[] {
                            1, 0, 0,
                            0, Math.cos(pitch), Math.sin(pitch),
                            0, -Math.sin(pitch), Math.cos(pitch)
                        });
                    Matrix3 transform = headingTransform.multiply(pitchTransform);
//
//                    g2.translate(getWidth() / 2, getHeight() / 2);
//                    g2.setColor(Color.WHITE);
//                    for (Triangle t : tris) {
//                        Vector3 v1 = transform.transform(t.v1);
//                        Vector3 v2 = transform.transform(t.v2);
//                        Vector3 v3 = transform.transform(t.v3);
//                        Path2D path = new Path2D.Double();
//                        path.moveTo(v1.x, v1.y);
//                        path.lineTo(v2.x, v2.y);
//                        path.lineTo(v3.x, v3.y);
//                        path.closePath();
//                        g2.draw(path);
//                    }
                    
                    BufferedImage img = 
                    	    new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

                    	for (Triangle t : tris) {
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
                    	    int maxX = (int) Math.min(img.getWidth() - 1, 
                    	                              Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
                    	    int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
                    	    int maxY = (int) Math.min(img.getHeight() - 1,
                    	                              Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

                    	    double triangleArea =
                    	       (v1.y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - v1.x);

                    	    for (int y = minY; y <= maxY; y++) {
                    	        for (int x = minX; x <= maxX; x++) {
                    	            double b1 = 
                    	              ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
                    	            double b2 =
                    	              ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
                    	            double b3 =
                    	              ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;
                    	            if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {
                    	                img.setRGB(x, y, t.color.getRGB());
                    	            }
                    	        }
                    	    }

                    	}
                    	

                    	g2.drawImage(img, 0, 0, null);
                }
            };
        pane.add(renderPanel, BorderLayout.CENTER);
        
        headingSlider.addChangeListener(e -> renderPanel.repaint());
        pitchSlider.addChangeListener(e -> renderPanel.repaint());

        frame.setSize(400, 400);
        frame.setVisible(true);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
