package com.atguigu.gmall.oms.dao;

import com.atguigu.gmall.oms.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author ccyj
 * @email ccyj1024@126.com
 * @date 2019-09-21 13:51:10
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
