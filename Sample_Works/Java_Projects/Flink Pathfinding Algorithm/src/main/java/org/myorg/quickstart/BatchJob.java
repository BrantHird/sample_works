/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.myorg.quickstart;

import org.apache.flink.api.java.ExecutionEnvironment;

import org.apache.flink.api.common.ProgramDescription;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.graph.Edge;
import org.apache.flink.graph.Graph;
import org.apache.flink.graph.Vertex;
import org.apache.flink.graph.gsa.ApplyFunction;
import org.apache.flink.graph.gsa.GatherFunction;
import org.apache.flink.graph.gsa.Neighbor;
import org.apache.flink.graph.gsa.SumFunction;
import org.apache.flink.graph.utils.Tuple3ToEdgeMap;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.graph.utils.Tuple2ToEdgeMap;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.types.NullValue;
import org.apache.flink.graph.library.SingleSourceShortestPaths;
import org.apache.flink.graph.library.SingleSourceShortestPaths.MinDistanceMessenger;
import org.apache.flink.graph.library.SingleSourceShortestPaths.VertexDistanceUpdater;
import java.io.File;  // Import the File class
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors
import java.lang.Math;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.BasicStroke;

// everything until the last few lines is generating a graph, and the image for that graph

public class BatchJob {
	public static int numPoints = 10000;

	public static void main(String[] args) throws Exception {

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

		for (int i = 0; i < neighbours.size(); i ++) {
			for (int j = 0; j < neighbours.get(i).size(); j ++) {
				for (int k = 0; k < neighbours.get(neighbours.get(i).get(j)).size(); k ++) {
					if (neighbours.get(neighbours.get(i).get(j)).get(k).equals(i)) {
						neighbours.get(neighbours.get(i).get(j)).remove(neighbours.get(neighbours.get(i).get(j)).get(k));
					}
				}
			}
		}

		int edges = 0;
		for (int i = 0; i < neighbours.size(); i ++) {
			edges += neighbours.get(i).size();
		}

		System.out.println("Num edges: " + edges);
			
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
			FileWriter f = new FileWriter("quickstart/edgeList.txt");
			
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
			FileWriter f = new FileWriter("quickstart/vertexList.txt");
			
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
			g2d.drawOval((int)points.get(i).getX(), (int)points.get(i).getY(), 1, 1);

		}

		for (int i = 0; i < numPoints; i ++) {
			for (int j = 0; j < neighbours.get(i).size(); j ++) {
				g2d.drawLine((int)points.get(i).getX(), (int)points.get(i).getY(), (int)points.get(neighbours.get(i).get(j)).getX(), (int)points.get(neighbours.get(i).get(j)).getY());
			}
		}
		

        try {
            ImageIO.write(img, "jpg", new File("quickstart/graph.jpg"));
        } 
        catch (IOException e) {
        }
		

		// flink stuff starts here, converting into vertex and edge objects and trying to get flink to build a graph from it

		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		
		ArrayList<Vertex<Long, Long>> vertexArrayList = new ArrayList<Vertex<Long, Long>>();


		for (int i = 0; i < numPoints; i ++) {
			Long l1 = Long.valueOf(i);
			Long l2 = Long.valueOf(1);
			Vertex<Long, Long> v = new Vertex<Long, Long>(l1, l2);
			vertexArrayList.add(v);
		}

		ArrayList<Edge<Long, String>> edgeArrayList = new ArrayList<Edge<Long, String>>();


		for (int i = 0; i < numPoints; i ++) {
			for (int j = 0; j < neighbours.get(i).size(); j ++) {
				Long l1 = Long.valueOf(i);
				Long l2 = Long.valueOf(neighbours.get(i).get(j));
				Edge<Long, String> e = new Edge<Long, String>(l1, l2, "1.0");
				edgeArrayList.add(e);
			}
		}

		List<Vertex<Long, Long>> vertexList = vertexArrayList;

		List<Edge<Long, String>> edgeList = edgeArrayList;



		Graph<Long, Long, String> graph = Graph.fromCollection(vertexList, edgeList, env);

		int maxIterations = 10;
		
		Graph<Long, Long, String> result = graph.runScatterGatherIteration(
			new MinDistanceMessenger(), new VertexDistanceUpdater(), maxIterations);
			
		DataSet<Vertex<Long, Long>> singleSourceShortestPaths = result.getVertices();

		singleSourceShortestPaths.print();

	}
}
