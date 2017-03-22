package pt.iscte.hmcgf.extractor.test;

import static org.junit.Assert.*;
import java.util.Collection;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import org.junit.Before;
import org.junit.Test;
import pt.iscte.hmcgf.extractor.ReflectionRelationExtractor;
import pt.iscte.hmcgf.extractor.relations.GraphRelationStorage;
import pt.iscte.hmcgf.extractor.relations.Relation;
import pt.iscte.hmcgf.extractor.relations.RelationStorage;
import pt.iscte.hmcgf.extractor.relations.Relation.RelationType;

public class JavaMailTestSuite
{
	private RelationStorage	graph;
	public final String		NAMESPACE		= "javax.mail";
	public final String		SESSION			= Session.class.getCanonicalName();
	public final String		MIME_MESSAGE	= MimeMessage.class.getCanonicalName();
	public final String		TRANSPORT		= Transport.class.getCanonicalName();

	@Before
	public void setUpBefore() throws Exception
	{
		long startTime = System.nanoTime();
		graph = new GraphRelationStorage();
		ReflectionRelationExtractor e = new ReflectionRelationExtractor(graph);
		e.analyseClasses(NAMESPACE);
		long estimatedTime = System.nanoTime() - startTime;
		System.out.println("Loading of type graph for namespace " + NAMESPACE + " ocurred in " + String.format("%,8d", estimatedTime) + " ns");
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
	public void testSession()
	{
		/*
		 * Checks for the existance of: Message message = new MimeMessage(session);
		 */
		Collection<Relation> relationsForType = graph.getRelationsForType(SESSION);
		// TODO ALSO CHECK TO GO TO SUPER TYPE (MESSAGE)
		assertNotNull(relationsForType);
		assertTrue(relationsForType.size() > 0);
		boolean found = false;
		for (Relation relation : relationsForType)
		{
			if (relation.getDestination().equals(MIME_MESSAGE) &&
					relation.getRelationType().equals(RelationType.PARAM_IN_CONSTRUCTOR) &&
					relation.getIntermediary().equals(MIME_MESSAGE) &&
					relation.getNumInternalParameters() == 1)
			{
				found = true;
				break;
			}
		}
		assertTrue(found);
	}
	@Test
	public void testMessage()
	{
		/*
		 * Checks for the existance of: Transport.send(message);
		 */
		for (Relation r : graph.getAllRelations())
		{
			System.out.println(r);
		}
		Collection<Relation> relationsForType = graph.getRelationsForType(Message.class.getCanonicalName());
		assertNotNull(relationsForType);
		assertTrue(relationsForType.size() > 0);
		boolean found = false;
		for (Relation relation : relationsForType)
		{
			System.out.println(relation.getMethodName());
			if (relation.getDestination().equals("void") &&
					relation.getRelationType().equals(RelationType.PARAM_IN_STATIC_METHOD) &&
					relation.getIntermediary().equals(TRANSPORT) &&
					relation.getMethodName().equals("send") &&
					relation.getNumInternalParameters() == 1)
			{
				found = true;
				break;
			}
		}
		assertTrue(found);
	}
}
