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
import pt.iscte.hmcgf.extractor.relations.Type;
import pt.iscte.hmcgf.extractor.relations.Relation.RelationType;

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
		e.analyseClasses("pt.iscte.hmcgf.extractor.test.dummy", true);
	}

	@Test
	public void testTypeExtraction()
	{
		assertNotNull(graph.getTypeByCanonicalName(A));
		assertNotNull(graph.getTypeByCanonicalName(B));
		assertNotNull(graph.getTypeByCanonicalName(C));
		assertNotNull(graph.getTypeByCanonicalName(D));
		assertNotNull(graph.getTypeByCanonicalName(Z));
		assertTrue(graph.getTypeByCanonicalName(A).getCanonicalName().equals(A));
		assertTrue(graph.getTypeByCanonicalName(B).getCanonicalName().equals(B));
		assertTrue(graph.getTypeByCanonicalName(C).getCanonicalName().equals(C));
		assertTrue(graph.getTypeByCanonicalName(D).getCanonicalName().equals(D));
		assertTrue(graph.getTypeByCanonicalName(Z).getCanonicalName().equals(Z));
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
		Type typeForC = graph.getTypeByCanonicalName(C);
		Type typeForB = graph.getTypeByCanonicalName(B);
		Collection<Relation> relationsForC = graph.getRelationsForType(typeForC);
		boolean found = false;
		assertTrue(relationsForC.size() > 0);
		for (Relation relation : relationsForC)
		{
			if (relation.getRelationType().equals(RelationType.PARAM_IN_STATIC_METHOD)
					&& relation.getIntermediary().equals(relation.getSource())
					&& relation.getIntermediary().equals(typeForC)
					&& relation.getDestination().equals(typeForB))
				found = true;

		}
		assertTrue(found);
	}
	@Test
	public void testIndirectStaticRelation()
	{
		Type typeForA = graph.getTypeByCanonicalName(A);
		Type typeForC = graph.getTypeByCanonicalName(C);
		Type typeForB = graph.getTypeByCanonicalName(B);
		Collection<Relation> relationForA = graph.getRelationsForType(typeForA);
		boolean found = false;
		assertTrue(relationForA.size() > 0);
		for (Relation relation : relationForA)
		{
			if (relation.getRelationType().equals(RelationType.PARAM_IN_STATIC_METHOD)
					&& !relation.getIntermediary().equals(relation.getSource())
					&& relation.getIntermediary().equals(typeForB)
					&& relation.getDestination().equals(typeForC)
					&& relation.getSource().equals(typeForA))
				found = true;

		}
		assertTrue(found);
	}

	@Test
	public void testParameterType()
	{
		for (Relation r : graph.getAllRelations())
		{
			if (r.isImplicit())
				assertTrue(r.getAllParamenters().contains(r.getMainType()));
			else if (r.getAllParamenters().size() > 0
					&& !r.getSource().getCanonicalName().endsWith(ReflectionRelationExtractor.NO_TYPE)
					&& !r.getRelationType().equals(RelationType.INSTANCE_IN_INSTANCE_METHOD))
				assertTrue(r.getAllParamenters().contains(r.getSource()));
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
	public void testExistanceSubTypeRelation()
	{
		int count = 0;
		for (Relation r : graph.getAllRelations())
		{
			if (r.isImplicit())
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
			if (r.isImplicit())
				assertNotNull(r.getMainType());
		}
	}
	@Test
	public void testSubTypeRelation()
	{
		int count = 0;
		for (Relation r : graph.getAllRelations())
		{
			if (r.isImplicit())
			{
				assertNotNull(r.getMainType());
				Collection<Relation> relationsForMainType = graph.getRelationsForType(r.getMainType());
				try
				{
					assertTrue(Class.forName(r.getMainType().getCanonicalName()).isAssignableFrom(Class.forName(r.getSource().getCanonicalName())));
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
		Collection<Type> allTypes = graph.getAllTypes();
		for (Type type : allTypes)
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
		Type typeForA = graph.getTypeByCanonicalName(A);
		Type typeForB = graph.getTypeByCanonicalName(B);
		Type typeForC = graph.getTypeByCanonicalName(C);
		Type typeForD = graph.getTypeByCanonicalName(D);
		Collection<Type> allTypes = graph.getAllTypes();
		assertTrue(allTypes.size() > 0);
		assertTrue(allTypes.contains(typeForA));
		assertTrue(allTypes.contains(typeForB));
		assertTrue(allTypes.contains(typeForC));
		assertTrue(allTypes.contains(typeForD));
	}
	@Test
	public void testConstructors()
	{
		int count = 0;
		for (Relation r : graph.getAllRelations())
		{
			if (r.getRelationType().equals(RelationType.PARAM_IN_CONSTRUCTOR))
				count++;
		}
		assertTrue(count > 0);
	}

	@Test
	public void testNonStaticConstructors()
	{
		for (Relation r : graph.getAllRelations())
		{
			if (r.getRelationType().equals(RelationType.PARAM_IN_CONSTRUCTOR) && r.getRelationType().equals(RelationType.PARAM_IN_STATIC_METHOD))
				fail("Found one static constructor");
		}
	}

	@Test
	public void testInnerClassDetection()
	{
		System.out.println(graph.getAllTypes());
		Type typeForZ = graph.getTypeByCanonicalName(Z);
		assertTrue(graph.getAllTypes().contains(typeForZ));
	}
}
