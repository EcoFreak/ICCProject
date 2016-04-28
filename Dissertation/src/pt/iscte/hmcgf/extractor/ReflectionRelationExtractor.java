package pt.iscte.hmcgf.extractor;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.reflections.Reflections;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.ClassPath;
import pt.iscte.hmcgf.extractor.relations.MethodRelation;
import pt.iscte.hmcgf.extractor.relations.RelationStorage;

public class ReflectionRelationExtractor implements RelationExtractor
{
	private RelationStorage					storage;
	private Reflections						reflectionsInstance;
	private Multimap<Class<?>, Class<?>>	storedSubTypes;
	public ReflectionRelationExtractor(RelationStorage storage)
	{
		this.storage = storage;
	}
	@Override
	public boolean analyseClasses(String wildcard)
	{
		this.reflectionsInstance = new Reflections(wildcard);
		this.storedSubTypes = ArrayListMultimap.create();
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
					addConstructorRelationForAllSubTypes(paramType, c, c, constructor);
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
			for (Class<?> paramType : method.getParameterTypes())
			{
				if (paramType.getCanonicalName().startsWith(wildcard))
				{
					// TODO ADD OTHER PARAMETER INFO
					addMethodRelationForAllSubTypes(paramType, c, method.getReturnType(), method);
				}
			}
		}
	}
	private void addMethodRelationForAllSubTypes(Class<?> from, Class<?> intermidiary, Class<?> to, Method method)
	{
		storage.addMethodRelation(
				new MethodRelation(from.getCanonicalName(), to.getCanonicalName(), intermidiary.getCanonicalName(), method.getName(),
						Modifier.isStatic(method.getModifiers()), false, method.getParameterCount()));
		for (Class<?> subType : getSubTypesOfClass(from))
		{
			storage.addMethodRelation(
					new MethodRelation(subType.getCanonicalName(), to.getCanonicalName(), intermidiary.getCanonicalName(), method.getName(),
							Modifier.isStatic(method.getModifiers()), false, method.getParameterCount()));
		}

	}
	private void addConstructorRelationForAllSubTypes(Class<?> from, Class<?> intermidiary, Class<?> to, Constructor<?> constructor)
	{
		storage.addMethodRelation(
				new MethodRelation(from.getCanonicalName(), to.getCanonicalName(), intermidiary.getCanonicalName(), constructor.getName(),
						false, true, constructor.getParameterCount()));
		for (Class<?> subType : getSubTypesOfClass(from))
		{
			storage.addMethodRelation(
					new MethodRelation(subType.getCanonicalName(), to.getCanonicalName(), intermidiary.getCanonicalName(), constructor.getName(),
							false, true, constructor.getParameterCount()));
		}

	}
	private Collection<Class<?>> getSubTypesOfClass(Class<?> c)
	{
		if (!this.storedSubTypes.containsKey(c))
			this.storedSubTypes.putAll(c, reflectionsInstance.getSubTypesOf(c));
		return this.storedSubTypes.get(c);
	}
	private List<Class<?>> getAllClasses(String wildcard)
	{
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		try
		{
			for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClassesRecursive(wildcard))
			{
				// if (info.getName().startsWith(wildcard))
				// {
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
				// }
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
