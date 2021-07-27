package com.cwh.mall.service.impl;

import com.cwh.mall.bo.UserDetail;
import com.cwh.mall.common.annotation.CheckRedisConnnection;
import com.cwh.mall.common.config.BaseKeyGenerator;
import com.cwh.mall.common.util.RequestUtil;
import com.cwh.mall.config.UmsAdminIdKeyGenerator;
import com.cwh.mall.config.UmsAdminNameKeyGenerator;
import com.cwh.mall.dao.UmsAdminRoleResourceMapper;
import com.cwh.mall.dto.UmsAdminLoginParam;
import com.cwh.mall.dto.UmsAdminParam;
import com.cwh.mall.mbg.mapper.UmsAdminLoginLogMapper;
import com.cwh.mall.mbg.model.UmsAdminLoginLog;
import com.cwh.mall.mbg.model.UmsMenu;
import com.cwh.mall.mbg.model.UmsResource;
import com.cwh.mall.security.component.JWTManager;
import com.cwh.mall.service.RefreshTokenRedisService;
import com.cwh.mall.service.UmsAdminRedisService;
import com.cwh.mall.service.UmsAdminService;
import com.cwh.mall.mbg.mapper.UmsAdminMapper;
import com.cwh.mall.mbg.model.UmsAdmin;
import com.github.pagehelper.PageHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author cwh
 * @date 2021/7/13 11:04
 */
@Service
@Slf4j
public class UmsAdminServiceImpl implements UmsAdminService {
    /**
     * 过期时间，单位为s
     */
    @Value("${security.jwt.expiration}")
    private Long expiration;

    @Value("${security.jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private UmsAdminMapper umsAdminMapper;

    @Autowired
    private UmsAdminRoleResourceMapper umsAdminRoleResourceMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UmsAdminNameKeyGenerator umsAdminNameKeyGenerator;

    @Autowired
    private UmsAdminIdKeyGenerator umsAdminIdKeyGenerator;

    @Autowired
    private JWTManager jwtManager;

    @Autowired
    private UmsAdminLoginLogMapper umsAdminLoginLogMapper;

    @Autowired
    private UmsAdminRedisService umsAdminRedisService;

    /**
     *自调用以避免方法内调用缓存不生效
     */
    @Autowired
    private UmsAdminServiceImpl umsAdminService;

    //@Autowired
    //private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RefreshTokenRedisService refreshTokenRedisService;

    @Value("${security.jwt.refreshTokenExpiration}")
    private Long refreshTokenExpiration;





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
        //umsAdminRedisService.setAdmin(umsAdmin);
        return umsAdmin;
    }

