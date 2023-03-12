package club.hue.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Mapper
@Repository
public interface BasicInfoMapper {

    // 学生端学分评价页面获取学生自己的所有加分详情
    @Select("select ta.id,name,score,action from authentications ta " +
            "left join " +
            "(select id,score,action from authentication_score) tb " +
            "on ta.id=tb.id " +
            "where substring(ta.id,14)=#{userid} and (state=1 or state=2) and ta.year=#{year}")
    List<HashMap<String, Object>> getAddScoreMaterialDetail(String userid, String year);

    // 学生端学分评价获取学生所有文件
    @Select("select ta.id,name,score,imgurl from " +
            "(select * from authentications where substring(id,14)=#{userid} and (state=1 or state=2) and year=#{year}) ta " +
            "left join " +
            "(select id, score from authentication_score) tb " +
            "on ta.id=tb.id")
    List<HashMap<String, Object>> getStudentAllScoreMaterials(String userid, String year);

    // 学生端在填写学分申请的时候查询相关赛事
    @Select("select id,title as value from contests where title like concat('%',#{words},'%') and state=1")
    List<HashMap<String, Object>> getContestSearchResultByWords(String words);

    // 教师端学生评价页面获取学生的学业成绩和综合成绩
    @Select("select id, name, score_main, score_other, score_plus, score_sub, score_final, (@rowno:=@rowno+1) as rank from " +
            "(select tmain.id, name, tmain.score_main, ifnull(tother.score_other,0) score_other, ifnull(tf.score_plus,0) as score_plus, ifnull(te.score_sub,0) as score_sub, (truncate((tmain.score_main+ifnull(tother.score_other,0)),4)+ifnull(tf.score_plus,0)-ifnull(te.score_sub,0)) as score_final from " +
            "(select id,year,truncate(sum(grade*score)/sum(score),4)*(select weight from weights_config where type=2) as score_main from grades where year=#{year} group by id) tmain " +
            "left join " +
            "(select substring(ta.id, 14) as id, ifnull(sum(tb.score),0) score_other from authentications ta " +
            "left join " +
            "(select id, score from authentication_score) tb " +
            "on ta.id = tb.id " +
            "group by substring(ta.id, 16)) tother " +
            "on tmain.id=tother.id " +
            "left join " +
            "(select userid,name,major_code from user_info where year=(select year from user_info_manager where userid=#{managerId}) and major_code=#{majorCode}) td " +
            "on tmain.id=td.userid " +
            "left join " +
            "(select stu_id, year, sum(score) as score_sub from score_extra where type=0 and year=#{year} group by stu_id) te " +
            "on tmain.id = te.stu_id " +
            "left join " +
            "(select stu_id, year, sum(score) as score_plus from score_extra where type=1 and year=#{year} group by stu_id) tf " +
            "on tmain.id = tf.stu_id " +
            "where td.major_code=#{majorCode} " +
            "order by score_final desc, tmain.score_main desc, tmain.id desc) te, (select (@rowno:=0)) tf")
    List<HashMap<String, Object>> getAllStudentScore(String year, String majorCode, String managerId);

    // 学生根据自己的所在的年级和专业获取当年的评价结果,semester是学生是多少届的，major是学生专业，year是当前学年
    @Select("select id, name, score_main, score_other, score_plus, score_sub, score_final, (@rowno:=@rowno+1) as rank from " +
            "(select tmain.id, name, tmain.score_main, ifnull(tother.score_other,0) score_other, ifnull(tf.score_plus,0) as score_plus, ifnull(te.score_sub,0) as score_sub, (truncate((tmain.score_main + ifnull(tother.score_other,0)),4) + ifnull(tf.score_plus,0)-ifnull(te.score_sub,0)) as score_final from " +
            "(select id,year,truncate(sum(grade*score)/sum(score),4)*(select weight from weights_config where type=2) as score_main from grades where year=#{year} group by id) tmain " +
            "left join " +
            "(select substring(ta.id, 14) as id, ifnull(sum(tb.score), 0) score_other from authentications ta " +
            "left join " +
            "(select id, score from authentication_score) tb " +
            "on ta.id=tb.id " +
            "group by substring(ta.id, 16)) tother " +
            "on tmain.id=tother.id " +
            "left join " +
            "(select userid, name, major_code from user_info where year=(select year from user_info where userid=#{userid}) and major_code=(select major_code from user_info where userid=#{userid})) td " +
            "on td.userid=tmain.id " +
            "left join " +
            "(select stu_id, year, sum(score) as score_sub from score_extra where type=0 and year=#{year} group by stu_id) te " +
            "on tmain.id = te.stu_id " +
            "left join " +
            "(select stu_id, year, sum(score) as score_plus from score_extra where type=1 and year=#{year} group by stu_id) tf " +
            "on tmain.id = tf.stu_id " +
            "where td.major_code=(select major_code from user_info where userid=#{userid}) " +
            "order by score_final desc, tmain.score_main desc, tmain.id desc) te, (select (@rowno:=0)) tf")
    List<HashMap<String, Object>> getAllStudentScoreThemselves(String year, String userid);

