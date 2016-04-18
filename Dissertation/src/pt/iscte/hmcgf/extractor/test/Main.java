package pt.iscte.hmcgf.extractor.test;

import javax.swing.JFrame;
import org.jgraph.JGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import pt.iscte.hmcgf.extractor.ReflectionRelationExtractor;
import pt.iscte.hmcgf.extractor.relations.GraphRelationStorage;

public class Main
{

	public static void main(String[] args)
	{
		GraphRelationStorage s = new GraphRelationStorage();
		new ReflectionRelationExtractor(s).analyseClasses("org.jgrapht");

		JGraphModelAdapter adapter = new JGraphModelAdapter(s.getGraph());
		JGraph jgraph = new JGraph(adapter);
		JFrame jframe = new JFrame("teste");
		jframe.setSize(300, 300);
		jframe.getContentPane().add(jgraph);
		jframe.setVisible(true);
		// TODO Adicionar code completion

	}

}
