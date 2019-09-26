package com.atguigu.gmall.pms.service.impl;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.dao.AttrGroupDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import com.atguigu.gmall.pms.vo.AttrgroupVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    AttrAttrgroupRelationDao relationDao;
    @Autowired
    AttrDao attrDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryAttrGroupByCid(Long catId, QueryCondition queryCondition) {
        QueryWrapper<AttrGroupEntity> pageVoQueryWrapper = new QueryWrapper<>();
        if(pageVoQueryWrapper != null){
            pageVoQueryWrapper.eq("catelog_id",catId);
        }
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(queryCondition),pageVoQueryWrapper
        );

        return new PageVo(page);
    }

    @Override
    public AttrgroupVO queryGroupWithAttrByGid(Long gid) {

        AttrgroupVO attrgroupVO = new AttrgroupVO();

        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(gid);
        BeanUtils.copyProperties(attrGroupEntity,attrgroupVO);

        List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", gid));


        if(CollectionUtils.isEmpty(relationEntities)){
            return attrgroupVO;
        }
        attrgroupVO.setRelations(relationEntities);
        List<Long> ids=relationEntities.stream().map(relationEntity -> relationEntity.getAttrId()).collect(Collectors.toList());
        List<AttrEntity> attrEntities = attrDao.selectBatchIds(ids);
        attrgroupVO.setAttrEntities(attrEntities);
        return attrgroupVO;
    }

    @Override
    public List<AttrgroupVO> queryGroupWithAttrByCid(Long catId) {
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catId));
        if (CollectionUtils.isEmpty(attrGroupEntities)) {
            return null;
        }
       return attrGroupEntities.stream().map(attrGroup -> {
            return this.queryGroupWithAttrByGid(attrGroup.getAttrGroupId());
        }).collect(Collectors.toList());

    }

}