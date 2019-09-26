package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import lombok.Data;

import java.util.List;

/**
 * @author ccyj
 * @date 2019/9/25 - 21:05
 */
@Data
public class SpuInfoVO extends SpuInfoEntity {
    private List<String> spuImages;
    private List<ProductAttrValueVO> baseAttrs;
    private List<SkuInfoVO> skus;
}
