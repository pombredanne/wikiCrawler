package ggg.utils;

/*
 * Cloud9: A Hadoop toolkit for working with big data
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.PriorityQueue;
import java.util.Set;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.algorithms.importance.Ranking;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

/**
 * following
 * https://github.com/lintool/Cloud9/blob/master/src/dist/edu/umd/cloud9
 * /example/pagerank/SequentialPageRank.java
 */
public class JungPageRank {

	public static void main(String[] args) throws IOException {
		float alpha = 0.15f;
		String infile = "/var/lib/datasets/rawdata/wikipedia/de/personlized search/wiki-links.tsv";

		int edgeCnt = 0;
		DirectedSparseGraph<Integer, Integer> graph = new DirectedSparseGraph<Integer, Integer>();

		BufferedReader data = new BufferedReader(new InputStreamReader(
				new FileInputStream(infile)));

		String line;
		while ((line = data.readLine()) != null) {
			line.trim();
			String[] arr = line.split("\\t");
			graph.addEdge(new Integer(edgeCnt++), Integer.parseInt(arr[0]),
					Integer.parseInt(arr[1]));
			if (edgeCnt % 1000000 == 1) {
				System.out.println(edgeCnt + "\t lines");
			}
			if (edgeCnt > 10000) {
				break;
			}
		}

		data.close();

		WeakComponentClusterer<Integer, Integer> clusterer = new WeakComponentClusterer<Integer, Integer>();

		Set<Set<Integer>> components = clusterer.transform(graph);
		int numComponents = components.size();
		System.out.println("Number of components: " + numComponents);
		System.out.println("Number of edges: " + graph.getEdgeCount());
		System.out.println("Number of nodes: " + graph.getVertexCount());
		System.out.println("Random jump factor: " + alpha);

		// Compute PageRank.
		PageRank<Integer, Integer> ranker = new PageRank<Integer, Integer>(
				graph, alpha);
		ranker.evaluate();

		System.out.println("PAGE RANK DONE");

		// Use priority queue to sort vertices by PageRank values.
		PriorityQueue<Ranking<Integer>> q = new PriorityQueue<Ranking<Integer>>();
		int i = 0;
		for (Integer pmid : graph.getVertices()) {
			q.add(new Ranking<Integer>(i++, ranker.getVertexScore(pmid), pmid));
		}

		System.out.println("SORTING DONE");

		// Print PageRank values.
		System.out.println("\nPageRank of nodes, in descending order:");
		Ranking<Integer> r = null;
		int cnt = 0;
		BufferedWriter bw = new BufferedWriter(
				new FileWriter(
						"/var/lib/datasets/rawdata/wikipedia/de/personlized search/wiki-pr.tsv"));
		while ((r = q.poll()) != null) {
			bw.write(r.getRanked() + "\t" + r.rankScore + "\n");
			if (cnt++ % 100000 == 0) {
				System.out.println(cnt + "  nodes written out to file");
			}
		}
		bw.flush();
		bw.close();
	}
}