package com.github.system.router.service.impl;

import com.github.system.router.dto.AsyncRouter;
import com.github.system.router.dto.AsyncRouterChildren;
import com.github.system.router.dto.AsyncRouterMeta;
import com.github.system.router.service.SystemRouterService;
import com.github.system.task.entity.AutoIndex;
import com.github.system.task.service.AutoIndexService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SystemRouterServiceImpl implements SystemRouterService {

    @Resource
    private AutoIndexService autoIndexService;


    @Override
    public List<AsyncRouter> getAsyncRouter() {
        // 悲催 不知道前端怎么手动渲染我想要的路由 只能按照前端平台底层封装好的格式给他组装完返回去0.0
        List<AsyncRouter> asyncRouterList = new ArrayList<>();
        asyncRouterList.add(getAutoIndexRouter());
        return asyncRouterList;
    }

    private AsyncRouter getAutoIndexRouter() {
        List<AutoIndex> autoIndices = autoIndexService.userList();
        List<AsyncRouterChildren> routerChildrenList = new ArrayList<>();
        for (AutoIndex autoIndex : autoIndices) {
            AsyncRouterMeta meta = new AsyncRouterMeta();
            meta.setTitle(autoIndex.getName());
            meta.setIcon(autoIndex.getIcon());
            meta.setShowParents(true);
            AsyncRouterChildren asyncRouterChildren = new AsyncRouterChildren();
            asyncRouterChildren.setMeta(meta);
            asyncRouterChildren.setPath("/auto/task?id=" + autoIndex.getId());
//            asyncRouterChildren.setComponent("/auto/task/index");
            asyncRouterChildren.setName("TaskInfoPage" + autoIndex.getCode());
            asyncRouterChildren.setParams(Map.of("id", String.valueOf(autoIndex.getId())));
            asyncRouterChildren.setQuery(Map.of("id", String.valueOf(autoIndex.getId())));
            routerChildrenList.add(asyncRouterChildren);
        }
        AsyncRouterMeta asyncRouterMeta = new AsyncRouterMeta();
        asyncRouterMeta.setIcon("listCheck");
        asyncRouterMeta.setTitle("自动任务列表");
        asyncRouterMeta.setRank(10);
        AsyncRouter asyncRouter = new AsyncRouter();
        asyncRouter.setPath("/auto/task");
        asyncRouter.setMeta(asyncRouterMeta);
        asyncRouter.setChildren(routerChildrenList);
        return asyncRouter;
    }

}