    // 教师端 学生评价页面 获取某个学生的所有已经通过审核的加分材料
    @Select("select id,name,state from authentications where substring(id,14)=#{userid} and (state=1 or state=2) order by substring(id,1,13) desc")
    List<HashMap<String, Object>> getStudentAuthenticationsById(String userid);

    // 教师 获取每一个测评点的规则
    @Select("select * from contests_config")
    List<HashMap<String, Object>> getContestConfig();

    // 删除主表contest_config里面的所有内容
    @Delete("delete from contests_config")
    Integer deleteAllContestConfig();

    // 插入赛事规则入新临时表
    @Insert("<script>" +
            "insert into contests_config_temp values" +
            "<foreach collection = \"list\" item=\"item\" index=\"index\" separator=\",\">" +
            "(#{item.uuid},#{item.name},#{item.config},#{item.indexid})" +
            "</foreach>" +
            "</script>")
    Integer updateContestConfigTemp(List<HashMap<String, Object>> list);

    // 插入赛事规则入新临时表
    @Insert("insert into contests_config select * from contests_config_temp")
    Integer updateContestConfig();

    // 将临时规则表的数据清空
    @Delete("delete from contests_config_temp")
    Integer deleteContestsConfigTemp();

    // 学生端获取自己的学业成绩、综合成绩
    @Select("select tmain.id, tmain.score_main, ifnull(tother.score_other,0) score_other, ifnull(tf.score_plus,0) as score_plus, ifnull(te.score_sub,0) as score_sub, (truncate((tmain.score_main+ifnull(tother.score_other,0)),4)+ifnull(tf.score_plus,0)-ifnull(te.score_sub,0)) as score_final from " +
            "(select id,year,ifnull(truncate(sum(grade*score)/sum(score),4)*(select weight from weights_config where type=2),0) as score_main from grades where year=#{year} and id=#{id} group by id) tmain " +
            "left join " +
            "(select substring(ta.id, 14) as id, ifnull(sum(tb.score), 0) score_other from authentications ta " +
            "left join " +
            "(select id, score from authentication_score) tb " +
            "on ta.id = tb.id " +
            "where year=#{year}" +
            "group by substring(ta.id, 16)) tother " +
            "left join " +
            "(select stu_id, year, sum(score) as score_sub from score_extra where type=0 and year=#{year} group by stu_id) te " +
            "on tother.id = te.stu_id " +
            "left join " +
            "(select stu_id, year, sum(score) as score_plus from score_extra where type=1 and year=#{year} group by stu_id) tf " +
            "on tother.id = tf.stu_id " +
            "on tmain.id=tother.id")
    HashMap<String, Object> getFinalGradeById(String id, String year);

    // 获取当前的所有班级
    @Select("select * from majors")
    List<HashMap<String, Object>> getAllClasses();

    // 获取辅导员账号列表
    @Select("select ta.userid, role, name, year from users ta " +
            "left join " +
            "(select * from user_info_manager) tb " +
            "on ta.userid=tb.userid " +
            "where ta.role='manager'")
    List<HashMap<String, Object>> getManagerList();

    // 教师和学生可以获取某个学生的加分和减分项
    @Select("select * from score_extra where stu_id=#{userid} and year=#{year}")
    List<HashMap<String, Object>> getScoreManually(String userid, String year);

    // 教师删除某个手动加分项
    @Delete("delete from score_extra where uid=#{uid}")
    int deleteScoreManually(String uid);

