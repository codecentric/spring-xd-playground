package de.codecentric.xd;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.codecentric.xd.model.DistributionModel;
import de.codecentric.xd.model.SimpleManipulationModel;
import de.codecentric.xd.url.SimpleUrlCreator;
import de.codecentric.xd.url.UrlCreator;

public class TestLogFileCreator {

	public static void main(String[] args) throws InterruptedException, IOException {
		ExecutorService executorService = Executors.newFixedThreadPool(5);

		UrlCreator urlCreator = new SimpleUrlCreator("http://movieservice.codecentric.de/movies/", 200, 100);
		DistributionModel durationDistributionModel = new DistributionModel(new SimpleManipulationModel(1), 80, 20);
		DistributionModel timeBetweenLogEntriesDistributionModel = new DistributionModel(new SimpleManipulationModel(1), 1000, 100);
		LogFileCreator logFileCreator = new LogFileCreator("host1", "movieservice", urlCreator, durationDistributionModel, timeBetweenLogEntriesDistributionModel, "/tmp/xd/input/movies.log");
		executorService.submit(new LogFileCreatorRunnable(logFileCreator));

		urlCreator = new SimpleUrlCreator("http://actorservice.codecentric.de/actors/", 200, 100);
		durationDistributionModel = new DistributionModel(new SimpleManipulationModel(1), 40, 10);
		timeBetweenLogEntriesDistributionModel = new DistributionModel(new SimpleManipulationModel(1), 4000, 1000);
		logFileCreator = new LogFileCreator("host1", "actorservice", urlCreator, durationDistributionModel, timeBetweenLogEntriesDistributionModel, "/tmp/xd/input/actors.log");
		executorService.submit(new LogFileCreatorRunnable(logFileCreator));
		
//		Thread.sleep(30000);
//		
//		executorService.shutdownNow();
	}
	
	private static class LogFileCreatorRunnable implements Runnable{
		
		private LogFileCreator logFileCreator;

		public LogFileCreatorRunnable(LogFileCreator logFileCreator) {
			super();
			this.logFileCreator = logFileCreator;
		}
		
		public void run() {
			logFileCreator.writeLogFile();
		}

	}

}
