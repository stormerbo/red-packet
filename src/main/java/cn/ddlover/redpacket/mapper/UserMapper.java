package cn.ddlover.redpacket.mapper;

import cn.ddlover.redpacket.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2021/4/15 14:45
 */
@Mapper
public interface UserMapper {
  User queryByUserId(@Param("userId")Integer userId);

  void updateUser(User user);
}
