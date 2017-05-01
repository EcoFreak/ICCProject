package pt.iscte.hmcgf.extractor;

import java.util.List;
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
	 * @param namespace
	 *            in format: pt.iscte.hmcgf
	 * @return true if analysis executed successfully, false otherwise
	 */
	public boolean analyseClasses(String namespace, boolean exploreSubtypes);

	/**
	 * Perform analysis to the classes matching the provided wildcard
	 * 
	 * @param namespaces
	 *            list of namespaces
	 * @return true if analysis executed successfully, false otherwise
	 */
	public boolean analyseClasses(List<String> namespaces, boolean exploreSubtypes);
	/**
	 * Retrieve RelationStorage Object from Extractor
	 * 
	 * @return RelationStorage Object
	 */
	public RelationStorage getRelationStorage();
}
