package pt.iscte.hmcgf.extractor.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import pt.iscte.hmcgf.extractor.ReflectionRelationExtractor;
import pt.iscte.hmcgf.extractor.relations.GraphRelationStorage;
import pt.iscte.hmcgf.extractor.relations.Relation;
import pt.iscte.hmcgf.extractor.relations.RelationStorage;

public class DummyTestSuite
{
	private RelationStorage		graph;
	public static final String	A	= "pt.iscte.hmcgf.extractor.test.dummy.A";
	public static final String	B	= "pt.iscte.hmcgf.extractor.test.dummy.B";
	public static final String	C	= "pt.iscte.hmcgf.extractor.test.dummy.C";
	public static final String	D	= "pt.iscte.hmcgf.extractor.test.dummy.D";
	public static final String	Z	= "pt.iscte.hmcgf.extractor.test.dummy.D.Z";
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
		assertTrue(graph.getRelationCount() > 0);
	}
	@Test
	public void testNumberOfTypes()
	{
		assertTrue(graph.getTypeCount() > 0);
	}
	@Test
	public void testDirectStaticRelation()
	{
		Collection<Relation> relationsForC = graph.getRelationsForType(C);
		boolean found = false;
		assertTrue(relationsForC.size() > 0);
		for (Relation relation : relationsForC)
		{
			if (relation.IsStatic()
					&& relation.getIntermediary().equals(relation.getSource())
					&& relation.getIntermediary().equals(C)
					&& relation.getDestination().equals(B))
				found = true;

		}
		assertTrue(found);
	}
	@Test
	public void testIndirectStaticRelation()
	{
		Collection<Relation> relationForA = graph.getRelationsForType(A);
		boolean found = false;
		assertTrue(relationForA.size() > 0);
		for (Relation relation : relationForA)
		{
			if (relation.IsStatic()
					&& !relation.getIntermediary().equals(relation.getSource())
					&& relation.getIntermediary().equals(B)
					&& relation.getDestination().equals(C)
					&& relation.getSource().equals(A))
				found = true;

		}
		assertTrue(found);
	}

	@Test
	public void testParameterType()
	{
		for (Relation r : graph.getAllRelations())
		{
			if (r.requiresCast())
				assertTrue(r.getParamenters().contains(r.getMainType()));
			else
				assertTrue(r.getParamenters().contains(r.getSource()));
		}
	}
	@Test
	public void testRelations()
	{
		for (Relation r : graph.getAllRelations())
		{
			assertTrue(graph.getRelationsForType(r.getSource()).contains(r));
		}
	}

	@Test
	public void testParameterCount()
	{
		for (Relation r : graph.getAllRelations())
		{
			if (r.getNumParameters() == 0)
				fail("Relation without any parameters");
		}
	}

	@Test
	public void testExistanceSubTypeRelation()
	{
		int count = 0;
		for (Relation r : graph.getAllRelations())
		{
			if (r.requiresCast())
				count++;
		}
		assertTrue(count > 0);
	}
	@Test
	public void testValidRelations()
	{
		for (Relation r : graph.getAllRelations())
		{
			assertNotNull(r.getSource());
			assertNotNull(r.getIntermediary());
			assertNotNull(r.getDestination());
			if (r.requiresCast())
				assertNotNull(r.getMainType());
		}
	}
	@Test
	public void testSubTypeRelation()
	{
		int count = 0;
		for (Relation r : graph.getAllRelations())
		{
			if (r.requiresCast())
			{
				assertNotNull(r.getMainType());
				Collection<Relation> relationsForMainType = graph.getRelationsForType(r.getMainType());
				try
				{
					assertTrue(Class.forName(r.getMainType()).isAssignableFrom(Class.forName(r.getSource())));
					assertTrue(relationsForMainType.size() > 0);
					for (Relation mainTypeRelation : relationsForMainType)
					{
						if (mainTypeRelation.isEquivalent(r))
							count++;
					}
				}
				catch (ClassNotFoundException e)
				{
					fail(e.getMessage());
				}
				assertTrue(relationsForMainType.size() > 0);
			}
		}
		assertTrue(count > 0);
	}
	@Test
	public void testRelationSource()
	{
		Collection<String> allTypes = graph.getAllTypes();
		for (String type : allTypes)
		{
			Collection<Relation> relationsForType = graph.getRelationsForType(type);
			for (Relation relation : relationsForType)
			{
				assertTrue(relation.getSource().equals(type));
			}
		}
	}
	@Test
	public void testIfAllTypesExists()
	{
		Collection<String> allTypes = graph.getAllTypes();
		assertTrue(allTypes.size() > 0);
		assertTrue(allTypes.contains(A));
		assertTrue(allTypes.contains(B));
		assertTrue(allTypes.contains(C));
		assertTrue(allTypes.contains(D));
	}
	@Test
	public void testConstructors()
	{
		int count = 0;
		for (Relation r : graph.getAllRelations())
		{
			if (r.isConstructor())
				count++;
		}
		assertTrue(count > 0);
	}

	@Test
	public void testNonStaticConstructors()
	{
		for (Relation r : graph.getAllRelations())
		{
			if (r.isConstructor() && r.IsStatic())
				fail("Found one static constructor");
		}
	}

	@Test
	public void testInnerClassDetection()
	{
		System.out.println(graph.getAllTypes());
		assertTrue(graph.getAllTypes().contains(Z));
	}
}
