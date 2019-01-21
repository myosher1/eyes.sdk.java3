package com.applitools.utils;

import java.util.Map;

public class EfficientStringReplace {
    public static String efficientStringReplace(char[] refIdToken, String input, Map<String, String> replacements) {
        int resultBufferSize = input.length();
        for (String rep : replacements.values()) {
            resultBufferSize += rep.length();
        }
        StringBuilder result = new StringBuilder(resultBufferSize);
        StringBuilder refId = new StringBuilder();
        int inLen = input.length();
        int refTokenLen = refIdToken.length;
        START:
        for (int i = 0; i < inLen; ++i) {

            boolean refTokenEncountered = input.charAt(i) == refIdToken[0];
            if (refTokenEncountered) {
                for (int j = 1; j < refTokenLen; ++j) {
                    if (input.charAt(i + j) != refIdToken[j]) {
                        refTokenEncountered = false;
                        break;
                    }
                }
                if (refTokenEncountered) {
                    refId.setLength(0);
                    for (i += refTokenLen; i < inLen; ++i) {
                        boolean refEndTokenEncountered = input.charAt(i) == refIdToken[0];
                        if (refEndTokenEncountered) {
                            for (int j = 1; j < refTokenLen; ++j) {
                                if (input.charAt(i + j) != refIdToken[j]) {
                                    refEndTokenEncountered = false;
                                    break;
                                }
                            }
                            if (refEndTokenEncountered) {
                                String rep = replacements.get(refId.toString());
                                if (rep != null) {
                                    result.append(rep);
                                    i += refTokenLen-1;
                                    continue START;
                                }
                            }
                        }
                        if (!refEndTokenEncountered) {
                            refId.append(input.charAt(i));
                        }
                    }

                }
            }

            if (!refTokenEncountered) {
                result.append(input.charAt(i));
            }
        }
        return result.toString();
    }
}
