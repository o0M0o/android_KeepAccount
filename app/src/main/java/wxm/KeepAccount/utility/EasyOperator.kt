package wxm.KeepAccount.utility

/**
 * @author      WangXM
 * @version     create：2018/5/23
 */
object EasyOperator {
    fun<T> doThree(term:()->Boolean, trueTerm:()->T, falseTerm:()->T):T {
        return if(term())  trueTerm()  else  falseTerm()
    }

    fun<T, R> doObj(obj:T?, trueTerm:(t:T)->R, falseTerm:()->R):R {
        return if(null == obj)  falseTerm()   else trueTerm(obj)
    }
}
