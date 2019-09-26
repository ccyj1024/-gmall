package com.atguigu.gmall.pms.service.impl;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.pms.dao.SpuInfoDao;
import com.atguigu.gmall.pms.dao.SpuInfoDescDao;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsFeign;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.ProductAttrValueVO;
import com.atguigu.gmall.pms.vo.SkuInfoVO;
import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.atguigu.gmall.sms.vo.SkuSaleVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    private SpuInfoDescDao spuInfoDescDao;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private GmallSmsFeign smsFeign;
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
    public void saveSpuWithSku(SpuInfoVO spuInfoVO) {
        // 1. 保存spu相关的信息
        // 1.1.  保存spuInfo
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfoVO, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUodateTime(spuInfoEntity.getCreateTime());
        this.save(spuInfoEntity);
        Long spuId = spuInfoEntity.getId();

        //保存图片
        if (!CollectionUtils.isEmpty(spuInfoVO.getSpuImages())) {
            SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
            spuInfoDescEntity.setSpuId(spuId);
            spuInfoDescEntity.setDecript(StringUtils.join(spuInfoVO.getSpuImages(), ","));
            this.spuInfoDescDao.insert(spuInfoDescEntity);
        }

        //保存product_attr_value == baseAttrs
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

        //保存Sku
        List<SkuInfoVO> SkuInfoVOS = spuInfoVO.getSkus();
        if(CollectionUtils.isEmpty(SkuInfoVOS)){
            return;
        }
        SkuInfoVOS.forEach(skuInfoVO -> {
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(skuInfoVO,skuInfoEntity);
            skuInfoEntity.setSpuId(spuId);
            List<String> images = skuInfoVO.getImages();
            if(!CollectionUtils.isEmpty(images)){
                skuInfoEntity.setSkuDefaultImg(StringUtils.isEmpty(skuInfoEntity.getSkuDefaultImg())?images.get(0):skuInfoEntity.getSkuDefaultImg());
            }
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString().toUpperCase());
            skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
            skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
            skuInfoService.save(skuInfoEntity);
            Long skuId = skuInfoEntity.getSkuId();


            //保存图片
            if(!CollectionUtils.isEmpty(images)){
                List<SkuImagesEntity> skuImagesEntities = images.stream().map(image ->{
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(image);
                    skuImagesEntity.setImgSort(1);
                    skuImagesEntity.setDefaultImg(StringUtils.equals(skuInfoEntity.getSkuDefaultImg(),image)?1:0);
                    return skuImagesEntity;
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(skuImagesEntities);
            }

            //sku属性
            List<SkuSaleAttrValueEntity> saleAttrs = skuInfoVO.getSaleAttrs();
            if(!CollectionUtils.isEmpty(saleAttrs)){
                saleAttrs.forEach(saleAttr ->{
                    saleAttr.setSkuId(skuId);
                    saleAttr.setAttrSort(1);
                });
                skuSaleAttrValueService.saveBatch(saleAttrs);
            }

            //优惠
            SkuSaleVO skuSaleVO = new SkuSaleVO();
            BeanUtils.copyProperties(skuInfoVO,skuSaleVO);
            skuSaleVO.setSkuId(skuId);
            smsFeign.saveSale(skuSaleVO);


        });


    }

}