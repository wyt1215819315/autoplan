package com.system.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenjj
 * @version 1.0
 * @date 2022/1/21 9:06
 */
public class PageDataVO<E> {
    private int start;
    private int total;
    private List<E> rows;
    private int pageSize = 10;

    public PageDataVO() {
    }

    public PageDataVO(int start, int pageSize) {
        this.start = start;
        this.pageSize = pageSize;
    }

    public PageDataVO(long total, List<E> rows) {
        this.total = (int) total;
        this.rows = rows;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getStart() {
        return this.start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<E> getRows() {
        return this.rows == null ? new ArrayList<E>() : this.rows;
    }

    public void setRows(List<E> rows) {
        this.rows = rows;
    }
}
