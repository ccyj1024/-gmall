package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.AttrgroupVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 属性分组
 *
 * @author ccyj
 * @email ccyj1024@126.com
 * @date 2019-09-21 13:20:07
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo queryAttrGroupByCid(Long catId, QueryCondition queryCondition);

    AttrgroupVO queryGroupWithAttrByGid(Long gid);

    List<AttrgroupVO> queryGroupWithAttrByCid(Long catId);
}

