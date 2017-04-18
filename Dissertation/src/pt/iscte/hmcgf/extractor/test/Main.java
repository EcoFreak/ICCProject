package pt.iscte.hmcgf.extractor.test;

import pt.iscte.hmcgf.extractor.ReflectionRelationExtractor;
import pt.iscte.hmcgf.extractor.RelationAnalyser;
import pt.iscte.hmcgf.extractor.RelationSimulator;
import pt.iscte.hmcgf.extractor.relations.GraphRelationStorage;

public class Main
{

	public static void main(String[] args)
	{
		GraphRelationStorage s = new GraphRelationStorage();
		ReflectionRelationExtractor e = new ReflectionRelationExtractor(s);
		e.analyseClasses("org.eclipse.swt", false);
		RelationAnalyser.analiseGraph("org.eclipse.swt", s);
		RelationSimulator simulator = new RelationSimulator(false);
		simulator.simulate("org.eclipse.swt", s, 5);

		/*
		 * JFrame frame = new JFrame(); JGraphLayoutPanel layoutPanel = new JGraphLayoutPanel(graph); frame.getContentPane().add(layoutPanel);
		 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); frame.pack(); frame.setSize(640, 520); //frame.setVisible(true);
		 */
	}

}
