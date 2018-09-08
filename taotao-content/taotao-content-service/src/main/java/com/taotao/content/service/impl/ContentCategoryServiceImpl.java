package com.taotao.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
	@Autowired
	private TbContentCategoryMapper tbContentCategoryMapper;
	@Override
	public List<EasyUITreeNode> getContentCategoryList(long parentId) {
		// TODO Auto-generated method stub
		List<TbContentCategory> contentCategories = tbContentCategoryMapper.getTbContentCategoryByParentId(parentId);
		List<EasyUITreeNode> result = new ArrayList();
		for(TbContentCategory contentCategory:contentCategories){
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(contentCategory.getId());
			node.setText(contentCategory.getName());
			node.setState(contentCategory.getIsParent()?"closed":"open");
			result.add(node);
		}
		return result;
	}
	@Override
	public TaotaoResult addContentCategory(long parentId,String name) {
		// TODO Auto-generated method stub
		TbContentCategory category = new TbContentCategory();
		category.setParentId(parentId);
		category.setName(name);
		category.setSortOrder(1);
		category.setStatus(1);
		category.setIsParent(false);
		Date date = new Date();
		category.setCreated(date);
		category.setUpdated(date);
		tbContentCategoryMapper.addContentCategory(category);
		TbContentCategory contentCategory = tbContentCategoryMapper.getTbContentCategoryById(parentId);
		if(!contentCategory.getIsParent()){
			contentCategory.setId(parentId);
			contentCategory.setIsParent(true);
			tbContentCategoryMapper.updateTbContentCategory(contentCategory);
		}
		//回传给页面显示
		return TaotaoResult.ok(category);
	}

}
