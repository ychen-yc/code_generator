package com.qxs.generator.web.service.log.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.qxs.generator.web.exception.BusinessException;
import com.qxs.generator.web.model.GenerateResult;
import com.qxs.generator.web.model.connection.Database;
import com.qxs.generator.web.model.connection.GenerateParameter;
import com.qxs.generator.web.model.connection.Ssh;
import com.qxs.generator.web.model.log.Generate;
import com.qxs.generator.web.model.user.User;
import com.qxs.generator.web.repository.log.IGenerateRepository;
import com.qxs.generator.web.service.IGeneratorService;
import com.qxs.generator.web.service.log.IGenerateService;
import com.qxs.generator.web.util.EncryptUtil;

@Service
public class GenerateServiceImpl implements IGenerateService {
	
	@Autowired
	private IGenerateRepository generateRepository;
	
	@Autowired
	private IGeneratorService generatorService;

	@Transactional
	@Override
	public String insert(Generate generate) {
		generate.setUserId(getUserId());
		
		return generateRepository.saveAndFlush(generate).getId();
	}
	
	@Transactional
	@Override
	public Generate getById(String id) {
		Generate generate = generateRepository.getOne(id);
		generate.setGenerateParameterDatabase(formatJson(generate.getGenerateParameterDatabase()));
		if(StringUtils.hasLength(generate.getGenerateParameterSsh())) {
			generate.setGenerateParameterSsh(formatJson(generate.getGenerateParameterSsh()));
		}
		
		generate.setGenerateParameterParameter(formatJson(generate.getGenerateParameterParameter()));
		return generate;
	}
	
	private String formatJson(String json) {
		JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
	}

	@Transactional
	@Override
	public Page<Generate> findList(String search, Integer offset, Integer limit, String sort, String order) {
		Sort s = Sort.unsorted();
		Pageable pageable = null;
		if(StringUtils.hasLength(sort)) {
			s = new Sort(Direction.fromOptionalString(order).orElse(Direction.ASC), sort);
		}
		if(offset != null) {
			pageable = PageRequest.of(offset / limit, limit, s);
		}
		
		Generate generate = new Generate();
		generate.setUserId(getUserId());
		
		return StringUtils.isEmpty(search) ? 
				generateRepository.findAll(Example.of(generate),pageable) : generateRepository.findAll(
						new Specification<Generate>() {
							
							private static final long serialVersionUID = -3066008312509276295L;

							@Override
							public Predicate toPredicate(Root<Generate> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
								List<Predicate> predicate = new ArrayList<>();
				                if(StringUtils.hasLength(search)){
				                    //生成代码开始时间
				                    predicate.add(cb.like(root.get("generateStartDate").as(String.class), "%"+search+"%"));
				                    //生成代码结束时间
				                    predicate.add(cb.like(root.get("generateStopDate").as(String.class), "%"+search+"%"));
				                    //生成代码的数据库参数(密码为加密之后的)
				                    predicate.add(cb.like(root.get("generateParameterDatabase").as(String.class), "%"+search+"%"));
				                    //生成代码的ssh连接参数(密码为加密之后的)
				                    predicate.add(cb.like(root.get("generateParameterSsh").as(String.class), "%"+search+"%"));
				                    //生成代码的生成配置参数(密码为加密之后的)
				                    predicate.add(cb.like(root.get("generateParameterParameter").as(String.class), "%"+search+"%"));
				                    //失败原因
				                    predicate.add(cb.like(root.get("failReason").as(String.class), "%"+search+"%"));
				                }
				                
				                Predicate userPredicate = cb.like(root.get("userId").as(String.class), generate.getUserId());
				                
				                return query.where(cb.and(userPredicate), cb.or(predicate.toArray(new Predicate[predicate.size()]))).getRestriction();
							}
						}, pageable);
	}
	
	@Override
	public GenerateResult generate(String id) {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		User user = (User) authentication.getPrincipal();
		
		Generate generate = getById(id);
		Gson gson = new Gson();
		
		Database database = gson.fromJson(generate.getGenerateParameterDatabase(), Database.class);
		Ssh ssh = gson.fromJson(generate.getGenerateParameterSsh(), Ssh.class);
		GenerateParameter generateParameter = gson.fromJson(generate.getGenerateParameterParameter(), GenerateParameter.class);
		
		//处理密码参数
		if(StringUtils.hasLength(database.getPassword())) {
			database.setPassword(EncryptUtil.desDecode(database.getPassword(), user.getId()));
		}
		if(ssh != null && StringUtils.hasLength(ssh.getPassword())) {
			ssh.setPassword(EncryptUtil.desDecode(ssh.getPassword(), user.getId()));
		}
		
		return generatorService.generate(database, ssh, generateParameter, false);
	}

	private String getUserId() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
			
		Authentication authentication = securityContext.getAuthentication();
		
		if(authentication == null) {
			throw new BusinessException("未获取到登录用户");
		}
		
		User user = (User) authentication.getPrincipal();
		
		return user.getId();
	}
}
