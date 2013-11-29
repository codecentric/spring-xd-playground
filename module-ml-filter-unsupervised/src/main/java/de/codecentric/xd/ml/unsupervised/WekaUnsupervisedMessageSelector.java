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

	public WekaUnsupervisedMessageSelector() throws Exception {
		super();
		this.clusterer = new Cobweb();
		FastVector attInfo = new FastVector();
		Attribute attribute = new Attribute("messageContentString",(FastVector)null);
		attInfo.addElement(attribute);
		Instances structure = new Instances("name",attInfo ,1);
		this.clusterer.buildClusterer(structure);
	}

	/* (non-Javadoc)
	 * @see org.springframework.integration.core.MessageSelector#accept(org.springframework.messaging.Message)
	 */
	@Override
	public boolean accept(Message<?> message) {
		Attribute attribute = new Attribute("messageContentString",(FastVector)null);
		Instance instance = new Instance(1);
		instance.setValue(attribute, message.getPayload().toString());
		try {
			clusterer.updateClusterer(instance);
			clusterer.updateFinished();
			double[] distributionForInstance = clusterer.distributionForInstance(instance);
			System.out.println(distributionForInstance);
//			double classInstance = clusterer.classifyInstance(instance);
//			if (Instance.isMissingValue(classInstance)){
//				return true;
//			}
//			// Filter funktioniert nur auf einer großen Menge von Instances, nicht auf einer Instanz.
//			// Man könnte sich natürlich die Instanzen begrenzt halten, oder sich einen InterquartileRange-Filter
//			// bauen, der seine Thresholds kontinuierlich updatet, aber das ist dann im Prinzip das Klassifizieren
//			// auf einer Klasse. Eigentlich wollen wir ja mehrere.
//			InterquartileRange interquartileRange = new InterquartileRange();
//			Instances filteredInstances = Filter.useFilter(instances, interquartileRange);
		} catch (Exception e) {
			// TODO write error to error channel
		}
		return false;
	}

}
