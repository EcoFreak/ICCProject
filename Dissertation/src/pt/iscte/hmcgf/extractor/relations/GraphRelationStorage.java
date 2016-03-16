package pt.iscte.hmcgf.extractor.relations;

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
	public void addFieldRelation(FieldRelation r)
	{
		prep(r.getSource(), r.getDestination());
		addRelation(r);
	}
	@Override
	public Collection<Relation> getRelationsForType(String type)
	{
		return graph.outgoingEdgesOf(type);
	}
	// TODO REMOVE TEMPORARY METHOD
	public DirectedGraph<String, Relation> getGraph()
	{
		return graph;
	}

}
