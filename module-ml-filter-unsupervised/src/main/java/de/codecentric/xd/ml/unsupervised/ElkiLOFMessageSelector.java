package de.codecentric.xd.ml.unsupervised;


import java.util.Arrays;

import org.springframework.integration.core.MessageSelector;
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

    private LOF<NumberVector<Double>, DoubleDistance> lof;
    private UpdatableDatabase db;
    private OutlierResult result;

    public ElkiLOFMessageSelector() throws Exception {
        super();
        initLOF();
        initDatabase(0d);
    }

    private void initDatabase(double initialDataPoint) {
        db = new HashmapDatabase(new ArrayAdapterDatabaseConnection(new double[][]{{initialDataPoint, initialDataPoint}}), null);
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
        Double[] newData = (Double[]) message.getPayload();

        try {
            Relation<Object> relation = db.getRelation(TypeUtil.DOUBLE_VECTOR_FIELD);
            DoubleVector doubleVector = new DoubleVector(newData);
            DBIDs insertedId = db.insert(MultipleObjectsBundle.makeSimple(relation.getDataTypeInformation(), Arrays.asList(doubleVector)));

            if (relation.size() > 100 && result == null) {
                result = lof.run(db);
            }

            if (result != null) {
                double score = result.getScores().get(insertedId.iter()).doubleValue();
                System.out.println("Score of new data point (" + newData[0] + ","+newData[1]+"): " + score);
                return score > 2;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
