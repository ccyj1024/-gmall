package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author ccyj
 * @date 2019/9/24 - 19:10
 */
@Data
public class AttrgroupVO extends AttrGroupEntity implements Serializable {
    private List<AttrEntity> attrEntities;
    private List<AttrAttrgroupRelationEntity> relations;

}
