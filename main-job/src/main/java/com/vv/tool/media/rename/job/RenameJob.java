package com.vv.tool.media.rename.job;

import com.vv.tool.media.rename.constants.MediaConstants;
import com.vv.tool.media.rename.info.MediaInfo;
import com.vv.tool.media.rename.tree.TreeNode;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenameJob {

    private String path;

    private TreeNode<MediaInfo> node;

//    private Map

    public RenameJob(String path) {
        this.path = path;
    }


    public void doJob() {
        MediaInfo mediaInfo = buildMediaInfo(new File(path));
        node = new TreeNode<MediaInfo>(mediaInfo);
        detailVideoFile(node, mediaInfo);

        //遍历处理每个文件
        List<TreeNode<MediaInfo>> elementsIndex = node.getElementsIndex();
        for (TreeNode<MediaInfo> t : elementsIndex) {
            MediaInfo data;
            if (t.isRoot() || (data = t.getData()) == null || (!data.isMediaFile() && !data.isMediaInfo())) {
                continue;
            }
            String tagName = data.getTagName();
            String e = findE(tagName);
            if (e == null || "".equals(e)) {
                continue;
            }
            String s = t.getParent().getData().getTagName();
            String newName = s + "E" + e;
            System.out.println(newName);
            String filePath = data.getFile().getPath();
            String fileName = data.getFileName();
            filePath = filePath.substring(0, filePath.indexOf(fileName));
            String newFileName = filePath + newName + "." + data.getFileSuffix();
            data.getFile().renameTo(new File(newFileName));
            System.out.println(newFileName);
        }
    }

    public void detailVideoFile(TreeNode<MediaInfo> node, MediaInfo mediaInfo) {
        File file = mediaInfo.getFile();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                MediaInfo fMediaInfo = buildMediaInfo(f);
                TreeNode<MediaInfo> childNode = node.addChild(fMediaInfo);
                detailVideoFile(childNode, fMediaInfo);
            }
        }

    }

    public MediaInfo buildMediaInfo(File file) {
        return new MediaInfo(file);
    }

    public String findE(String str) {
        List<String> regs = Arrays.asList(
                "\\.*[eE]([0-9]+)\\.*",
                "\\[[0-9]+\\]"
        );
        for (String r : regs) {
            String s = find(r, str);
            if (s != null) {
                return findENumber(s);
            }
        }
        return null;
    }

    public String findENumber(String str) {
        if (str == null) {
            return "";
        }
        String reg = "\\d+";
        return find(reg, str);
    }

    public String find(String reg, String str) {
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        if (m.find()) {
            return m.group(0);
        }
        return null;
    }

    public void detailVideoFile(File file) {
        if (file.isFile()) {
            String name = file.getName();
            System.out.println(name);
            String[] split = name.split("\\.");
            if (split.length <= 1) {
                //文件没有类型，不处理
                return;
            }
            String fileSuffix = split[split.length - 1];
            if (!MediaConstants.VIDEO_SUFFIX_SET.contains(fileSuffix)) {
                return;
            }
            System.out.println("文件类型：" + fileSuffix);
        }
    }
}
