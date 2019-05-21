package com.htgames.nutspoker.util.word;

public class FilterSet {
    private final long[] elements = new long[1024];

    public FilterSet() {
    }

    public void add(int no) {
        this.elements[no >>> 6] |= 1L << (no & 63);
    }

    public void add(int... no) {
        int[] var5 = no;
        int var4 = no.length;

        for (int var3 = 0; var3 < var4; ++var3) {
            int currNo = var5[var3];
            this.elements[currNo >>> 6] |= 1L << (currNo & 63);
        }

    }

    public void remove(int no) {
        this.elements[no >>> 6] &= ~(1L << (no & 63));
    }

    public boolean addAndNotify(int no) {
        int eWordNum = no >>> 6;
        long oldElements = this.elements[eWordNum];
        this.elements[eWordNum] |= 1L << (no & 63);
        boolean result = this.elements[eWordNum] != oldElements;
        return result;
    }

    public boolean removeAndNotify(int no) {
        int eWordNum = no >>> 6;
        long oldElements = this.elements[eWordNum];
        this.elements[eWordNum] &= ~(1L << (no & 63));
        boolean result = this.elements[eWordNum] != oldElements;
        return result;
    }

    public boolean contains(int no) {
        return (this.elements[no >>> 6] & 1L << (no & 63)) != 0L;
    }

    public boolean containsAll(int... no) {
        if (no.length == 0) {
            return true;
        } else {
            int[] var5 = no;
            int var4 = no.length;

            for (int var3 = 0; var3 < var4; ++var3) {
                int currNo = var5[var3];
                if ((this.elements[currNo >>> 6] & 1L << (currNo & 63)) == 0L) {
                    return false;
                }
            }

            return true;
        }
    }

    public boolean containsAll_ueslessWay(int... no) {
        long[] elements = new long[this.elements.length];
        int[] var6 = no;
        int var5 = no.length;

        int i;
        for (int var4 = 0; var4 < var5; ++var4) {
            i = var6[var4];
            elements[i >>> 6] |= 1L << (i & 63);
        }

        for (i = 0; i < elements.length; ++i) {
            if ((elements[i] & ~this.elements[i]) != 0L) {
                return false;
            }
        }

        return true;
    }

    public int size() {
        int size = 0;
        long[] var6 = this.elements;
        int var5 = this.elements.length;

        for (int var4 = 0; var4 < var5; ++var4) {
            long element = var6[var4];
            size += Long.bitCount(element);
        }

        return size;
    }

    public static void main(String[] args) {
        FilterSet oi = new FilterSet();
        System.out.println(oi.elements.length);
    }
}
