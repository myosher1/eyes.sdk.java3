package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.FileLogger;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class TestJanetsAccounts {
	static int i = 0;

	static final Customer[] customers = {
// 			new Customer() {{ accountName = "Farmers Insurance"; accountType = "Prospect"; website = "farmersinsurance.com";}},
			new Customer() {{ accountName = "Bayer ADP [External ID Data Push - Admin Use Only]"; accountType = "Customer"; website = "bayer.com";}},
// 			new Customer() {{ accountName = "T-Mobile"; accountType = "Prospect"; website = "http://www.t-mobile.com//";}},
 			new Customer() {{ accountName = "Abbott Laboratories"; accountType = "Prospect"; website = "http://www.abbott.com/";}},
 			new Customer() {{ accountName = "American International Group, Inc. (AIG)"; accountType = "Customer"; website = "http://www.aig.com";}},
			new Customer() {{ accountName = "Allergan"; accountType = "Prospect"; website = "http://www.allergan.com/";}},
			new Customer() {{ accountName = "AOL - US"; accountType = "Customer"; website = "http://www.aol.com";}},
			new Customer() {{ accountName = "DIRECTV Group, Inc."; accountType = "Prospect"; website = "http://www.directv.com/";}},
			new Customer() {{ accountName = "Gilead Sciences, Inc."; accountType = "Prospect"; website = "http://www.gilead.com";}},
			new Customer() {{ accountName = "HP"; accountType = "Prospect"; website = "http://www.hp.com";}},
			new Customer() {{ accountName = "Hewlett Packard Enterprise"; accountType = "Prospect"; website = "http://www.hpe.com";}},
			new Customer() {{ accountName = "Illumina"; accountType = "Prospect"; website = "http://www.illumina.com";}},
			new Customer() {{ accountName = "Stanley Healthcare"; accountType = "Prospect"; website = "http://www.stanleyhealthcare.com";}},
			new Customer() {{ accountName = "Twitter"; accountType = "Customer"; website = "https://twitter.com";}},
			new Customer() {{ accountName = "LDS Church"; accountType = "Prospect"; website = "https://www.lds.org/";}},
			new Customer() {{ accountName = "Blue Cross Blue Shield of Nebraska"; accountType = "Prospect"; website = "https://www.nebraskablue.com/";}},
			new Customer() {{ accountName = "Overstock.com"; accountType = "Prospect"; website = "overstock.com";}},
			new Customer() {{ accountName = "Charles Schwab"; accountType = "Customer"; website = "schwab.com";}},
			new Customer() {{ accountName = "TMZ"; accountType = "Prospect"; website = "tmz.com";}},
			new Customer() {{ accountName = "Walmart"; accountType = "Prospect"; website = "walmartlabs.com";}},
			new Customer() {{ accountName = "Abreon"; accountType = "Prospect"; website = "www.abreon.com";}},
//			new Customer() {{ accountName = "American Express"; accountType = "Prospect"; website = "www.aexp.com";}},
			new Customer() {{ accountName = "Amgen Biotechnology Inc"; accountType = "Prospect"; website = "www.amgen.com";}},
			new Customer() {{ accountName = "AOL Parent Account"; accountType = "Customer"; website = "www.aol.com";}},
			new Customer() {{ accountName = "Apple"; accountType = "Customer"; website = "www.apple.com";}},
			new Customer() {{ accountName = "AT&T"; accountType = "Prospect"; website = "www.att.com";}},
			new Customer() {{ accountName = "Bally Technologies , Inc."; accountType = "Prospect"; website = "www.ballytech.com";}},
			new Customer() {{ accountName = "Bayer AG"; accountType = "Customer"; website = "www.bayer.com";}},
			new Customer() {{ accountName = "Benefit Cosmetics"; accountType = "Prospect"; website = "www.benefitcosmetics.com/";}},
			new Customer() {{ accountName = "Best Western International"; accountType = "Prospect"; website = "www.bestwestern.com";}},
			new Customer() {{ accountName = "Bristol-Myers Squibb"; accountType = "Prospect"; website = "www.bms.com";}},
			new Customer() {{ accountName = "National Bank of Canada"; accountType = "Customer"; website = "www.bnc.ca";}},
			new Customer() {{ accountName = "Boyd Gaming Corporation"; accountType = "Prospect"; website = "www.boydgaming.com";}},
			new Customer() {{ accountName = "Choice Hotels"; accountType = "Customer"; website = "www.choicehotels.com";}},
			new Customer() {{ accountName = "CR England Inc."; accountType = "Prospect"; website = "www.crengland.com";}},
			new Customer() {{ accountName = "Discount Tire Co."; accountType = "Customer"; website = "www.discounttire.com";}},
			new Customer() {{ accountName = "DISH Network L.L.C"; accountType = "Prospect"; website = "www.dish.com";}},
			new Customer() {{ accountName = "The Walt Disney Company"; accountType = "Prospect"; website = "www.disney.com";}},
			new Customer() {{ accountName = "Disney Plus"; accountType = "Prospect"; website = "www.disneyplus.com";}},
			new Customer() {{ accountName = "doTERRA"; accountType = "Prospect"; website = "www.doterra.com";}},
			new Customer() {{ accountName = "GAP"; accountType = "Customer"; website = "www.gap.com";}},
			new Customer() {{ accountName = "Garmin International"; accountType = "Customer"; website = "www.garmin.com";}},
			new Customer() {{ accountName = "General Electric"; accountType = "Customer"; website = "www.ge.com";}},
			new Customer() {{ accountName = "GE Digital"; accountType = "Prospect"; website = "www.ge.com/digital";}},
			new Customer() {{ accountName = "GE Healthcare"; accountType = "Customer"; website = "www.gehealthcare.com";}},
			new Customer() {{ accountName = "GlaxoSmithKline"; accountType = "Customer"; website = "www.gsk.com";}},
			new Customer() {{ accountName = "Jack In a Box"; accountType = "Prospect"; website = "www.jackinthebox.com";}},
			new Customer() {{ accountName = "Johnson & Johnson"; accountType = "Customer"; website = "www.jnj.com";}},
			new Customer() {{ accountName = "Kiewit"; accountType = "Prospect"; website = "www.kiewit.com";}},
			new Customer() {{ accountName = "Kaiser Permanente"; accountType = "Customer"; website = "www.kp.org";}},
			new Customer() {{ accountName = "Eli Lilly and Co."; accountType = "Customer"; website = "www.lilly.com";}},
			new Customer() {{ accountName = "Merck"; accountType = "Prospect"; website = "www.merck.com";}},
			new Customer() {{ accountName = "Mutual of Omaha"; accountType = "Prospect"; website = "www.mutualofomaha.com/‎";}},
			new Customer() {{ accountName = "National Geographic"; accountType = "Customer"; website = "www.natgeo.com";}},
			new Customer() {{ accountName = "Nu Skin Enterprises , Inc."; accountType = "Prospect"; website = "www.nuskin.com";}},
			new Customer() {{ accountName = "PCM"; accountType = "Prospect"; website = "www.pcm.com";}},
			new Customer() {{ accountName = "Petco"; accountType = "Prospect"; website = "www.petco.com";}},
			new Customer() {{ accountName = "Qualcomm Incorporated"; accountType = "Prospect"; website = "www.qualcomm.com";}},
			new Customer() {{ accountName = "Republic Services"; accountType = "Prospect"; website = "www.republicservices.com";}},
			new Customer() {{ accountName = "ResMed Ltd."; accountType = "Prospect"; website = "www.resmed.com";}},
			new Customer() {{ accountName = "Charles Schwab Retirement Plan Services"; accountType = "Prospect"; website = "www.schwab.com";}},
			new Customer() {{ accountName = "Scientific Games Corporation"; accountType = "Prospect"; website = "www.scientificgames.com";}},
			new Customer() {{ accountName = "Scotiabank"; accountType = "Customer"; website = "www.scotiabank.com";}},
			new Customer() {{ accountName = "SkyWest , Inc."; accountType = "Prospect"; website = "www.skywest.com";}},
			new Customer() {{ accountName = "Stanley Black & Decker"; accountType = "Customer"; website = "www.stanleyblackanddecker.com";}},
			new Customer() {{ accountName = "TEVA Pharmaceuticals"; accountType = "Prospect"; website = "www.tevapharm.com";}},
			new Customer() {{ accountName = "Verizon"; accountType = "Customer"; website = "www.verizondigitalmedia.com";}},
			new Customer() {{ accountName = "Vista Outdoor Inc."; accountType = "Prospect"; website = "www.vistaoutdoor.com";}},
			new Customer() {{ accountName = "Yahoo"; accountType = "Customer"; website = "www.yahoo.com";}},
			new Customer() {{ accountName = "Zions Bancorporation"; accountType = "Prospect"; website = "www.zionsbancorporation.com";}},
			};
	private static VisualGridRunner runner =  new VisualGridRunner(50);

	private static void testVisualGrid(Customer customer) {

		if (customer.website.startsWith("http") == false)
			customer.website = "http://" + customer.website;

		System.out.println();
		System.out.println("---------------------------------");
		System.out.println(customer.accountName);
		System.out.println(customer.website);

//		Map<String, String> mobileEmulation = new HashMap<>();
//
//		mobileEmulation.put("deviceName", "Nexus 5");
//
//
//		ChromeOptions chromeOptions = new ChromeOptions();
//		chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
		WebDriver driver = new ChromeDriver();

		driver.get(customer.website);

		// Create a runner with concurrency of 50

		Eyes eyes = new Eyes(runner);
		
		eyes.setLogHandler(new FileLogger("log_3.149.4-beta."+i+".log",true,true));
		i++;
		
		// Create SeleniumConfiguration.
		Configuration sconf = new Configuration();

		// Set test name
		sconf.setTestName(customer.accountName + " (" + customer.accountType + ") 3.149.4");

		// Set app name
		sconf.setAppName("Visual Grid CS");

		for (DeviceName deviceName : DeviceName.values()) {
			//ChromeEmulationInfo device = new ChromeEmulationInfo(deviceName, ScreenOrientation.PORTRAIT);
			sconf.addDeviceEmulation(deviceName);
			break;
		}

		// Set the configuration object to eyes
		eyes.setConfiguration(sconf);

		try {

			eyes.setForceFullPageScreenshot(true);
			// eyes.setMatchLevel(MatchLevel.LAYOUT);

			eyes.setBatch(new BatchInfo(customer.accountName + " (" + customer.accountType + ") 3.149.4"));

			System.out.println("open Eyes");
			// Call Open on eyes to initialize a test session
			eyes.open(driver);

			//Eyes.setViewportSize(driver, new RectangleSize(1200, 600));

			System.out.println("eyes.check");
			eyes.check(Target.window().fully().withName(customer.website));

//			eyes.close(false);
			System.out.println("eyes.close");
		} catch (Exception e) {
			e.printStackTrace();
		}
		driver.quit();
	}

	public static void main(String[] args) {

		try {
			for (Customer customer : customers)
				testVisualGrid(customer);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			runner.getAllTestResults();
		}
	}

	private static class Customer {
		String website;
		String accountName;
		String accountType;
	}
}