    // 教师修改某个自动加分项
    @Update("update score_extra set score=#{score}, type=#{type}, `desc`=#{desc} where uid=#{uid}")
    int modifyScoreManually(double score, int type, String desc, String uid);

    // 教学辅导员上传excel文件直接存入数据库
    @Insert("<script>" +
            "insert into grades values" +
            "<foreach collection = \"list\" item=\"item\" index=\"index\" separator=\",\">" +
            "(#{item.id},#{item.code},#{item.year},#{item.grade},#{item.score},#{item.semester})" +
            "</foreach>" +
            "</script>")
    Integer readXlsFile(List<HashMap<String, Object>> list);

    // 将学生名单存入学生信息表
    @Insert("<script>" +
            "insert ignore into user_info values" +
            "<foreach collection = \"list\" item=\"item\" index=\"index\" separator=\",\">" +
            "(#{item.userid},#{item.name},#{item.major_code},#{item.class},#{item.year})" +
            "</foreach>" +
            "</script>")
    Integer addStudents(List<HashMap<String, Object>> list);

    // 教师获取当前没有设置密码的学生列表
    @Select("select userid from user_info where userid not in (select userid from users)")
    List<HashMap<String, Object>> getUnSavedStudentList();

    // 与上面配套使用，根据列表插入相关数据
    @Insert("<script>" +
            "insert ignore into users values" +
            "<foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\">" +
            "(#{item.userid},UPPER(md5(#{item.userid})),'student')" +
            "</foreach>" +
            "</script>")
    int addStudentList(List<HashMap<String, Object>> list);

    @Select("select uuid, name from contests_config")
    List<HashMap<String, String>> getRule();

    @Select("select * from contests_config where uuid=(select ruleid from contests where id=#{id})")
    HashMap<String, String> getContestConfigById(String id);

    @Select("select classconfig from contests_config where uuid=#{uuid}")
    String  getContestConfigByUuid(String uuid);

    @Select("select weight from weights_config where type=2")
    Object getGradePercent();

    @Update("update weights_config set weight=#{percent} where type=2")
    int modifyGradePercent(float percent);

    @Select("select * from indexs")
    List<HashMap<String, String>> getIndexs();

    @Insert("insert into indexs values(#{uuid}, #{name})")
    int addIndex(String uuid, String name);

    @Update("update indexs set name=#{name} where uuid=#{uuid}")
    int modifyIndex(String name, String uuid);

    @Delete("delete from indexs where uuid=#{uuid}")
    int deleteIndex(String uuid);

    @Select("select * from majors")
    List<HashMap<String, String>> getMajors();

    @Insert("insert into majors values(#{uuid}, #{code}, #{name})")
    int addMajor(String uuid, String code, String name);

    @Update("update majors set major_name=#{name}, major_code=#{code} where uuid=#{uuid}")
    int modifyMajor(String name, String code, String uuid);

    @Delete("delete from majors where uuid=#{uuid}")
    int deleteMajor(String code);

    @Delete("delete from contests_config")
    int deleteContestConfig();

    @Select("select count(ta.id) as count, td.name, td.uuid from " +
            "(select ruleid, id from authentications where year=#{year}) ta " +
            "left join " +
            "(select userid, major_code from user_info) tb " +
            "on tb.userid=substring(ta.id, 14) " +
            "left join " +
            "(select uuid, name, indexid from contests_config) tc " +
            "on ta.ruleid=tc.uuid " +
            "left join " +
            "(select uuid, name from indexs) td " +
            "on tc.indexid=td.uuid " +
            "where major_code=#{code} " +
            "group by td.name")
    List<HashMap<String, Object>> getIndexData(String year, String code);

    // 导出指标配置
    @Select("select uuid, convert(name using gbk) as name into outfile #{path} from indexs")
    Integer exportIndexConfig(String path);

    // 导出加分规则配置
    @Select("select uuid, convert(name using gbk) as name, convert(classconfig using gbk) as classconfig, indexid into outfile #{path} from contests_config")
    Integer exportRuleConfig(String path);

    // 导出专业配置
    @Select("select uuid, major_code, convert(major_name using gbk) as major_name into outfile #{path} from majors")
    Integer exportMajorConfig(String path);

}
















