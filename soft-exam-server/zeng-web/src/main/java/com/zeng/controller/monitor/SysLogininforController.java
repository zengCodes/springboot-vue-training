package com.zeng.controller.monitor;

import com.zeng.common.annotation.Log;
import com.zeng.common.core.controller.BaseController;
import com.zeng.common.core.domain.ResponseBean;
import com.zeng.common.core.domain.entity.SysUser;
import com.zeng.common.core.page.TableDataInfo;
import com.zeng.common.enums.BusinessType;
import com.zeng.common.utils.poi.ExcelUtil;
import com.zeng.system.domain.SysLogininfor;
import com.zeng.system.service.ISysLogininforService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统访问记录
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/logininfor")
public class SysLogininforController extends BaseController {
    @Autowired
    private ISysLogininforService logininforService;

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysLogininfor logininfor) {
        startPage();
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
        return getDataTable(list);
    }

    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:export')")
    @GetMapping("/export")
    public ResponseBean export(SysLogininfor logininfor) {
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
        ExcelUtil<SysLogininfor> util = new ExcelUtil<SysLogininfor>(SysLogininfor.class);
        return util.exportExcel(list, "登录日志");
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public ResponseBean remove(@PathVariable Long[] infoIds) {
        return toAjax(logininforService.deleteLogininforByIds(infoIds));
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public ResponseBean clean() {
        logininforService.cleanLogininfor();
        return ResponseBean.success();
    }

    /**
     * 登入报表
     *
     * @return
     */
    @PostMapping("/loginReport")
    @ApiOperation(value = "登入报表", notes = "用户登入报表")
    public ResponseBean loginReport(@RequestBody SysUser sysUser) {
        List<Map<String, Object>> meList = logininforService.userLoginReport(sysUser);
        Map<String, Object> map = new HashMap<>();
        sysUser.setUserName(null);
        List<Map<String, Object>> mapList = logininforService.userLoginReport(sysUser);
        map.put("me", meList);
        map.put("all", mapList);
        return ResponseBean.success(map);
    }
}
