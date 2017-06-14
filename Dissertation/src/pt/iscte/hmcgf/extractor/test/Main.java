package pt.iscte.hmcgf.extractor.test;

import pt.iscte.hmcgf.extractor.ReflectionRelationExtractor;
import pt.iscte.hmcgf.extractor.RelationAnalyser;
import pt.iscte.hmcgf.extractor.RelationSimulator;
import pt.iscte.hmcgf.extractor.relations.GraphRelationStorage;

public class Main {

	private static final int NUM_STEPS = 100;

	public static void main(String[] args) {
		GraphRelationStorage s = new GraphRelationStorage();
		ReflectionRelationExtractor e = new ReflectionRelationExtractor(s);
		// e.analyseClasses("javax.mail", false);
		String api = "org.apache.pdfbox.util";
		e.analyseClasses(api, false);
		// "org.apache.pdfbox"
		// e.analyseClasses("javax.xml.validation", false);
		RelationAnalyser.analiseGraph(api, s);
		RelationSimulator simulator = new RelationSimulator(false);
		simulator.simulate(api, s, NUM_STEPS);
		// simulator.simulate("org.jfree.chart", s, NUM_STEPS);
		// simulator.simulate("javax.xml.validation", s, NUM_STEPS);
		// simulator.simulate("org.eclipse.swt", s, NUM_STEPS);

		/*
		 * JFrame frame = new JFrame();
		 * 
		 * DirectedPseudograph<Type, Relation> graph = s.getGraph();
		 * 
		 * //System.out.println("Number of verticies before: " +
		 * graph.vertexSet().size());
		 * //graph.removeAllVertices(Arrays.asList(new String[] { "int", "void",
		 * "double", "boolean", "java.lang.String" }));
		 * //System.out.println("Number of verticies before: " +
		 * graph.vertexSet().size()); JGraphLayoutPanel layoutPanel = new
		 * JGraphLayoutPanel(graph); frame.getContentPane().add(layoutPanel);
		 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); frame.pack();
		 * frame.setSize(640, 520); frame.setVisible(true); //
		 * frame.setVisible(true);
		 */

	}

}
