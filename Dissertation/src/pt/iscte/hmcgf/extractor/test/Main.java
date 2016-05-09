package pt.iscte.hmcgf.extractor.test;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.Map;
import javax.swing.JFrame;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgrapht.ext.JGraphModelAdapter;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.graph.JGraphSimpleLayout;
import pt.iscte.hmcgf.extractor.ReflectionRelationExtractor;
import pt.iscte.hmcgf.extractor.relations.GraphRelationStorage;
import pt.iscte.hmcgf.extractor.relations.Relation;

public class Main
{

	public static void main(String[] args)
	{
		GraphRelationStorage s = new GraphRelationStorage();
		ReflectionRelationExtractor e = new ReflectionRelationExtractor(s);
		//e.analyseClasses("pt.iscte.hmcgf.extractor.test.dummy");
		e.analyseClasses("javax.mail");

		AttributeMap vertexAttrMap = JGraphModelAdapter.createDefaultVertexAttributes();
		vertexAttrMap.applyValue("bounds", new Rectangle(0, 0, 300, 30));
		AttributeMap edgeAttrMap = JGraphModelAdapter.createDefaultEdgeAttributes(s.getGraph());
		JGraphModelAdapter adapter = new JGraphModelAdapter(s.getGraph(), vertexAttrMap, edgeAttrMap);
		JGraph jgraph = new JGraph(adapter);
		// JFRAME CREATION
		JFrame jframe = new JFrame("teste");
		jframe.setSize(1000, 1000);
		jframe.getContentPane().add(jgraph);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// AUTO LAYOUT

		JGraphSimpleLayout graphLayout = new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_RANDOM, 800, 800);
		JGraphFacade graphFacade = new JGraphFacade(jgraph);
		graphLayout.run(graphFacade);
		Map nestedMap = graphFacade.createNestedMap(true, true);
		jgraph.getGraphLayoutCache().edit(nestedMap);
		jframe.setVisible(true);
		jframe.setState(JFrame.MAXIMIZED_BOTH);
	}

}
