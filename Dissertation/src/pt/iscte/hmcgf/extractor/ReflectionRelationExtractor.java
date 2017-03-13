package pt.iscte.hmcgf.extractor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.reflections.ReflectionUtils;
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
	public static final String				NO_TYPE	= "NO_TYPE";
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
				System.err.println(e);
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
			int numValidParamsFound = 0;
			for (Class<?> paramType : constructor.getParameterTypes())
			{
				if (paramType.getCanonicalName().startsWith(wildcard))
				{
					numValidParamsFound++;
					addConstructorRelationForAllSubTypes(paramType, c, c, constructor, convertParameterTypes(constructor.getParameterTypes()));
				}
			}
			if (numValidParamsFound == 0)
			{
				// Constructor has no parameters or no API Types are used as parameters, create relationship for type NO_TYPE
				storage.addRelation(
						new Relation(wildcard + "." + NO_TYPE, normalizeType(c.getCanonicalName()), normalizeType(c.getCanonicalName()),
								constructor.getName(), true, true, false, null, convertParameterTypes(constructor.getParameterTypes())));
			}
		}
	}

	private void handleMethods(Class<?> c, String wildcard) throws NoClassDefFoundError
	{
		Method[] methods = c.getMethods();
		for (Method method : methods)
		{
			if (method.getDeclaringClass().equals(Object.class) || (!method.getReturnType().getCanonicalName().startsWith(wildcard)))
				continue;
			int numValidParamsFound = 0;
			for (Class<?> paramType : method.getParameterTypes())
			{
				if (paramType.getCanonicalName().startsWith(wildcard))
				{
					numValidParamsFound++;
					addMethodRelationForAllSubTypes(paramType, c, method.getReturnType(), method, convertParameterTypes(method.getParameterTypes()));
				}
			}
			if (numValidParamsFound == 0)
			{
				// Method has no parameters or no API Types are used as parameters, create relationship for type NO_TYPE
				storage.addRelation(
						new Relation(wildcard + "." + NO_TYPE, normalizeType(method.getReturnType().getCanonicalName()),
								normalizeType(c.getCanonicalName()),
								method.getName(), Modifier.isStatic(method.getModifiers()), false, false, null,
								convertParameterTypes(method.getParameterTypes())));
			}
		}
	}
	private String normalizeType(String canonicalName)
	{
		return canonicalName.replaceAll("\\[\\]", "");
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
				new Relation(normalizeType(from.getCanonicalName()), normalizeType(to.getCanonicalName()), intermidiary.getCanonicalName(),
						method.getName(), Modifier.isStatic(method.getModifiers()), false, false, null, parameters));
		for (Class<?> subType : getSubTypesOfClass(from))
		{
			storage.addRelation(
					new Relation(normalizeType(subType.getCanonicalName()), normalizeType(to.getCanonicalName()), intermidiary.getCanonicalName(),
							method.getName(), Modifier.isStatic(method.getModifiers()), false, true, from.getCanonicalName(), parameters));
		}

	}
	private void addConstructorRelationForAllSubTypes(Class<?> from, Class<?> intermidiary, Class<?> to, Constructor<?> constructor,
			Collection<String> parameters)
	{
		storage.addRelation(
				new Relation(normalizeType(from.getCanonicalName()), normalizeType(to.getCanonicalName()), intermidiary.getCanonicalName(),
						constructor.getName(), Modifier.isStatic(constructor.getModifiers()), true, false, null, parameters));
		for (Class<?> subType : getSubTypesOfClass(from))
		{
			storage.addRelation(
					new Relation(normalizeType(subType.getCanonicalName()), normalizeType(to.getCanonicalName()), intermidiary.getCanonicalName(),
							constructor.getName(), Modifier.isStatic(constructor.getModifiers()), true, true, from.getCanonicalName(), parameters));
		}

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
		classLoadersList.add(ClassLoader.getSystemClassLoader());

		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setScanners(new SubTypesScanner(false).filterResultsBy(
						new FilterBuilder().include(Object.class.getName())),
						new ResourcesScanner())
				// .setUrls(ClasspathHelper.forPackage(wildcard))
				.setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
				.filterInputsBy(new FilterBuilder().includePackage(wildcard)));
		// .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(wildcard))));

		classes = reflections.getSubTypesOf(Object.class);
		System.out.println(wildcard + ": Found " + classes.size() + " types");
		return classes;
	}
	@Override
	public RelationStorage getRelationStorage()
	{
		return storage;
	}

}