    /**
     * 用以实现UserDetailService
     * @param username
     * @return
     */
    //@Cacheable(cacheNames = "userDetail" ,keyGenerator = "umsAdminNameKeyGenerator")
    @Override
    public UserDetails loadUserByUsername(String username) {
        //获取用户
        UmsAdmin umsAdmin = umsAdminService.getAdminByUsername(username);
        //获取用户所拥有的权限
        Set<SimpleGrantedAuthority> authorities = umsAdminService.getResourceList(umsAdmin.getId()).stream()
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
    @Cacheable(cacheNames = "resourceList", keyGenerator = "umsAdminIdKeyGenerator")
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
    @CachePut(cacheNames = "umsAdmin", keyGenerator = "umsAdminNameKeyGenerator")
    public UmsAdmin register(UmsAdminParam umsAdminParam) {
        UmsAdmin umsAdmin = new UmsAdmin();
        BeanUtils.copyProperties(umsAdminParam,umsAdmin);
        umsAdmin.setCreateTime(new Date());
        umsAdmin.setStatus(1);

        String encodePassword = passwordEncoder.encode(umsAdmin.getPassword());
        umsAdmin.setPassword(encodePassword);
        umsAdminMapper.insert(umsAdmin);
        log.info("注册活动：用户-{}  时间-{}",umsAdmin.getUsername(),umsAdmin.getCreateTime());
        return umsAdmin;


    }

    /**
     * 根据用户id获取用户
     * @param id
     * @return
     */
    @Override
    @Cacheable(cacheNames = "umsAdmin", keyGenerator = "umsAdminIdKeyGenerator")
    public UmsAdmin getAdminById(Long id) {

        UmsAdmin umsAdmin = umsAdminMapper.selectByPrimaryKey(id);
        if(umsAdmin == null){
            throw new UsernameNotFoundException("未找到该用户");
        }else {
            return umsAdmin;
        }
    }

    /**
     * 登录操作
     * 添加登录记录
     * @param umsAdminLoginParam 包括用户名，密码
     * @return 返回token, refreshToken, 过期时间戳
     */
    @Override
    @CheckRedisConnnection
    public Map<String,Object> login(UmsAdminLoginParam umsAdminLoginParam) {
        String username = umsAdminLoginParam.getUsername();
        String password = umsAdminLoginParam.getPassword();

        String token = null;
        String refreshToken = null;
        Map<String,Object> tokenMap = new HashMap<>();
        Date expireDate = new Date(System.currentTimeMillis() + expiration*1000);
        //进行密码校验
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
        //refreshToken用于获取新的token
        refreshToken = jwtManager.generateRefreshToken(username);
        //将refreshToken存储在redis中，以保持更新状态
        StringJoiner stringJoiner = new StringJoiner(":");
        stringJoiner.add("refreshToken");
        stringJoiner.add(username);
        //redisTemplate.opsForValue().set(stringJoiner.toString(),refreshToken,refreshTokenExpiration,TimeUnit.SECONDS);

        refreshTokenRedisService.setRefreshToken(stringJoiner.toString(),refreshToken,refreshTokenExpiration,TimeUnit.SECONDS);
        //token过期时间,较短，在时间戳之前直接携带token访问功能接口
        // 时间戳之后携带refreshToken访问refresh接口获取新的token
        tokenMap.put("token",token);
        tokenMap.put("refreshToken", refreshToken);
        tokenMap.put("expireDate", expireDate);
        //检查是否已经存在refreshtoken
        //如果存在，则设置延长refreshtoken的过期时间
        //如果不存在，则创建refreshtoken并存储在redis中

        //StringJoiner stringJoiner = new StringJoiner(":");
        //stringJoiner.add("token");
        //stringJoiner.add(username);
        //
        //insertTokenToRedis(stringJoiner.toString(),tokenHead,token);

        //checkAndGenerateRefreshToken(username);
        //添加登录记录
        insertLoginRecord(username);

        return tokenMap;
    }

    /**
     * 检查是否存在refreshToken
     * 这里refreshToken不考虑jwt中的过期时间，以redis中的过期时间为准
     * 若不存在生成新refreshToken
     * 存在则延长过期时间
     * @param username
     */
    //private void checkAndGenerateRefreshToken(String username){
    //    //redis中存储的键
    //    StringJoiner stringJoiner = new StringJoiner(":");
    //    stringJoiner.add("refreshToken");
    //    stringJoiner.add(username);
    //    String key = stringJoiner.toString();
    //
    //    ValueOperations<String, String> valueOperations= redisTemplate.opsForValue();
    //
    //    String refreshToken = valueOperations.get(key);
    //    //不存在refreshToken，创建refreshToken
    //    if(refreshToken == null) {
    //        refreshToken = jwtManager.generateRefreshToken(username);
    //        //valueOperations.set(key,refreshToken,refreshTokenExpiration,TimeUnit.SECONDS);
    //    }else {
    //        try {
    //            Claims claims = jwtManager.parseRefreshToken(refreshToken);
    //            if(claims == null){
    //                //非法refreshToken，直接删除
    //                valueOperations.getOperations().delete(key);
    //            }
    //        }catch (ExpiredJwtException e){
    //        //    refreshToken过期,不予处理，以redis中过期时间为准
    //        }
    //    }
    //    //存在则直接延长过期时间
    //    valueOperations.set(key,refreshToken,refreshTokenExpiration,TimeUnit.SECONDS);
    //
    //}
    /**
     * 登出操作
     * 即设置redis中token值为null
     * @param username
     */
    @Override
    public void logout(String username){
        StringJoiner stringJoiner = new StringJoiner(":");
        stringJoiner.add("refreshToken");
        stringJoiner.add(username);
        //redisTemplate.opsForValue().getOperations().delete(stringJoiner.toString());
        refreshTokenRedisService.deleteRefreshToken(stringJoiner.toString());
    }

    /**
     * 更新用户角色id
     * @param umsAdminId 用户id
     * @param umsRoleId 角色id
     * @return true为更新成功,false为失败
     */
    @Override
    public Boolean updateUmsAdminRole(Long umsAdminId, Long umsRoleId) {

            return true;
    }

    /**
     * 刷新token
     * 校验传入的refreshToken
     * 如果合法且未过期，颁发新的token
     * @param refreshToken
     * @return
     */
    @Override
    public Map<String,Object> refreshToken(String refreshToken){

        Map<String,Object> tokenMap = new HashMap<>();

        Claims claims = jwtManager.parseRefreshToken(refreshToken);
        //不合法refreshtoken或者token已经过期
        if(claims == null || claims.getExpiration().before(new Date())){
            return null;
        }else {
        //    合法，返回新的token
            String token = jwtManager.generateToken(claims.getSubject());
            Date expireDate = new Date(System.currentTimeMillis() + expiration*1000);
            tokenMap.put("token",token);
            tokenMap.put("expireDate",expireDate);
            return tokenMap;
        }

    }


    ///**
    // * 将token放到redis中
    // * @param tokenHead token头，例如Bear
    // * @param token token
    // */
    //@Override
    //public void insertTokenToRedis(String key,String tokenHead, String token) {
    //
    //    StringBuilder stringBuilder = new StringBuilder();
    //    stringBuilder.append(tokenHead);
    //    stringBuilder.append(" ");
    //    stringBuilder.append(token);
    //
    //    redisTemplate.opsForValue().set(key,stringBuilder.toString(),expiration*1000, TimeUnit.MILLISECONDS);
    //
    //}

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
        //更新umsAdmin表中登录时间
        umsAdminMapper.updateLoginTime(umsAdmin.getId(),umsAdminLoginLog.getCreateTime());

        log.info("登录活动：用户-{}  时间-{}",umsAdmin.getUsername(),umsAdminLoginLog.getCreateTime());

    }

