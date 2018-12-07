package com.zz.upms.admin.listener;

import com.zz.upms.base.common.constans.SessionConstants;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Deque;

/**
 * shiro的session监听
 * 当用户退出登录，或者session过期时，将用户从缓存队列中踢出
 */
@Component
public class KickOutSessionListener extends SessionListenerAdapter {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private Cache<String, Deque<Serializable>> cache;

	@Autowired
	@Qualifier("shiroCacheManager")
	public void setCacheManager(CacheManager cacheManager) {
		this.cache = cacheManager.getCache(SessionConstants.KICK_OUT_SESSION_KEY);
	}

	private void removeSessionFromCache(Session session) {
		String userName = (String) session.getAttribute(SessionConstants.USER_NAME);
		if (userName != null) {
			logger.info("remove user {} session: {}", userName, session.getId());
			synchronized (this.cache) {
				Deque<Serializable> deque = cache.get(userName);
				if (deque != null) {
					deque.remove(session.getId());
				}
			}
		}
	}

	@Override
	public void onStop(Session session) {
		this.removeSessionFromCache(session);
	}

	@Override
	public void onExpiration(Session session) {
		this.removeSessionFromCache(session);
	}
}