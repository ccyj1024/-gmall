package com.atguigu.gmall.pms.service.impl;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.pms.dao.SpuInfoDao;
import com.atguigu.gmall.pms.dao.SpuInfoDescDao;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuInfoDescEntity;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.pms.service.ProductAttrValueService;
import com.atguigu.gmall.pms.service.SkuInfoService;
import com.atguigu.gmall.pms.service.SpuInfoService;
import com.atguigu.gmall.pms.vo.ProductAttrValueVO;
import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    private SpuInfoDescDao spuInfoDescDao;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;



    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo querySpuByKeyPage(QueryCondition queryCondition, Long catId) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();

        if(catId != 0 ){
            queryWrapper.eq("catelog_id",catId);
        }

        String key = queryCondition.getKey();
        if(StringUtils.isNotBlank(key)){
            queryWrapper.and(t->{
                return t.eq("id",key).or().like("spu_name",key);
            });
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(queryCondition),
                queryWrapper
        );


        return new PageVo(page);
    }

    @Override
    @GlobalTransactional//(propagation = Propagation.REQUIRED)
    public void saveSpuWithSku(SpuInfoVO spuInfoVO) {
        // 1. 保存spu相关的信息
        // 1.1.  保存spuInfo
        Long spuId = saveSpuInfo(spuInfoVO);

        //保存图片
        saveSpuInfoDesc(spuInfoVO, spuId);

        //保存product_attr_value == baseAttrs
        saveBaseAttr(spuInfoVO, spuId);

        //保存Sku
        skuInfoService.saveSkuinfo(spuInfoVO,spuId);

       int i = 1/0;

    }


    @Transactional
    public void saveBaseAttr(SpuInfoVO spuInfoVO, Long spuId) {
        List<ProductAttrValueVO> baseAttrs = spuInfoVO.getBaseAttrs();
        if (!CollectionUtils.isEmpty(baseAttrs)) {
            List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(baseAttr -> {
                ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                productAttrValueEntity.setAttrId(baseAttr.getAttrId());
                productAttrValueEntity.setAttrName(baseAttr.getAttrName());
                productAttrValueEntity.setSpuId(spuId);
                productAttrValueEntity.setAttrValue(baseAttr.getAttrValue());
                productAttrValueEntity.setAttrSort(1);
                productAttrValueEntity.setQuickShow(0);
                return productAttrValueEntity;
            }).collect(Collectors.toList());
            productAttrValueService.saveBatch(productAttrValueEntities);
        }
    }
    @Transactional
    public void saveSpuInfoDesc(SpuInfoVO spuInfoVO, Long spuId) {
        if (!CollectionUtils.isEmpty(spuInfoVO.getSpuImages())) {
            SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
            spuInfoDescEntity.setSpuId(spuId);
            spuInfoDescEntity.setDecript(StringUtils.join(spuInfoVO.getSpuImages(), ","));
            this.spuInfoDescDao.insert(spuInfoDescEntity);
        }
    }
    @Transactional
    public Long saveSpuInfo(SpuInfoVO spuInfoVO){
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfoVO, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUodateTime(spuInfoEntity.getCreateTime());
        this.save(spuInfoEntity);
        return spuInfoEntity.getId();
    }

}