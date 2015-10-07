package org.aitek.collections.core;

import org.aitek.collections.gui.Main;
import org.aitek.collections.gui.StatsPanel;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

public class SetSample extends CollectionSample implements PropertyChangeListener {

	private ConcurrentSkipListSet<Integer> concurrent;

	private LinkedHashSet<Integer> linkedHashSet;
	private TreeSet<Integer> treeSet;
	private Task task;

	public SetSample(StatsPanel statsPanel, Main main) {

		super(statsPanel, main);
		COLLECTION_TYPES = 3;
		times = new long[COLLECTION_TYPES];
		concurrent = new ConcurrentSkipListSet<Integer>();

		linkedHashSet = new LinkedHashSet<Integer>();
		treeSet = new TreeSet<Integer>();
	}

	public HashSet<OperationType> getSupportedOperations() {

		HashSet<OperationType> set = new HashSet<OperationType>();

		set.add(OperationType.POPULATE);
		set.add(OperationType.INSERT);
		set.add(OperationType.REMOVE);
		set.add(OperationType.ITERATE);

		return set;
	}

	public void execute(OperationType operation) {

		this.currentOperation = operation;

		task = new Task();
		task.addPropertyChangeListener(this);
		task.execute();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		statusBar.updateProgressBar(task.getProgress());
	}

	private class Task extends SwingWorker<Void, Void> {

		private double mult;

		@Override
		public Void doInBackground() {

			mult = 100d / iterations;
			switch (currentOperation) {
				case POPULATE:
					times = fillSets();
					statsPanel.setTimes("Populating", times);
				break;
				case INSERT:
					times = insertIntoSets();
					statsPanel.setTimes("Inserting new elements", times);
				break;
				case REMOVE:
					times = removeFromSets();
					statsPanel.setTimes("Removing existing elements", times);
				break;
				case ITERATE:
					times = iterateOnSets();
					statsPanel.setTimes("Iterating elements", times);
				break;
			}

			return null;
		}

		@Override
		public void done() {

			main.setButtonsState();
			main.setReady();
		}

		private long[] fillSets() {

			long[] times = new long[COLLECTION_TYPES];
			main.setWorking("Filling set with " + getListFormattedSize() + " elements...");
			setProgress(0);

			for (int z = 0; z <= iterations; z++) {

				concurrent.clear();
				linkedHashSet.clear();
				treeSet.clear();

				int toBeInserted[] = new int[listSize];
				for (int j = 0; j < getListSize(); j++) {
					toBeInserted[j] = ((int) (Math.random() * listSize));
				}

				long startingTime = System.nanoTime();
				for (int j = 0; j < getListSize(); j++) {
					concurrent.add(toBeInserted[j]);
				}
				times[0] += System.nanoTime() - startingTime;

				startingTime = System.nanoTime();
				for (int j = 0; j < getListSize(); j++) {
					linkedHashSet.add(toBeInserted[j]);
				}
				times[1] += System.nanoTime() - startingTime;

				startingTime = System.nanoTime();
				for (int j = 0; j < getListSize(); j++) {
					treeSet.add(toBeInserted[j]);
				}
				times[2] += System.nanoTime() - startingTime;

				setProgress((int) (z * mult));
			}
			for (int z = 0; z < COLLECTION_TYPES; z++) {
				times[z] = times[z] / iterations / 1000;
			}
		//System.out.println(copyonwritearrayset.size());
			return times;
		}

		private long[] insertIntoSets() {

			long[] times = new long[COLLECTION_TYPES];
			main.setWorking("Inserting elements into set...");
			setProgress(0);
			for (int z = 0; z <= iterations; z++) {

				int toBeInserted = (int) (Math.random() * listSize);
				long startingTime = System.nanoTime();
				for (int j = 0; j < 50; j++)
					concurrent.add(toBeInserted);
				times[0] += System.nanoTime() - startingTime;

				startingTime = System.nanoTime();
				for (int j = 0; j < 50; j++)
					linkedHashSet.add(toBeInserted);
				times[1] += System.nanoTime() - startingTime;

				startingTime = System.nanoTime();
				for (int j = 0; j < 50; j++)
					treeSet.add(toBeInserted);
				times[2] += System.nanoTime() - startingTime;

				setProgress((int) (z * mult));
			}
			for (int z = 0; z < COLLECTION_TYPES; z++) {
				times[z] = times[z] / iterations / 1000;
			}

			return times;
		}

		private long[] removeFromSets() {

			long[] times = new long[COLLECTION_TYPES];
			main.setWorking("Removing elements from set...");
			setProgress(0);

			for (int z = 0; z <= iterations; z++) {

				int toBeRemoved = (int) (Math.random() * listSize);

				long startingTime = System.nanoTime();
				for (int j = 0; j < 50; j++)
					concurrent.remove(toBeRemoved);
				times[0] += System.nanoTime() - startingTime;

				startingTime = System.nanoTime();
				for (int j = 0; j < 50; j++)
					linkedHashSet.remove(toBeRemoved);
				times[1] += System.nanoTime() - startingTime;

				startingTime = System.nanoTime();
				for (int j = 0; j < 50; j++)
					treeSet.remove(toBeRemoved);
				times[2] += System.nanoTime() - startingTime;

				setProgress((int) (z * mult));
			}
			for (int z = 0; z < COLLECTION_TYPES; z++) {
				times[z] = times[z] / iterations / 1000;
			}

			return times;
		}

		private long[] iterateOnSets() {

			long[] times = new long[COLLECTION_TYPES];
			main.setWorking("Iterating on elements...");
			setProgress(0);

			for (int z = 0; z <= iterations; z++) {

				long startingTime = System.nanoTime();
				Iterator<Integer> iterator = concurrent.iterator();
				while (iterator.hasNext()) {
					iterator.next();
				}
				times[0] += System.nanoTime() - startingTime;

				startingTime = System.nanoTime();
				iterator = linkedHashSet.iterator();
				while (iterator.hasNext()) {
					iterator.next();
				}
				times[1] += System.nanoTime() - startingTime;

				startingTime = System.nanoTime();
				iterator = treeSet.iterator();
				while (iterator.hasNext()) {
					iterator.next();
				}
				times[2] += System.nanoTime() - startingTime;
				setProgress((int) (z * mult));
			}

			for (int z = 0; z < COLLECTION_TYPES; z++) {
				times[z] = times[z] / iterations / 1000;
			}

			setProgress(100);

			return times;
		}

	}

	@Override
	public boolean isPopulated() {

		return treeSet.size() > 0;
	}

}
