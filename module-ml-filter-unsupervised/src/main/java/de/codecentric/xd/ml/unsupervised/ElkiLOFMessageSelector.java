package de.codecentric.xd.ml.unsupervised;


import java.util.Arrays;

import org.springframework.integration.core.MessageSelector;
import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;

import de.lmu.ifi.dbs.elki.algorithm.outlier.LOF;
import de.lmu.ifi.dbs.elki.algorithm.outlier.OnlineLOF;
import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.HashmapDatabase;
import de.lmu.ifi.dbs.elki.database.UpdatableDatabase;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.datasource.ArrayAdapterDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.bundle.MultipleObjectsBundle;
import de.lmu.ifi.dbs.elki.distance.distancevalue.DoubleDistance;
import de.lmu.ifi.dbs.elki.result.outlier.OutlierResult;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;

public class ElkiLOFMessageSelector implements MessageSelector {

	public static final String ELKI_DOUBLE_VECTOR = "ELKI_DOUBLE_VECTOR";

	private LOF<NumberVector<Double>, DoubleDistance> lof;
    private UpdatableDatabase db;
    private OutlierResult result;
    
    private Transformer defaultElkiLOFMessageTransformer = new DefaultElkiLOFMessageTransformer();

    public ElkiLOFMessageSelector(int dimensions) throws Exception {
        super();
        initLOF();
        initDatabase(0d, dimensions);
    }

    private void initDatabase(double initialDataPoint, int dimensions) {
    	double[][] initialVector = new double[1][dimensions];
    	for (int i = 0; i<dimensions;i++){
    		initialVector[0][i] = initialDataPoint;
    	}
        db = new HashmapDatabase(new ArrayAdapterDatabaseConnection(initialVector), null);
        db.initialize();
    }

    private void initLOF() {
        ListParameterization params = new ListParameterization();
        params.addParameter(LOF.K_ID, 20);

        lof = ClassGenericsUtil.parameterizeOrAbort(OnlineLOF.class, params);
    }

    /* (non-Javadoc)
     * @see org.springframework.integration.core.MessageSelector#accept(org.springframework.messaging.Message)
     */
    @Override
    public boolean accept(Message<?> message) {
    	if (!message.getHeaders().containsKey(ELKI_DOUBLE_VECTOR)){
    		message = defaultElkiLOFMessageTransformer.transform(message);
    	}
        Double[] newData = message.getHeaders().get(ELKI_DOUBLE_VECTOR, Double[].class);

        try {
            Relation<Object> relation = db.getRelation(TypeUtil.DOUBLE_VECTOR_FIELD);
            DoubleVector doubleVector = new DoubleVector(newData);
            DBIDs insertedId = db.insert(MultipleObjectsBundle.makeSimple(relation.getDataTypeInformation(), Arrays.asList(doubleVector)));

            if (relation.size() > 100 && result == null) {
                result = lof.run(db);
            }

            if (result != null) {
                double score = result.getScores().get(insertedId.iter()).doubleValue();
                //System.out.println("Score of new data point (" + newData[0] + ","+newData[1]+"): " + score);
                return score > 2;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
