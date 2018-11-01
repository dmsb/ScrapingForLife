package scrapingforlife;

public class SearchModel {

	private String name;
	private String email;
	private String database;
	private String peptideTolValue;
	private String peptideTolUnity;
	private String peakListFolderPath;
	private String peakListResultPath;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public String getPeptideTolValue() {
		return peptideTolValue;
	}
	public void setPeptideTolValue(String peptideTolValue) {
		this.peptideTolValue = peptideTolValue;
	}
	public String getPeptideTolUnity() {
		return peptideTolUnity;
	}
	public void setPeptideTolUnity(String peptideTolUnity) {
		this.peptideTolUnity = peptideTolUnity;
	}
	public String getPeakListFolderPath() {
		return this.peakListFolderPath;
	}
	public void setPeakListFolderPath(String peakListFolderPath) {
		this.peakListFolderPath = peakListFolderPath;
	}
	public String getPeakListResultPath() {
		return peakListResultPath;
	}
	public void setPeakListResultPath(String peakListResultPath) {
		this.peakListResultPath = peakListResultPath;
	}
}
