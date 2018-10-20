package top.goingtop.dao;

import java.util.List;
import java.util.Map;

import top.goingtop.pojo.User;
public interface UserDao {

	/**
	 * 通过id查询用户实体
	 * @param id
	 * @return
	 */
	public User findById(String id);
	/**
	 * 更新用户信息
	 * @param user
	 * @return
	 */
	public int update(User user);
	/**
	 * 根据条件查询用户集合
	 * @param map
	 * @return
	 */
	public List<User> find(Map<String , Object> map);
	/**
	 * 根据条件获取总记录数
	 * @param map
	 * @return
	 */
	public Long getTotal(Map<String, Object> map);
	/**
	 * 删除用户
	 * @param id
	 * @return
	 */
	public int delete(String id);
	/**
	 * 添加用户
	 * @param user
	 * @return
	 */
	public int add(User user);
}
