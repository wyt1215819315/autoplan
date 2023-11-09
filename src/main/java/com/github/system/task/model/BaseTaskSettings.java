package com.github.system.task.model;

/**
 * 任务配置信息基类
 * BaseTaskService里的T类型
 * 这里的字段，在用户新增任务时都会被持久化到数据库供之后的任务调用
 * 可以使用javax的validation进行字段验证
 * 如果注解验证还不满足 可以实现BaseTaskService中的validate方法，私有化注解验证并处理要储存的数据
 */
public class BaseTaskSettings {
}
