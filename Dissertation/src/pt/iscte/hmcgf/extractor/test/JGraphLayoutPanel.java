package pt.iscte.hmcgf.extractor.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DirectedPseudograph;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.graph.JGraphSimpleLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;
import com.jgraph.layout.organic.JGraphFastOrganicLayout;
import com.jgraph.layout.organic.JGraphOrganicLayout;
import com.jgraph.layout.organic.JGraphSelfOrganizingOrganicLayout;
import com.jgraph.layout.tree.JGraphCompactTreeLayout;
import com.jgraph.layout.tree.JGraphRadialTreeLayout;
import com.jgraph.layout.tree.JGraphTreeLayout;
import com.l2fprod.common.swing.JTaskPane;
import com.l2fprod.common.swing.JTaskPaneGroup;
import pt.iscte.hmcgf.extractor.relations.Relation;

public class JGraphLayoutPanel extends JPanel
{
	private static final long						serialVersionUID	= -1504418916720411157L;
	protected static JGraphGraphFactory				graphFactory		= new JGraphGraphFactory();
	protected JGraph								graph;
	protected DirectedPseudograph<String, Relation>	sourceGraph;
	protected JTaskPane								taskPane			= new JTaskPane();
	protected JGraphLayoutMorphingManager			morpher				= new JGraphLayoutMorphingManager();
	protected JSplitPane							splitPane;
	private JScrollPane								scrollPane;
	public JGraphLayoutPanel(DirectedPseudograph<String, Relation> graph)
	{
		super(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

		// Configures the graph
		this.sourceGraph = graph;

		// Filter primitive types
		AttributeMap vertexAttrMap = JGraphModelAdapter.createDefaultVertexAttributes();
		vertexAttrMap.applyValue("bounds", new Rectangle(0, 0, 300, 30));
		AttributeMap edgeAttrMap = JGraphModelAdapter.createDefaultEdgeAttributes(sourceGraph);
		JGraphModelAdapter adapter = new JGraphModelAdapter(sourceGraph, vertexAttrMap, edgeAttrMap);
		this.graph = new JGraph(adapter);

		// Configures the taskpane
		configureTaskpane();

		// Adds the split pane
		scrollPane = new JScrollPane(this.graph);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(taskPane), scrollPane);
		add(splitPane, BorderLayout.CENTER);

		// Adds the status bar
		JLabel version = new JLabel(JGraph.VERSION);
		version.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		version.setFont(version.getFont().deriveFont(Font.PLAIN));
		add(version, BorderLayout.SOUTH);
	}

