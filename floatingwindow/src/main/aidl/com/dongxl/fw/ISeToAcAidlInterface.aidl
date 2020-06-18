// ISeToAcAidlInterface.aidl
package com.dongxl.fw;

// Declare any non-default types here with import statements

interface ISeToAcAidlInterface {

    void onBindSuc();
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
