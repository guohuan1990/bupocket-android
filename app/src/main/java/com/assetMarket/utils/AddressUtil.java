package com.assetMarket.utils;

public class AddressUtil {
    public static String anonymous(String address){
        String partOne = address.substring(0,5);
        String partTwo = address.substring(address.length()-5,address.length());
        return partOne + "***" + partTwo;
    }
}
