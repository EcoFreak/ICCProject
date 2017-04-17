package pt.iscte.hmcgf.extractor.test;

import org.jgrapht.graph.DirectedPseudograph;
import pt.iscte.hmcgf.extractor.ReflectionRelationExtractor;
import pt.iscte.hmcgf.extractor.RelationAnalyser;
import pt.iscte.hmcgf.extractor.relations.GraphRelationStorage;
import pt.iscte.hmcgf.extractor.relations.Relation;
import pt.iscte.hmcgf.extractor.relations.Type;

public class Main {

	public static void main(String[] args) {
		// System.out.println(new
		// FastClasspathScanner("!",Panel.class.getPackage().getName()).scan().getNamesOfAllClasses().size());
		// System.out.println(new
		// FastClasspathScanner("!!").scan().getNamesOfAllStandardClasses().contains("java.lang.String"));
		// System.out.println(new
		// FastClasspathScanner("!!","org.eclipse.swt").scan().getNamesOfAllStandardClasses().size());
		// System.out.println(new
		// FastClasspathScanner().scan().getNamesOfAllStandardClasses().size());
		GraphRelationStorage s = new GraphRelationStorage();
		ReflectionRelationExtractor e = new ReflectionRelationExtractor(s);
		// e.analyseClasses("pt.iscte.hmcgf.extractor.test.dummy",false);
		// e.analyseClasses("javax.mail", false);
		// e.analyseClasses("javax.xml", false);
		e.analyseClasses("org.eclipse.swt", false);
		// e.analyseClasses("org.jfree.chart", false);
		// e.analyseClasses("javax.swing", false);
		DirectedPseudograph<Type, Relation> graph = s.getGraph();
		// RelationAnalyser.analiseGraph("javax.mail", graph);
		// RelationAnalyser.analiseGraph("javax.xml", graph);
		// RelationAnalyser.analiseGraph("pt.iscte.hmcgf.extractor.test.dummy",
		// graph);
		RelationAnalyser.analiseGraph("org.eclipse.swt", graph);
		// RelationAnalyser.analiseGraph("org.jfree.chart", graph);
		// RelationAnalyser.analiseGraph("", graph);

		/*
		 * JFrame frame = new JFrame(); JGraphLayoutPanel layoutPanel = new
		 * JGraphLayoutPanel(graph); frame.getContentPane().add(layoutPanel);
		 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); frame.pack();
		 * frame.setSize(640, 520); //frame.setVisible(true);
		 */
	}

}
