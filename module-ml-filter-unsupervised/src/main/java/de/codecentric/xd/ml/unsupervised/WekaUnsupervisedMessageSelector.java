package de.codecentric.xd.ml.unsupervised;

import org.springframework.integration.core.MessageSelector;
import org.springframework.messaging.Message;

import weka.clusterers.Cobweb;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * This message selector uses unsupervised classifying of incoming messages. Messages similar to messages that come
 * regularly are discarded, only suspicious, unnormal messages are let through for further investigation.
 *
 * @author tobias.flohre
 */
public class WekaUnsupervisedMessageSelector implements MessageSelector {

	private Cobweb clusterer;
	private final Attribute numericAttribute;
	private Instances trainingData;

	public WekaUnsupervisedMessageSelector() throws Exception {
		super();
		clusterer = new Cobweb();
		numericAttribute = new Attribute("messageContent", 0);
		FastVector attInfo = new FastVector();
		attInfo.addElement(numericAttribute);

		trainingData = new Instances("name", attInfo, 0);
		this.clusterer.buildClusterer(trainingData);
	}

	/* (non-Javadoc)
	 * @see org.springframework.integration.core.MessageSelector#accept(org.springframework.messaging.Message)
	 */
	@Override
	public boolean accept(Message<?> message) {
		Instance instance = new Instance(1);
		instance.setDataset(trainingData);
		instance.setValue(numericAttribute, Double.valueOf(message.getPayload().toString()));
		trainingData.add(instance);
		try {
			clusterer.updateClusterer(instance);
			clusterer.updateFinished();
			System.out.println("Number of Clusters: " + clusterer.numberOfClusters());
			double[] distributionForInstance = clusterer.distributionForInstance(instance);
			System.out.println("Distribution of " + instance.toString());
			for (int i = 0; i < distributionForInstance.length; ++i) {
				System.out.println(String.valueOf(i) + ": " + distributionForInstance[i]);
			}
			//double classInstance = clusterer.classifyInstance(instance);
			//if (Instance.isMissingValue(classInstance)){
			//	return true;
			//}
//			// Filter funktioniert nur auf einer gro�en Menge von Instances, nicht auf einer Instanz.
//			// Man k�nnte sich nat�rlich die Instanzen begrenzt halten, oder sich einen InterquartileRange-Filter
//			// bauen, der seine Thresholds kontinuierlich updatet, aber das ist dann im Prinzip das Klassifizieren
//			// auf einer Klasse. Eigentlich wollen wir ja mehrere.
//			InterquartileRange interquartileRange = new InterquartileRange();
//			Instances filteredInstances = Filter.useFilter(instances, interquartileRange);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return false;
	}

}
