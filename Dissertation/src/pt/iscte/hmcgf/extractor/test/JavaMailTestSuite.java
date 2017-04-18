package pt.iscte.hmcgf.extractor.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
import pt.iscte.hmcgf.extractor.relations.Relation.RelationType;
import pt.iscte.hmcgf.extractor.relations.RelationStorage;
import pt.iscte.hmcgf.extractor.relations.Type;

public class JavaMailTestSuite
{
	private RelationStorage	graph;
	public final String		NAMESPACE		= "javax.mail";
	public final String		SESSION			= Session.class.getCanonicalName();
	public final String		MIME_MESSAGE	= MimeMessage.class.getCanonicalName();
	public final String		MESSAGE			= Message.class.getCanonicalName();
	public final String		TRANSPORT		= Transport.class.getCanonicalName();

	@Before
	public void setUpBefore() throws Exception
	{
		long startTime = System.nanoTime();
		graph = new GraphRelationStorage();
		ReflectionRelationExtractor e = new ReflectionRelationExtractor(graph);
		e.analyseClasses(NAMESPACE, true);
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
		Type typeForSession = graph.getTypeByCanonicalName(SESSION);
		Type typeForMimeMessage = graph.getTypeByCanonicalName(MIME_MESSAGE);
		assertNotNull(typeForSession);
		assertTrue(typeForSession.getCanonicalName().equals(SESSION));
		Collection<Relation> relationsForType = graph.getOutgoingRelationsForType(typeForSession);
		// TODO ALSO CHECK TO GO TO SUPER TYPE (MESSAGE)
		assertNotNull(relationsForType);
		assertTrue(relationsForType.size() > 0);
		boolean found = false;
		for (Relation relation : relationsForType)
		{
			if (relation.getDestination().equals(typeForMimeMessage) &&
					relation.getRelationType().equals(RelationType.PARAM_IN_CONSTRUCTOR) &&
					relation.getIntermediary().equals(typeForMimeMessage) &&
					relation.getNumAllParameters() == 1)
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
		Type typeForMessage = graph.getTypeByCanonicalName(MESSAGE);
		Type typeForTransport = graph.getTypeByCanonicalName(TRANSPORT);
		assertNotNull(typeForMessage);
		assertTrue(typeForMessage.getCanonicalName().equals(MESSAGE));
		Collection<Relation> relationsForType = graph.getOutgoingRelationsForType(typeForMessage);
		assertNotNull(relationsForType);
		assertTrue(relationsForType.size() > 0);
		boolean found = false;
		for (Relation relation : relationsForType)
		{
			if (relation.getDestination().equals("void") &&
					relation.getRelationType().equals(RelationType.PARAM_IN_STATIC_METHOD) &&
					relation.getIntermediary().equals(typeForTransport) &&
					relation.getMethodName().equals("send") &&
					relation.getNumAllParameters() == 1)
			{
				found = true;
				break;
			}
		}
		assertTrue(found);
	}
}
