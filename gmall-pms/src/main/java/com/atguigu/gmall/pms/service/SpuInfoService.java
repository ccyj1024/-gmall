package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * spu信息
 *
 * @author ccyj
 * @email ccyj1024@126.com
 * @date 2019-09-21 13:20:07
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo querySpuByKeyPage(QueryCondition queryCondition, Long catId);

    void saveSpuWithSku(SpuInfoVO spuInfoVO);


    void saveBaseAttr(SpuInfoVO spuInfoVO, Long spuId);

    void saveSpuInfoDesc(SpuInfoVO spuInfoVO, Long spuId);

    Long saveSpuInfo(SpuInfoVO spuInfoVO);
}

