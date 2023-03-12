package club.hue.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Mapper
@Repository
public interface MaterialMapper {

    // 学生提交学分申请证明
    @Insert("insert into authentications values(#{id},#{name},null,null,#{imgurl},0,#{year},#{ruleid},#{levelstr})")
    Integer addNewAuthentication(String id, String name, String imgurl, String year, String ruleid, String levelstr);

    // 教师端获取所有学生的申请数据，根据传递的值选择不同的类型
    @Select("select id,name,ifnull(checktime, '-') as checktime,state,applier,ifnull(checker,'-') as checker from authentications a " +
            "left join " +
            "(select userid, major_code, name as applier from user_info where year=(select year from user_info_manager where userid=#{managerId})) b " +
            "on substring(a.id, 14, 15)=b.userid " +
            "left join " +
            "(select userid, name as checker from user_info_manager) c " +
            "on a.checkid=c.userid " +
            "where (case when #{state}=3 then (a.state=0 or a.state=1 or a.state=2) else a.state=#{state} end) and year=#{year} and b.major_code=#{major}" +
            "order by substring(a.id, 1, 13) desc " +
            "limit #{curPage}, #{pageSize}")
    List<HashMap<String, Object>> getAllAuthentications( Integer curPage, Integer pageSize, Integer state, String year, String major, String managerId);

    // 教师端获取所有学生的申请数据的总数量，根据传递的值获取不同的类型
    @Select("select count(*) from authentications a " +
            "left join " +
            "(select userid, major_code from user_info) b " +
            "on substring(a.id, 14, 15)=b.userid " +
            "left join " +
            "(select * from majors) c " +
            "on b.major_code=c.major_code " +
            "where (case #{state} when 3 then (a.state=0 or a.state=1 or a.state=2) else state=#{state} end) and year=#{year} and c.major_code=#{major}")
    Integer getAuthenticationsCount(Integer state, String year, String major);

    // 学生端获取自己的所有申请材料
    @Select("select id, name, state from authentications where substring(id, 14, 15)=#{userid} and year=#{year} order by substring(id, 1, 13) desc limit #{curPage}, #{pageSize}")
    List<HashMap<String, Object>> getAuthenticationsById(String userid, Integer curPage, Integer pageSize, String year);

    // 学生端获取自己所有的申请材料的总数，与上一个配套使用
    @Select("select count(*) from authentications where substring(id, 14, 15)=#{userid} and year=#{year}")
    Integer getAuthenticationByIdCount(String userid, String year);

    // 学生端通过加分申请id获取申请的具体内容
    @Select("select id,name,checktime,state,year,imgurl from authentications where id=#{id}")
    HashMap<String, Object> getAuthenticationById(String id);


    // 学生端标记自己的申请材料为异议，并说明理由
    @Update("update authentication_score set " +
            "desca=(case when `action` in (1,2,3) then desca else #{desc} end), " +
            "`action`=(case when `action` in (2,3) then `action` else 1 end) " +
            "where id=#{id}")
    Integer submitAgainst(String id, String desc);

    // 学生撤回一项学分申请异议
    @Update("update authentication_score set `action`=(case when `action`=1 then 0 else `action` end) where id=#{id}")
    Integer recallAgainst(String id);

    // 教师端获取所有异议状态申请列表
    @Select("select id, `name`, `action`, desca, descb from " +
            "(authentication_score left join " +
            "(select userid, `name`, `year` from user_info) tb " +
            "on substring(id,14)=tb.userid) " +
            "where `year`=(select `year` from user_info_manager where userid=#{userid}) and " +
            "(case when #{action}=1 then action=1 when #{action}=2 then action=2 when #{action}=3 then action=3 else (action in (1, 2, 3)) end) " +
            "order by id desc limit #{curPage}, #{pageSize}")
    List<HashMap<String, Object>>getAgainstList(int curPage, int pageSize, String userid, int action);

    // 与上面配套使用，查询异议状态申请总数
    @Select("select count(*) from " +
            "(authentication_score left join " +
            "(select userid, `year` from user_info) tb " +
            "on substring(id,14)=tb.userid) " +
            "where `year`=(select `year` from user_info_manager where userid=#{userid}) and " +
            "(case when #{action}=1 then action=1 when #{action}=2 then action=2 when #{action}=3 then action=3 else (action in (1, 2, 3)) end)")
    Integer getAgainstListTotal(String userid, int action);

