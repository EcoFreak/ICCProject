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
	public void addMethodRelation(MethodRelation r);

	/**
	 * Retrieves all relations found for given type
	 * 
	 * @param type
	 * @return
	 */
	public Collection<Relation> getRelationsForType(String type);

	public int getTypeCount();
	public int getRelationCount();
}
