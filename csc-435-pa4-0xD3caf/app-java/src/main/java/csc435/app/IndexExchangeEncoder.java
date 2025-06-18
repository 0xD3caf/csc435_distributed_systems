package csc435.app;

import java.util.HashMap;

public class IndexExchangeEncoder {
    // TLV style data exchange encoding
    // VALID TYPE FLAGS -- REGI_REQ, REGI_RPL, INDX_REQ, INDX_RPL, SRCH_REQ, SRCH_RPL, QUIT_REQ
    // Each flag is 8 characters
    // Each ID is 2 characters
    // all remaining message is data, if necessary


    public String[] decodeMsg (String data) {
        // parses info from start of string, type and length
        // uses length to read
        String[] tlvValues = new String[3];
        String flag = data.substring(0,8);
        int iD = Integer.parseInt(data.substring(8,10));
        String value = data.substring(10);

        tlvValues[0] = flag;
        tlvValues[1] = String.valueOf(iD);
        tlvValues[2] = value;
        return tlvValues;
    }

    public String encodeMsg (String flag, int iD, String msg){
        String iDStr = String.valueOf(iD);
        if (iD < 10) iDStr = "0" + iDStr;
        return String.format("%s%s%s", flag, iDStr, msg);}

    public String encodeMsg (String flag, int iD, HashMap<String, Long> words, String path) {
        String hashMapString = path + ",";
        for (String word : words.keySet()) {
            hashMapString = hashMapString.concat(word + ":" + (String.valueOf(words.get(word))) + ",");
        }
        return encodeMsg(flag, iD, hashMapString);
    }
}
