package wxm.KeepAccount.utility

/**
 * @author      WangXM
 * @version     create：2018/5/24
 */

/**
 * if term is true run [trueTerm] else run [falseTerm]
 */
fun <R> Boolean.doJudge(trueTerm:()->R, falseTerm:()->R): R {
    return if(this)  trueTerm()   else falseTerm()
}

/**
 * if term is true run [trueObj] else run [falseObj]
 */
fun <R> Boolean.doJudge(trueObj:R, falseObj:R): R {
    return if(this)  trueObj   else falseObj
}