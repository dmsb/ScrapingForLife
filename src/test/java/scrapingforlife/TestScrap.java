package scrapingforlife;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import scrapingforlife.configuration.DriverFactory;

public class TestScrap implements Runnable {

	private SearchModel searchModel;
	private Path peakListPath;
	private DriverFactory driverFactory;
	private String[][] peptideCombinations = {
			{"Da", "1.2"},
			{"Da", "1.0"},
			{"Da", "0.8"},
			{"Da", "0.6"},
			{"Da", "0.4"},
			{"Da", "0.2"},
			{"ppm", "100"},
			{"ppm", "200"}
	};
	

	public TestScrap(SearchModel searchModel, Path peakListPath) {
		this.driverFactory =  new DriverFactory();
		this.searchModel = searchModel;
		this.peakListPath = peakListPath;
	}

	@Override
	public void run() {
        
        final RemoteWebDriver webDriver = driverFactory.getDriver();
		for(int i = 0; i < this.peptideCombinations.length; i++) {
			
			webDriver.get("http://www.matrixscience.com/cgi/search_form.pl?FORMVER=2&SEARCH=PMF");
			completeQueryParameters(webDriver, i);
			
			// Waiting 10 seconds to get result page correctly instead processing page
			WebDriverWait wait = new WebDriverWait(webDriver, 10);
			wait.until(ExpectedConditions.urlContains("http://www.matrixscience.com/cgi/master_results.pl"));
			
			// Getting fields to be save	    
			writeResults(webDriver, i);
			webDriver.manage().deleteAllCookies();
		}
		
		webDriver.quit();
	}

	private void writeResults(final RemoteWebDriver driver, final Integer i) {
		final List<WebElement> resultHeader = driver.findElementsByXPath("/html/body/font[1]/pre//b");
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.searchModel.getPeakListResultPath() + 
				this.peakListPath.getFileName().toString() + 
				"_unit_" + this.peptideCombinations[i][0] +
				"_value_" + this.peptideCombinations[i][1] +
				"_result.txt"))){
			for(final WebElement header : resultHeader) {
				writer.write(header.getText());
				writer.newLine();
			}
			writer.newLine();
			buildProteinDetails(driver, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void completeQueryParameters(final RemoteWebDriver driver, int i) {
		final Select dataBaseSelect = new Select(driver.findElement(By.name("DB")));
		dataBaseSelect.selectByValue(searchModel.getDatabase());
		dataBaseSelect.deselectByValue("contaminants");
		
		
		if(driver.findElement(By.id("USERNAME")).getText().isEmpty()) {
			driver.findElement(By.id("USERNAME")).sendKeys(searchModel.getName());
		}
		
		if(driver.findElement(By.id("USEREMAIL")).getText().isEmpty()) {
			driver.findElement(By.id("USEREMAIL")).sendKeys(searchModel.getEmail());
		}
		
		final Select unitySelect = new Select(driver.findElement(By.name("TOLU")));
		unitySelect.selectByValue(this.peptideCombinations[i][0]);
		
		driver.findElement(By.name("TOL")).sendKeys(Keys.CONTROL + "a");
		driver.findElement(By.name("TOL")).sendKeys(Keys.DELETE);
		driver.findElement(By.name("TOL")).sendKeys(this.peptideCombinations[i][1]);
		
		driver.findElement(By.id("InputRadio-DATAFILE")).click();
		driver.findElement(By.id("InputSource-DATAFILE")).sendKeys(peakListPath.toAbsolutePath().toString());
		
		driver.findElement(By.id("Start_Search_Button")).submit();
	}

	private void buildProteinDetails(final RemoteWebDriver driver, final BufferedWriter writer) {
		final List<WebElement> resultProteinIds = driver.findElementsByXPath("/html/body/form[2]/table/tbody/tr[1]/td[2]/tt/a");
		final List<String> proteinDetailsUrls = resultProteinIds.stream()
				.map(element -> {return element.getAttribute("href");}).collect(Collectors.toList());
		for(final String proteinDetailUrl : proteinDetailsUrls) {
			driver.navigate().to(proteinDetailUrl);
			final List<WebElement> proteinDetails = driver.findElementsByXPath("/html/body/form/table[1]/tbody/tr");
			try {
				for(WebElement detail : proteinDetails) {
					final String detailTitle = detail.findElement(By.tagName("th")).getText();
					final String detailValue = detail.findElement(By.tagName("td")).getText();
					writer.write(detailTitle + detailValue);
					writer.newLine();
				}
				writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
