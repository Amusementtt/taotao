package com.taotao.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.taotao.common.utils.JsonUtils;
import com.taotao.jedis.JedisClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentService;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
@Service
public class ContentServiceImpl implements ContentService {
	@Autowired
	private JedisClient jedisClient;
	@Autowired
	private TbContentMapper tbContentMapper;
	@Value("${CONTENT_KEY}")
	private String CONTENT_KEY;
	@Override
	public EasyUIDataGridResult findContentAll(long categoryId) {
		// TODO Auto-generated method stub
		List<TbContent> tbContents = tbContentMapper.findTbContentAll(categoryId);
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setTotal(tbContents.size());
		result.setRows(tbContents);
		return result;
	}
	
	@Override
	public TaotaoResult addContent(TbContent tbContent) {
		// TODO Auto-generated method stub
		jedisClient.hdel(CONTENT_KEY,tbContent.getCategoryId()+"");
		Date date = new Date();
		tbContent.setCreated(date);
		tbContent.setUpdated(date);
		tbContentMapper.addContent(tbContent);
		return TaotaoResult.ok();
	}

	@Override
	public List<TbContent> getTbContent(long categoryId) {
		// TODO Auto-generated method stub
		//取缓存
		String json = jedisClient.hget(CONTENT_KEY,categoryId+"");
		if(StringUtils.isNotBlank(json)){
			List<TbContent> result = JsonUtils.jsonToList(json,TbContent.class);
			System.out.println("从缓存中取");
			return result;
		}
		List<TbContent> tbContents = tbContentMapper.findTbContentAll(categoryId);
		//加缓存
		try {
			jedisClient.hset(CONTENT_KEY,categoryId+"",JsonUtils.objectToJson(tbContents));
			System.out.println("加入缓存");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tbContents;
	}

}
