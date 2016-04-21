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
			handleMethods(c, wildcard);
			handleConstructors(c, wildcard);
		}
		return classes.isEmpty();
	}
	private void handleConstructors(Class<?> c, String wildcard)
	{
		Constructor<?>[] constructors = c.getConstructors();
		for (Constructor<?> constructor : constructors)
		{
			for (Class<?> paramType : constructor.getParameterTypes())
			{
				if (paramType.getCanonicalName().startsWith(wildcard))
				{
					storage.addMethodRelation(
							new MethodRelation(paramType.getCanonicalName(), c.getCanonicalName(), c.getCanonicalName(), constructor.getName(),
									Modifier.isStatic(constructor.getModifiers()), true, constructor.getParameterCount()));
				}
			}
		}
	}
	private void handleMethods(Class<?> c, String wildcard)
	{
		// Method[] methods = c.getDeclaredMethods();
		Method[] methods = c.getMethods();
		for (Method method : methods)
		{
			if (method.getDeclaringClass().equals(Object.class))
				continue;
			String ret = method.getReturnType().getCanonicalName();
			for (Class<?> paramType : method.getParameterTypes())
			{
				if (paramType.getCanonicalName().startsWith(wildcard))
				{
					// TODO ADD OTHER PARAMETER INFO
					storage.addMethodRelation(
							new MethodRelation(paramType.getCanonicalName(), ret, c.getCanonicalName(), method.getName(),
									Modifier.isStatic(method.getModifiers()), false, method.getParameterCount()));
				}
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
						// TODO HANDLE ERROR
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
