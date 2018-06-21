package wxm.KeepAccount.preference

import wxm.androidutil.improve.let1

/**
 * @author      WangXM
 * @version     create：2018/6/22
 */
abstract class PreferenceBase {
    /**
     * load preference [pn] with default value [pd] for app
     */
    protected abstract fun loadPreference(pn:String, pd:String):String

    /**
     * save value [pv] to preference [pn] for app
     */
    protected abstract fun savePreference(pn:String, pv:String)

    /**
     * parse preference from [lns] use [delimiter] to divide values
     * example : value1:value2:value3 -> (value1)-(value2)-(value3)
     */
    protected fun parsePreference(lns: String, delimiter: String): Array<String> {
        return lns.split(delimiter.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    /**
     * parse preference from [lns]
     * use [delimiter1] to divide values
     * use [delimiter2] to divide key-value in one value
     * example : key1-value1:key2-value2:key3-value3 -> (key1:value1)-(key2:value2)-(key3:value3)
     */
    protected fun parsePreference(lns: String, delimiter1: String, delimiter2: String)
            : Map<String, String> {
        val ret = HashMap<String, String>()
        lns.split(delimiter1.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                .forEach {
                    parsePreference(it, delimiter2).let1 {
                        if (it.size == 2) {
                            ret[it[0]] = it[1]
                        }
                    }
                }

        return ret
    }

    /**
     * parse settings [acts] to string use [divider] as splitter
     * example : (value1)-(value2)-(value3) -> value1:value2:value3
     */
    protected fun parseToPreferences(acts: List<String>, divider:String): String {
        var ff = true
        val sb = StringBuilder()
        acts.forEach {
            if (!ff) {
                sb.append(divider)
            } else {
                ff = false
            }

            sb.append(it)
        }

        return sb.toString()
    }


    /**
     * parse preference [acts] to string
     * use [delimiter1] to divide values
     * use [delimiter2] to divide key-value in one value
     * example : (key1:value1)-(key2:value2)-(key3:value3) -> key1-value1:key2-value2:key3-value3
     */
    protected fun parseToPreferences(acts: Map<String, Any>, delimiter1: String, delimiter2: String): String {
        val ret = StringBuilder()
        acts.forEach { t, u ->
            StringBuilder().apply {
                append(t).append(delimiter2).append(u.toString())
            }.let {
                if (ret.isEmpty())
                    ret.append(it)
                else
                    ret.append(delimiter1).append(it)

                Unit
            }
        }

        return ret.toString()
    }

    /// PRIVATE BEGIN
    /// PRIVATE END
}