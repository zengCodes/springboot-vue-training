package com.zeng.controller.system;

import com.zeng.common.constant.Constants;
import com.zeng.business.converter.MenuConverter;
import com.zeng.common.core.domain.ResponseBean;
import com.zeng.common.core.domain.entity.SysMenu;
import com.zeng.common.core.domain.entity.SysUser;
import com.zeng.common.core.domain.model.LoginBody;
import com.zeng.common.core.domain.model.LoginUser;
import com.zeng.common.utils.SecurityUtils;
import com.zeng.common.utils.ServletUtils;
import com.zeng.framework.web.service.SysLoginService;
import com.zeng.framework.web.service.SysPermissionService;
import com.zeng.framework.web.service.TokenService;
import com.zeng.system.service.ISysMenuService;
import com.zeng.system.service.ISysUserService;
import com.zeng.system.service.impl.SysUserServiceImpl;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * 登录验证
 *
 * @author zeng
 */
@RestController
@Slf4j
public class SysLoginController {
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysUserService iSysUserService;

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登入", notes = "接收参数用户名和密码,登入成功后,返回Token")
    public ResponseBean login(@RequestBody LoginBody loginBody) {
        ResponseBean ajax = ResponseBean.success();
        // 查询用户类型
        SysUser sysUser = iSysUserService.selectUserByUserName(loginBody.getUsername());
        if (sysUser.getUserType() == "00") {
            // 生成令牌
            String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                    loginBody.getUuid());
            ajax.put(Constants.TOKEN, token);
            return ajax;
        } else {
            // 生成令牌
            String token = loginService.login(loginBody.getUsername(), loginBody.getPassword());
            ajax.put(Constants.TOKEN, token);
            return ajax;
        }


    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getInfo")
    @ApiOperation(value = "登录用户信息", notes = "用户登入信息")
    public ResponseBean getInfo() {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser user = loginUser.getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        ResponseBean ajax = ResponseBean.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("/getRouters")
    @ApiOperation(value = "路由信息", notes = "根据用户获取路由信息")
    public ResponseBean getRouters() {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return ResponseBean.success(MenuConverter.converterToMenuNodeVO(menus));
    }
}
