package com.vv.tool.media.rename;

import com.vv.tool.media.rename.job.RenameJob;

public class MainJobApplication {

    public static void main(String[] args) {
        new RenameJob("/Volumes/movie/电视剧/少年歌行").doJob();
    }

}
