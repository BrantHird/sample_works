import java.io.File;  // Import the File class
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
import java.lang.Math;
import java.awt.Point;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.BasicStroke;

public class graphGenerator {

	public static int numPoints = 10000;
	
	public static void main (String[] args) {

		// generate a list of random points

		ArrayList<Point> points = new ArrayList<Point>();

		for (int i = 0; i < numPoints; i ++) {
			Point p = new Point((int)(Math.random()*numPoints), (int)(Math.random()*numPoints));
			points.add(p);
		}

		// calculate each points closest 4 neighbours

		ArrayList<ArrayList<Integer>> neighbours = new ArrayList<ArrayList<Integer>>();

		for (int i = 0; i < numPoints; i ++) {
			ArrayList<Integer> n = new ArrayList<Integer>();
			neighbours.add(n);
		}

		for (int i = 0; i < numPoints; i ++) {

			for (int k = 0; k < 4; k ++) {

				double distance = numPoints*2;
				int ind = -1;
				for (Integer j = 0; j < numPoints; j ++) {
					if (points.get(i).distance(points.get(j)) < distance && i!=j) {
						boolean add = true;
						for (int l = 0; l < neighbours.get(i).size(); l ++) {
							if (j.equals(neighbours.get(i).get(l))) {
								add = false;
							}
						}

						if (add) {

							distance = points.get(i).distance(points.get(j));
							ind = j;
						}
					}
				}

				neighbours.get(i).add(ind);

			}
		}

		// remove duplicate edges
		// i realised this probably means the graph is directed instead of undirected, so commenting out for now

		/*
		for (int i = 0; i < neighbours.size(); i ++) {
			for (int j = 0; j < neighbours.get(i).size(); j ++) {
				for (int k = 0; k < neighbours.get(neighbours.get(i).get(j)).size(); k ++) {
					if (neighbours.get(neighbours.get(i).get(j)).get(k).equals(i)) {
						neighbours.get(neighbours.get(i).get(j)).remove(neighbours.get(neighbours.get(i).get(j)).get(k));
					}
				}
			}
		}
		*/

		int edges = 0;
		for (int i = 0; i < neighbours.size(); i ++) {
			edges += neighbours.get(i).size();
		}

		System.out.println("Num edges: " + edges);
			
			
		// a different format of csv of the graph	
		
		// try {
		// 	FileWriter f = new FileWriter("graph.csv");
		// 	f.write("id,x,y,neighbour1,neighbour2,neighbour3,neighbour4\n");
			
		// 	for (int i = 0; i < numPoints; i ++) {
		// 		f.write(i + "," + (int)points.get(i).getX() + "," + (int)points.get(i).getY());
		// 		for (int j = 0; j < neighbours.get(i).size(); j ++) {
		// 			f.write("," + neighbours.get(i).get(j));
		// 		}
		// 		f.write("\n");
		// 	}
			
		// 	f.close();
		// } catch (IOException e) {
		// 	System.out.println("An error occurred.");
		// 	e.printStackTrace();
		// }

		try {
			FileWriter f = new FileWriter("edgeList.txt");
			
			for (int i = 0; i < neighbours.size(); i ++) {
				for (int j = 0; j < neighbours.get(i).size(); j ++) {
					f.write(i + "," + neighbours.get(i).get(j).toString() + "," + "1.0");
					f.write("\n");
				}
			}
			
			f.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
		try {
			FileWriter f = new FileWriter("vertexList.txt");
			
			for (int i = 0; i < neighbours.size(); i ++) {
				//f.write(i + "," + points.get(i).getX() + "," + points.get(i).getY());
				f.write(i + ",1");
				f.write("\n");
			}
			
			f.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		// create an image of the graph for reference

		BufferedImage img;

		img = new BufferedImage(numPoints, numPoints, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setBackground(Color.WHITE);

		for (int i = 0; i < numPoints; i ++) {	
			g2d.fillOval((int)points.get(i).getX()-3, (int)points.get(i).getY()-3, 6, 6);

		}

		for (int i = 0; i < numPoints; i ++) {
			for (int j = 0; j < neighbours.get(i).size(); j ++) {
				g2d.drawLine((int)points.get(i).getX(), (int)points.get(i).getY(), (int)points.get(neighbours.get(i).get(j)).getX(), (int)points.get(neighbours.get(i).get(j)).getY());
			}
		}
		

        try {
            ImageIO.write(img, "jpg", new File("graph.jpg"));
        } 
        catch (IOException e) {
        }
	
	}


}