    /**
     * 更新用户数据
     * @param umsAdmin
     * @return 返回是否成功
     */
    @Override
    @Caching (
        put = {
            @CachePut(cacheNames = "umsAdmin", keyGenerator = "umsAdminNameKeyGenerator"),
            @CachePut(cacheNames = "umsAdmin", keyGenerator = "umsAdminIdKeyGenerator")
        }
    )
    public UmsAdmin updateUmsAdmin(UmsAdmin umsAdmin) {
        //id错误，找不到该用户信息
        if(umsAdminMapper.selectByPrimaryKey(umsAdmin.getId())==null){
            throw new UsernameNotFoundException("找不到该用户");
        }else {
            String encodePassword = passwordEncoder.encode(umsAdmin.getPassword());
            umsAdmin.setPassword(encodePassword);
            //更新成功
            if(umsAdminMapper.updateByPrimaryKey(umsAdmin)>0){
                return umsAdmin;
            }else {
                return null;
            }
        }

    }

    /**
     * 根据用户id删除用户
     * @param umsAdmin
     * @return
     */
    @Override
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "umsAdmin", keyGenerator = "umsAdminNameKeyGenerator"),
                    @CacheEvict(cacheNames = "umsAdmin", keyGenerator = "umsAdminIdKeyGenerator")
            }
    )
    public Boolean deleteUmsAdmin(UmsAdmin umsAdmin) {

        return umsAdminMapper.deleteByPrimaryKey(umsAdmin.getId())>0;

    }

    /**
     * 获取用户可操作性菜单列表
     * @param id
     * @return
     */
    @Override
    public List<UmsMenu> getUmsAdminMenu(Long id){
        List<UmsMenu> umsMenuList = umsAdminMapper.getUmsAdminMenu(id);
        return umsMenuList;
    }

    /**
     * 通过字段查询姓名或者用户名类似的用户
     * 分页表示
     * @param keyword 字段
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 返回页列表对象
     */
    @Override
    public List<UmsAdmin> getUmsAdminByNameLike(String keyword,Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        keyword = "%" + keyword + "%";
        return umsAdminMapper.selectByLikeName(keyword);
    }




}
