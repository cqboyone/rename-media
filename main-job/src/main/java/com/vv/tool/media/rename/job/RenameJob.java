package com.vv.tool.media.rename.job;

import com.vv.tool.media.rename.constants.MediaConstants;
import com.vv.tool.media.rename.info.MediaInfo;
import com.vv.tool.media.rename.tree.TreeNode;
import com.vv.tool.media.rename.util.StrUtil;
import lombok.Getter;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        //设置季集数
        rootElementsConsumer(this::setE);
        //增加对e排序重新编号，可选
        sort();

        //重命名
        rootElementsConsumer(this::rename);


    }

    /**
     * 处理root下面的所有对象（所有数据都放到一层了，所以遍历一次）
     *
     * @param consumer
     */
    public void rootElementsConsumer(Consumer<TreeNode<MediaInfo>> consumer) {

        //遍历处理每个文件
        List<TreeNode<MediaInfo>> elementsIndex = node.getElementsIndex();
        for (TreeNode<MediaInfo> t : elementsIndex) {
            MediaInfo data;
            if (t.isRoot() || (data = t.getData()) == null || (!data.isMediaFile() && !data.isMediaInfo())) {
                continue;
            }

            //传入的函数
            consumer.accept(t);
        }
    }

    /**
     * 设置集数
     *
     * @param t
     */
    private void setE(TreeNode<MediaInfo> t) {
        MediaInfo data = t.getData();
        String tagName = data.getTagName();
        String e = findE(tagName);
        if (e == null || "".equals(e)) {
            return;
        }
        try {
            data.setE(Integer.valueOf(e));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        try {
            String parentTageName = t.getParent().getData().getTagName();
            data.setS(Integer.valueOf(findS(parentTageName)));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    /**
     * 对树对象的data的文件进行重命名
     *
     * @param t 对象
     */
    private void rename(TreeNode<MediaInfo> t) {
        MediaInfo data = t.getData();
        String newName = "S" + data.getS() + "E" + data.getE();
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
                //对mac上奇奇怪怪前缀进行过滤
                if (!supportFilePrefix(f.getName())) {
                    continue;
                }

                String parentName = node.getData().getFileName();
                if (StrUtil.isBlank(parentName)) {
                    String s = findS(parentName);
                    if (StrUtil.isBlank(s)) {
                        fMediaInfo.setS(Integer.valueOf(s));
                    }
                }
                TreeNode<MediaInfo> childNode = node.addChild(fMediaInfo);
                detailVideoFile(childNode, fMediaInfo);
            }
        }

    }

    private boolean supportFilePrefix(String fileName) {
        for (String fix : MediaConstants.UN_SUPPORT_PREFIX_SET) {
            if (fileName.startsWith(fix)) {
                return false;
            }
        }
        return true;
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
        return findByRegs(regs, str);
    }

    public String findS(String str) {
        List<String> regs = Arrays.asList(
                "[S|s][0-9]+"
        );
        return findByRegs(regs, str);
    }

    public String findByRegs(List<String> regs, String str) {
        for (String r : regs) {
            String s = findByRegs(r, str);
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
        return findByRegs(reg, str);
    }

    public String findByRegs(String reg, String str) {
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        if (m.find()) {
            return m.group(0);
        }
        return null;
    }

    /**
     * 增加对e排序重新编号
     */
    private void sort() {
        List<TreeNode<MediaInfo>> elements = node.getElementsIndex();
        Map<Integer, List<MediaInfo>> collect
                = elements.stream()
                .map(TreeNode::getData)
                .filter(m -> m.getS() != null)
                .collect(Collectors.groupingBy(MediaInfo::getS));
        collect.forEach((key, value) -> {
            //检查是都识别了集数
            boolean b = allHaveE(value);
            if (!b) {
                System.out.println("S" + key + "有集数未识别！");
                return;
            }
            List<MediaInfo> sortedList
                    = value.stream().sorted(Comparator.comparing(MediaInfo::getE)).collect(Collectors.toList());
            //重设e
            for (int i = 0; i < sortedList.size(); i++) {
                sortedList.get(i).setE(i + 1);
            }
            System.out.println();
        });

    }

    /**
     * 是否都含有集数
     *
     * @param list
     * @return
     */
    private boolean allHaveE(List<MediaInfo> list) {
        for (MediaInfo mediaInfo : list) {
            if (mediaInfo.getE() == null) {
                return false;
            }
        }
        return true;
    }
}
