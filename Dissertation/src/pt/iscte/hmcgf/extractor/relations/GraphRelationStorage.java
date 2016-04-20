package pt.iscte.hmcgf.extractor.relations;

import java.util.ArrayList;
import java.util.Collection;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

public class GraphRelationStorage implements RelationStorage
{
	private DirectedGraph<String, Relation> graph;
	public GraphRelationStorage()
	{
		graph = new DefaultDirectedGraph<String, Relation>(Relation.class);
	}
	private void prep(String s, String d)
	{
		if (!graph.containsVertex(s))
			graph.addVertex(s);
		if (!graph.containsVertex(d))
			graph.addVertex(d);
	}
	private void addRelation(Relation r)
	{
		if (!graph.containsEdge(r))
			graph.addEdge(r.getSource(), r.getDestination(), r);
	}
	@Override
	public void addMethodRelation(MethodRelation r)
	{
		System.out.println("Adding Method realtion " + r);
		prep(r.getSource(), r.getDestination());
		addRelation(r);
	}

	@Override
	public Collection<Relation> getRelationsForType(String type)
	{
		if (graph.containsVertex(type))
			return graph.outgoingEdgesOf(type);
		return new ArrayList<Relation>();
	}
	// TODO REMOVE TEMPORARY METHOD
	public DirectedGraph<String, Relation> getGraph()
	{
		return graph;
	}
	@Override
	public int getTypeCount()
	{
		return graph.vertexSet().size();
	}
	@Override
	public int getRelationCount()
	{
		return graph.edgeSet().size();
	}

}
