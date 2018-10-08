package com.acis.downstreemfeed;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

	public enum Enum_Config {
		UNIXHostServer("hostServer"), UserName("username"), PassWord("password"), DB2HostURL("dbHostURL");
		private String value;

		private Enum_Config(String str_Value) {
			this.value = str_Value;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	/**
	 * Function Name : getConfigPropValue Description : This function is used to
	 * value from config.properties File
	 **/
	public String getConfigPropValue(Enum_Config key) {
		Properties prop = new Properties();
		InputStream input = null;
		String value = "";
		try {

			input = new FileInputStream("config.properties");
			// load a properties file
			prop.load(input);
			value = prop.getProperty(key.getValue());

		} catch (IOException ex) {
			System.out.println("Unable to Fetech Values from config.properties File");
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return value;
	}
}
