package pt.iscte.hmcgf.extractor.relations;

import java.util.ArrayList;
import java.util.Collection;
import org.jgrapht.graph.DirectedPseudograph;

public class GraphRelationStorage implements RelationStorage
{
	private DirectedPseudograph<Type, Relation> graph;
	public GraphRelationStorage()
	{
		graph = new DirectedPseudograph<Type, Relation>(Relation.class);
	}
	private void prep(Type s, Type d)
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
	public Collection<Relation> getRelationsForType(Type type)
	{
		if (graph.containsVertex(type))
			return graph.outgoingEdgesOf(type);
		return new ArrayList<Relation>();
	}
	// TODO REMOVE TEMPORARY METHOD
	public DirectedPseudograph<Type, Relation> getGraph()
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
	public Collection<Type> getAllTypes()
	{
		return this.graph.vertexSet();
	}
	@Override
	public Collection<Relation> getAllRelations()
	{
		return this.graph.edgeSet();
	}
	@Override
	public Collection<Relation> getRelationsForString(String name)
	{
		Type t = getTypeByCanonicalName(name);
		if (t == null)
			return new ArrayList<Relation>();
		return getRelationsForType(t);
	}
	@Override
	public Type getTypeByCanonicalName(String canonicalName)
	{
		for (Type t : getAllTypes())
		{
			if (t.getCanonicalName().equals(canonicalName))
				return t;
		}
		return null;
	}

}
