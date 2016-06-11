package pt.iscte.hmcgf.extractor.relations;

import java.util.ArrayList;
import java.util.Collection;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DirectedPseudograph;

public class GraphRelationStorage implements RelationStorage
{
	private DirectedPseudograph<String, Relation> graph;
	public GraphRelationStorage()
	{
		graph = new DirectedPseudograph<String, Relation>(Relation.class);
	}
	private void prep(String s, String d)
	{
		if (!graph.containsVertex(s))
			graph.addVertex(s);
		if (!graph.containsVertex(d))
			graph.addVertex(d);
	}
	@Override
	public boolean addRelation(Relation r)
	{
		prep(r.getSource(), r.getDestination());
		if (!graph.containsEdge(r))
			return graph.addEdge(r.getSource(), r.getDestination(), r);
		else
			return false;
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
	@Override
	public Collection<String> getAllTypes()
	{
		return this.graph.vertexSet();
	}
	@Override
	public Collection<Relation> getAllRelations()
	{
		return this.graph.edgeSet();
	}

}
