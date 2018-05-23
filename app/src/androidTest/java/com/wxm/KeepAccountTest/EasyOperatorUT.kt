package com.wxm.KeepAccountTest

import android.test.AndroidTestCase
import junit.framework.Assert
import wxm.KeepAccount.utility.EasyOperator

/**
 * @author      WangXM
 * @version     create：2018/5/23
 */
class EasyOperatorUT : AndroidTestCase() {
    fun testDoObjOper() {
        Assert.assertEquals(1, EasyOperator.doObj(1, { t -> t}, {0}))
        Assert.assertEquals(0, EasyOperator.doObj(null, { _ -> 1}, {0}))
    }

    fun testDoThreeOper()   {
        Assert.assertTrue(EasyOperator.doThree({true}, { true }, { false}))
        Assert.assertTrue(EasyOperator.doThree({false}, { false}, { true}))
    }
}