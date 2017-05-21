package pt.iscte.hmcgf.extractor.test;

import javax.swing.JFrame;

import org.jgrapht.graph.DirectedPseudograph;

import pt.iscte.hmcgf.extractor.ReflectionRelationExtractor;
import pt.iscte.hmcgf.extractor.RelationAnalyser;
import pt.iscte.hmcgf.extractor.RelationSimulator;
import pt.iscte.hmcgf.extractor.relations.GraphRelationStorage;
import pt.iscte.hmcgf.extractor.relations.Relation;
import pt.iscte.hmcgf.extractor.relations.Type;

public class Main {

	private static final int NUM_STEPS = 100;

	public static void main(String[] args) {
		GraphRelationStorage s = new GraphRelationStorage();
		ReflectionRelationExtractor e = new ReflectionRelationExtractor(s);
		e.analyseClasses("javax.mail", false);
		RelationAnalyser.analiseGraph("javax.mail", s);
		RelationSimulator simulator = new RelationSimulator(false);
		simulator.simulate("javax.mail", s, NUM_STEPS);
		// simulator.simulate("org.eclipse.swt", s, NUM_STEPS);

		/*		JFrame frame = new JFrame();
		
		DirectedPseudograph<Type, Relation> graph = s.getGraph();

		//System.out.println("Number of verticies before: " + graph.vertexSet().size());
		//graph.removeAllVertices(Arrays.asList(new String[] { "int", "void", "double", "boolean", "java.lang.String" }));
		//System.out.println("Number of verticies before: " + graph.vertexSet().size());
		JGraphLayoutPanel layoutPanel = new JGraphLayoutPanel(graph);
		frame.getContentPane().add(layoutPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(640, 520);
		frame.setVisible(true);
		// frame.setVisible(true);*/

	}

}
