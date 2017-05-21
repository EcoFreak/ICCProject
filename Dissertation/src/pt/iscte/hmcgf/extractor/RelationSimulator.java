package pt.iscte.hmcgf.extractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import pt.iscte.hmcgf.extractor.relations.Relation;
import pt.iscte.hmcgf.extractor.relations.RelationStorage;
import pt.iscte.hmcgf.extractor.relations.Type;

public class RelationSimulator {
	private static final int NUM_SUGGESTIONS = 30;
	private List<Type> scope;
	private Type lastTypeAdded;
	private boolean chooseFirst;

	public RelationSimulator(boolean chooseFirst) {
		this.chooseFirst = chooseFirst;
		scope = new ArrayList<>();
	}

	public List<Type> getScope() {
		return scope;
	}

	public void simulate(String namespace, RelationStorage storage, int numLines) {
		ArrayList<String> temp = new ArrayList<>();
		temp.add(namespace);
		simulate(temp, storage, numLines);
	}

	public void simulate(List<String> namespaces, RelationStorage storage, int numLines) {
		// step 1. get all no cost relations, sort by outgoing types (unique)
		ReflectionSimulatorComparator comparator = new ReflectionSimulatorComparator(storage,
				new SimpleGainCalulator(storage));
		RelationWithNoCost noCostFilter = new RelationWithNoCost(scope);
		NewTypeProduction newTypeFilter = new NewTypeProduction(scope);
		Collection<Relation> allRelations = storage.getAllRelationsInNamespace(namespaces);
		// Clone collection
		List<Relation> filtered = new ArrayList<>(allRelations);
		// filter collection
		filterRelations(filtered, noCostFilter);
		Collections.sort(filtered, comparator);
		processRelations(filtered);
		// step 2. with the produced type get relations also sorted by outgoing
		// types (unique)
		for (int i = 1; i < numLines; i++) {
			System.out.println(scope);
			 List<Relation> auxList = new ArrayList<>(allRelations);
			//List<Relation> auxList = storage.getOutgoingRelationsForType(lastTypeAdded);
			filterRelations(auxList, noCostFilter);
			filterRelations(auxList, newTypeFilter);
			Collections.sort(auxList, comparator);
			processRelations(auxList);
		}

	}

	private void processRelations(List<Relation> relations) {
		Relation r = null;
		int choice = -1;
		if (chooseFirst)
			r = relations.get(0);
		else {
			do {
				for (int i = 0; i < NUM_SUGGESTIONS && i < relations.size(); i++)
					System.out.println((i + 1) + ": " + relations.get(i).getUsageExample());
				System.out.print("Choice: ");
				try {
					choice = Integer.parseInt(new Scanner(System.in).nextLine());
				} catch (NumberFormatException e) {
					choice = -1;
				}
			} while (choice < 1 || choice > NUM_SUGGESTIONS || choice >= relations.size());
		}
		r = relations.get(choice - 1);
		Type t = r.getDestination();
		processTypeForStack(t);
		lastTypeAdded = t;
		System.out.println(r.getUsageExample());
	}

	private void processTypeForStack(Type t) {
		if (!scope.contains(t))
			scope.add(t);

		/*
		 * for (Type subtype : t.getSupertypes()) { if
		 * (!scope.contains(subtype)) scope.add(subtype); }
		 */
	}

	public static void filterRelations(List<Relation> col, Filter<Relation> filter) {
		for (Iterator<Relation> i = col.iterator(); i.hasNext();) {
			Relation obj = i.next();
			if (filter.filter(obj)) {
				i.remove();
			}
		}
	}

	public Type getLastTypeAdded() {
		return lastTypeAdded;
	}

	public class ReflectionSimulatorComparator implements Comparator<Relation> {
		private RelationStorage storage;
		private GainCalculator<Type> calculator;

		public ReflectionSimulatorComparator(RelationStorage storage, GainCalculator<Type> calculator) {
			this.storage = storage;
			this.calculator = calculator;
		}

		@Override
		public int compare(Relation r1, Relation r2) {

			/*
			 * if (r1.getRelationType().equals(Relation.RelationType.
			 * PARAM_IN_CONSTRUCTOR) &&
			 * !r2.getRelationType().equals(Relation.RelationType.
			 * PARAM_IN_CONSTRUCTOR)) return -1; else if
			 * (!r1.getRelationType().equals(Relation.RelationType.
			 * PARAM_IN_CONSTRUCTOR) &&
			 * r2.getRelationType().equals(Relation.RelationType.
			 * PARAM_IN_CONSTRUCTOR)) return 1; return
			 * storage.getOutgoingRelationsForType(r2.getDestination()).size() -
			 * storage.getOutgoingRelationsForType(r1.getDestination()).size();
			 */
			return (int) Math.ceil((calculator.calculateGainForType(r2.getDestination()) * r2.getBaseValue())
					- (calculator.calculateGainForType(r1.getDestination()) * r1.getBaseValue()));
		}
	}

	public class RelationWithNoCost implements Filter<Relation> {

		private List<Type> scope;

		public RelationWithNoCost(List<Type> scope) {
			this.scope = scope;
		}

		@Override
		public boolean filter(Relation t) {
			return t.calculateCost(scope) != 1;
		}

	}

	public class NewTypeProduction implements Filter<Relation> {

		private List<Type> scope;

		public NewTypeProduction(List<Type> scope) {
			this.scope = scope;
		}

		@Override
		public boolean filter(Relation t) {
			return scope.contains(t.getDestination());
		}

	}

	public interface Filter<R extends Relation> {
		public boolean filter(R r);
	}

	public interface GainCalculator<T extends Type> {
		public int calculateGainForType(T t);
	}

	public class SimpleGainCalulator implements GainCalculator<Type> {
		private RelationStorage storage;

		public SimpleGainCalulator(RelationStorage storage) {
			this.storage = storage;
		}

		@Override
		public int calculateGainForType(Type t) {
			return RelationStorage.getUniqueOutgoingTypesForRelationshipSet(storage.getOutgoingRelationsForType(t))
					.size();
		}

	}
}
