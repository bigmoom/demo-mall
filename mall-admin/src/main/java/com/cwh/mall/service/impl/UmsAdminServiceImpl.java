package com.cwh.mall.service.impl;

import com.cwh.mall.bo.UserDetail;
import com.cwh.mall.common.config.BaseKeyGenerator;
import com.cwh.mall.common.util.RequestUtil;
import com.cwh.mall.config.UmsAdminNameKeyGenerator;
import com.cwh.mall.dao.UmsAdminRoleResourceMapper;
import com.cwh.mall.dto.UmsAdminLoginParam;
import com.cwh.mall.dto.UmsAdminParam;
import com.cwh.mall.mbg.mapper.UmsAdminLoginLogMapper;
import com.cwh.mall.mbg.model.UmsAdminLoginLog;
import com.cwh.mall.mbg.model.UmsResource;
import com.cwh.mall.security.component.JWTManager;
import com.cwh.mall.service.UmsAdminService;
import com.cwh.mall.mbg.mapper.UmsAdminMapper;
import com.cwh.mall.mbg.model.UmsAdmin;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author cwh
 * @date 2021/7/13 11:04
 */
@Service
@Slf4j
public class UmsAdminServiceImpl implements UmsAdminService {

    @Autowired
    private UmsAdminMapper umsAdminMapper;

    @Autowired
    private UmsAdminRoleResourceMapper umsAdminRoleResourceMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UmsAdminNameKeyGenerator umsAdminNameKeyGenerator;

    @Autowired
    private JWTManager jwtManager;

    @Autowired
    private UmsAdminLoginLogMapper umsAdminLoginLogMapper;
    /**
     * 通过用户名获取用户
     * @param username
     * @return
     */
    @Override
    @Cacheable(cacheNames = "umsAdmin" ,keyGenerator = "umsAdminNameKeyGenerator")
    public UmsAdmin getAdminByUsername(String username) {
        UmsAdmin umsAdmin = umsAdminMapper.selectByUsername(username);
        if(umsAdmin == null){
            throw new UsernameNotFoundException("用户没有找到");
        }
        return umsAdmin;
    }

    /**
     * 用以实现UserDetailService
     * @param username
     * @return
     */
    @Cacheable(cacheNames = "userDetail" ,keyGenerator = "umsAdminNameKeyGenerator")
    @Override
    public UserDetails loadUserByUsername(String username) {
        //获取用户
        UmsAdmin umsAdmin = this.getAdminByUsername(username);
        //获取用户所拥有的权限
        Set<SimpleGrantedAuthority> authorities = getResourceList(umsAdmin.getId()).stream()
                .map(role -> new SimpleGrantedAuthority(role.getId()+":"+role.getName()))
                .collect(Collectors.toSet());


        //返回UserDetails实现对象即可
        return new UserDetail(umsAdmin,authorities);

    }

    /**
     * 获取用户对应角色权限所拥有的资源
     * @param adminId
     * @return
     */
    @Cacheable(cacheNames = "resourceList", key = "#p0")
    @Override
    public List<UmsResource> getResourceList(Long adminId) {

        List<UmsResource> resourceList = umsAdminRoleResourceMapper.getResourceList(adminId);

        return resourceList;
    }

    /**
     * 用户注册，通过复制umsAdminParam中的属性并添加创建时间
     * 对密码进行加密后存入数据库
     * @param umsAdminParam
     * @return
     */
    @Override
    @CachePut(cacheNames = "umsAdmin", key = "#p0.username")
    public UmsAdmin register(UmsAdminParam umsAdminParam) {
        UmsAdmin umsAdmin = new UmsAdmin();
        BeanUtils.copyProperties(umsAdminParam,umsAdmin);
        umsAdmin.setCreateTime(new Date());
        umsAdmin.setStatus(1);

        String encodePassword = passwordEncoder.encode(umsAdmin.getPassword());
        umsAdmin.setPassword(encodePassword);
        umsAdminMapper.insert(umsAdmin);
        return umsAdmin;


    }

    @Override
    @Cacheable(cacheNames = "umsAdmin", key = "#p0")
    public UmsAdmin getAdminById(Long id) {
        return umsAdminMapper.selectByPrimaryKey(id);
    }

    @Override
    public String login(UmsAdminLoginParam umsAdminLoginParam) {
        String username = umsAdminLoginParam.getUsername();
        String password = umsAdminLoginParam.getPassword();

        String token = null;
        //进行密码校验
        try{
            UserDetails userDetails = this.loadUserByUsername(username);
            if(!passwordEncoder.matches(password,userDetails.getPassword())){
                throw new BadCredentialsException("密码错误");
            }
            //判断用户账号状态是否为可用状态
            if(!userDetails.isEnabled()){
                throw new DisabledException("账户不可用");
            }
            //设置上下文中authentication
            //密码不明文放在上下文中，所以设置为Null
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            token = jwtManager.generateToken(username);

            //添加登录记录
            insertLoginRecord(username);
        }catch (AuthenticationException e){
            log.error("登录异常：{}",e.getMessage());
        }

        return token;

    }

    /**
     * 添加用户的登录记录
     * （adminID,createTime,ip,address,agent）
     * @param username
     */
    private void insertLoginRecord(String username){
        //获取用户信息
        UmsAdmin umsAdmin = getAdminByUsername(username);
        //设置登录信息
        UmsAdminLoginLog umsAdminLoginLog = new UmsAdminLoginLog();
        umsAdminLoginLog.setAdminId(umsAdmin.getId());
        umsAdminLoginLog.setCreateTime(new Date());
        //通过RequestUtil获取ip
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        umsAdminLoginLog.setIp(RequestUtil.getRequestIp(request));
        //获取用户登录agent
        umsAdminLoginLog.setUserAgent(request.getHeader("User-Agent"));

        umsAdminLoginLogMapper.insert(umsAdminLoginLog);

    }

    /**
     * 更新用户数据
     * @param umsAdmin
     * @return 返回是否成功
     */
    @Override
    @Caching (
        put = {
            @CachePut(cacheNames = "umsAdmin", key = "#p0.username"),
            @CachePut(cacheNames = "umsAdmin", key = "#p0.id")
        }
    )
    public Boolean updateUmsAdmin(UmsAdmin umsAdmin) {
        //id错误，找不到该用户信息
        if(umsAdminMapper.selectByPrimaryKey(umsAdmin.getId())==null){
            throw new UsernameNotFoundException("找不到该用户");
        }else {
            String encodePassword = passwordEncoder.encode(umsAdmin.getPassword());
            umsAdmin.setPassword(encodePassword);
            return umsAdminMapper.updateByPrimaryKey(umsAdmin)>0;
        }
    }

    /**
     * 根据用户id删除用户
     * @param id
     * @return
     */
    @Override
    @CacheEvict(cacheNames = "umsAdmin", key = "#p0")
    public Boolean deleteUmsAdmin(Long id) {
        return umsAdminMapper.deleteByPrimaryKey(id)>0;
    }


}
