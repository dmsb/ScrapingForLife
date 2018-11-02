package scrapingforlife;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.openqa.selenium.remote.RemoteWebDriver;

import scrapingforlife.configuration.DriverFactory;

public class TestThread {

	public static void main(String args[]) {
		final DriverFactory driverFactory = new DriverFactory();
		final RemoteWebDriver webDriver = driverFactory.getDriver();
		final SearchModel searchModel = buildSearchModel();
		final List<Path> peakListPaths = buildPeakListPaths(searchModel.getPeakListFolderPath());
		
		final ExecutorService service = Executors.newFixedThreadPool(1);
		
		IntStream.range(0, peakListPaths.size())
			.forEach(i -> service.submit(new TestScrap(webDriver, searchModel, peakListPaths.get(i))));
		service.shutdown();
	}

	private static List<Path> buildPeakListPaths(final String peakListFolderPath) {
		final List<Path> peakListPaths = new ArrayList<>();
		try (Stream<Path> paths = Files.walk(Paths.get(peakListFolderPath))) {
			peakListPaths.addAll(paths.filter(Files::isRegularFile).collect(Collectors.toList()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return peakListPaths;
	}

	private static SearchModel buildSearchModel() {
		final SearchModel searchModel = new SearchModel();
		searchModel.setName("Diogo");
		searchModel.setEmail("diogomsb1@gmail.com");
		searchModel.setDatabase("SwissProt");
		searchModel.setPeakListFolderPath("F:\\Downloads\\peaklists\\Rodrigo\\");
		searchModel.setPeakListResultPath("F:\\Pick List Results\\");
		return searchModel;
	}
}