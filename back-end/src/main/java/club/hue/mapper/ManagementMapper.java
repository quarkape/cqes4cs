package club.hue.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Mapper
@Repository
public interface ManagementMapper {

    // 教师和学生修改自己的密码
    @Update("update users set password=#{password} where userid=#{userid}")
    Integer changePassword(String password, String userid);

    // 教师端重置学生的密码
    @Update("update users set password=#{password} where userid=#{id}")
    Integer resetPassword(String password, String id);

    // 获取自己的基本资料
    @Select("select userid, name, major_name, class from user_info ta " +
            "left join " +
            "(select major_code, major_name from majors) tb " +
            "on ta.major_code=tb.major_code " +
            "where ta.userid=#{userid}")
    HashMap<String, Object> getUserBasicInfo(String userid);

    // 删除教师账号
    @Delete("delete users, user_info_manager from users, user_info_manager where users.userid=user_info_manager.userid and users.userid=#{userid}")
    int deleteAccountManager(String userid);

    // 获取学生列表
    @Select("select ta.userid,role,name,tb.class from users ta " +
            "left join " +
            "(select userid, name, year,class,major_code from user_info) tb " +
            "on ta.userid=tb.userid " +
            "where ta.role='student' and tb.year=(select year from user_info_manager where userid=#{userid}) and tb.major_code=#{major}" +
            "order by userid asc " +
            "limit #{curPage}, #{pageSize}")
    List<HashMap<String, Object>> getAllStudentList(String userid, String major, int curPage, int pageSize);

    // 获取学生数量，与上面的配套使用
    @Select("select count(*) from users ta " +
            "left join " +
            "(select userid, name, year,class,major_code from user_info) tb " +
            "on ta.userid=tb.userid " +
            "where ta.role='student' and tb.year=(select year from user_info_manager where userid=#{userid}) and tb.major_code=#{major}")
    int getStudentNumByMajor(String userid, String major);

    // 给学生手动加分/减分
    @Insert("insert into score_extra values(#{uid},#{stuId},#{year},#{score},#{type},#{desc})")
    Integer modifyScoreManually(String uid, String stuId, String year, double score, int type, String desc);

    // 修改教师的管理年级
    @Update("update user_info_manager set year=#{year} where userid=#{userid}")
    int alterManagerYear(String userid, String year);

    // 新增一个教师账号
    @Insert("insert into users values(#{userid}, #{password}, 'manager')")
    int addManagerToUsers(String userid, String password);

    // 新增一个管理员账号的基本信息
    @Insert("insert into user_info_manager values(#{userid}, #{name}, #{year})")
    int addManagerToUserInfo(String userid, String name, String year);

}

















