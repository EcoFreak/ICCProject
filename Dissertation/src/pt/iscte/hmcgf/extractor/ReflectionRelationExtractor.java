package pt.iscte.hmcgf.extractor;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import com.google.common.reflect.ClassPath;
import pt.iscte.hmcgf.extractor.relations.GraphRelationStorage;
import pt.iscte.hmcgf.extractor.relations.MethodRelation;
import pt.iscte.hmcgf.extractor.relations.RelationStorage;

public class ReflectionRelationExtractor implements RelationExtractor
{
	private GraphRelationStorage storage;
	public ReflectionRelationExtractor(GraphRelationStorage storage)
	{
		this.storage = storage;
	}
	@Override
	public boolean analyseClasses(String wildcard)
	{
		List<Class<?>> classes = getAllClasses(wildcard);
		for (Class<?> class1 : classes)
		{
			Method[] methods = class1.getMethods();
			for (Method method : methods)
			{
				String ret = method.getReturnType().getCanonicalName();
				for (Class<?> method2 : method.getParameterTypes())
				{
					storage.addMethodRelation(
							new MethodRelation(method2.getCanonicalName(), ret, method.getName(), Modifier.isStatic(method.getModifiers()), false,
									method.getParameterCount()));
				}
			}
		}
		return classes.isEmpty();
	}
	private List<Class<?>> getAllClasses(String wildcard)
	{
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		try
		{
			for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses())
			{
				if (info.getName().startsWith(wildcard))
				{
					final Class<?> clazz;
					try
					{
						clazz = info.load();
						classes.add(clazz);
						// do something with your clazz
					}
					catch (NoClassDefFoundError ex)
					{

					}
					
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return classes;
	}
	@Override
	public RelationStorage getRelationStorage()
	{
		return storage;
	}

}
