package com.zz.upms.base.aop;

import com.zz.upms.base.common.constans.Constants;
import com.zz.upms.base.dac.DacField;
import com.zz.upms.base.dac.DacUtils;
import com.zz.upms.base.dac.EnableDAC;
import com.zz.upms.base.entity.system.PmUser;
import com.zz.upms.base.service.shiro.ShiroDbRealm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * ************************************
 * create by Intellij IDEA
 * 数据访问控制拦截器(数据权限控制)
 * @author Francis.zz
 * @date 2020-07-10 17:42
 * ************************************
 */
@Aspect
@Component
@Slf4j
public class DACInterceptor {
    /**
     * 设置切入点
     */
    @Pointcut(value = "@annotation(enableDAC)", argNames = "enableDAC")
    public void pointCut(EnableDAC enableDAC) {
    
    }
    
    /**
     * 这里只能过滤实现了{@link DacField}接口的数据。如果是分页数据，这里无法改变分页的total总数，如需实现可通过前端分页先过滤全部数据。
     */
    @Around(value = "pointCut(enableDAC)")
    public Object idempotentConsumeDBAround(ProceedingJoinPoint joinPoint, EnableDAC enableDAC) {
        Object[] args = joinPoint.getArgs();
        Object result = null;
        try {
            result = joinPoint.proceed(args);
            /*ShiroDbRealm.ShiroUser curUser = (ShiroDbRealm.ShiroUser) SecurityUtils.getSubject().getPrincipal();
            if(Constants.SUPER_ADMIN.equals(curUser.id)) {
                // 超管不过滤数据
                return result;
            }*/
            
            if(result == null) {
                return null;
            }
            String getMethodName = enableDAC.getMethod();
            String setMethodName = enableDAC.setMethod();
            Class<?> setMethodParam = enableDAC.setMethodParams();
    
            if(StringUtils.isNotEmpty(getMethodName) && StringUtils.isNotEmpty(setMethodName)) {
                return filterData(result, getMethodName, setMethodName, setMethodParam);
            }
            return filterData(result);
        } catch (Throwable throwable) {
            log.error("getProxy error", throwable);
        }
        return result;
    }
    
    private Object filterData(Object oriData, String getMethodName, String setMethodName, Class<?> setMethodParam) {
        try {
            Method getMethod = oriData.getClass().getMethod(getMethodName);
            Method setMethod = oriData.getClass().getMethod(setMethodName, setMethodParam);
            
            Object data = getMethod.invoke(oriData);
            if(!setMethodParam.isAssignableFrom(data.getClass())) {
                log.warn("[DAC] get method response data is not assignable from set method param");
                return oriData;
            }
            Object result = filterData(data);
            
            // 将过滤结果放回原始数据中
            setMethod.invoke(oriData, result);
            
        }catch (Exception e) {
            log.warn("[DAC] filter data error", e);
            return oriData;
        }
        return oriData;
    }
    
    private Object filterData(Object oriData) {
        if(oriData == null) {
            return null;
        }
        List<String> curDacGroup = getCurUserDacGroup();
        if(ObjectUtils.isEmpty(curDacGroup)) {
            return null;
        }
        if(oriData instanceof DacField) {
            if(!getCurUserDacGroup().contains(((DacField) oriData).getDacField())) {
                return null;
            }
        } else if(oriData instanceof Iterable) {
            Iterator it = ((Iterable) oriData).iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if(o instanceof DacField && !curDacGroup.contains(((DacField) o).getDacField())) {
                    it.remove();
                }
            }
        }
        
        return oriData;
    }
    
    private List<String> getCurUserDacGroup() {
        ShiroDbRealm.ShiroUser curUser = (ShiroDbRealm.ShiroUser) SecurityUtils.getSubject().getPrincipal();
        List<String> curDacGroup = DacUtils.getDacGroup(curUser.getDacGroup());
        
        return curDacGroup;
    }
}
