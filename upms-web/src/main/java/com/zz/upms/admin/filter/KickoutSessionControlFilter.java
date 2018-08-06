package com.zz.upms.admin.filter;

import com.zz.upms.base.common.constans.SessionConstants;
import com.zz.upms.base.service.shiro.ShiroDbRealm;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

/**
 * 控制同一账号登陆人数的filter
 */
@Component
public class KickoutSessionControlFilter extends AccessControlFilter {
	private Logger log = LoggerFactory.getLogger(getClass());

	// 踢出后到的地址
	private String kickoutUrl;
	// 踢出之前登录的/之后登录的用户 默认踢出之前登录的用户
	private boolean kickoutAfter = false;
	// 同一个帐号最大会话数 默认1
	private int maxSession = 1;
	private SessionManager sessionManager;
	private Cache<String, Deque<Serializable>> cache;

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		return false;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		
		Subject subject = getSubject(request, response);
		if (!subject.isAuthenticated() && !subject.isRemembered()) {
			// 如果没有登录，直接进行之后的流程
			return true;
		}

		Session session = subject.getSession();
		ShiroDbRealm.ShiroUser user = (ShiroDbRealm.ShiroUser) subject.getPrincipal();
		Serializable sessionId = session.getId();
		
		if (SessionConstants.ADMIN.equals(user.username)) {
			return true;
		}
		
		Deque<Serializable> deque = cache.get(user.username);
		if (deque == null) {
			deque = new LinkedList<Serializable>();
			cache.put(user.username, deque);
		}

		// 如果队列里没有此sessionId，且用户没有被踢出；放入队列
		if (!deque.contains(sessionId) && session.getAttribute(SessionConstants.KICK_FLAG) == null) {
			deque.push(sessionId);
		}

		// 如果队列里的sessionId数超出最大会话数，开始踢人
		while (deque.size() > maxSession) {
			Serializable kickoutSessionId = null;
			if (kickoutAfter) { // 如果踢出后者
				kickoutSessionId = deque.removeFirst();
			} else { // 否则踢出前者
				kickoutSessionId = deque.removeLast();
			}
			try {
				// sessionManager.get
				Session kickoutSession = sessionManager.getSession(new DefaultSessionKey(kickoutSessionId));
				if (kickoutSession != null) {
					// 设置会话的kickout属性表示踢出了
					kickoutSession.setAttribute(SessionConstants.KICK_FLAG, true);
				}
			} catch (Exception e) {
				log.error("KickoutSessionControlFilter getSession error.", e);
			}
		}

		// 如果被踢出了，直接退出，重定向到踢出后的地址
		if (session.getAttribute(SessionConstants.KICK_FLAG) != null) {
			// 会话被踢出了
			try {
				subject.logout();
			} catch (Exception e) {
				log.error("KickoutSessionControlFilter logout error.", e);
			}
			saveRequest(request);
			if ("XMLHttpRequest".equalsIgnoreCase(httpServletRequest
					.getHeader("X-Requested-With"))) {
				httpServletResponse.setCharacterEncoding("UTF-8");
				PrintWriter out = httpServletResponse.getWriter();
				out.println("{status:'login_kickout',content:'账号在其他地方登录'}");
				out.flush();
				out.close();
				return false;
			}else {
				WebUtils.issueRedirect(request, response, kickoutUrl);
			}
			return false;
		}

		return true;
	}

	public void setKickoutUrl(String kickoutUrl) {
		this.kickoutUrl = kickoutUrl;
	}

	public void setKickoutAfter(boolean kickoutAfter) {
		this.kickoutAfter = kickoutAfter;
	}

	public void setMaxSession(int maxSession) {
		this.maxSession = maxSession;
	}

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cache = cacheManager.getCache(SessionConstants.KICK_OUT_SESSION_KEY);
	}
}