package pt.iscte.hmcgf.extractor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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
		Set<Class<?>> classes = getAllClasses(wildcard);
		for (Class<?> c : classes)
		{
			try
			{
				handleMethods(c, wildcard);
				handleConstructors(c, wildcard);
			}
			catch (NoClassDefFoundError e)
			{
				// SUPRESS WARNING WITH NOCLASSDEF ERRORS
			}
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

	private void handleMethods(Class<?> c, String wildcard) throws NoClassDefFoundError
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
//		for (Class<?> subType : getSubTypesOfClass(from))
//		{
//			storage.addRelation(
//					new Relation(subType.getCanonicalName(), to.getCanonicalName(), intermidiary.getCanonicalName(), method.getName(),
//							Modifier.isStatic(method.getModifiers()), false, true, from.getCanonicalName(), parameters));
//		}

	}
	private void addConstructorRelationForAllSubTypes(Class<?> from, Class<?> intermidiary, Class<?> to, Constructor<?> constructor,
			Collection<String> parameters)
	{
		storage.addRelation(
				new Relation(from.getCanonicalName(), to.getCanonicalName(), intermidiary.getCanonicalName(), constructor.getName(),
						Modifier.isStatic(constructor.getModifiers()), true, false, null, parameters));
//		for (Class<?> subType : getSubTypesOfClass(from))
//		{
//			storage.addRelation(
//					new Relation(subType.getCanonicalName(), to.getCanonicalName(), intermidiary.getCanonicalName(), constructor.getName(),
//							Modifier.isStatic(constructor.getModifiers()), true, true, from.getCanonicalName(), parameters));
//		}

	}
	private Collection<Class<?>> getSubTypesOfClass(Class<?> c)
	{
		if (!this.storedSubTypes.containsKey(c))
			this.storedSubTypes.putAll(c, reflectionsInstance.getSubTypesOf(c));
		return this.storedSubTypes.get(c);
	}
	private Set<Class<?>> getAllClasses(String wildcard)
	{
		Set<Class<?>> classes = null;
		List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		classLoadersList.add(ClasspathHelper.staticClassLoader());

		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
				.setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
				.filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(wildcard))));

		classes = reflections.getSubTypesOf(Object.class);
		return classes;
	}
	@Override
	public RelationStorage getRelationStorage()
	{
		return storage;
	}

}
