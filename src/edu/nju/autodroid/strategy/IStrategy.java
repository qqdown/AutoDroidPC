package edu.nju.autodroid.strategy;

public interface IStrategy {
	/**
	 * 获取当前策略的名字
	 * @return 策略名
	 */
	public abstract String getName();
	

	/**
	 * 开始策略
	 * @return 策略是否成功
	 */
	public abstract boolean run();

}
