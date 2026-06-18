package com.anxinban.dto;


/**
 * PageResult 类。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */

import java.util.List;

/**
 * 通用分页查询结果封装类。
 *
 * <p>用于封装列表查询接口的分页数据，包含当前页数据列表、总记录数、当前页码及每页大小。
 * 所有需要分页返回的 Controller 接口统一使用此对象作为 {@link ApiResponse#data} 的 payload。</p>
 *
 * @param <T> 列表元素的数据类型
 */
public class PageResult<T> {

    /** 当前页数据列表，若该页无数据则为空列表（建议不为 null） */
    private List<T> list;

    /** 符合条件的总记录数，用于前端计算总页数 */
    private long total;

    /** 当前页码，从 1 开始计数 */
    private int page;

    /** 每页大小，即单次查询返回的最大记录数 */
    private int pageSize;

    /**
     * 默认无参构造方法。
     *
     * <p>供 JSON 反序列化及框架实例化使用。</p>
     */
    public PageResult() {
    }

    /**
     * 全参构造方法。
     *
     * @param list     当前页数据列表
     * @param total    总记录数
     * @param page     当前页码
     * @param pageSize 每页大小
     */
    public PageResult(List<T> list, long total, int page, int pageSize) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }

    /**
     * 获取当前页数据列表。
     *
     * @return 当前页数据列表
     */
    public List<T> getList() {
        return list;
    }

    /**
     * 设置当前页数据列表。
     *
     * @param list 当前页数据列表
     */
    public void setList(List<T> list) {
        this.list = list;
    }

    /**
     * 获取总记录数。
     *
     * @return 总记录数
     */
    public long getTotal() {
        return total;
    }

    /**
     * 设置总记录数。
     *
     * @param total 总记录数
     */
    public void setTotal(long total) {
        this.total = total;
    }

    /**
     * 获取当前页码。
     *
     * @return 当前页码
     */
    public int getPage() {
        return page;
    }

    /**
     * 设置当前页码。
     *
     * @param page 当前页码
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * 获取每页大小。
     *
     * @return 每页大小
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 设置每页大小。
     *
     * @param pageSize 每页大小
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
