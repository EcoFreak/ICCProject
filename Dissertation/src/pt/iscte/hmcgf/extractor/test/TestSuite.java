package pt.iscte.hmcgf.extractor.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import pt.iscte.hmcgf.extractor.ReflectionRelationExtractor;
import pt.iscte.hmcgf.extractor.relations.GraphRelationStorage;
import pt.iscte.hmcgf.extractor.relations.RelationStorage;

public class TestSuite
{
	private RelationStorage graph;

	@Before
	public void setUpBefore() throws Exception
	{
		graph = new GraphRelationStorage();
		ReflectionRelationExtractor e = new ReflectionRelationExtractor(graph);
		e.analyseClasses("pt.iscte.hmcgf.extractor.test.dummy");
	}

	@Test
	public void testNumberOfRelations()
	{
		assertEquals(10, graph.getTypeCount());
	}
	@Test
	public void testNumberOfTypes()
	{
		assertEquals(10, graph.getRelationCount());
	}
	@Test
	public void testDirectStaticRelation()
	{
		System.out.println(graph.getRelationCount());
		assertTrue(graph.getRelationCount() > 0);
	}
	@Test
	public void testIndirectStaticRelation()
	{
		System.out.println(graph.getRelationCount());
		assertTrue(graph.getRelationCount() > 0);
	}

}
