package com.enhancell.remotesample;


/**
 * Created by Asiya Khatib on 25,July,2017
 * Verveba Telecom pvt Ltd.SERVICE_URL_STS_INFO
 */
public class UrlConstants {

    public static final String UPLOAD_FILE_PATH = "/uploadFile";
    public static String SERVICE_URL_RF_INFO = UPLOAD_FILE_PATH + "/verveba/validate/val";
    public static String SERVICE_URL_LICENSE_INFO = UPLOAD_FILE_PATH + "/verveba/register/validity";
    public static String SERVICE_URL_SETTING_INFO = UPLOAD_FILE_PATH + "/verveba/setting/getSettings";
    public static String SERVICE_URL_CSFB_INFO = UPLOAD_FILE_PATH + "/verveba/setting/getCSFBMONum";
    public static String SERVICE_URL_CSV_INFO = UPLOAD_FILE_PATH + "/verveba/setting/getFileSettings";
    public static String SARESULT_URI = UPLOAD_FILE_PATH + "/verveba/getSectorSwapResult/create";
    public static String SCUBA_EVENT_LOG_CODES_URL = "/Tapas2/ScubaLogEventCodes/read";

    //mobility urls
    public static String SERVICE_URL_MM_INFO = UPLOAD_FILE_PATH + "/verveba/mastermobility/create";
    public static String SERVICE_URL_MT_INFO = UPLOAD_FILE_PATH + "/verveba/mobilitytest/create";
    public static String SERVICE_URL_DLST_INFO = UrlConstants.SARESULT_URI;
    public static String SERVICE_URL_MHO_INFO = UPLOAD_FILE_PATH + "/verveba/horesult/create";
    public static String SERVICE_URL_MST_INFO = UPLOAD_FILE_PATH + "/verveba/errorInfo/insert";
    public static String SERVICE_URL_DLST_INFO_CELLINFO = UPLOAD_FILE_PATH + "/verveba/getcellinfo/create";
    public static String SERVICE_URL_UPLOADIMAGE = "/Tapas2/capture/uploadCapturefilewithdetails/create";
    public static String SERVICE_URL_INDOOR_UPLOADCSV = "/Tapas2/capture/indoorMobility/uploadCSVfilewithdetails/create";
    public static String SERVICE_URL_SURVEY = "/Tapas2/capture/uploadSiteSurveywithdetails/create";
    public static String SERVICE_SARESULT_URI = UPLOAD_FILE_PATH + "/verveba/getSectorSwapResult/create";
    public static String SERVICE_URL_DOWNLOAD_BANDMAPPING = "/Tapas2/Csvfile/downloadCsvfile?csvfilename=Band_Earfcn_Mapping.csv";

    // stationary urls
    public static String SERVICE_URL_MST_INFO_STATIONARY = UPLOAD_FILE_PATH + "/verveba/masterstationary/create";
    public static String SERVICE_URL_STS_INFO = UPLOAD_FILE_PATH + "/verveba/testSetting/create";
    public static String DL_URL_INFO = UPLOAD_FILE_PATH + "/verveba/pdlparse/create";
    public static String UL_URL_INFO = UPLOAD_FILE_PATH + "/verveba/pulparse/create";
    public static String SA_URL_INFO = UPLOAD_FILE_PATH + "/verveba/siteAnalysis/getResult";
    public static String CSFBMT_URL_INFO = UPLOAD_FILE_PATH + "/verveba/csfbparse/mt";
    public static String CSFBMO_URL_INFO = UPLOAD_FILE_PATH + "/verveba/csfbparse/mo";

   /* public static String UPLOAD_DL = UrlConstants.UPLOAD_FILE_PATH + "/UploadULDL";
    public static String UPLOAD_PING = UrlConstants.UPLOAD_FILE_PATH + "/UploadPing";
    public static String UPLOADQML_URL = UrlConstants.UPLOAD_FILE_PATH + "/UploadQMLFile";*/

    public static String UPLOAD_DL_NEW = "/Tapas2/uploadFile/UploadULDL";
    public static String UPLOAD_PING_NEW = "/Tapas2/uploadFile/UploadPing";
    public static String UPLOADQML_URL_NEW = "/Tapas2/uploadFile/UploadQMLFile";

    //http://23.253.246.81:80/uploadFile/verveba/mastercatests/create
    //http://23.253.246.81:80/uploadFile/verveba/catests/create
    // CA urls

    public static String CA_TEST_ID = UrlConstants.UPLOAD_FILE_PATH + "/verveba/mastercatests/create";
    public static String CA_TEST_RESULT = UrlConstants.UPLOAD_FILE_PATH + "/verveba/catests/create";

    public static String VoLTE_TEST_ID = UrlConstants.UPLOAD_FILE_PATH + "/verveba/mastervoltetests/create";
    public static String VoLTE_TEST_RESULT = UrlConstants.UPLOAD_FILE_PATH + "/verveba/voltetests/create";
}
