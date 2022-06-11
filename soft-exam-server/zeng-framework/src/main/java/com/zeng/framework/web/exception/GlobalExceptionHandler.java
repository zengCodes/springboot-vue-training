package com.zeng.framework.web.exception;

import com.zeng.common.constant.HttpStatus;
import com.zeng.common.core.domain.ResponseBean;
import com.zeng.common.exception.BaseException;
import com.zeng.common.exception.CustomException;
import com.zeng.common.exception.DemoModeException;
import com.zeng.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理器
 *
 * @author ruoyi
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 基础异常
     */
    @ExceptionHandler(BaseException.class)
    public ResponseBean baseException(BaseException e) {
        return ResponseBean.error(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(CustomException.class)
    public ResponseBean businessException(CustomException e) {
        if (StringUtils.isNull(e.getCode())) {
            return ResponseBean.error(e.getMessage());
        }
        return ResponseBean.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseBean handlerNoFoundException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseBean.error(HttpStatus.NOT_FOUND, "路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseBean handleAuthorizationException(AccessDeniedException e) {
        log.error(e.getMessage());
        return ResponseBean.error(HttpStatus.FORBIDDEN, "没有权限，请联系管理员授权");
    }

    @ExceptionHandler(AccountExpiredException.class)
    public ResponseBean handleAccountExpiredException(AccountExpiredException e) {
        log.error(e.getMessage(), e);
        return ResponseBean.error(e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseBean handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseBean.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseBean handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseBean.error(e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseBean validatedBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return ResponseBean.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object validExceptionHandler(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseBean.error(message);
    }

    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class)
    public ResponseBean demoModeException(DemoModeException e) {
        return ResponseBean.error("演示模式，不允许操作");
    }
}