	@SuppressWarnings({ "serial", "deprecation" })
	public void configureTaskpane()
	{
		JTaskPaneGroup taskGroup = new JTaskPaneGroup();
		taskGroup.setText("Layout");
		taskGroup.add(new AbstractAction("Hierarchical")
		{
			public void actionPerformed(ActionEvent e)
			{
				execute(new JGraphHierarchicalLayout());
			}
		});
		taskGroup.add(new AbstractAction("Fast Organic")
		{
			public void actionPerformed(ActionEvent e)
			{
				JGraphFastOrganicLayout layout = new JGraphFastOrganicLayout();
				layout.setForceConstant(60);
				execute(layout);
			}
		});
		taskGroup.add(new AbstractAction("Organic")
		{
			public void actionPerformed(ActionEvent e)
			{
				JGraphOrganicLayout layout = new JGraphOrganicLayout();
				layout.setOptimizeBorderLine(false);
				execute(layout);
			}
		});

		taskGroup.add(new AbstractAction("Self-Organizing")
		{
			public void actionPerformed(ActionEvent e)
			{
				JGraphSelfOrganizingOrganicLayout layout = new JGraphSelfOrganizingOrganicLayout();
				layout.setStartRadius(4);
				layout.setMaxIterationsMultiple(40);
				layout.setDensityFactor(20000);
				execute(new JGraphSelfOrganizingOrganicLayout());
			}
		});
		taskGroup.add(new AbstractAction("Compact Tree")
		{
			public void actionPerformed(ActionEvent e)
			{
				execute(new JGraphCompactTreeLayout());
			}
		});
		taskGroup.add(new AbstractAction("Radialtree")
		{
			public void actionPerformed(ActionEvent e)
			{
				execute(new JGraphRadialTreeLayout());
			}
		});
		taskGroup.add(new AbstractAction("Tree")
		{
			public void actionPerformed(ActionEvent e)
			{
				execute(new JGraphTreeLayout());
			}
		});
		taskGroup.add(new AbstractAction("Reset")
		{
			public void actionPerformed(ActionEvent e)
			{
				reset();
			}
		});
		taskGroup.add(new AbstractAction("Tilt")
		{
			public void actionPerformed(ActionEvent e)
			{
				execute(new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_TILT, 100, 100));
			}
		});
		taskGroup.add(new AbstractAction("Random")
		{
			public void actionPerformed(ActionEvent e)
			{
				execute(new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_RANDOM, 640, 480));
			}
		});

		taskPane.add(taskGroup);

		taskGroup = new JTaskPaneGroup();
		taskGroup.setText("Graph");

		taskGroup.add(new AbstractAction("Zoom IN")
		{
			public void actionPerformed(ActionEvent e)
			{
				graph.setScale(graph.getScale() + 0.1);
			}
		});
		taskGroup.add(new AbstractAction("Zoom OUT")
		{
			public void actionPerformed(ActionEvent e)
			{
				graph.setScale(graph.getScale() - 0.1);
			}
		});
		taskGroup.add(new AbstractAction("Actual Size")
		{
			public void actionPerformed(ActionEvent e)
			{
				graph.setScale(1);
			}
		});
		taskGroup.add(new AbstractAction("Fit Window")
		{
			public void actionPerformed(ActionEvent e)
			{
				JGraphLayoutMorphingManager.fitViewport(graph);
			}
		});
		taskPane.add(taskGroup);
	}
	/**
	 * Shows something useful. Should be called after the enclosing frame has been made visible.
	 */
	public void init()
	{
		graphFactory.insertConnectedGraphSampleData(graph,
				createCellAttributes(new Point2D.Double(0, 0)),
				createEdgeAttributes());
		reset();
	}

	/**
	 * Resets the graph to a circular layout.
	 */
	public void reset()
	{
		execute(new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_CIRCLE));
		graph.clearSelection();
		JGraphLayoutMorphingManager.fitViewport(graph);
	}

	public void execute(final JGraphLayout layout)
	{
		if (graph != null && graph.isEnabled() && graph.isMoveable() && layout != null)
		{
			final JGraphFacade facade = createFacade(graph);
			final ProgressMonitor progressMonitor = (layout instanceof JGraphLayout.Stoppable)
					? createProgressMonitor(graph, (JGraphLayout.Stoppable) layout) : null;
			new Thread()
			{
				public void run()
				{
					synchronized (this)
					{
						try
						{
							layout.run(facade);
							SwingUtilities.invokeLater(new Runnable()
							{
								public void run()
								{
									boolean ignoreResult = false;
									if (progressMonitor != null)
									{
										ignoreResult = progressMonitor.isCanceled();
										progressMonitor.close();
									}
									if (!ignoreResult)
									{
										Map map = facade.createNestedMap(true, true);
										morpher.morph(graph, map);
										graph.requestFocus();
									}
								}
							});
						}
						catch (Exception e)
						{
							e.printStackTrace();
							JOptionPane.showMessageDialog(graph, e.getMessage());
						}
					}
				}
			}.start();
		}
	}

	/**
	 * Creates a {@link JGraphFacade} and makes sure it contains a valid set of root cells if the specified layout is a tree layout. A root cell in
	 * this context is one that has no incoming edges.
	 * 
	 * @param graph
	 *            The graph to use for the facade.
	 * @return Returns a new facade for the specified layout and graph.
	 */
	protected JGraphFacade createFacade(JGraph graph)
	{
		// Creates and configures the facade using the global switches
		JGraphFacade facade = new JGraphFacade(graph, graph.getSelectionCells());
		facade.setIgnoresUnconnectedCells(true);
		facade.setIgnoresCellsInGroups(true);
		facade.setIgnoresHiddenCells(true);
		facade.setDirected(true);

		// Removes all existing control points from edges
		facade.resetControlPoints();
		return facade;
	}

	/**
	 * Creates a {@link JGraphLayoutProgressMonitor} for the specified layout.
	 * 
	 * @param graph
	 *            The graph to use as the parent component.
	 * @param layout
	 *            The layout to create the progress monitor for.
	 * @return Returns a new progress monitor.
	 */
	protected ProgressMonitor createProgressMonitor(JGraph graph,
			JGraphLayout.Stoppable layout)
	{
		ProgressMonitor monitor = new JGraphLayoutProgressMonitor(graph,
				((JGraphLayout.Stoppable) layout).getProgress(),
				"PerformingLayout");
		monitor.setMillisToDecideToPopup(100);
		monitor.setMillisToPopup(500);
		return monitor;
	}

	/**
	 * Hook from GraphEd to set attributes of a new cell
	 */
	public Map createCellAttributes(Point2D point)
	{
		Map map = new Hashtable();
		// Snap the Point to the Grid
		point = graph.snap((Point2D) point.clone());
		// Add a Bounds Attribute to the Map
		GraphConstants.setBounds(map, new Rectangle2D.Double(point.getX(),
				point.getY(), 0, 0));
		// Make sure the cell is resized on insert
		GraphConstants.setResize(map, true);
		// Add a nice looking gradient background
		GraphConstants.setGradientColor(map, Color.blue);
		// Add a Border Color Attribute to the Map
		GraphConstants.setBorderColor(map, Color.black);
		// Add a White Background
		GraphConstants.setBackground(map, Color.white);
		// Make Vertex Opaque
		GraphConstants.setOpaque(map, true);
		GraphConstants.setInset(map, 2);
		GraphConstants.setGradientColor(map, new Color(200, 200, 255));
		return map;
	}

	/**
	 * Hook from GraphEd to set attributes of a new edge
	 */
	public Map createEdgeAttributes()
	{
		Map map = new Hashtable();
		// Add a Line End Attribute
		GraphConstants.setLineEnd(map, GraphConstants.ARROW_SIMPLE);
		// Add a label along edge attribute
		GraphConstants.setLabelAlongEdge(map, true);
		// Adds a parallel edge router
		GraphConstants.setLineStyle(map, GraphConstants.STYLE_SPLINE);
		GraphConstants.setFont(map, GraphConstants.DEFAULTFONT.deriveFont(10f));
		return map;
	}

}
