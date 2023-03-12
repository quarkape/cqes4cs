package club.hue.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Mapper
@Repository
public interface PushMapper {

    // 获取自己的所有的通知
    @Select("select * from notifys where substring(id, 14)=#{userid} order by id desc")
    List<HashMap<String, Object>> getAllNotifys(String userid);

    // 标记通知为已读状态
    @Update("update notifys set state=1 where id=#{id}")
    Integer readNotify(String id);

    // 标记全部通知为已读状态
    @Update("update notifys set state=1 where substring(id, 14, 15)=#{userid}")
    Integer readAllNotify(String userid);

    // 用户自己修改密码的时候发送一则通知
    @Insert("insert into notifys values(#{id},#{title},#{detail},0)")
    Integer addOneNotify(String id, String title, String detail);

    // 删除所有的通知（个人中心的通知）
    @Delete("delete from notifys where substring(id, 14)=#{userid}")
    Integer deleteAllNotify(String userid);

    // 删除一则通知（个人中心的通知）
    @Delete("delete from notifys where id=#{id}")
    int deleteOneNotify(String id);
}