package pt.iscte.hmcgf.extractor;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import com.google.common.reflect.ClassPath;
import pt.iscte.hmcgf.extractor.relations.MethodRelation;
import pt.iscte.hmcgf.extractor.relations.RelationStorage;

public class ReflectionRelationExtractor implements RelationExtractor
{
	private RelationStorage storage;
	public ReflectionRelationExtractor(RelationStorage storage)
	{
		this.storage = storage;
	}
	@Override
	public boolean analyseClasses(String wildcard)
	{
		List<Class<?>> classes = getAllClasses(wildcard);
		for (Class<?> c : classes)
		{
			handleMethods(c);
			handleConstructors(c);
		}
		return classes.isEmpty();
	}
	private void handleConstructors(Class<?> c)
	{
		Constructor<?>[] constructors = c.getConstructors();
		for (Constructor<?> constructor : constructors)
		{
			for (Class<?> paramType : constructor.getParameterTypes())
			{
				storage.addMethodRelation(
						new MethodRelation(paramType.getCanonicalName(), c.getCanonicalName(), c.getCanonicalName(), constructor.getName(),
								Modifier.isStatic(constructor.getModifiers()), true, constructor.getParameterCount()));
			}
		}
	}
	private void handleMethods(Class<?> c)
	{
		Method[] methods = c.getDeclaredMethods();
		for (Method method : methods)
		{
			String ret = method.getReturnType().getCanonicalName();
			for (Class<?> paramType : method.getParameterTypes())
			{
				storage.addMethodRelation(
						new MethodRelation(paramType.getCanonicalName(), ret, c.getCanonicalName(), method.getName(),
								Modifier.isStatic(method.getModifiers()), false, method.getParameterCount()));
			}
		}
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
