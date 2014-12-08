package com.htyd.fan.om.util.base;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 创建一个可缓存的线程池,如果线程池的大小超过了处理任务所需要的线程，
 * 那么就会回收部分空闲（60秒不执行任务）的线程。当任务数增加时，此线程池又可以智能的添加新线程来处理任务
 * 此线程池不会对线程池大小做限制，线程池大小完全依赖于操作系统（或者说JVM）能够创建的最大线程大小。
 * 使用：ThreadPool.runMethod(r);
 */
public class ThreadPool {

	private static ExecutorService threadPool = null;

	private ThreadPool() {

	}

	/**
	 * 获取单例实例
	 * 
	 */
	private static class PoolHolder {
		private static ExecutorService instance = Executors
				.newCachedThreadPool();
	}

	private static ExecutorService getInstance() {
		return PoolHolder.instance;

	}

	/**
	 * 执行指定线程
	 * 
	 * @param r
	 * @return
	 */
	public static <R extends Runnable> void runMethod(R r) {
		if ((threadPool = getInstance()) == null) {
			// 意外情况threadPool为空时处理
			threadPool = Executors.newCachedThreadPool();
		}
		if (r != null) {
			// 执行线程
			threadPool.execute(r);
		}
	}

	/**
	 * 关闭线程池
	 */
	public static void shutDown() {
		if (threadPool != null) {
			threadPool.shutdown();
		}
	}

}
