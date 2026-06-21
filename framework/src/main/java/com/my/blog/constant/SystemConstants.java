package com.my.blog.constant;

public class SystemConstants {
    /**文章草稿状态**/
    public static final int ARTICLE_STATUS_DRAFT = 1;
    /**文章发布状态**/
    public static final int ARTICLE_STATUS_NORMAL = 0;
    /**
     * 友链已审核通过
     */
    public static final int Link_STATUS_PASS = 0;

    /**
     * 评论类型：文章评论
     */
    public static final String ARTICLE_COMMENT = "0";

    /**
     * 评论类型：友链评论
     */
    public static final String LINK_COMMENT = "1";

    /**菜单类型：目录**/
    public static final String TYPE_DIR = "M";
    /**菜单类型：菜单**/
    public static final String TYPE_MENU = "C";
    /**菜单类型：按钮**/
    public static final String TYPE_BUTTON = "F";
}