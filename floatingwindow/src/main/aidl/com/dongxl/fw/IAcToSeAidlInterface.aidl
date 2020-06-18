// IAcToSeAidlInterface.aidl
package com.dongxl.fw;
import com.dongxl.fw.ISeToAcAidlInterface;
// Declare any non-default types here with import statements

interface IAcToSeAidlInterface {

    void onBindSeToAc(ISeToAcAidlInterface aidlInterface);
    void onUnbindSeToAc();
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

}
