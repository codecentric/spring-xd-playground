package de.codecentric.xd.ml.unsupervised;


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
import de.lmu.ifi.dbs.elki.utilities.exceptions.UnableToComplyException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;
import org.apache.log4j.Logger;
import org.springframework.integration.core.MessageSelector;
import org.springframework.messaging.Message;

import java.util.Arrays;

public class ElkiLOFMessageSelector implements MessageSelector {

    public static final String ELKI_DOUBLE_VECTOR = "ELKI_DOUBLE_VECTOR";
    public static final int DEFAULT_K_ID = 20;
    public static final double DEFAULT_THRESHOLD = 2d;

    private static final Logger LOGGER = Logger.getLogger(ElkiLOFMessageSelector.class);
    public static final int MINIMUM_NUMBER_OF_DATAPOINTS = 100;

    private LOF<NumberVector<Double>, DoubleDistance> lof;
    private UpdatableDatabase db;
    private OutlierResult result;

    private int dimensions;
    private int k_id;
    private double threshold;

    public ElkiLOFMessageSelector(int dimensions) {
        this(dimensions, DEFAULT_K_ID, DEFAULT_THRESHOLD);
    }

    public ElkiLOFMessageSelector(int dimensions, int k_id, double threshold) {
        super();
        this.dimensions = dimensions;
        this.k_id = k_id;
        this.threshold = threshold;
        LOGGER.debug("Initialising LOF MessageSelector with dimensions=" + dimensions + ", k=" + k_id + " and threshold=" + threshold);
        initLOF();
        initDatabase(0d);
    }

    private void initDatabase(double initialDataPoint) {
        double[][] initialVector = new double[1][dimensions];
        for (int i = 0; i < dimensions; i++) {
            initialVector[0][i] = initialDataPoint;
        }
        db = new HashmapDatabase(new ArrayAdapterDatabaseConnection(initialVector), null);
        db.initialize();
    }

    private void initLOF() {
        ListParameterization params = new ListParameterization();
        params.addParameter(LOF.K_ID, k_id);

        lof = ClassGenericsUtil.parameterizeOrAbort(OnlineLOF.class, params);
    }

    /* (non-Javadoc)
     * @see org.springframework.integration.core.MessageSelector#accept(org.springframework.messaging.Message)
     */
    @Override
    public synchronized boolean accept(Message<?> message) {
        if (!message.getHeaders().containsKey(ELKI_DOUBLE_VECTOR)) {
            LOGGER.warn("No header " + ELKI_DOUBLE_VECTOR + " present");
            return false;
        }

        Double[] newData = message.getHeaders().get(ELKI_DOUBLE_VECTOR, Double[].class);
        if (newData.length != dimensions) {
            LOGGER.warn("Invalid length of " + ELKI_DOUBLE_VECTOR + ", expected " + dimensions + " but got " + newData.length);
            return false;
        }

        try {
            Relation<Object> relation = db.getRelation(TypeUtil.DOUBLE_VECTOR_FIELD);
            DoubleVector doubleVector = new DoubleVector(newData);
            DBIDs insertedId = db.insert(MultipleObjectsBundle.makeSimple(relation.getDataTypeInformation(), Arrays.asList(doubleVector)));

            if (relation.size() > MINIMUM_NUMBER_OF_DATAPOINTS && result == null) {
                result = lof.run(db);
            }

            if (result == null) {
                LOGGER.info("Minimum number of datapoints " + MINIMUM_NUMBER_OF_DATAPOINTS + " not yet reached");
                return false;
            }

            double score = result.getScores().get(insertedId.iter()).doubleValue();
            LOGGER.debug("Computed LOF score: " + score);
            return score > threshold;
        } catch (UnableToComplyException e) {
            LOGGER.error("Unable to insert new data point into LOF classifier: " + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected exception: " + e.getMessage());
            throw e;
        }
    }
}
