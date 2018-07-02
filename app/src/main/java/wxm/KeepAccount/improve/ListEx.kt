package wxm.KeepAccount.improve

/**
 * @author      WangXM
 * @version     create：2018/7/6
 */

/**
 * if have (at least one) item let [predicate] true, return true. else return false
 */
inline fun <T> Iterable<T>.exist(predicate: (T) -> Boolean): Boolean {
    return !notExist(predicate)
}

/**
 * opposite for exist
 */
inline fun <T> Iterable<T>.notExist(predicate: (T) -> Boolean): Boolean {
    return firstOrNull(predicate) == null
}