package pt.iscte.hmcgf.extractor.test;

import javax.swing.JFrame;
import pt.iscte.hmcgf.extractor.ReflectionRelationExtractor;
import pt.iscte.hmcgf.extractor.relations.GraphRelationStorage;

public class Main
{

	public static void main(String[] args)
	{
		GraphRelationStorage s = new GraphRelationStorage();
		ReflectionRelationExtractor e = new ReflectionRelationExtractor(s);
		// e.analyseClasses("pt.iscte.hmcgf.extractor.test.dummy");
		e.analyseClasses("javax.mail");

		JFrame frame = new JFrame();
		JGraphLayoutPanel layoutPanel = new JGraphLayoutPanel(s.getGraph());
		frame.getContentPane().add(layoutPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(640, 520);
		frame.setVisible(true);
	}

}
