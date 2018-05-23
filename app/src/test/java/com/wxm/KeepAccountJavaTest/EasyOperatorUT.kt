package com.wxm.KeepAccountJavaTest

import junit.framework.Assert
import org.junit.Test
import wxm.KeepAccount.utility.EasyOperator


/**
 * @author      WangXM
 * @version     create：2018/5/23
 */
class EasyOperatorUT   {
    @Test
    fun testDoObjOper() {
        Assert.assertEquals(1, EasyOperator.doObj(1, { t -> t}, {0}))
        Assert.assertEquals(0, EasyOperator.doObj(null, { _ -> 1}, {0}))
    }

    @Test
    fun testDoThreeOper()   {
        Assert.assertTrue(EasyOperator.doThree({true}, { true }, { false}))
        Assert.assertTrue(EasyOperator.doThree({false}, { false}, { true}))
    }
}