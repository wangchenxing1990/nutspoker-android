package com.htgames.nutspoker.util.word;

import android.content.res.AssetFileDescriptor;

import com.htgames.nutspoker.ChessApp;
import com.netease.nim.uikit.common.util.log.LogUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WordFilter {
    public static char sign = 42;
    public static String path = "";
    private static final FilterSet set = new FilterSet();
    private static final Map<Integer, WordNode> nodes = new HashMap(1024, 1.0F);

    public WordFilter() {
    }

    public static void load() {
        try {
            long e = System.nanoTime();
            init();
            e = System.nanoTime() - e;
            System.out.println("加载时间 : " + e + "ns");
            System.out.println("加载时间 : " + e / 1000000L + "ms");
        } catch (Exception var2) {
            throw new RuntimeException("初始化过滤器失败");
        }
    }

    private static void init() {
        BufferedReader br = null;
        ArrayList words;
        try {
            br = new BufferedReader(new InputStreamReader(ChessApp.sAppContext.getAssets().open("word2.txt"), "UTF-8"));
            words = new ArrayList(1200);
            String e = "";
            while((e = br.readLine()) != null) {
                if(!"".equals(e) && e != null) {
                    words.add(e);
                }
            }
        } catch (Exception var10) {
            throw new RuntimeException(var10);
        } finally {
            try {
                if(br != null) {
                    br.close();
                }
            } catch (IOException var9) {
            }

        }
        addSensitiveWord(words);
    }

    private static void addSensitiveWord(List<String> words) {
        Iterator var6 = words.iterator();

        while(var6.hasNext()) {
            String curr = (String)var6.next();
            char[] chs = curr.toCharArray();
            char fchar = chs[0];
            WordNode fnode;
            if(!set.contains(fchar)) {
                set.add(fchar);
                fnode = new WordNode(fchar, chs.length == 1);
                nodes.put(Integer.valueOf(fchar), fnode);
            } else {
                fnode = (WordNode)nodes.get(Integer.valueOf(fchar));
                if(!fnode.isLast() && chs.length == 1) {
                    fnode.setLast(true);
                }
            }

            int lastIndex = chs.length - 1;

            for(int i = 1; i < chs.length; ++i) {
                fnode = fnode.addIfNoExist(chs[i], i == lastIndex);
            }
        }
    }

    public static final String doFilter(String src) {
        char[] chs = src.toCharArray();
        int length = chs.length;

        for(int i = 0; i < length; ++i) {
            char currc = chs[i];
            if(set.contains(currc)) {
                WordNode node = (WordNode)nodes.get(Integer.valueOf(currc));
                if(node != null) {
                    boolean couldMark = false;
                    int markNum = -1;
                    if(node.isLast()) {
                        couldMark = true;
                        markNum = 0;
                    }

                    int k = i;

                    while(true) {
                        ++k;
                        if(k >= length) {
                            break;
                        }

                        node = node.querySub(chs[k]);
                        if(node == null) {
                            break;
                        }

                        if(node.isLast()) {
                            couldMark = true;
                            markNum = k - i;
                        }
                    }

                    if(couldMark) {
                        for(k = 0; k <= markNum; ++k) {
                            chs[k + i] = sign;
                        }

                        i += markNum;
                    }
                }
            }
        }

        return new String(chs);
    }
}
