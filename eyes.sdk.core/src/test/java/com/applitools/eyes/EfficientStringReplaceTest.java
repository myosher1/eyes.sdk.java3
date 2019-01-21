package com.applitools.eyes;

import com.applitools.utils.EfficientStringReplace;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class EfficientStringReplaceTest {

    @DataProvider(name = "dp")
    public static Object[][] dp() {
        return new Object[][]{
                {"@<", "abcdef@<0@<ghijklmnop@<1@<qrstuv@<2@<wx@<1@<@<0@<yz",
                        new HashMap<String, String>() {
                            {
                                put("0", "ABCDEFG");
                                put("1", "HIJKLMNOP");
                                put("2", "QRSTUV");
                                put("3", "WXYZ");
                            }
                        },

                        "abcdefABCDEFGghijklmnopHIJKLMNOPqrstuvQRSTUVwxHIJKLMNOPABCDEFGyz"}
        };
    }

    @Test(dataProvider = "dp")
    public void efficientStringReplace(String refIdToken, String input, Map<String, String> replacements, String expectedResult) {
        String result = EfficientStringReplace.efficientStringReplace( refIdToken.toCharArray(), input, replacements);
        Assert.assertEquals(result, expectedResult);
    }


}
