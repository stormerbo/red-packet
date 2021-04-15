package cn.ddlover.redpacket.mapper;

import cn.ddlover.redpacket.entity.RedPacketRecord;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2021/4/15 14:56
 */
@Mapper
public interface RedPacketRecordMapper {

  void insert(RedPacketRecord redPacketRecord);


  void batchInsert(@Param("list") List<RedPacketRecord> redPacketRecordList);

  int countBySerialNo(Long serialNo);
}
