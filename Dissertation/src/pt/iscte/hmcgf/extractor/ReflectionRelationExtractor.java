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
import pt.iscte.hmcgf.extractor.relations.Relation;
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
					addConstructorRelationForAllSubTypes(paramType, c, c, constructor, convertParameterTypes(constructor.getParameterTypes()));
				}
			}
		}
	}

	private void handleMethods(Class<?> c, String wildcard)
	{
		Method[] methods = c.getMethods();
		for (Method method : methods)
		{
			if (method.getDeclaringClass().equals(Object.class))
				continue;
			for (Class<?> paramType : method.getParameterTypes())
			{
				if (paramType.getCanonicalName().startsWith(wildcard))
				{
					addMethodRelationForAllSubTypes(paramType, c, method.getReturnType(), method, convertParameterTypes(method.getParameterTypes()));
				}
			}
		}
	}
	private Collection<String> convertParameterTypes(Class<?>[] params)
	{
		ArrayList<String> stringParams = new ArrayList<>();
		for (Class<?> c : params)
		{
			stringParams.add(c.getCanonicalName());
		}
		return stringParams;
	}
	private void addMethodRelationForAllSubTypes(Class<?> from, Class<?> intermidiary, Class<?> to, Method method, Collection<String> parameters)
	{
		storage.addRelation(
				new Relation(from.getCanonicalName(), to.getCanonicalName(), intermidiary.getCanonicalName(), method.getName(),
						Modifier.isStatic(method.getModifiers()), false, false, null, parameters));
		for (Class<?> subType : getSubTypesOfClass(from))
		{
			storage.addRelation(
					new Relation(subType.getCanonicalName(), to.getCanonicalName(), intermidiary.getCanonicalName(), method.getName(),
							Modifier.isStatic(method.getModifiers()), false, true, from.getCanonicalName(), parameters));
		}

	}
	private void addConstructorRelationForAllSubTypes(Class<?> from, Class<?> intermidiary, Class<?> to, Constructor<?> constructor,
			Collection<String> parameters)
	{
		storage.addRelation(
				new Relation(from.getCanonicalName(), to.getCanonicalName(), intermidiary.getCanonicalName(), constructor.getName(),
						Modifier.isStatic(constructor.getModifiers()), true, false, null, parameters));
		for (Class<?> subType : getSubTypesOfClass(from))
		{
			storage.addRelation(
					new Relation(subType.getCanonicalName(), to.getCanonicalName(), intermidiary.getCanonicalName(), constructor.getName(),
							Modifier.isStatic(constructor.getModifiers()), true, true, from.getCanonicalName(), parameters));
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
