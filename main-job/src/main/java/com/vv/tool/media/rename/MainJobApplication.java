package com.vv.tool.media.rename;

import com.vv.tool.media.rename.job.RenameJob;

public class MainJobApplication {

    public static void main(String[] args) {
        new RenameJob("/Users/zhangwei/Downloads/龙珠").doJob();
    }

}
