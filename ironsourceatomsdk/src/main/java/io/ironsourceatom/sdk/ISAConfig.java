package io.ironsourceatom.sdk;

import android.content.Context;
import android.webkit.URLUtil;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Locale;

class ISAConfig {

    private static final String TAG = ISAConfig.class.getSimpleName();
    private static final Object sInstanceLock = new Object();
    protected static final String DEFAULT_URL = "http://track.atom-data.io/bulk";
    protected static final String DEFAULT_BULK_URL = "http://track.atom-data.io/bulk";
    protected static final int KILOBYTE = 1024;
    protected static final int DEFAULT_BULK_SIZE = 4;
    protected static final int DEFAULT_NUM_OF_RETRIES = 2;
    protected static final int DEFAULT_FLUSH_INTERVAL = 10 * 1000;
    protected static final int DEFAULT_MAX_REQUEST_LIMIT = KILOBYTE * KILOBYTE;
    protected static final int DEFAUL_MAX_DATABASE_LIMIT = KILOBYTE * KILOBYTE * 10;
    protected static final int DEFAULT_ALLOWED_NETWORK_TYPES = ~0;
    //SharedPreferences keys for metadata
    protected static final String KEY_BULK_SIZE = "bulk_size";
    protected static final String KEY_IB_END_POINT = "ib_end_point";
    protected static final String KEY_FLUSH_INTERVAL = "flush_interval";
    protected static final String KEY_IB_END_POINT_BULK = "ib_end_point_bulk";
    protected static final String KEY_MAX_REQUEST_LIMIT = "max_request_limit";
    protected static final String KEY_MAX_DATABASE_LIMIT = "max_database_limit";
    protected static final String KEY_ENABLE_ERROR_REPORTING = "sdk_tracker_enabled";
    protected static final String KEY_ALLOWED_OVER_ROAMING = "allow_roaming_flush";
    protected static final String KEY_ALLOWED_NETWORK_TYPES = "allowed_network_types";
    // IronSourceAtom sTracker configuration
    protected static String IRONBEAST_TRACKER_TABLE = "ironsourceatom_sdk";
    protected static String IRONBEAST_TRACKER_TOKEN = "5ALP9S8DUSpnL3hm4N8BewFnzZqzKt";
    private static ISAConfig sInstance;

    ISAPrefService mISAPrefService;
    private boolean mEnableErrorReporting;
    private boolean mAllowedOverRoaming;
    private int mAllowedNetworkTypes;
    private int mBulkSize;
    private int mFlushInterval;
    private HashMap<String, String> mIBEndPoint;
    private HashMap<String, String> mIBEndPointBulk;
    private long mMaximumRequestLimit;
    private long mMaximumDatabaseLimit;

    ISAConfig(Context context) {
        loadConfig(context);
    }

    static ISAConfig getInstance(Context context) {
        synchronized (sInstanceLock) {
            if (null == sInstance) {
                sInstance = new ISAConfig(context);
            }
        }
        return sInstance;
    }

    /**
     * function called on instance initialization and load data from SharedPref service
     *
     * @param context
     */
    void loadConfig(Context context) {
        mISAPrefService = getPrefService(context);
        mIBEndPoint = new HashMap<>();
        mIBEndPointBulk = new HashMap<>();
        mEnableErrorReporting = mISAPrefService.load(KEY_ENABLE_ERROR_REPORTING, false);
        mAllowedOverRoaming = mISAPrefService.load(KEY_ALLOWED_OVER_ROAMING, true);
        mAllowedNetworkTypes = mISAPrefService.load(KEY_ALLOWED_NETWORK_TYPES, DEFAULT_ALLOWED_NETWORK_TYPES);
        mFlushInterval = mISAPrefService.load(KEY_FLUSH_INTERVAL, DEFAULT_FLUSH_INTERVAL);
        mMaximumRequestLimit = mISAPrefService.load(KEY_MAX_REQUEST_LIMIT, DEFAULT_MAX_REQUEST_LIMIT);
        mMaximumDatabaseLimit = mISAPrefService.load(KEY_MAX_DATABASE_LIMIT, DEFAUL_MAX_DATABASE_LIMIT);
        mBulkSize = mISAPrefService.load(KEY_BULK_SIZE, DEFAULT_BULK_SIZE);
    }

    /**
     * Function provide custom end point url for report if was set or default IronSourceAtom Url
     *
     * @param token unique publisher token
     * @return url of tracker end point
     */
    public String getIBEndPoint(String token) {
        if (mIBEndPoint.containsKey(token)) {
            return mIBEndPoint.get(token);
        }
        String url = mISAPrefService.load(String.format("%s_%s", KEY_IB_END_POINT, token));
        if (URLUtil.isValidUrl(url)) {
            mIBEndPoint.put(token, url);
            return url;
        }
        return DEFAULT_URL;
    }

