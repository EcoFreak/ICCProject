package pt.iscte.hmcgf.extractor.test;

import pt.iscte.hmcgf.extractor.ReflectionRelationExtractor;
import pt.iscte.hmcgf.extractor.RelationAnalyser;
import pt.iscte.hmcgf.extractor.RelationSimulator;
import pt.iscte.hmcgf.extractor.relations.GraphRelationStorage;

public class Main
{

	private static final int NUM_STEPS = 100;

	public static void main(String[] args)
	{
		GraphRelationStorage s = new GraphRelationStorage();
		ReflectionRelationExtractor e = new ReflectionRelationExtractor(s);
		e.analyseClasses("javax.mail", false);
		RelationAnalyser.analiseGraph("javax.mail", s);
		RelationSimulator simulator = new RelationSimulator(false);
		simulator.simulate("javax.mail", s, NUM_STEPS);
		//simulator.simulate("org.eclipse.swt", s, NUM_STEPS);

		/*
		 * JFrame frame = new JFrame(); JGraphLayoutPanel layoutPanel = new JGraphLayoutPanel(graph); frame.getContentPane().add(layoutPanel);
		 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); frame.pack(); frame.setSize(640, 520); //frame.setVisible(true);
		 */
	}

}
