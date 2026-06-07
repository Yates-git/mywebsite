package com.example.mywebsite;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * GlobalExceptionHandler.java - 全局异常处理器
 *
 * 作用：
 * - 统一处理未捕获的异常，返回友好的错误页
 * - 避免在 Controller 中堆叠 try/catch
 * - 将异常细节记录到日志，方便排查
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 数据完整性约束违反（唯一键、外键等）
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView handleIntegrity(DataIntegrityViolationException ex) {
        log.warn("数据完整性约束违反: {}", ex.getMostSpecificCause().getMessage());
        ModelAndView mv = new ModelAndView("error");
        mv.addObject("status", 400);
        mv.addObject("error", "操作失败：数据违反唯一性或外键约束");
        return mv;
    }

    /**
     * 实体未找到
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleNotFound(EntityNotFoundException ex) {
        ModelAndView mv = new ModelAndView("error");
        mv.addObject("status", 404);
        mv.addObject("error", "未找到对应记录");
        return mv;
    }

    /**
     * 兜底异常处理
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAll(Exception ex) {
        log.error("未处理异常", ex);
        ModelAndView mv = new ModelAndView("error");
        mv.addObject("status", 500);
        mv.addObject("error", "服务器内部错误");
        return mv;
    }

    /**
     * HTTP 方法不被支持（如把 POST 改 GET 后访问）
     * 不在这里处理会落到兜底返回 500，应当返回 405
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ModelAndView> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        ModelAndView mv = new ModelAndView("error");
        mv.addObject("status", 405);
        mv.addObject("error", "请求方法不被允许：" + ex.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(mv);
    }
}
