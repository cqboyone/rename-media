package com.vv.tool.media.rename.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MediaConstants {

    public static String VIDEO_SUFFIX_STR = "avi,rmvb,mkv,mp4";
    public static String INFO_SUFFIX_STR = "nfo,jpg,png,jpeg,gif";

    public static Set<String> VIDEO_SUFFIX_SET = new HashSet<String>(Arrays.asList(VIDEO_SUFFIX_STR.split(",")));
    public static Set<String> INFO_SUFFIX_SET = new HashSet<String>(Arrays.asList(INFO_SUFFIX_STR.split(",")));
}
