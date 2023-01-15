package com.vv.tool.media.rename;

import com.vv.tool.media.rename.info.MediaInfo;
import com.vv.tool.media.rename.tree.TreeNode;
import org.junit.Test;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainJobApplicationTests {

    @Test
    public void testMediaInfo() {
//        File file1 = new File("/Volumes/movie/动画片/小猪佩奇/S07");
//        File file2 = new File("/Volumes/movie/动画片/小猪佩奇/S07/小猪佩奇第七季.Peppa.Pig.Season.7.E02.4K.WEB-DL.H265.AAC-OurTV.mp4");
//        MediaInfoV1 mediaInfoV11 = new MediaInfoV1(null, file1);
//        MediaInfoV1 mediaInfoV12 = new MediaInfoV1(file1, file2);
//        System.out.println(mediaInfoV11);
//        System.out.println(mediaInfoV12);
    }

    @Test
    public void testTreeNode() {
        File fileRoot = new File("/Volumes/movie/动画片/小猪佩奇");
        MediaInfo mediaInfoRoot = new MediaInfo(fileRoot);
        TreeNode<MediaInfo> node = new TreeNode<MediaInfo>(mediaInfoRoot);
        node.addChild(new MediaInfo(new File("/Volumes/movie/动画片/小猪佩奇/S07")));
        node.addChild(new MediaInfo(new File("/Volumes/movie/动画片/小猪佩奇/S07/season02-poster.jpg")));

        System.out.println(node);
    }

    @Test
    public void testP(){
        String sqlServerDateTime = "小猪佩奇第一季.Peppa.Pig.Season.1.E04.4K.WEB-DL.H265.AAC-OurTV.mp4";
        String reg = "\\.*[eE]([0-9]+)\\.*";    // ( 为特殊字符，需要用 \\ 转义
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(sqlServerDateTime);
        if(m.find()){
           Object rawData = m.group(1);
            System.out.println(rawData);  // 组提取字符串 0x993902CE
        }
    }

}
