package pt.iscte.hmcgf.extractor.test;

import java.awt.Rectangle;
import javax.swing.JFrame;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgrapht.ext.JGraphModelAdapter;
import com.jgraph.layout.demo.JGraphLayoutPanel;
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

		AttributeMap vertexAttrMap = JGraphModelAdapter.createDefaultVertexAttributes();
		vertexAttrMap.applyValue("bounds", new Rectangle(0, 0, 300, 30));
		AttributeMap edgeAttrMap = JGraphModelAdapter.createDefaultEdgeAttributes(s.getGraph());
		JGraphModelAdapter adapter = new JGraphModelAdapter(s.getGraph(), vertexAttrMap, edgeAttrMap);
		JGraph jgraph = new JGraph(adapter);
		JFrame frame = new JFrame();
		JGraphLayoutPanel layoutPanel = new JGraphLayoutPanel(jgraph);
		frame.getContentPane().add(layoutPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(640, 520);
		frame.setVisible(true);
		try
		{
			Thread.sleep(200);
		}
		catch (InterruptedException e1)
		{
			// continue
		}
		/*
		 * // JFRAME CREATION JFrame jframe = new JFrame("teste"); jframe.setSize(1800, 1280); JScrollPane scrollpane = new JScrollPane(jgraph);
		 * jframe.getContentPane().add(scrollpane); jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		 * // TODO HIDE PRIMITIVE TYPES 
		 * W// TODO FILTER
		 * BY NAME
		 * 
		 * // JGraphSimpleLayout graphLayout = new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_RANDOM, 800, 800); JGraphLayout graphLayout = new
		 * JGraphRadialTreeLayout(); JGraphFacade graphFacade = new JGraphFacade(jgraph); graphLayout.run(graphFacade); Map nestedMap =
		 * graphFacade.createNestedMap(true, true); jgraph.getGraphLayoutCache().edit(nestedMap); jframe.setVisible(true);
		 * jframe.setState(JFrame.MAXIMIZED_BOTH);
		 */
	}

}
