package pt.iscte.hmcgf.extractor.test;

import java.util.Collection;
import javax.swing.JFrame;
import org.jgraph.JGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import pt.iscte.hmcgf.extractor.ReflectionRelationExtractor;
import pt.iscte.hmcgf.extractor.relations.GraphRelationStorage;
import pt.iscte.hmcgf.extractor.relations.Relation;

public class Main
{

	public static void main(String[] args)
	{
		GraphRelationStorage s = new GraphRelationStorage();
		ReflectionRelationExtractor e = new ReflectionRelationExtractor(s);
		e.analyseClasses("org.jgrapht");
		Collection<Relation> types = s.getRelationsForType("org.jgraph.JGraph");
		JGraphModelAdapter adapter = new JGraphModelAdapter(s.getGraph());
		JGraph jgraph = new JGraph(adapter);
		JFrame jframe = new JFrame("teste");
		jframe.setSize(300, 300);
		jframe.getContentPane().add(jgraph);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setVisible(true);
		// TODO Adicionar code completion
	}

}
