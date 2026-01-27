package com.tmyx.backend.mapper;

import com.tmyx.backend.entity.Article;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleMapper {

    // 检查文章（id）
    @Select("select * from article where id = #{id}")
    public Article findById(Integer id);

    // 插入文章
    @Insert("insert into article(title, file_name, content_url, upload_time) " +
            "values(#{title}, #{fileName}, #{contentUrl}, now())")
    public int insert(Article article);

    // 查询所有文章(按时间顺序)
    @Select("select * from article order by upload_time desc")
    public List<Article> findAll();

    // 更新文章标题
    @Update("update article set title = #{title} where id = #{id}")
    public int updateTitle(@Param("id") Integer id, @Param("title") String title);

    // 删除文章
    @Delete("delete from article where id = #{id}")
    public int delete(Integer id);
}
