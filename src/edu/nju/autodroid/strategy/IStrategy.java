package edu.nju.autodroid.strategy;

public interface IStrategy {
	/**
	 * ��ȡ��ǰ���Ե�����
	 * @return ������
	 */
	public abstract String getName();
	

	/**
	 * ��ʼ����
	 * @return �����Ƿ�ɹ�
	 */
	public abstract boolean run();

}
