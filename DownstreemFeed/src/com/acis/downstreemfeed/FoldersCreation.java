package com.acis.downstreemfeed;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;

public class FoldersCreation {

	public enum Enum_Feeds {
		ASOClientPricing("asoclientpricing"), RXSolution("rxsolutions"), UP2S("alliance");
		private String folderName;
		private Enum_Feeds(String str_Value) {
			this.folderName = str_Value;
		}
		public String getFolderName() {
			return folderName;
		}
		@Override
		public String toString() {
			return folderName;
		}
	}
	protected String str_maindir = "C:\\acisat7\\ftp";

	/**
	 * Function Name : verifyFolderPath Description : This function is used to
	 * verify all folder presence in Specified Drive
	 **/
	public boolean verifyFolderPath() {
		boolean checkflag = false;
		try {
			java.io.File file = new java.io.File(str_maindir);
			// Verify for Main Directory
			if (!file.isDirectory()) {
				// Create acisat7 Folder in C:\ Drive
				new java.io.File(str_maindir).mkdirs();
			}
			// Verify for Folder in C:\ Driver
			String folderPath = "";
			for (Enum_Feeds folder : EnumSet.allOf(Enum_Feeds.class)) {
				folderPath = str_maindir + "\\" + folder.getFolderName();
				if (!new java.io.File(folderPath).isDirectory()) {
					// Create Folder if it doesn't Exist
					new java.io.File(folderPath).mkdirs();
				}
				String strNewFolder = "";
				// Create a new directory with today's date
				strNewFolder = folderPath + "\\" + getTodaysDate();
				if (!new java.io.File(strNewFolder).isDirectory()) {
					// Create Folder with today's date if it doesn't Exist
					new java.io.File(strNewFolder).mkdirs();
				}
			}
			checkflag = true;
		} catch (Exception exception) {
			System.out.println("Unable to create Folders");
		}
		return checkflag;
	}

	public String getOutputFolderPath(Enum_Feeds feedType) {
		// Verify all Folders are Present
		boolean verifyFolderPaths = verifyFolderPath();
		String filePath = str_maindir;
		if (verifyFolderPaths) {
			filePath = filePath + "\\" + feedType.getFolderName() + "\\" + getTodaysDate() + "\\";
		}
		return filePath;
	}

	/**
	 * Function Name : getTodaysDate Description : This function is used to get
	 * formatted today's Date
	 **/
	private String getTodaysDate() {
		SimpleDateFormat dateformatter = new SimpleDateFormat("dd-MM-yyyy");
		Date todaysDate = new Date();
		String str_date = dateformatter.format(todaysDate);
		return str_date.toString();
	}
	
	/*public static void main(String[] args){
		System.out.println(new FoldersCreation().getOutputFolderPath(Enum_Feeds.UP2S));
	}*/
}