    // 教师通过/驳回学分申请异议，并增加一次处理次数
    @Update("update authentication_score set action=#{action}, descb=#{descb} where id=#{id}")
    Integer modifyAgainst(String id, String descb, int action);

    // 教师端获取某一个学分申请的详细信息
    @Select("select id,name,imgurl,ruleid,levelstr,state from authentications where id=#{id}")
    HashMap<String, Object> getCertainAuthenticationById(String id);

    // 查询某个加分申请的分数
    @Select("select score from authentication_score where id=#{id}")
    Float getCertainAuthenticationByIdWithScore(String id);

    // 教师修改用户学分申请并审核通过
    @Update("update authentications set name=#{name},checkid=#{checkid},checktime=#{checktime},state=#{state},ruleid=#{ruleid},levelstr=#{levelstr} where id=#{id}")
    Integer checkAuthenticationPass(String name, String checkid, String checktime, int state, String ruleid, String levelstr, String id);

    // 教师修改用户学分申请并审核通过
    @Update("update authentications set name=#{name},checkid=#{checkid},checktime=#{checktime},ruleid=#{ruleid},levelstr=#{levelstr} where id=#{id}")
    Integer checkAuthenticationPassModify(String name, String checkid, String checktime, String ruleid, String levelstr, String id);

    // 教师审核通过材料之后，设置材料的分数
    @Insert("insert into authentication_score value(#{id}, #{score}, 0, '', '', 0)")
    Integer addNewMaterialScore(String id, Float score);

    // 教师更新加分申请对应的分数
    @Update("update authentication_score set score=#{score} where id=#{id}")
    Integer updateMaterialScore(String id, Float score);

    // 教师审核不通过某一项学分申请，删除学分信息数据
    @Delete("delete from authentications where id=#{id}")
    Integer checkAuthenticationNotPassA(String id);

    // 与上面配套，删除分值数据
    @Delete("delete from authentication_score where id=#{id}")
    Integer checkAuthenticationNotPassB(String id);

    // 检查学分申请状态并决定是否需要删除
    @Select("select state from authentications where id=#{id}")
//    @Delete("delete from authentications where id=#{id} and state=0")
    Integer checkAuthenticationState(String id);

    // 与上面的配套使用
    @Select("select imgurl from authentications where id=#{id}")
    String getAuthenticationImgUrlById(String id);

    // 根据赛事id获取加分配置（id获取赛事内容，赛事内容又包含typea和typeb）
    @Select("select classconfig from contests_config where uuid=(select ruleid from authentications where id=#{id})")
    String getClassConfigByType(String id);

    // 教师审核不通过学分申请之后，向学生发送通知
    @Insert("insert into notifys values(#{id}, #{title}, #{detail}, 0)")
    int addNotifyWhenCheckNotPassed(String id, String title, String detail);

    // 教师端导出学生评价结果文件
    @Select("select '学号' as id, '姓名' as name, convert('学业成绩' using gbk) as score_main, convert('其他学分' using gbk) as score_other, convert('手动加分' using gbk) as score_plus, convert('手动扣分' using gbk) as score_sub, convert('综合成绩' using gbk) as score_final, convert('班级排名' using gbk) as rank union select * from " +
            "(select convert(id using gbk) as id, name, score_main, score_other, score_plus, score_sub, score_final, (@rowno:=@rowno+1) as rank from " +
            "(select tmain.id, convert(name using gbk) as name, tmain.score_main, ifnull(tother.score_other,0) score_other, ifnull(tf.score_plus,0) as score_plus, ifnull(te.score_sub,0) as score_sub, (truncate((tmain.score_main+ifnull(tother.score_other,0)),4)+ifnull(tf.score_plus,0)-ifnull(te.score_sub,0)) as score_final from " +
            "(select id,year,truncate(sum(grade*score)/sum(score),4)*(select weight from weights_config where type=2) as score_main from grades where year=#{year} group by id) tmain " +
            "left join " +
            "(select substring(ta.id, 14) as id, sum(tb.score) score_other from authentications ta " +
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
            "order by score_final desc, tmain.score_main desc, tmain.id desc) te, (select (@rowno:=0)) tf) tg " +
            "into outfile #{fileName}")
    Integer exportResultResultFile(String year, String majorCode, String managerId, String fileName);

}
