Spring XD Playground
====================

The module-ml-filter-unsupervised contains a filter for outlier detection. This is an example, how to integrate machine
learning techniques into spring xd. The filter can be used for example, to detect abnormal requests in logfiles.

The filter uses the LOF algorithm of the ELKI library.

Quickstart
==========
To deploy the filter, you have to
1. build the project (with gradle)
```
cd spring-xd-playground/module-ml-filter-unsupervised
gradle build
```
2. place the resulting jar in the lib-folder of Spring XD
```
SPRING_XD_HOME=/opt/spring-xd-1.0.0.M4/xd #or wherever your spring-xd installation resides
cp build/libs/module-ml-filter-unsupervised-1.0.0-SNAPSHOT.jar $SPRING_XD_HOME/lib/
```
3. place the XML definitions from `/src/main/resources` in the folder `/modules/processor´ of your Spring XD installation
```
cp src/main/resources/*.xml $SPRING_XD_HOME/modules/processor
```

Afterwards, you can use two new processors:
* `loglinetransformer` transforms an simple logline (e.g. in the common logging format) into json representation
* `outlierfilter` builds a LOF classifier and drops all messages that are not classified as outliers.

Example
=======
The following stream will tail the file `/tmp/xd/input/logoutliers` and write any abnormal loglines into the file
`/tmp/xd/output/logoutliers.out`. The following commands can be executed inside the Spring-XD Shell (or using the
REST-Service of Spring XD):
```
xd:> stream create logoutlier --definition "tail | loglinetransformer | filter --expression=payload!='null' | outlierfilter | file
```

To test it, you can e.g. pipe the contents of the [provided example logfile](https://raw.github.com/codecentric/spring-xd-playground/master/module-ml-filter-unsupervised/src/test/resources/small-example-access.log) into this stream:
```
cat spring-xd-playground/module-ml-filter-unsupervised/src/test/resources/small-example-access.log >> /tmp/xd/input/logoutlier
```