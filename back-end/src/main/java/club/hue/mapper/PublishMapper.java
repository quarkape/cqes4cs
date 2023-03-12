package club.hue.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Mapper
@Repository
public interface PublishMapper {

    // 学生端或者教师端增加赛事的时候查看当前赛事是否已经添加过
    @Select("select title from contests where title=#{title}")
    String checkContestDuplicate(String title);

    // 教学管理端新增一条赛事
    @Insert("insert into contests values(#{id},#{title},#{content},1,#{adder},#{ruleid})")
    Integer addContestOfManager(String id, String title, String content, String adder, String ruleid);

    // 教学管理端 赛事管理页面 获取所有的赛事
        @Select("select id,title from contests where state=1 order by id desc limit #{curPage}, #{pageSize}")
    List<HashMap<String, Object>> getAllContest(Integer curPage, Integer pageSize);

    // 教学管理端获取所有赛事的数量
    // 与上面配套使用
    @Select("select count(*) from contests where state=1")
    Integer getTotalContest();

    // 删除一条赛事内容
    @Delete("delete from contests where id=#{id}")
    Integer deleteContest(String id);

    // 与上一个方法配套使用，根据id获取某个赛事的具体内容
    @Select("select id,title,content,ruleid from contests where id=#{id}")
    HashMap<String, Object> getContestDetailById(String id);

    // 教师修改赛事
    @Update("update contests set title=#{title},content=#{content},state=1,ruleid=#{rule} where id=#{id}")
    Integer checkContestPassed(String title, String content, String rule, String id);
}



















