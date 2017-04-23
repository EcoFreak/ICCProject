package pt.iscte.hmcgf.extractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.management.relation.RelationType;

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
		// step 1. get all no cost relations, sort by outgoing types (unique)
		ReflectionSimulatorComparator comparator = new ReflectionSimulatorComparator(storage);
		RelationWithNoCost noCostFilter = new RelationWithNoCost(scope);
		NewTypeProduction newTypeFilter = new NewTypeProduction(scope);
		Collection<Relation> allRelations = storage.getAllRelationsInNamespace(namespace);
		// Clone collection
		List<Relation> filtered = new ArrayList<>(allRelations);
		// filter collection
		filterRelations(filtered, noCostFilter);
		Collections.sort(filtered, comparator);
		processRelations(filtered);
		scope.add(storage.getTypeByCanonicalName("org.eclipse.swt.widgets.Composite"));
		// step 2. with the produced type get relations also sorted by outgoing
		// types (unique)
		for (int i = 1; i < numLines; i++) {
			System.out.println(scope);
			// List<Relation> auxList = new ArrayList<>(allRelations);
			List<Relation> auxList = storage.getOutgoingRelationsForType(lastTypeAdded);
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
		if (!scope.contains(t))
			scope.add(t);

		lastTypeAdded = t;
		System.out.println(r.getUsageExample());
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

		public ReflectionSimulatorComparator(RelationStorage storage) {
			this.storage = storage;
		}

		@Override
		public int compare(Relation r1, Relation r2) {

			// if
			// (r1.getRelationType().equals(Relation.RelationType.PARAM_IN_CONSTRUCTOR)
			// &&
			// !r2.getRelationType().equals(Relation.RelationType.PARAM_IN_CONSTRUCTOR))
			// return -1;
			// else if
			// (!r1.getRelationType().equals(Relation.RelationType.PARAM_IN_CONSTRUCTOR)
			// &&
			// r2.getRelationType().equals(Relation.RelationType.PARAM_IN_CONSTRUCTOR))
			// return 1;
			// return
			// storage.getOutgoingRelationsForType(r2.getDestination()).size()
			// -
			// storage.getOutgoingRelationsForType(r1.getDestination()).size();

			return RelationStorage
					.getUniqueOutgoingTypesForRelationshipSet(storage.getOutgoingRelationsForType(r2.getDestination()))
					.size()
					- RelationStorage.getUniqueOutgoingTypesForRelationshipSet(
							storage.getOutgoingRelationsForType(r1.getDestination())).size();
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

	public interface Filter<T> {
		public boolean filter(T t);
	}
}
