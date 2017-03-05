package pt.iscte.hmcgf.extractor.test;

import java.util.Arrays;
import javax.swing.JFrame;
import org.jgrapht.graph.DirectedPseudograph;
import pt.iscte.hmcgf.extractor.ReflectionRelationExtractor;
import pt.iscte.hmcgf.extractor.RelationAnalyser;
import pt.iscte.hmcgf.extractor.relations.GraphRelationStorage;
import pt.iscte.hmcgf.extractor.relations.Relation;

public class Main
{

	public static void main(String[] args)
	{
		GraphRelationStorage s = new GraphRelationStorage();
		ReflectionRelationExtractor e = new ReflectionRelationExtractor(s);
		// e.analyseClasses("pt.iscte.hmcgf.extractor.test.dummy");
		e.analyseClasses("javax.mail");
		// e.analyseClasses("javax.xml");
		e.analyseClasses("org.jfree.chart");
		// e.analyseClasses("javax.swing");
		JFrame frame = new JFrame();
		DirectedPseudograph<String, Relation> graph = s.getGraph();
		RelationAnalyser.analiseGraph("javax.mail", graph);
		// RelationAnalyser.analiseGraph("javax.xml", graph);
		RelationAnalyser.analiseGraph("org.jfree.chart", graph);
		// RelationAnalyser.analiseGraph("", graph);

		/*
		 * JGraphLayoutPanel layoutPanel = new JGraphLayoutPanel(graph); frame.getContentPane().add(layoutPanel);
		 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); frame.pack(); frame.setSize(640, 520); //frame.setVisible(true);
		 */
	}

}
