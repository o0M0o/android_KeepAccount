package wxm.KeepAccount.ui.base.Adapter

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.support.annotation.LayoutRes
import android.support.annotation.RequiresApi
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ThemedSpinnerAdapter

/**
 * Created by WangXM on 2018/3/28.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
open class LVAdapter
/**
 * Constructor
 *
 * @param context  The context where the View associated with this SimpleAdapter is running
 * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
 * Maps contain the data for each row, and should include all the entries specified in
 * "from"
 * @param resource Resource identifier of a view layout that defines the views for this list
 * item. The layout file should include at least those named views defined in "to"
 */
(context: Context, private val mData: List<*>,
 @LayoutRes resource: Int) : BaseAdapter(), ThemedSpinnerAdapter {
    private val mInflater: LayoutInflater

    /**
     * Returns the [android.widget.SimpleAdapter.ViewBinder] used to bind data to views.
     *
     * @return a ViewBinder or null if the binder does not exist
     * @see .setViewBinder
     */
    /**
     * Sets the binder used to bind data to views.
     *
     * @param viewBinder the binder used to bind data to views, can be null to
     * remove the existing binder
     * @see .getViewBinder
     */
    var viewBinder: android.widget.SimpleAdapter.ViewBinder? = null

    private val mResource: Int
    private var mDropDownResource: Int = 0

    /**
     * Layout inflater used for [.getDropDownView].
     */
    private var mDropDownInflater: LayoutInflater? = null

    init {
        mDropDownResource = resource
        mResource = mDropDownResource
        mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    /**
     * @see android.widget.Adapter.getCount
     */
    override fun getCount(): Int {
        return mData.size
    }

    /**
     * @see android.widget.Adapter.getItem
     */
    override fun getItem(position: Int): Any? {
        return mData[position]
    }

    /**
     * @see android.widget.Adapter.getItemId
     */
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    /**
     * @see android.widget.Adapter.getView
     */
    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        return createViewFromResource(mInflater, position, convertView, parent, mResource)
    }

    private fun createViewFromResource(inflater: LayoutInflater, position: Int, convertView: View?,
                                       parent: ViewGroup, resource: Int): View {
        return convertView ?: inflater.inflate(resource, parent, false)
    }

    /**
     *
     * Sets the layout resource to create the drop down views.
     *
     * @param resource the layout resource defining the drop down views
     * @see .getDropDownView
     */
    fun setDropDownViewResource(resource: Int) {
        mDropDownResource = resource
    }

    /**
     * Sets the [android.content.res.Resources.Theme] against which drop-down views are
     * inflated.
     *
     *
     * By default, drop-down views are inflated against the theme of the
     * [Context] passed to the adapter's constructor.
     *
     * @param theme the theme against which to inflate drop-down views or
     * `null` to use the theme from the adapter's context
     * @see .getDropDownView
     */
    override fun setDropDownViewTheme(theme: Resources.Theme?) {
        if (theme == null) {
            mDropDownInflater = null
        } else if (theme == mInflater.context.theme) {
            mDropDownInflater = mInflater
        } else {
            val context = ContextThemeWrapper(mInflater.context, theme)
            mDropDownInflater = LayoutInflater.from(context)
        }
    }

    override fun getDropDownViewTheme(): Resources.Theme? {
        return if (mDropDownInflater == null) null else mDropDownInflater!!.context.theme
    }

    override fun getDropDownView(position: Int, convertView: View, parent: ViewGroup): View {
        val inflater = if (mDropDownInflater == null) mInflater else mDropDownInflater
        return createViewFromResource(inflater!!, position, convertView, parent, mDropDownResource)
    }

    override fun getViewTypeCount(): Int {
        val org_ct = count
        return if (org_ct < 1) 1 else org_ct
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
