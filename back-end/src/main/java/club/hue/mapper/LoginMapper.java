package club.hue.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.HashMap;


@Mapper
@Repository
public interface LoginMapper {

    // 通过用户id获取用户对象，用来做用户名是否存在的判断
    @Select("select userid,password,role from users where userid=#{userid}")
    HashMap<String, Object> getUserByUserid(String userid);

    // 获取登陆后显示未读的通知的数量
    @Select("select count(*) from notifys where substring(id, 14, 15)=#{userid} and state=0")
    Integer getUnreadNotifyCount(String userid);
}
