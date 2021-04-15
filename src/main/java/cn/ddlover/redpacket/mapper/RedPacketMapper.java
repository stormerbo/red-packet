package cn.ddlover.redpacket.mapper;

import cn.ddlover.redpacket.entity.RedPacket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2021/4/15 14:47
 */
@Mapper
public interface RedPacketMapper {

  int  insert(RedPacket redPacket);

  RedPacket queryById(@Param("redPacketId")Integer redPacketId);

  int updateById(RedPacket redPacket);
}
