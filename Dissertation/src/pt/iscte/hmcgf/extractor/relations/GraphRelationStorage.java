package pt.iscte.hmcgf.extractor.relations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
	public Set<Type> getAllTypes()
	{
		return this.graph.vertexSet();
	}

	@Override
	public Set<Relation> getAllRelations()
	{
		return this.graph.edgeSet();
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

	@Override
	public List<Relation> getOutgoingRelationsForType(Type type)
	{
		if (graph.containsVertex(type))
			return new ArrayList<Relation>(graph.outgoingEdgesOf(type));
		return new ArrayList<Relation>();
	}
	@Override
	public List<Relation> getOutgoingRelationsForString(String name)
	{
		Type t = getTypeByCanonicalName(name);
		if (t == null)
			return new ArrayList<Relation>();
		return getOutgoingRelationsForType(t);
	}
	@Override
	public List<Relation> getIncomingRelationsForType(Type type)
	{
		if (graph.containsVertex(type))
			return new ArrayList<Relation>(graph.incomingEdgesOf(type));
		return new ArrayList<Relation>();
	}
	@Override
	public List<Relation> getIncomingRelationsForString(String name)
	{
		Type t = getTypeByCanonicalName(name);
		if (t == null)
			return new ArrayList<Relation>();
		return getIncomingRelationsForType(t);
	}
	@Override
	public List<Relation> getAllRelationsInNamespace(String namespace)
	{
		ArrayList<Relation> relationsForNamespace = new ArrayList<>();
		for (Relation r : graph.edgeSet())
		{
			if (r.getSource().getCanonicalName().startsWith(namespace))
				relationsForNamespace.add(r);
		}
		return relationsForNamespace;
	}

}
