package com.vv.tool.media.rename.job;

import com.vv.tool.media.rename.info.MediaInfo;
import com.vv.tool.media.rename.tree.TreeNode;
import lombok.Getter;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenameJob {

    private String path;

    @Getter
    private TreeNode<MediaInfo> node;

//    private Map

    public RenameJob(String path) {
        this.path = path;
        MediaInfo mediaInfo = buildMediaInfo(new File(path));
        node = new TreeNode<>(mediaInfo);
        detailVideoFile(node, mediaInfo);
    }


    public void doJob() {
        //重命名
        rootElementsConsumer(this::rename);

//        //增加对e排序重新编号
//        rootElementsConsumer(data -> {
//            Integer e = data.getE();
//
//
//            System.out.println();
//        });
    }

    /**
     * 处理root下面的所有对象（所有数据都放到一层了，所以遍历一次）
     *
     * @param consumer
     */
    public void rootElementsConsumer(Consumer<MediaInfo> consumer) {

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
            data.setE(Integer.valueOf(e));
            //传入的函数
            consumer.accept(data);
        }
    }


    /**
     * 对树对象的data的文件进行重命名
     *
     * @param data 对象
     */
    private void rename(MediaInfo data) {
        String s = data.getTagName();
        String newName = s + "E" + data.getE();
        String filePath = data.getFile().getPath();
        String fileName = data.getFileName();
        filePath = filePath.substring(0, filePath.indexOf(fileName));
        String newFileName = filePath + newName + "." + data.getFileSuffix();
        data.getFile().renameTo(new File(newFileName));
        System.out.println(newFileName);
    }

    private void detailVideoFile(TreeNode<MediaInfo> node, MediaInfo mediaInfo) {
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
                "\\[[0-9]+\\]",
                "\\【[0-9]+\\】"
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

}
