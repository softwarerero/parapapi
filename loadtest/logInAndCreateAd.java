package com.example.tests;

import com.thoughtworks.selenium.*;
import java.util.regex.Pattern;

public class LogInAndCreateAd extends SeleneseTestCase {
	public void setUp() throws Exception {
		setUp("http://parapapi.com:9002/", "*chrome");
	}
	public void testUntitled2() throws Exception {
		for(int i=0; i<2; i++) {
				
			selenium.open("/");
			selenium.click("//input[@value='Registrieren']");
			selenium.waitForPageToLoad("30000");
			selenium.type("object_nickname", "bob" + i);
			selenium.type("object_email", "bob" + i + "@funcom.de");
			selenium.type("object_password", "bob");
			selenium.type("object_password", "bob");
			selenium.type("object_passwordConfirmation", "bob");
			selenium.type("object_firstNames", "Bob" + i);
			selenium.type("object_lastNames", "Tester");
			selenium.type("object_phone", "021-12345");
			selenium.click("object_acceptConditions");
			selenium.waitForPageToLoad("30000");
			selenium.type("code", "hPpvo");
			selenium.click("//input[@value='Speichern']");
			selenium.waitForPageToLoad("30000");
			selenium.type("username", "bob0@gmail.com");
			selenium.type("password", "secret");
			selenium.click("signin");
			selenium.waitForPageToLoad("30000");
		}
	}
}
