package cn.sparrowmini.common.service;


import cn.sparrowmini.common.constant.PermissionEnum;
import cn.sparrowmini.common.model.ModelAttributeId;

public interface ModelPermissionService {

	/**
	 * 增加规则引擎的判断
	 * 
	 * @param modelId
	 * @param permission
	 * @param username
	 * @param entity
	 * @return
	 */
	boolean hasPermission(String modelId, PermissionEnum permission, String username, Object entity);


	/**
	 * 增加规则引擎的判断
	 * 
	 * @param attributePK
	 * @param permission
	 * @param username
	 * @param entity
	 * @return
	 */
	boolean hasPermission(ModelAttributeId attributePK, PermissionEnum permission, String username, Object entity);
}
