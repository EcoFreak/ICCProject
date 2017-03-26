package pt.iscte.hmcgf.extractor;

import pt.iscte.hmcgf.extractor.relations.RelationStorage;

/**
 * 
 * @author Henrique Ferreira
 *
 */
public interface RelationExtractor
{
	/**
	 * Perform analysis to the classes matching the provided wildcard
	 * 
	 * @param wildcard
	 *            wildcard in format: pt.iscte.hmcgf.*
	 * @return true if analysis executed successfully, false otherwise
	 */
	public boolean analyseClasses(String wildcard, boolean exploreSubtypes);
	/**
	 * Retrieve RelationStorage Object from Extractor
	 * 
	 * @return RelationStorage Object
	 */
	public RelationStorage getRelationStorage();
}
