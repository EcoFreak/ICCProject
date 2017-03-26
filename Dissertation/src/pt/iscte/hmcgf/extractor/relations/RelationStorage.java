package pt.iscte.hmcgf.extractor.relations;

import java.util.Collection;

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
	 * Returns all relations found for given type
	 * 
	 * @param type
	 * @return
	 */
	public Collection<Relation> getRelationsForType(Type type);
	/**
	 * Returns all relations found for given type canonical name
	 * 
	 * @param type
	 * @return
	 */
	public Collection<Relation> getRelationsForString(String type);

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
	public Collection<Type> getAllTypes();

	/**
	 * Returns a collection with all stored relations
	 * 
	 * @return collection of Relation
	 */
	public Collection<Relation> getAllRelations();
}
