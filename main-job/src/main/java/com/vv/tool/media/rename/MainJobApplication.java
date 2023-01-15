package com.vv.tool.media.rename;

import com.vv.tool.media.rename.job.RenameJob;

public class MainJobApplication {

    public static void main(String[] args) {
        new RenameJob("/Volumes/movie/动画片/小猪佩奇").doJob();
    }

}
