package com.zz.upms.base.service.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

import java.io.Serializable;
import java.util.UUID;

/**
 * 自定义的sessionId生成器
 */
public class RouteSessionIdGenerator implements SessionIdGenerator {

	public Serializable generateId(Session session) {
		return new StringBuffer().append(UUID.randomUUID().toString()).toString();
	}
}
