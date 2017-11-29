package org.openhab.binding.synologysurveillancestation.internal.webapi;

/**
 * Constants for APIs
 *
 * @author Nils
 *
 */
public class ApiConstants {

    // API configuration versions
    public static final String API_VERSION_01 = "1";
    public static final String API_VERSION_02 = "2";
    public static final String API_VERSION_03 = "3";
    public static final String API_VERSION_04 = "4";
    public static final String API_VERSION_05 = "5";
    public static final String API_VERSION_06 = "6";
    public static final String API_VERSION_07 = "7";
    public static final String API_VERSION_08 = "8";
    public static final String API_VERSION_09 = "9";

    // API configuration scripts
    public static final String API_SCRIPT_AUTH = "/webapi/auth.cgi";
    public static final String API_SCRIPT_ENTRY = "/webapi/entry.cgi";

    // API methods
    public static final String METHOD_LOGIN = "Login";
    public static final String METHOD_LOGOUT = "Logout";

    public static final String METHOD_LIST = "List";
    public static final String METHOD_GETINFO = "GetInfo";
    public static final String METHOD_ENABLE = "Enable";
    public static final String METHOD_DISABLE = "Disable";
    public static final String METHOD_GETSNAPSHOT = "GetSnapshot";

}
