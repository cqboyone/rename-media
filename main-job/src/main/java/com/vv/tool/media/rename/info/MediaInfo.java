package com.vv.tool.media.rename.info;

import com.vv.tool.media.rename.constants.MediaConstants;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Data
@NoArgsConstructor
public class MediaInfo {

    private File file;

    private String fileName;

    private String fileSuffix;

    private String tagName;

    private boolean isMediaFile;
    private boolean isMediaInfo;

    /**
     * 第几季，预留
     */
    private Integer s;
    /**
     * 第几集
     */
    private Integer e;

    public MediaInfo(File file) {
        this.file = file;
        fileName = file.getName();
        if (file.isFile()) {
            String[] split = fileName.split("\\.");
            if (split.length > 1) {
                fileSuffix = split[split.length - 1];
                int indexOf = fileName.indexOf(fileSuffix);
                tagName = fileName.substring(0, indexOf - 1);
                if (MediaConstants.VIDEO_SUFFIX_SET.contains(fileSuffix)) {
                    isMediaFile = true;
                } else if (MediaConstants.INFO_SUFFIX_SET.contains(fileSuffix)) {
                    isMediaInfo = true;
                }
            }
        } else if (file.isDirectory()) {
            tagName = fileName;
        }
    }

}
