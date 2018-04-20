package wxm.KeepAccount.utility;

/**
 * @author WangXM
 * @version create：2018/4/20
 */
public abstract class ItemDataHolder<T, D> {
    private T   mTag;
    private D   mData;

    public ItemDataHolder() {
        mTag    = null;
        mData   = null;
    }

    public ItemDataHolder(T tag) {
        setTag(tag);
    }

    /**
     * set holder tag
     * @param tag       new data tag
     */
    public void setTag(T tag)    {
        mTag = tag;
        mData = null;
    }

    /**
     * get holder tag
     * @return          holder tag
     */
    public T getTag()    {
        return mTag;
    }

    /**
     * get data
     * @return      load & return data
     */
    public D getData()  {
        if(null == mTag)
            return null;

        if(null == mData)   {
            mData = getDataByTag(mTag);
        }

        return mData;
    }

    /**
     * use tag load data
     * @param tag       data tag
     * @return          data
     */
    protected abstract D getDataByTag(T tag);
}
