package pt.iscte.hmcgf.extractor.test;

import java.awt.Panel;
import org.jgrapht.graph.DirectedPseudograph;
import pt.iscte.hmcgf.extractor.ReflectionRelationExtractor;
import pt.iscte.hmcgf.extractor.RelationAnalyser;
import pt.iscte.hmcgf.extractor.relations.GraphRelationStorage;
import pt.iscte.hmcgf.extractor.relations.Relation;
import pt.iscte.hmcgf.extractor.relations.Type;

public class Main
{

	public static void main(String[] args)
	{
		GraphRelationStorage s = new GraphRelationStorage();
		ReflectionRelationExtractor e = new ReflectionRelationExtractor(s);
		// e.analyseClasses("pt.iscte.hmcgf.extractor.test.dummy");
		//e.analyseClasses("javax.mail", false);
		// e.analyseClasses("javax.xml");
		e.analyseClasses("org.eclipse.swt", false);
		e.analyseClasses("javax.swing", false);
		e.analyseClasses("org.jfree.chart", false);
		// e.analyseClasses("javax.swing");
		DirectedPseudograph<Type, Relation> graph = s.getGraph();
		// RelationAnalyser.analiseGraph("javax.mail", graph);
		// RelationAnalyser.analiseGraph("javax.xml", graph);
		// RelationAnalyser.analiseGraph("pt.iscte.hmcgf.extractor.test.dummy",
		// graph);
		RelationAnalyser.analiseGraph("org.jfree.chart", graph);
		// RelationAnalyser.analiseGraph("", graph);

		/*
		 * JFrame frame = new JFrame(); JGraphLayoutPanel layoutPanel = new JGraphLayoutPanel(graph); frame.getContentPane().add(layoutPanel);
		 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); frame.pack(); frame.setSize(640, 520); //frame.setVisible(true);
		 */
	}

}