    /**
     * Function set custom URL for tracker
     *
     * @param token uniq publisher token
     * @param url   custom tracker URL
     */
    protected void setISAEndPoint(String token, String url) {
        mIBEndPoint.put(token, url);
        mISAPrefService.save(String.format("%s_%s", KEY_IB_END_POINT, token), url);
    }

    /**
     * Function provide custom end point url for bulk report if was set or default IronSourceAtom Url
     *
     * @param token unique publisher token
     * @return url of tracker end point if
     */
    public String getIBEndPointBulk(String token) {
        if (mIBEndPointBulk.containsKey(token)) {
            return mIBEndPointBulk.get(token);
        }
        String url = mISAPrefService.load(String.format("%s_%s", KEY_IB_END_POINT_BULK, token));
        if (URLUtil.isValidUrl(url)) {
            mIBEndPointBulk.put(token, url);
            return url;
        }
        return DEFAULT_BULK_URL;
    }

    /**
     * Function set custom URL for tracker
     *
     * @param token unique publisher token
     * @param url   custom tracker URL
     * @throws MalformedURLException
     */
    protected void setISAEndPointBulk(String token, String url) {
        mIBEndPointBulk.put(token, url);
        mISAPrefService.save(String.format("%s_%s", KEY_IB_END_POINT_BULK, token), url);
    }

    /**
     * Function return the max number of reports in sending bulk
     *
     * @return
     */
    public int getBulkSize() {
        return mBulkSize;
    }

    /**
     * Function set the max number of reports in sending bulk
     *
     * @param size max number of reports in bulk
     */
    void setBulkSize(int size) {
        mBulkSize = size > 0 ? size : mBulkSize;
        mISAPrefService.save(KEY_BULK_SIZE, mBulkSize);
    }

    /**
     * Function return next flush time of report
     *
     * @return automatic flush time
     */
    public int getFlushInterval() {
        return mFlushInterval;
    }

    void setFlushInterval(int ms) {
        mFlushInterval = ms;
        mISAPrefService.save(KEY_FLUSH_INTERVAL, mFlushInterval);
    }

    public long getMaximumRequestLimit() {
        return mMaximumRequestLimit;
    }

    void setMaximumRequestLimit(long bytes) {
        mMaximumRequestLimit = bytes >= KILOBYTE ? bytes : mMaximumRequestLimit;
        mISAPrefService.save(KEY_MAX_REQUEST_LIMIT, mMaximumRequestLimit);
    }

    /**
     * @return maximum size of saved reports
     */
    public long getMaximumDatabaseLimit() {
        return mMaximumDatabaseLimit;
    }

    void setMaximumDatabaseLimit(long bytes) {
        mMaximumDatabaseLimit = bytes >= (KILOBYTE * KILOBYTE) ? bytes : mMaximumDatabaseLimit;
        mISAPrefService.save(KEY_MAX_DATABASE_LIMIT, mMaximumDatabaseLimit);
    }

    /**
     * @return sdk num of retries on sending report failed
     */
    public int getNumOfRetries() {
        return DEFAULT_NUM_OF_RETRIES;
    }

    /**
     * Enable the SDK error-tracker.
     */
    public void enableErrorReporting() {
        mEnableErrorReporting = true;
        mISAPrefService.save(KEY_ENABLE_ERROR_REPORTING, mEnableErrorReporting);
    }

    /**
     * return if SDK sending error reports
     *
     * @return boolean
     */
    public boolean isErrorReportingEnabled() {
        return mEnableErrorReporting;
    }

    /**
     * return if the SDK should send reports only when the device is connected via WiFi.
     *
     * @return boolean
     */
    public boolean isAllowedOverRoaming() {
        return mAllowedOverRoaming;
    }

    /**
     * Set whether the SDK can keep sending over a roaming connection.
     */
    public void setAllowedOverRoaming(boolean allowed) {
        mAllowedOverRoaming = allowed;
        mISAPrefService.save(KEY_ALLOWED_OVER_ROAMING, mAllowedOverRoaming);
    }

    /**
     * Restrict the types of networks over which this SDK can keep making HTTP requests.
     * By default, all network types are allowed
     */
    public void setAllowedNetworkTypes(int flags) {
        mAllowedNetworkTypes = flags;
        mISAPrefService.save(KEY_ALLOWED_NETWORK_TYPES, mAllowedNetworkTypes);
    }

    protected int getAllowedNetworkTypes() {
        return mAllowedNetworkTypes;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "[%s] flushInterval %d " +
                        "req limit %d db limit %s bSize %d error enable ",
                TAG, mFlushInterval, mMaximumRequestLimit,
                mMaximumDatabaseLimit, mBulkSize) +
                mEnableErrorReporting;
    }


    /**
     * Function provide Preference service to save and load ISAConfig data
     *
     * @param context application Context
     * @return SharePrefService
     */
    protected ISAPrefService getPrefService(Context context) {
        return ISAPrefService.getInstance(context);
    }

    public enum LOG_TYPE {
        PRODUCTION, DEBUG
    }


}