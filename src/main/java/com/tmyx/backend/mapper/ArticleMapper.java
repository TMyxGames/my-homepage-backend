package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.Article;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleMapper {

    // 获取全部文章(按时间顺序)
    @Select("select * from article order by upload_time desc")
    public List<Article> findAll();

    // 根据id获取文章
    @Select("select * from article where id = #{id}")
    public Article findById(String id);

    // 查询所有已发布文章（status=1）
    @Select("select * from article where status = 1 order by upload_time desc")
    public List<Article> findPublished();

    // 查询所有草稿文章（status=0）
    @Select("select * from article where status = 0 order by upload_time desc")
    public List<Article> findDrafts();

    // 添加文章
    @Insert("insert into article(id, title, content_url, status, upload_time) " +
            "values(#{id}, #{title}, #{contentUrl}, #{status}, #{uploadTime})")
    public int insert(Article article);

    // 更新文章标题
    @Update("update article set title = #{title} where id = #{id}")
    public int updateTitle(@Param("id") String id, @Param("title") String title);

    // 更新文章
    @Update("update article set title = #{title}, content_url = #{contentUrl}, " +
            "status = #{status}, upload_time = #{uploadTime} where id = #{id}")
    public int update(Article article);

    // 删除文章
    @Delete("delete from article where id = #{id}")
    public int deleteById(String id);
}
