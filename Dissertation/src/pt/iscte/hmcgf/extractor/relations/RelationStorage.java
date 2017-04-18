package pt.iscte.hmcgf.extractor.relations;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface RelationStorage
{
	/**
	 * Stores a given method relation
	 * 
	 * @param r
	 *            method relation object
	 */
	public boolean addRelation(Relation r);

	/**
	 * Returns all outgoing relations found for given type
	 * 
	 * @param type
	 * @return
	 */
	public List<Relation> getOutgoingRelationsForType(Type type);
	/**
	 * Returns all outgoing relations found for given type canonical name
	 * 
	 * @param type
	 * @return
	 */
	public List<Relation> getOutgoingRelationsForString(String type);

	/**
	 * Returns all incoming relations found for given type
	 * 
	 * @param type
	 * @return
	 */
	public List<Relation> getIncomingRelationsForType(Type type);
	/**
	 * Returns all incoming relations found for given type canonical name
	 * 
	 * @param type
	 * @return
	 */
	public List<Relation> getIncomingRelationsForString(String type);

	/***
	 * Get Type from String canonical name
	 * 
	 * @param canonicalName
	 * @return
	 */
	public Type getTypeByCanonicalName(String canonicalName);

	/**
	 * Returns number of types stored
	 * 
	 * @return number of types
	 */
	public int getTypeCount();

	/**
	 * Returns number of relations between types
	 * 
	 * @return number of relations
	 */
	public int getRelationCount();

	/**
	 * Returns a collection with all stored types
	 * 
	 * @return collection of String (Type)
	 */
	public Set<Type> getAllTypes();

	/**
	 * Returns a collection with all stored relations
	 * 
	 * @return collection of Relation
	 */
	public Set<Relation> getAllRelations();

	/**
	 * Returns a collection with all stored relations in provided namespace
	 * 
	 * @return collection of Relation
	 */
	public List<Relation> getAllRelationsInNamespace(String namespace);

	
	public static Set<Type> getUniqueOutgoingTypesForRelationshipSet(Collection<Relation> set)
	{
		HashSet<Type> outgoingTypes = new HashSet<Type>();
		for (Relation r : set)
		{
			outgoingTypes.add(r.getDestination());
		}
		return outgoingTypes;
	}

	public static Set<Type> getUniqueIncomingTypesForRelationshipSet(Collection<Relation> set)
	{
		HashSet<Type> incomingTypes = new HashSet<Type>();
		for (Relation r : set)
		{
			incomingTypes.add(r.getSource());
		}
		return incomingTypes;
	}

}
